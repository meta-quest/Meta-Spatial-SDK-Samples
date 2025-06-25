/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.immersive

import androidx.media3.exoplayer.ExoPlayer
import com.meta.spatial.core.Entity
import com.meta.spatial.core.SystemManager
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.Scene
import com.meta.spatial.samples.premiummediasample.BuildConfig
import com.meta.spatial.samples.premiummediasample.SPAWN_DISTANCE
import com.meta.spatial.samples.premiummediasample.data.CinemaState
import com.meta.spatial.samples.premiummediasample.data.HomeItem
import com.meta.spatial.samples.premiummediasample.data.MediaSource
import com.meta.spatial.samples.premiummediasample.entities.ControlsPanelEntity
import com.meta.spatial.samples.premiummediasample.entities.DebugControlsEntity.Companion.createDebugPanel
import com.meta.spatial.samples.premiummediasample.entities.ExoVideoEntity
import com.meta.spatial.samples.premiummediasample.entities.HomePanelEntity
import com.meta.spatial.samples.premiummediasample.entities.HomePanelEntity.Companion.homePanelFov
import com.meta.spatial.samples.premiummediasample.entities.buildCustomExoPlayer
import com.meta.spatial.samples.premiummediasample.events.ExoPlayerEvent
import com.meta.spatial.samples.premiummediasample.placeInFrontOfHead
import com.meta.spatial.samples.premiummediasample.service.IPCServiceConnection
import com.meta.spatial.samples.premiummediasample.setDistanceFov
import com.meta.spatial.samples.premiummediasample.systems.controlPanelVisibility.ControlPanelVisibilitySystem
import com.meta.spatial.samples.premiummediasample.systems.heroLighting.HeroLightingSystem
import com.meta.spatial.samples.premiummediasample.systems.panelReady.PanelReadySystem
import com.meta.spatial.samples.premiummediasample.systems.scalable.TouchScalableSystem
import com.meta.spatial.samples.premiummediasample.systems.tweenEngine.TweenEngineSystem
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.SpatialActivityManager
import dorkbox.tweenEngine.TweenEngine

class ImmersiveViewModel(
    val ipcServiceConnection: IPCServiceConnection,
    val systemManager: SystemManager
) {
  private lateinit var tweenEngine: TweenEngine
  private lateinit var controlVisibilitySystem: ControlPanelVisibilitySystem
  private lateinit var scalableSystemTouch: TouchScalableSystem

  lateinit var lightingPassthroughHandler: LightingPassthroughHandler
  lateinit var cinemaStateHandler: CinemaStateHandler

  lateinit var exoPlayer: ExoPlayer

  var debugControlsPanel: Entity? = null
  lateinit var controlsPanel: ControlsPanelEntity
  lateinit var homePanel: HomePanelEntity

  var currentExoPanel: ExoVideoEntity? = null

  private var wasPlayingBeforeVRPause = false

  fun initializeEntities(scene: Scene) {
    SpatialActivityManager.executeOnVrActivity<AppSystemActivity> { immersiveActivity ->
      exoPlayer = buildCustomExoPlayer(immersiveActivity.spatialContext, true)
    }

    tweenEngine = systemManager.findSystem<TweenEngineSystem>().tweenEngine
    scalableSystemTouch = systemManager.findSystem<TouchScalableSystem>()

    val heroLightingSystem = systemManager.findSystem<HeroLightingSystem>()
    lightingPassthroughHandler = LightingPassthroughHandler(scene, heroLightingSystem, tweenEngine)
    controlsPanel = ControlsPanelEntity(tweenEngine)
    homePanel = HomePanelEntity(tweenEngine)

    controlVisibilitySystem = ControlPanelVisibilitySystem(controlsPanel)
    systemManager.registerSystem(controlVisibilitySystem)

    if (BuildConfig.DEBUG) {
      debugControlsPanel = createDebugPanel(controlsPanel, ::transitionToHome)
    }

    cinemaStateHandler = CinemaStateHandler(this)
  }

  fun onHeadFound() {
    if (BuildConfig.DEBUG) {
      placeInFrontOfHead(
          debugControlsPanel!!,
          SPAWN_DISTANCE,
          angleYAxisFromHead = 90f,
          offset = Vector3(0f, -0.5f, 0f))
    }

    setDistanceFov(homePanel.entity, SPAWN_DISTANCE, homePanelFov)
  }

  fun onHomePanelDrawn() {
    systemManager.findSystem<PanelReadySystem>().executeWhenReady(homePanel.entity) {
      showHome(true)
    }
  }

  fun showHome(initialShow: Boolean = false) {
    homePanel.fadeVisibility(true)
    cinemaStateHandler.setCinemaState(
        CinemaState.Home, forceIsPlayingLighting = true) // Changes lighting
    if (!initialShow) {
      cinemaStateHandler.moveHomeToTVPosition()
    }
  }

  fun playHomeItem(homeItem: HomeItem) {
    // Hide home, then load, then fade in player
    homePanel.fadeVisibility(false) {
      createExoPanel(homeItem.media)
      val cinemaState =
          when (homeItem.media.videoShape) {
            MediaSource.VideoShape.Rectilinear -> CinemaState.TV
            MediaSource.VideoShape.Equirect180 -> CinemaState.Equirect180
          }
      systemManager.findSystem<PanelReadySystem>().executeWhenReady(currentExoPanel!!.entity) {
        currentExoPanel!!.showPlayer {
          controlVisibilitySystem.fadeAndStartTracking()
          if (cinemaState == CinemaState.TV) {
            scalableSystemTouch.registerEntity(currentExoPanel!!.entity)
          }
        }
        cinemaStateHandler.setCinemaState(cinemaState, homeItem, true)
      }
    }
  }

  fun transitionToHome() {
    lightingPassthroughHandler.fadeLightingMultiplier()
    currentExoPanel?.let {
      scalableSystemTouch.unregisterEntity(it.entity)
      scalableSystemTouch.forceHide(false)
    }
    controlVisibilitySystem.fadeAndStopTracking()
    currentExoPanel?.hidePlayer {
      showHome()
      destroyExoPanel()
    }
  }

  fun restartVideo() {
    exoPlayer.seekTo(0)
  }

  fun togglePlay(isPlaying: Boolean) {
    lightingPassthroughHandler.transitionLighting(cinemaStateHandler.cinemaState, isPlaying)
    currentExoPanel?.togglePlay(isPlaying)
  }

  fun toggleAudioMute(isMute: Boolean) {
    exoPlayer.volume = if (isMute) 0f else 1f
  }

  fun setPassthrough(passthrough: Float) {
    lightingPassthroughHandler.tintPassthrough(passthrough)
  }

  fun setLighting(value: Float) {
    lightingPassthroughHandler.setLighting(value)
  }

  fun seekTo(position: Float) {
    exoPlayer.seekTo((position * 1000).toLong())
  }

  private fun createExoPanel(mediaItem: MediaSource) {
    currentExoPanel = ExoVideoEntity.create(exoPlayer, mediaItem, tweenEngine, ipcServiceConnection)
    currentExoPanel?.let { exoPanel ->
      exoPanel.entity.registerEventListener<ExoPlayerEvent>(ExoPlayerEvent.ON_END) { _, _ ->
        lightingPassthroughHandler.transitionLighting(
            cinemaStateHandler.cinemaState, isPlaying = false)
      }
      if (mediaItem.videoShape == MediaSource.VideoShape.Rectilinear) {
        controlsPanel.attachToEntity(exoPanel.entity)
      }
    }
  }

  private fun destroyExoPanel() {
    controlsPanel.detachFromEntity()
    currentExoPanel?.destroy()
    currentExoPanel = null
  }

  fun center() {
    cinemaStateHandler.recenter()
    if (debugControlsPanel != null) {
      placeInFrontOfHead(debugControlsPanel!!, SPAWN_DISTANCE, angleYAxisFromHead = -90f)
    }
  }

  fun pauseApp() {
    wasPlayingBeforeVRPause = exoPlayer.isPlaying
    exoPlayer.pause()
  }

  fun resumeApp() {
    if (wasPlayingBeforeVRPause) {
      wasPlayingBeforeVRPause = false
      exoPlayer.play()
    }
  }

  fun destroy() {
    controlsPanel.destroy()
    currentExoPanel?.destroy()
    homePanel.entity.destroy()
  }
}
