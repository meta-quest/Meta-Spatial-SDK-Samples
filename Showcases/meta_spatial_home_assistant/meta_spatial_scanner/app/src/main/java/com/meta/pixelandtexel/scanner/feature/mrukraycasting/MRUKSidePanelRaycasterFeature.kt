package com.meta.pixelandtexel.scanner.feature.mrukraycasting

import android.os.Bundle
import com.meta.pixelandtexel.scanner.DiApplication
import com.meta.pixelandtexel.scanner.FollowHead
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.DynamicSmartThingScreen
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.DynamicSmartThingViewmodel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.MrukRaycastModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.repository.IMRUKObjectsRepository
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.system.FollowHeadSystem
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.ComponentRegistration
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SendRate
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SystemBase
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.PanelShapeLayerBlendType
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.PanelRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

/**
 * A Spatial SDK Feature which uses the device camera feed and a CV object detection model to
 * discover objects in the user's surroundings, assign them labels and persistent ids, and track
 * their position over time.
 *
 **/

class MRUKSidePanelRaycasterFeature(
    private val activity: AppSystemActivity,
) : SpatialFeature {
    companion object {
        private const val TAG = "MRUKSidePanelRaycasterFeature"
    }

    private val subscriptionScope = CoroutineScope(Dispatchers.Main)

    private var di: DiApplication = activity.application as DiApplication
    private val mrukObjectRepository: IMRUKObjectsRepository
    private val smartHomeRepository: SmartHomeRepository

    init {
        mrukObjectRepository = di.get()
        smartHomeRepository = di.get<SmartHomeRepository>()
        subscriptionScope.launch {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        activity.registerPanel(
            PanelRegistration(R.integer.object_panel_id) {
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
                    try {
                        val device =
                            mrukObjectRepository.lastAddedObjectDevice ?: return@composePanel
                        mrukObjectRepository.lastAddedObjectDevice = null
                        setContent {
                            val viewmodel = DynamicSmartThingViewmodel(
                                di.get(),
                                di.get(),
                                onCloseSmartThing = { removeSmartThing(device.name) },
                                onDisconnectDevice = { disconnectSmartThing(device.name) })
                            DynamicSmartThingScreen(
                                device = device,
                                viewModel = viewmodel,
                            )
                        }
                    } finally {
                        subscriptionScope.launch {
                            mrukObjectRepository.unlock()
                        }
                    }

                }
            },
        )
    }

    suspend fun addSmartThing(device: Device, spawnPose: Pose) {
        mrukObjectRepository.addMRUKObject(
            MrukRaycastModel(
                device = device,
                pose = spawnPose
            )
        )
    }

    fun removeSmartThing(deviceName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mrukObjectRepository.deleteMRUKObject(deviceName)
        }
    }

    fun disconnectSmartThing(deviceName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mrukObjectRepository.deleteFromDatabase(deviceName)
        }
    }

    suspend fun getAllSmartThings() {
        val mrukObjects = mrukObjectRepository.getAllMRUKObjects()
        val devices = smartHomeRepository.getDevices()
        mrukObjects.forEach { mrukObject ->
            val alreadyExists =
                mrukObjectRepository.mrukEntities.containsKey(mrukObject.device.name)
            if (alreadyExists) return@forEach

            val device = devices.find { it.name == mrukObject.device.name }
            if (device != null) {
                mrukObjectRepository.addMRUKObject(
                    MrukRaycastModel(
                        device = device,
                        pose = mrukObject.pose
                    )
                )
            }
        }
    }

    suspend fun deleteAllSmartThingEntities() {
        mrukObjectRepository.deleteAllMRUKObjects()
    }

    override fun systemsToRegister(): List<SystemBase> {
        val systems = mutableListOf<SystemBase>()
        systems.add(FollowHeadSystem())
        return systems
    }

    override fun componentsToRegister(): List<ComponentRegistration> {
        return listOf(
            ComponentRegistration.createConfig<FollowHead>(
                FollowHead.Companion,
                SendRate.DEFAULT,
            ),
        )
    }

    override fun onSceneReady() {
    }


    override fun onPauseActivity() {
        super.onPauseActivity()
    }

    override fun onDestroy() {

        subscriptionScope.cancel()
        super.onDestroy()
    }
}
