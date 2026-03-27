package com.meta.pixelandtexel.scanner

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import com.meta.pixelandtexel.scanner.android.views.smarthome.selection.DeviceSelectionScreen
import com.meta.pixelandtexel.scanner.android.views.smarthome.selection.DeviceSelectionViewModel
import com.meta.pixelandtexel.scanner.android.views.welcome.WelcomeScreen
import com.meta.pixelandtexel.scanner.ecs.OutlinedSystem
import com.meta.pixelandtexel.scanner.ecs.WristAttachedSystem
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.MRUKSidePanelRaycasterFeature
import com.meta.pixelandtexel.scanner.feature.objectdetection.ObjectDetectionFeature
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.IDisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.camera.enums.CameraStatus
import com.meta.pixelandtexel.scanner.feature.objectdetection.model.RaycastRequestModel
import com.meta.pixelandtexel.scanner.services.settings.SettingsService
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.composePanel
import com.meta.spatial.compose.panelViewLifecycleOwner
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.mruk.SurfaceType
import com.meta.spatial.core.SendRate
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLoadDeviceResult
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.PanelShapeLayerBlendType
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import java.io.File


class MainActivity : ActivityCompat.OnRequestPermissionsResultCallback, AppSystemActivity() {
    companion object {
        private const val TAG = "MainActivity"

        private const val PERMISSIONS_REQUEST_CODE = 1000
        private val PERMISSIONS_REQUIRED = arrayOf("horizonos.permission.HEADSET_CAMERA", "com.oculus.permission.USE_SCENE")
        const val MAX_DISTANCE = Float.MAX_VALUE
    }

    // used for scene inflation
    private var gltfxEntity: Entity? = null
    private val activityScope = CoroutineScope(Dispatchers.Main)

    private lateinit var permissionsResultCallback: (granted: Boolean) -> Unit

    // button for toggling the scanning
    private var scanControlsBtn: ImageButton? = null

    // our main scene entities
    private var welcomePanelEntity: Entity? = null

    // our main services for detected object, displaying helpful tips, and displaying pre-assembled
    // panel content for select objects (with 3D models)
    private lateinit var objectDetectionFeature: ObjectDetectionFeature
    private lateinit var mrukFeature: MRUKFeature
    private lateinit var mrukSidePanelRaycasterFeature: MRUKSidePanelRaycasterFeature


    lateinit var entityRepository: IDisplayedEntityRepository

    override fun registerFeatures(): List<SpatialFeature> {
        objectDetectionFeature =
            ObjectDetectionFeature(
                this,
                onStatusChanged = ::onObjectDetectionFeatureStatusChanged,
            )

        mrukFeature = MRUKFeature(this, systemManager)
        mrukSidePanelRaycasterFeature = MRUKSidePanelRaycasterFeature(this)
        return listOf(
            VRFeature(this),
            ComposeFeature(),
            objectDetectionFeature,
            mrukFeature,
            mrukSidePanelRaycasterFeature
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SettingsService.initialize(this)

        NetworkedAssetLoader.init(
            File(applicationContext.cacheDir.canonicalPath),
            OkHttpAssetFetcher()
        )

        // extra object detection handling and usability
        entityRepository = get()

        // register systems/components
        systemManager.unregisterSystem<LocomotionSystem>()

        componentManager.registerComponent<WristAttached>(WristAttached.Companion, SendRate.DEFAULT)
        systemManager.registerSystem(WristAttachedSystem())

        componentManager.registerComponent<Outlined>(Outlined.Companion, SendRate.DEFAULT)
        systemManager.registerSystem(OutlinedSystem(this))

        loadGLXF().invokeOnCompletion {
            val composition = glXFManager.getGLXFInfo("scanner_app_main_scene")

            // wait for system manager to initialize so we can get the underlying scene objects
            welcomePanelEntity = composition.getNodeByName("WelcomePanel").entity
        }
    }

    override fun onSceneReady() {
        super.onSceneReady()

        // set the reference space to enable re-centering
        scene.setReferenceSpace(ReferenceSpace.STAGE)

        requestPermissions { permissionsGranted ->
            loadScene(permissionsGranted)
        }

        scene.enablePassthrough(true)
    }

    private fun loadScene(scenePermissionsGranted: Boolean) {
        if (scenePermissionsGranted) {
            loadSceneFromDevice()
        } else {
            Log.d("JAVI DEBUG", "Permisos denegados. No se puede cargar la escena desde el dispositivo.")
        }
    }
    private fun loadSceneFromDevice() {
        val future = mrukFeature.loadSceneFromDevice(requestSceneCaptureIfNoDataFound = true)

        future.whenComplete { result: MRUKLoadDeviceResult, _ ->
            Log.d("JAVI DEBUG", "Scene loaded from device with result: $result")

            if (result != MRUKLoadDeviceResult.SUCCESS) {
                Log.d("JAVI DEBUG", "error")
            }
        }
    }

    override fun registerPanels(): List<PanelRegistration> {
        return listOf(
            PanelRegistration(R.integer.welcome_panel_id) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 368f
                    width = 0.368f
                    height = 0.404f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                    effectShader = "customPanel.frag" // just for demonstration purposes
                }
                composePanel {
                    setContent {
                        CompositionLocalProvider(
                            LocalOnBackPressedDispatcherOwner provides
                                    object : OnBackPressedDispatcherOwner {
                                        override val lifecycle: Lifecycle
                                            get() = this@MainActivity.panelViewLifecycleOwner.lifecycle

                                        override val onBackPressedDispatcher: OnBackPressedDispatcher
                                            get() = OnBackPressedDispatcher()
                                    }
                        ) {
                            WelcomeScreen {
                                welcomePanelEntity?.destroy()
                                welcomePanelEntity = null
                            }
                        }
                    }
                }
            },
            PanelRegistration(R.layout.ui_show_smart_things_button_view) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 80f
                    width = 0.04f
                    height = 0.04f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                panel {
                    val showSmartThingsButton =
                        rootView?.findViewById<ImageButton>(R.id.show_smart_things_btn)
                            ?: throw RuntimeException("Missing help button")

                    showSmartThingsButton.setOnClickListener {
                        welcomePanelEntity?.destroy()
                        welcomePanelEntity = null
                        stopScanning()
                        loadScene(true)

                        activityScope.launch {
                            mrukSidePanelRaycasterFeature.getAllSmartThings()
                        }
                    }
                }
            },
            PanelRegistration(R.layout.ui_delete_smart_things_button_view) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 80f
                    width = 0.04f
                    height = 0.04f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                panel {
                    val deleteBtn =
                        rootView?.findViewById<ImageButton>(R.id.delete_btn)
                            ?: throw RuntimeException("Missing delete button")

                    deleteBtn.setOnClickListener {
                        welcomePanelEntity?.destroy()
                        welcomePanelEntity = null
                        stopScanning()
                        loadScene(true)

                        activityScope.launch {
                            mrukSidePanelRaycasterFeature.deleteAllSmartThingEntities()
                        }
                    }
                }
            },
            PanelRegistration(R.layout.ui_camera_controls_view) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 80f
                    width = 0.04f
                    height = 0.04f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                panel {
                    scanControlsBtn =
                        rootView?.findViewById(R.id.camera_play_btn)
                            ?: throw RuntimeException("Missing camera play/pause button")

                    scanControlsBtn?.setOnClickListener {
                        welcomePanelEntity?.destroy()
                        welcomePanelEntity = null

                        when (objectDetectionFeature.status) {
                            CameraStatus.PAUSED -> {
                                // first ask permission if we haven't already
                                if (!hasPermissions()) {
                                    this@MainActivity.requestPermissions { granted ->
                                        if (granted) {
                                            startScanning()
                                        }
                                    }

                                    return@setOnClickListener
                                }

                                startScanning()
                            }

                            CameraStatus.SCANNING -> {
                                stopScanning()
                            }
                        }
                    }
                }
            },
            PanelRegistration(R.integer.info_panel_id) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 632f
                    width = 0.632f
                    height = 0.644f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                composePanel {
                    stopScanning()
                    val displayInfo = entityRepository.newViewModelData ?: return@composePanel
                    setContent {
                        val viewmodel = DeviceSelectionViewModel(displayInfo.data.type, get())
                        DeviceSelectionScreen(
                            viewModel = viewmodel,
                            onOptionSelected = { device ->
                                entityRepository.deleteEntity(displayInfo.entityId)
                                val spawnPose =
                                    getPanelHitSpawnPosition(displayInfo.data.raycastInfo)
                                if (spawnPose != null) {
                                    activityScope.launch {
                                        mrukSidePanelRaycasterFeature.addSmartThing(
                                            device,
                                            spawnPose
                                        )
                                    }
                                } else {
                                    Log.w(
                                        "MainActivity",
                                        "No se pudo obtener la posición de spawn para el objeto detectado."
                                    )
                                }

                            }
                        )
                    }
                }
            },
        )
    }

    private fun getPanelHitSpawnPosition(raycastModel: RaycastRequestModel): Pose? {
        val currentRoom = mrukFeature.getCurrentRoom()
        if (currentRoom == null) {
            Log.w("UpdateRaycastSystem", "Cannot raycast, no current room available.")
            return null
        }


        val hit = mrukFeature.raycastRoom(
            currentRoom.anchor.uuid,
            origin = raycastModel.headPosition,
            direction = raycastModel.direction,
            maxDistance = MAX_DISTANCE,
            SurfaceType.PLANE_VOLUME,
        ) ?: return null
        return Pose(hit.hitPosition, Quaternion.lookRotation(hit.hitNormal.normalize()))

    }


    /** Activates the object detection feature scanning, which turns on the user's camera. */
    private fun startScanning() {
        objectDetectionFeature.scan()
    }

    /** Stops the object detection and device camera. */
    private fun stopScanning() {
        objectDetectionFeature.pause()
    }

    /**
     * Executed when the object detection feature has scanning status has changed.
     *
     * @param newStatus The new [CameraStatus] camera scanning status
     */
    private fun onObjectDetectionFeatureStatusChanged(newStatus: CameraStatus) {
        scanControlsBtn?.setBackgroundResource(
            when (newStatus) {
                CameraStatus.PAUSED -> R.drawable.escaneo
                CameraStatus.SCANNING -> com.meta.spatial.uiset.R.drawable.ic_pause_circle_24
            }
        )
    }


    override fun onPause() {
        stopScanning()
        super.onPause()
    }

    private fun loadGLXF(): Job {
        gltfxEntity = Entity.Companion.create()
        return activityScope.launch {
            glXFManager.inflateGLXF(
                "apk:///scenes/Composition.glxf".toUri(),
                rootEntity = gltfxEntity!!,
                keyName = "scanner_app_main_scene",
            )
        }
    }

    private fun hasPermissions() =
        PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestPermissions(callback: (granted: Boolean) -> Unit) {
        permissionsResultCallback = callback

        if (hasPermissions()) {
            Log.d(TAG, "Los permisos ya estaban concedidos. Saltando la solicitud.")
            callback(true)
        } else {
            Log.d(TAG, "Permisos no concedidos. Solicitando al usuario...")
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                Log.d(TAG, "Todos los permisos fueron concedidos por el usuario.")
                permissionsResultCallback.invoke(true)
            } else {
                Log.w(TAG, "Al menos un permiso fue denegado.")
                permissionsResultCallback.invoke(false)
            }
        }
    }
}