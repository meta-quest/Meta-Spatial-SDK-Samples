// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.activities

import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.meta.pixelandtexel.scanner.Outlined
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.WristAttached
import com.meta.pixelandtexel.scanner.ecs.OutlinedSystem
import com.meta.pixelandtexel.scanner.ecs.WristAttachedSystem
import com.meta.pixelandtexel.scanner.models.CuratedObject
import com.meta.pixelandtexel.scanner.models.ObjectInfoRequest
import com.meta.pixelandtexel.scanner.objectdetection.ObjectDetectionFeature
import com.meta.pixelandtexel.scanner.objectdetection.camera.enums.CameraStatus
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObjectsResult
import com.meta.pixelandtexel.scanner.services.CuratedObjectHandler
import com.meta.pixelandtexel.scanner.services.TipManager
import com.meta.pixelandtexel.scanner.services.UserEvent
import com.meta.pixelandtexel.scanner.services.settings.SettingsService
import com.meta.pixelandtexel.scanner.utils.MathUtils.fromAxisAngle
import com.meta.pixelandtexel.scanner.viewmodels.CuratedObjectInfoViewModel
import com.meta.pixelandtexel.scanner.viewmodels.ObjectInfoViewModel
import com.meta.pixelandtexel.scanner.views.objectinfo.CuratedObjectInfoScreen
import com.meta.pixelandtexel.scanner.views.objectinfo.ObjectInfoScreen
import com.meta.pixelandtexel.scanner.views.welcome.WelcomeScreen
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.composePanel
import com.meta.spatial.compose.panelViewLifecycleOwner
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SendRate
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.io.File
import kotlin.math.PI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Main entry point for the Quest application. See the README for an in-depth description for how
 * this application functions, and see the official
 * [Meta Spatial SDK documentation](https://developers.meta.com/horizon/develop/spatial-sdk) for how
 * to build Spatial applications, or convert your existing Android application to function in the
 * Quest headset.
 */
class MainActivity : ActivityCompat.OnRequestPermissionsResultCallback, AppSystemActivity() {
  companion object {
    private const val TAG = "MainActivity"

    private const val PERMISSIONS_REQUEST_CODE = 1000
    private val PERMISSIONS_REQUIRED = arrayOf("horizonos.permission.HEADSET_CAMERA")

    private const val INFO_PANEL_WIDTH = 0.632f
    private const val CURATED_INFO_PANEL_WIDTH = 0.708f
  }

  // used for scene inflation
  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)

  private lateinit var permissionsResultCallback: (granted: Boolean) -> Unit

  // button for toggling the scanning
  private var cameraControlsBtn: ImageButton? = null

  // our main scene entities
  private var welcomePanelEntity: Entity? = null
  private var infoPanelEntity: Entity? = null
  private var curatedObjectEntity: Entity? = null

  // data used for panel content
  private var pendingInfoRequest: ObjectInfoRequest? = null
  private var pendingCuratedObject: CuratedObject? = null

  // our main services for detected object, displaying helpful tips, and displaying pre-assembled
  // panel content for select objects (with 3D models)
  private lateinit var objectDetectionFeature: ObjectDetectionFeature
  private lateinit var curatedObjectHandler: CuratedObjectHandler
  private lateinit var tipManager: TipManager

  override fun registerFeatures(): List<SpatialFeature> {
    objectDetectionFeature =
        ObjectDetectionFeature(
            this,
            onStatusChanged = ::onObjectDetectionFeatureStatusChanged,
            onDetectedObjects = ::onObjectsDetected,
            confirmTrackedObjectSelected = ::confirmTrackedObjectSelected,
            onTrackedObjectSelected = ::showInfoPanelForObject)

    return listOf(
        VRFeature(this),
        ComposeFeature(),
        objectDetectionFeature,
        IsdkFeature(this, spatial, systemManager))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    SettingsService.initialize(this)

    NetworkedAssetLoader.init(File(applicationContext.cacheDir.canonicalPath), OkHttpAssetFetcher())

    // extra object detection handling and usability

    curatedObjectHandler =
        CuratedObjectHandler(this, ::selectedCuratedObjectFromSelection, R.xml.objects)
    tipManager =
        TipManager(this) {
          stopScanning()
          curatedObjectHandler.spawnCuratedObjectsForSelection()
        }

    // register systems/components

    systemManager.unregisterSystem<LocomotionSystem>()

    // FIXME not working; prevent isdk components from automatically being added to all panels
    // systemManager.findSystem<IsdkToolkitBridgeSystem>().active = false

    componentManager.registerComponent<WristAttached>(WristAttached.Companion, SendRate.DEFAULT)
    systemManager.registerSystem(WristAttachedSystem())

    componentManager.registerComponent<Outlined>(Outlined.Companion, SendRate.DEFAULT)
    systemManager.registerSystem(OutlinedSystem(this))

    // wait for GLXF to load before accessing nodes inside it

    loadGLXF().invokeOnCompletion {
      val composition = glXFManager.getGLXFInfo("scanner_app_main_scene")

      // wait for system manager to initialize so we can get the underlying scene objects
      CoroutineScope(Dispatchers.Main).launch {
        delay(250)
        curatedObjectHandler.getObjectMeshEntities(this@MainActivity, composition)
      }

      welcomePanelEntity = composition.getNodeByName("WelcomePanel").entity
    }
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // set the reference space to enable re-centering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.2f)
    scene.updateIBLEnvironment("museum_lobby.env")

    scene.setViewOrigin(0.0f, 0.0f, 0.0f, 180.0f)

    scene.enablePassthrough(true)
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
            enableTransparent = true
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
                      }) {
                    WelcomeScreen {
                      welcomePanelEntity?.destroy()
                      welcomePanelEntity = null
                    }
                  }
            }
          }
        },
        PanelRegistration(R.layout.ui_help_button_view) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layoutWidthInDp = 80f
            width = 0.04f
            height = 0.04f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
          panel {
            val helpBtn =
                rootView?.findViewById<ImageButton>(R.id.help_btn)
                    ?: throw RuntimeException("Missing help button")

            helpBtn.setOnClickListener {
              welcomePanelEntity?.destroy()
              welcomePanelEntity = null
              stopScanning()
              dismissInfoPanel()
              tipManager.dismissTipPanels()
              curatedObjectHandler.dismissCuratedObjectsSelection()

              tipManager.showHelpPanel()
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
            enableTransparent = true
          }
          panel {
            cameraControlsBtn =
                rootView?.findViewById(R.id.camera_play_btn)
                    ?: throw RuntimeException("Missing camera play/pause button")

            cameraControlsBtn?.setOnClickListener {
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
            enableTransparent = true
          }
          composePanel {
            val vm =
                ObjectInfoViewModel(pendingInfoRequest!!, getString(R.string.object_query_template))
            pendingInfoRequest = null

            setContent {
              ObjectInfoScreen(
                  vm,
                  onResume = {
                    startScanning()
                    tipManager.reportUserEvent(UserEvent.DISMISSED_INFO_PANEL)
                  },
                  onClose = {
                    dismissInfoPanel()
                    tipManager.reportUserEvent(UserEvent.DISMISSED_INFO_PANEL)
                  })
            }
          }
        },
        PanelRegistration(R.integer.curated_info_panel_id) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layoutWidthInDp = 708f
            width = 0.708f
            height = 0.644f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
          composePanel {
            val vm = CuratedObjectInfoViewModel(pendingCuratedObject!!.ui, curatedObjectEntity)
            pendingCuratedObject = null

            setContent {
              CompositionLocalProvider(
                  LocalOnBackPressedDispatcherOwner provides
                      object : OnBackPressedDispatcherOwner {
                        override val lifecycle: Lifecycle
                          get() = this@MainActivity.panelViewLifecycleOwner.lifecycle

                        override val onBackPressedDispatcher: OnBackPressedDispatcher
                          get() = OnBackPressedDispatcher()
                      }) {
                    CuratedObjectInfoScreen(
                        vm, onResume = ::startScanning, onClose = ::dismissInfoPanel)
                  }
            }
          }
        })
  }

  /**
   * Destroys the info panel entity displaying the llama generated description or pre-written copy
   * and images about the detected object.
   */
  private fun dismissInfoPanel() {
    // destroy any info panel that may be displayed, and dismiss any curated object
    infoPanelEntity?.destroy()
    infoPanelEntity = null
    toggleCuratedObject(false)
    curatedObjectEntity = null
  }

  /** Activates the object detection feature scanning, which turns on the user's camera. */
  private fun startScanning() {
    dismissInfoPanel()
    tipManager.dismissTipPanels()
    curatedObjectHandler.dismissCuratedObjectsSelection()

    objectDetectionFeature.scan()

    tipManager.reportUserEvent(UserEvent.STARTED_SCANNING)
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
    cameraControlsBtn?.setBackgroundResource(
        when (newStatus) {
          CameraStatus.PAUSED -> com.meta.spatial.uiset.R.drawable.ic_play_circle_24
          CameraStatus.SCANNING -> com.meta.spatial.uiset.R.drawable.ic_pause_circle_24
        })
  }

  /**
   * The object detection feature has detected objects in the user's camera feed.
   *
   * @param result A [DetectedObjectsResult] representation of the object detection result.
   */
  private fun onObjectsDetected(result: DetectedObjectsResult) {
    if (result.objects.any()) {
      tipManager.reportUserEvent(UserEvent.DETECTED_OBJECT)
    }
  }

  /**
   * The object detection feature asking whether or not to confirm the selection of a detected
   * object by the user.
   *
   * @return Whether or not to allow the object to be selected. Return false to abort the image
   *   snapshot crop and conversion.
   */
  private fun confirmTrackedObjectSelected(): Boolean {
    if (pendingInfoRequest != null || pendingCuratedObject != null) {
      Log.w(TAG, "A pending request or curated object already exists")
      return false
    }

    if (infoPanelEntity != null) {
      Log.w(TAG, "An object info panel already exists")
      return false
    }

    return true
  }

  /**
   * The user selected an object detected by the object detection feature, and the selection was
   * confirmed by the confirmTrackedObjectSelected call; spawn an information panel for the object.
   *
   * @param name The label or name of the object detected in the camera feed
   * @param image An [android.media.Image] image of the detected object, cropped from the device
   *   camera feed frame.
   * @param pose A [Pose] pose representing the user's head position, and the direction vector from
   *   the head to the right edge of the detected object in the user's view, at eye level.
   */
  private fun showInfoPanelForObject(name: String, image: Bitmap, pose: Pose) {
    stopScanning()
    tipManager.dismissTipPanels()

    // spawn the ui for curated (pre-assembled content) object
    if (curatedObjectHandler.isCuratedObject(name)) {
      pendingCuratedObject = curatedObjectHandler.getObjectInfo(name)
      curatedObjectEntity = pendingCuratedObject!!.meshEntity

      val spawnPose = getPanelSpawnPosition(pose, CURATED_INFO_PANEL_WIDTH, 1.2f)
      infoPanelEntity =
          Entity.createPanelEntity(
              R.integer.curated_info_panel_id,
              Transform(spawnPose),
              Grabbable(type = GrabbableType.PIVOT_Y))

      tipManager.reportUserEvent(UserEvent.SELECTED_CURATED_OBJECT)

      // re-orient the mesh entity
      if (curatedObjectEntity != null) {
        // calculate the position and rotation from the panel pose and our offsets
        val position = spawnPose.t + spawnPose.q.times(pendingCuratedObject!!.meshPositionOffset)
        val rotation = spawnPose.q.times(pendingCuratedObject!!.meshRotationOffset)
        val objectPose = Pose(position, rotation)

        toggleCuratedObject(true, objectPose)
      }
    }
    // spawn the ui for generic object (info fetched from llama 3.2 vision)
    else {
      pendingInfoRequest = ObjectInfoRequest(name, image)

      val spawnPose = getPanelSpawnPosition(pose, INFO_PANEL_WIDTH)
      infoPanelEntity =
          Entity.createPanelEntity(
              R.integer.info_panel_id,
              Transform(spawnPose),
              Grabbable(type = GrabbableType.PIVOT_Y))

      tipManager.reportUserEvent(UserEvent.SELECTED_OBJECT)
    }
  }

  /**
   * After the user has activated the curated object selection mode, where the 3D objects
   * representing the curated object with pre-assembled panel content are spawned in front of the
   * user eye level, array radially, the user has selected one of the models.
   *
   * @param curatedObject The [CuratedObject] curated object that was selected.
   * @param pose A [Pose] pose representing the user's head position, and the direction vector from
   *   the head to the right edge of the selected object in the user's view, at eye level.
   */
  private fun selectedCuratedObjectFromSelection(curatedObject: CuratedObject, pose: Pose) {
    pendingCuratedObject = curatedObject
    curatedObjectEntity = pendingCuratedObject!!.meshEntity

    val spawnPose = getPanelSpawnPosition(pose, CURATED_INFO_PANEL_WIDTH, 1.2f)
    infoPanelEntity =
        Entity.createPanelEntity(
            R.integer.curated_info_panel_id,
            Transform(spawnPose),
            Grabbable(type = GrabbableType.PIVOT_Y))

    tipManager.reportUserEvent(UserEvent.SELECTED_CURATED_OBJECT)

    // reveal the mesh entity without moving it

    toggleCuratedObject(true)
  }

  /**
   * Compute the pose with which to orient the spawned info panel. Computes where to spawn the panel
   * so that it is just to the right of the object in the user's view, at eye level.
   *
   * @param rightEdgePose A [Pose] pose representing the user's head position, and the direction
   *   vector from the head to the right edge of the detected object in the user's view, at eye
   *   level.
   * @param panelWidth The width of the panel to be spawned
   * @param zDistance The desired distance away from the user's head to spawn the panel.
   * @return The [Pose] pose representing where to spawn the panel, and how to orient it.
   */
  private fun getPanelSpawnPosition(
      rightEdgePose: Pose,
      panelWidth: Float,
      zDistance: Float = 1f
  ): Pose {
    // get angle based on arc length of panel width / 2 at z distance
    val angle = (panelWidth / 2) / zDistance

    // rotate the pose forward direction by angle to get the new forward direction
    val newFwd =
        Quaternion.fromAxisAngle(Vector3.Up, angle * 180f / PI.toFloat())
            .times(rightEdgePose.forward())
            .normalize()

    // apply offset to lower the panel to eye height
    val position = rightEdgePose.t - Vector3(0f, 0.1f, 0f) + newFwd * zDistance
    val rotation = Quaternion.lookRotationAroundY(newFwd)

    return Pose(position, rotation)
  }

  /**
   * Show or dismiss a curated object – one with a 3D model and pre-assembled panel content.
   *
   * @param enabled Whether to show or dismiss the object.
   * @param pose If enabling, the [Pose] pose with which to re-orient the object.
   */
  private fun toggleCuratedObject(enabled: Boolean, pose: Pose? = null) {
    if (enabled) {
      if (pose != null) {
        curatedObjectEntity?.setComponent(Transform(pose))
      }
      curatedObjectEntity?.let { curatedObjectHandler.enableCuratedObject(it) }
    } else {
      curatedObjectEntity?.let { curatedObjectHandler.dismissCuratedObject(it) }
    }
  }

  override fun onPause() {
    stopScanning()
    super.onPause()
  }

  private fun loadGLXF(): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          "apk:///scenes/Composition.glxf".toUri(),
          rootEntity = gltfxEntity!!,
          keyName = "scanner_app_main_scene")
    }
  }

  // permissions requesting

  private fun hasPermissions() =
      PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
      }

  private fun requestPermissions(callback: (granted: Boolean) -> Unit) {
    permissionsResultCallback = callback

    ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    when (requestCode) {
      PERMISSIONS_REQUEST_CODE -> {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          Log.d(TAG, "Camera permission granted")
          permissionsResultCallback(true)
        } else {
          Log.w(TAG, "Camera permission denied")
          permissionsResultCallback(false)
        }
      }
    }
  }
}
