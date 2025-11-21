/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.immersive

import android.os.Bundle
import android.os.Message
import android.util.Log
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.samples.premiummediasample.AnchorOnLoad
import com.meta.spatial.samples.premiummediasample.Anchorable
import com.meta.spatial.samples.premiummediasample.BuildConfig
import com.meta.spatial.samples.premiummediasample.HeroLighting
import com.meta.spatial.samples.premiummediasample.LIGHT_AMBIENT_COLOR
import com.meta.spatial.samples.premiummediasample.LIGHT_SUN_COLOR
import com.meta.spatial.samples.premiummediasample.LIGHT_SUN_DIRECTION
import com.meta.spatial.samples.premiummediasample.PanelLayerAlpha
import com.meta.spatial.samples.premiummediasample.ReceiveLighting
import com.meta.spatial.samples.premiummediasample.Scalable
import com.meta.spatial.samples.premiummediasample.ScaledChild
import com.meta.spatial.samples.premiummediasample.ScaledParent
import com.meta.spatial.samples.premiummediasample.data.CinemaState
import com.meta.spatial.samples.premiummediasample.data.HomeItem
import com.meta.spatial.samples.premiummediasample.entities.ControlsPanelEntity
import com.meta.spatial.samples.premiummediasample.entities.HomePanelEntity
import com.meta.spatial.samples.premiummediasample.service.IPCMessageHandler
import com.meta.spatial.samples.premiummediasample.service.IPCService
import com.meta.spatial.samples.premiummediasample.service.IPCServiceConnection
import com.meta.spatial.samples.premiummediasample.systems.anchor.AnchorSnappingSystem
import com.meta.spatial.samples.premiummediasample.systems.headChecker.HeadCheckerSystem
import com.meta.spatial.samples.premiummediasample.systems.heroLighting.HeroLightingSystem
import com.meta.spatial.samples.premiummediasample.systems.heroLighting.WallLightingSystem
import com.meta.spatial.samples.premiummediasample.systems.metrics.HeapMetrics
import com.meta.spatial.samples.premiummediasample.systems.panelLayerAlpha.PanelLayerAlphaSystem
import com.meta.spatial.samples.premiummediasample.systems.panelReady.PanelReadySystem
import com.meta.spatial.samples.premiummediasample.systems.pointerInfo.PointerInfoSystem
import com.meta.spatial.samples.premiummediasample.systems.scalable.AnalogScalableSystem
import com.meta.spatial.samples.premiummediasample.systems.scalable.TouchScalableSystem
import com.meta.spatial.samples.premiummediasample.systems.scaleChildren.ScaleChildrenSystem
import com.meta.spatial.samples.premiummediasample.systems.tweenEngine.TweenEngineSystem
import com.meta.spatial.spatialaudio.SpatialAudioFeature
import com.meta.spatial.toolkit.AvatarSystem
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.vr.LocomotionSystem
import java.io.File

class ImmersiveActivity : BaseMrukActivity(), IPCMessageHandler {
  val ipcServiceConnection: IPCServiceConnection =
      IPCServiceConnection(this, this, IPCService.IMMERSIVE_CHANNEL)
  val spatialAudioFeature = SpatialAudioFeature()

  val immersiveViewModel =
      ImmersiveViewModel(ipcServiceConnection, systemManager, spatialAudioFeature)

  override fun registerFeatures(): List<SpatialFeature> {
    val parentFeatures = super.registerFeatures()
    val additionalFeatures =
        mutableListOf(
            OVRMetricsFeature(this, HeapMetrics()),
            ComposeFeature(),
            spatialAudioFeature,
        )
    if (BuildConfig.DEBUG) {
      additionalFeatures.add(CastInputForwardFeature(this))
      additionalFeatures.add(DataModelInspectorFeature(spatial, this.componentManager))
    }
    return parentFeatures + additionalFeatures
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    ipcServiceConnection.bindService()
    NetworkedAssetLoader.init(File(applicationContext.cacheDir.canonicalPath), OkHttpAssetFetcher())

    // Unregister locomotion system to prevent controller movement
    systemManager.unregisterSystem<LocomotionSystem>()

    // Turn off hands and controllers
    val avatarSystem = systemManager.findSystem<AvatarSystem>()
    avatarSystem.setShowControllers(false)
    avatarSystem.setShowHands(false)

    // Register Systems
    componentManager.registerComponent<Scalable>(Scalable.Companion)

    systemManager.registerSystem(TweenEngineSystem())

    val pointerInfoSystem = PointerInfoSystem()
    systemManager.registerSystem(pointerInfoSystem)
    systemManager.registerSystem(AnalogScalableSystem(pointerInfoSystem))

    componentManager.registerComponent<ScaledParent>(ScaledParent.Companion)
    componentManager.registerComponent<ScaledChild>(ScaledChild.Companion)
    systemManager.registerLateSystem(ScaleChildrenSystem())

    systemManager.registerSystem(HeadCheckerSystem(::onHeadFound))

    componentManager.registerComponent<HeroLighting>(HeroLighting.Companion)
    componentManager.registerComponent<ReceiveLighting>(ReceiveLighting.Companion)
    systemManager.registerSystem(HeroLightingSystem(true, false))

    componentManager.registerComponent<Anchorable>(Anchorable.Companion)
    componentManager.registerComponent<AnchorOnLoad>(AnchorOnLoad.Companion)
    systemManager.registerSystem(AnchorSnappingSystem())

    systemManager.registerSystem(TouchScalableSystem())

    systemManager.registerSystem(WallLightingSystem())

    componentManager.registerComponent<PanelLayerAlpha>(PanelLayerAlpha.Companion)
    systemManager.registerSystem(PanelLayerAlphaSystem())

    systemManager.registerSystem(PanelReadySystem())
  }

  override fun onSceneReady() {
    super.onSceneReady()

    scene.enablePassthrough(true)
    scene.setLightingEnvironment(
        ambientColor = LIGHT_AMBIENT_COLOR,
        sunColor = LIGHT_SUN_COLOR,
        sunDirection = LIGHT_SUN_DIRECTION,
    )

    immersiveViewModel.initializeEntities(scene)
  }

  private fun onHeadFound() {
    immersiveViewModel.onHeadFound()
  }

  override fun handleIPCMessage(msg: Message) {
    when (msg.what) {
      ImmersiveActivityCodes.HOME_PANEL_CONNECTED.ordinal -> {
        // Both the SceneObject needs to be available, and the compose needs to have fully drawn
        // before we can show the home panel. The HomePanelActivity will message when compose is
        // finished drawing.
        immersiveViewModel.onHomePanelDrawn()
      }
      ImmersiveActivityCodes.HOME_PANEL_SELECT_ITEM.ordinal -> {
        val homeItem: HomeItem? = msg.data.getSerializable("homeItem") as HomeItem?
        homeItem?.let { immersiveViewModel.playHomeItem(it) }
      }
      ImmersiveActivityCodes.CONTROL_PANEL_CLOSE_PLAYER.ordinal -> {
        immersiveViewModel.transitionToHome()
      }
      ImmersiveActivityCodes.CONTROL_PANEL_RESTART_VIDEO.ordinal -> {
        immersiveViewModel.restartVideo()
      }
      ImmersiveActivityCodes.CONTROL_PANEL_TOGGLE_PLAY.ordinal -> {
        immersiveViewModel.togglePlay(msg.data.getBoolean("isPlaying", true))
      }
      ImmersiveActivityCodes.CONTROL_PANEL_TOGGLE_MUTE.ordinal -> {
        immersiveViewModel.toggleAudioMute(msg.data.getBoolean("isMute", true))
      }
      ImmersiveActivityCodes.CONTROL_PANEL_SET_PASSTHROUGH.ordinal -> {
        immersiveViewModel.setPassthrough(msg.data.getFloat("passthrough", 0f))
      }
      ImmersiveActivityCodes.CONTROL_PANEL_SET_LIGHTING.ordinal -> {
        immersiveViewModel.setLighting(msg.data.getFloat("value", 0f))
      }
      ImmersiveActivityCodes.CONTROL_PANEL_SEEK_TO.ordinal -> {
        immersiveViewModel.seekTo(msg.data.getFloat("position", 0f))
      }
      ImmersiveActivityCodes.CONTROL_PANEL_SET_CINEMA_STATE.ordinal -> {
        val cinemaState: CinemaState? = msg.data.getSerializable("cinemaState") as CinemaState?
        if (cinemaState != null) {
          immersiveViewModel.cinemaStateHandler.setCinemaState(cinemaState)
        }
      }
      else -> Log.i("IPCService", "Unknown message code received at ImmersiveActivity")
    }
  }

  override fun onRecenter(isUserInitiated: Boolean) {
    super.onRecenter(isUserInitiated)
    immersiveViewModel.center()
  }

  override fun onHMDUnmounted() {
    super.onHMDUnmounted()
    immersiveViewModel.pauseApp()
  }

  override fun onHMDMounted() {
    super.onHMDMounted()
    immersiveViewModel.resumeApp()
  }

  override fun onVRPause() {
    super.onVRPause()
    immersiveViewModel.pauseApp()
  }

  override fun onVRReady() {
    super.onVRReady()
    immersiveViewModel.resumeApp()
  }

  override fun onSpatialShutdown() {
    immersiveViewModel.destroy()
    ipcServiceConnection.unbindService()
    super.onSpatialShutdown()
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        HomePanelEntity.panelRegistration(scene, spatialContext),
        ControlsPanelEntity.panelRegistration(scene, spatialContext),
    )
  }

  companion object {
    val TAG = "ImmersiveActivity"

    enum class ImmersiveActivityCodes {
      HOME_PANEL_CONNECTED,
      HOME_PANEL_SELECT_ITEM,
      CONTROL_PANEL_RESTART_VIDEO,
      CONTROL_PANEL_CLOSE_PLAYER,
      CONTROL_PANEL_TOGGLE_PLAY,
      CONTROL_PANEL_TOGGLE_MUTE,
      CONTROL_PANEL_SET_PASSTHROUGH,
      CONTROL_PANEL_SET_LIGHTING,
      CONTROL_PANEL_SEEK_TO,
      CONTROL_PANEL_SET_CINEMA_STATE,
    }
  }
}
