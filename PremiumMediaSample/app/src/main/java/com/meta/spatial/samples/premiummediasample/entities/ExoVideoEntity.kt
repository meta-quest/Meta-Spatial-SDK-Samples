/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package com.meta.spatial.samples.premiummediasample.entities

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Vector2
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.PanelConfigOptions
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.runtime.PanelShapeType
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.samples.premiummediasample.AnchorOnLoad
import com.meta.spatial.samples.premiummediasample.Anchorable
import com.meta.spatial.samples.premiummediasample.HeroLighting
import com.meta.spatial.samples.premiummediasample.MAX_SPAWN_DISTANCE
import com.meta.spatial.samples.premiummediasample.PanelLayerAlpha
import com.meta.spatial.samples.premiummediasample.R
import com.meta.spatial.samples.premiummediasample.Scalable
import com.meta.spatial.samples.premiummediasample.ScaledParent
import com.meta.spatial.samples.premiummediasample.SurfaceUtil
import com.meta.spatial.samples.premiummediasample.TIMINGS
import com.meta.spatial.samples.premiummediasample.data.MediaSource
import com.meta.spatial.samples.premiummediasample.data.VideoSource
import com.meta.spatial.samples.premiummediasample.getDisposableID
import com.meta.spatial.samples.premiummediasample.immersive.ControlPanelPollHandler
import com.meta.spatial.samples.premiummediasample.millisToFloat
import com.meta.spatial.samples.premiummediasample.service.IPCServiceConnection
import com.meta.spatial.samples.premiummediasample.unregisterPanel
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Hittable
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import dorkbox.tweenEngine.TweenEngine
import dorkbox.tweenEngine.TweenEquations
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt

/**
 * Class responsible for creating a streaming panel. Options include Mono or Stereo, DRM or
 * WallLighting, Rectangular vs 180
 */
class ExoVideoEntity(
    private val exoPlayer: ExoPlayer,
    mediaSource: MediaSource,
    panelConfigBlock: PanelConfigOptions.() -> Unit,
    panelRenderingStyle: PanelRenderingStyle,
    tweenEngine: TweenEngine,
    ipcServiceConnection: IPCServiceConnection,
) : FadingPanel(tweenEngine) {
  companion object {
    val TAG = "ExoPlayerEntity"

    const val BASE_PANEL_SIZE = 0.7f // 0.7 meters
    const val TIME_REMAINING_BEFORE_RESTART = 1_000L // 1 second

    fun create(
        exoPlayer: ExoPlayer,
        mediaSource: MediaSource,
        tweenEngine: TweenEngine,
        ipcServiceConnection: IPCServiceConnection
    ): ExoVideoEntity {
      val panelSize = Vector2(mediaSource.aspectRatio * BASE_PANEL_SIZE, BASE_PANEL_SIZE)
      val equirectRadius = 50f
      val drmEnabled =
          mediaSource.videoSource is VideoSource.Url &&
              mediaSource.videoSource.drmLicenseUrl != null

      // DRM can be enabled two ways:
      // 1. Activity Panel and Direct-To-Compositor
      // 2. Direct-To-Surface Panel
      // Enabling Direct-To-Surface for DRM panels
      // Also enabling Direct-To-Surface for Equirect panel due to high resolution
      val panelRenderingStyle =
          if (drmEnabled || mediaSource.videoShape != MediaSource.VideoShape.Rectilinear)
              PanelRenderingStyle.DIRECT_TO_SURFACE
          else PanelRenderingStyle.VIEWS

      val panelConfigBlock: PanelConfigOptions.() -> Unit = {
        stereoMode = mediaSource.stereoMode

        if (mediaSource.videoShape == MediaSource.VideoShape.Rectilinear) {
          width = panelSize.x
          height = panelSize.y
        } else if (mediaSource.videoShape == MediaSource.VideoShape.Equirect180) {
          radiusForCylinderOrSphere = equirectRadius
        }
        layoutWidthInPx = mediaSource.videoDimensionsPx.x
        layoutHeightInPx = mediaSource.videoDimensionsPx.y
        // Mips value also blurs reflections in WallLightingSystem by modifying the SceneTexture
        mips = mediaSource.mips
        // The WallLightingSystem relies on the SceneTexture
        forceSceneTexture = true
        themeResourceId = R.style.PanelAppThemeTransparent
        includeGlass = false
        layerConfig =
            LayerConfig(
                zIndex =
                    if (mediaSource.videoShape == MediaSource.VideoShape.Equirect180) -1 else 0,
                secure = drmEnabled)
        panelShapeType =
            when (mediaSource.videoShape) {
              MediaSource.VideoShape.Rectilinear -> PanelShapeType.QUAD
              MediaSource.VideoShape.Equirect180 -> PanelShapeType.EQUIRECT180
            }

        // want to disable left hand pinch so we can drag the panel around with hands
        if (mediaSource.videoShape == MediaSource.VideoShape.Rectilinear) {
          clickButtons =
              (ButtonBits.ButtonA or ButtonBits.ButtonTriggerL or ButtonBits.ButtonTriggerR)
        }
      }

      val exoVideo =
          ExoVideoEntity(
              exoPlayer = exoPlayer,
              mediaSource = mediaSource,
              panelConfigBlock = panelConfigBlock,
              panelRenderingStyle = panelRenderingStyle,
              tweenEngine = tweenEngine,
              ipcServiceConnection = ipcServiceConnection,
          )

      // WallLighting is only supported for Rectangular panels.
      if (mediaSource.videoShape == MediaSource.VideoShape.Rectilinear) {
        // Direct-to-surface rendering disables the SceneTexture, so HeroLighting will not work.
        if (panelRenderingStyle != PanelRenderingStyle.DIRECT_TO_SURFACE) {
          exoVideo.entity.setComponent(HeroLighting())
        }
        exoVideo.entity.setComponents(
            PanelDimensions(Vector2(panelSize.x, panelSize.y)),
            Anchorable(0.02f),
            AnchorOnLoad(scaleProportional = true, distanceCheck = MAX_SPAWN_DISTANCE + 0.5f),
            Grabbable(true, GrabbableType.PIVOT_Y),
            ScaledParent(),
            Scale(),
            Scalable(),
        )
      }
      return exoVideo
    }
  }

  val id: Int = getDisposableID()

  override lateinit var entity: Entity
  lateinit var playerView: PlayerView

  val controlPanelPollHandler: ControlPanelPollHandler =
      ControlPanelPollHandler(exoPlayer, ipcServiceConnection)

  init {
    if (panelRenderingStyle == PanelRenderingStyle.VIEWS) {
      createViewsPanel(panelConfigBlock, mediaSource)
    } else if (panelRenderingStyle == PanelRenderingStyle.DIRECT_TO_SURFACE) {
      createDirectToSurfacePanel(panelConfigBlock, mediaSource)
    }
  }

  /** The views-based panel can have UI and accepts input. Follows normal panel registration APIs */
  private fun createViewsPanel(
      panelConfigBlock: PanelConfigOptions.() -> Unit,
      mediaSource: MediaSource,
  ) {
    // Known bug crops panel dimensions based on device display resolution. This downscaling is
    // temporary and a fix is being worked on.
    // To support all devices, limiting maximum dimensions to that of Quest 2: 3600x1920
    val (downScaledWidthInPx, downScaledHeightInPx) =
        downScaleResolutionForQuest2(
            mediaSource.videoDimensionsPx.x, mediaSource.videoDimensionsPx.y)
    val overridePanelConfig: PanelConfigOptions.() -> Unit = {
      panelConfigBlock()
      layoutWidthInPx = downScaledWidthInPx
      layoutHeightInPx = downScaledHeightInPx
    }
    SpatialActivityManager.executeOnVrActivity<AppSystemActivity> { immersiveActivity ->
      immersiveActivity.registerPanel(
          PanelRegistration(id) {
            layoutResourceId = R.layout.spatialized_video
            config(true, overridePanelConfig)
            panel {
              playerView = rootView?.findViewById(R.id.video_view)!!

              playerView.player = exoPlayer
              // Disable PlayerView controllers
              playerView.useController = false
              // Because of the maximum view panel resolution bug, the panel dimensions may be
              // smaller than video resolution. RESIZE_MODE_FILL will scale down the video to fit.
              playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

              exoPlayer.setMediaSource(mediaSource, playerView.context)
              exoPlayer.prepare()
              exoPlayer.setHighQuality()
            }
          })
    }
    entity = Entity.create(Panel(id), Transform(), Visible(false), PanelLayerAlpha(0f))
  }

  private fun downScaleResolutionForQuest2(x: Int, y: Int): Pair<Int, Int> {
    // To support all devices, limiting maximum dimensions to that of Quest 2: 3600x1920
    val width = 3600
    val height = 1920
    // If video is smaller than dimensions, don't scale
    if (x <= width && y <= height) {
      return x to y
    }
    val scaleFactor = minOf(width.toDouble() / x, height.toDouble() / y)
    return (x * scaleFactor).roundToInt() to (y * scaleFactor).roundToInt()
  }

  /**
   * The Direct-To-Surface panel renders the exoplayer directly to the Panel Surface. This approach
   * enables DRM and is more performant than setting up a view for the panel. REQUIRES
   * DIRECT-TO-COMPOSITOR: The render thread will crash if mips != 1 && forceSceneTexture != false
   * && enableTransparent != false
   */
  private fun createDirectToSurfacePanel(
      panelConfigBlock: PanelConfigOptions.() -> Unit,
      mediaSource: MediaSource,
  ) {
    // Create entity
    entity =
        Entity.create(
            Transform(),
            Hittable(hittable = MeshCollision.LineTest),
            Visible(false),
            PanelLayerAlpha(0f),
        )

    val panelConfigOptions = PanelConfigOptions().apply(panelConfigBlock)
    // Enable Direct-To-Compositor: mips = 1, forceSceneTexture = false, enableTransparent = false
    panelConfigOptions.apply {
      mips = 1
      forceSceneTexture = false
      enableTransparent = false
    }

    SpatialActivityManager.executeOnVrActivity<AppSystemActivity> { immersiveActivity ->
      // Create PanelSceneObject with our configs
      val panelSceneObject = PanelSceneObject(immersiveActivity.scene, entity, panelConfigOptions)

      // Assign PanelSceneObject to entity.
      immersiveActivity.systemManager
          .findSystem<SceneObjectSystem>()
          .addSceneObject(
              entity, CompletableFuture<SceneObject>().apply { complete(panelSceneObject) })

      SurfaceUtil.paintBlack(panelSceneObject.getSurface())

      exoPlayer.setMediaSource(mediaSource, immersiveActivity)
      exoPlayer.prepare()
      exoPlayer.setHighQuality()
      // Set the exoplayer to render on the PanelSceneObject surface.
      exoPlayer.setVideoSurface(panelSceneObject.getSurface())
    }
  }

  fun togglePlay(isPlaying: Boolean) {
    if (isPlaying) {
      if ((exoPlayer.currentPosition + TIME_REMAINING_BEFORE_RESTART) > exoPlayer.duration) {
        exoPlayer.seekTo(0L)
      }
      exoPlayer.play()
    } else {
      exoPlayer.pause()
    }
  }

  fun showPlayer(onShowComplete: (() -> Unit)? = null) {
    controlPanelPollHandler.start()
    super.fadeVisibility(
        true, TIMINGS.EXOPLAYER_FADE_BOTH.millisToFloat(), TweenEquations.Circle_In) {
          exoPlayer.playWhenReady = true
          onShowComplete?.invoke()
        }
  }

  fun hidePlayer(onHideComplete: (() -> Unit)? = null) {
    exoPlayer.pause()
    controlPanelPollHandler.stop()
    super.fadeVisibility(
        false, TIMINGS.EXOPLAYER_FADE_BOTH.millisToFloat(), TweenEquations.Circle_Out) {
          onHideComplete?.invoke()
        }
  }

  fun destroy() {
    entity.destroy()
    exoPlayer.setVideoSurface(null)
    exoPlayer.clearMediaItems()
    controlPanelPollHandler.stop()
    currTween?.cancel()
    SpatialActivityManager.getVrActivity<AppSystemActivity>().unregisterPanel(id)
  }
}

enum class PanelRenderingStyle {
  VIEWS,
  DIRECT_TO_SURFACE
}
