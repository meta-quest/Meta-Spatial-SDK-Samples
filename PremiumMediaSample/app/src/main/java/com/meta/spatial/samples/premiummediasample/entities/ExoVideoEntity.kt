/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

@file:OptIn(UnstableApi::class)

package com.meta.spatial.samples.premiummediasample.entities

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Vector2
import com.meta.spatial.runtime.ButtonBits
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
import com.meta.spatial.spatialaudio.AudioSessionId
import com.meta.spatial.spatialaudio.AudioType
import com.meta.spatial.spatialaudio.SpatialAudioFeature
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Equirect180ShapeOptions
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.MediaPanelRenderOptions
import com.meta.spatial.toolkit.MediaPanelSettings
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.PanelInputOptions
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.PixelDisplayOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.ReadableMediaPanelRenderOptions
import com.meta.spatial.toolkit.ReadableMediaPanelSettings
import com.meta.spatial.toolkit.ReadableVideoSurfacePanelRegistration
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.VideoSurfacePanelRegistration
import com.meta.spatial.toolkit.Visible
import dorkbox.tweenEngine.TweenEngine
import dorkbox.tweenEngine.TweenEquations

/**
 * Class responsible for creating a streaming panel. Options include Mono or Stereo, DRM or
 * WallLighting, Rectangular vs 180
 */
class ExoVideoEntity(
    private val exoPlayer: ExoPlayer,
    mediaSource: MediaSource,
    panelRenderingStyle: PanelRenderingStyle,
    tweenEngine: TweenEngine,
    ipcServiceConnection: IPCServiceConnection,
    private val spatialAudioFeature: SpatialAudioFeature,
) : FadingPanel(tweenEngine) {
  companion object {
    val TAG = "ExoPlayerEntity"

    const val BASE_PANEL_SIZE = 0.7f // 0.7 meters
    const val TIME_REMAINING_BEFORE_RESTART = 1_000L // 1 second

    fun create(
        exoPlayer: ExoPlayer,
        mediaSource: MediaSource,
        tweenEngine: TweenEngine,
        ipcServiceConnection: IPCServiceConnection,
        spatialAudioFeature: SpatialAudioFeature,
    ): ExoVideoEntity {
      val panelSize = Vector2(mediaSource.aspectRatio * BASE_PANEL_SIZE, BASE_PANEL_SIZE)
      val drmEnabled =
          mediaSource.videoSource is VideoSource.Url &&
              mediaSource.videoSource.drmLicenseUrl != null

      // DRM can be enabled two ways:
      // 1. Activity panel (ActivityPanelRegistration, IntentPanelRegistration) + MediaPanelSettings
      // 2. VideoSurfacePanelRegistration panel
      val panelRenderingStyle =
          if (drmEnabled || mediaSource.videoShape != MediaSource.VideoShape.Rectilinear)
              PanelRenderingStyle.DIRECT_TO_SURFACE
          else PanelRenderingStyle.READABLE

      val exoVideo =
          ExoVideoEntity(
              exoPlayer = exoPlayer,
              mediaSource = mediaSource,
              panelRenderingStyle = panelRenderingStyle,
              tweenEngine = tweenEngine,
              ipcServiceConnection = ipcServiceConnection,
              spatialAudioFeature = spatialAudioFeature,
          )

      // WallLighting is only supported for Rectangular panels.
      if (mediaSource.videoShape == MediaSource.VideoShape.Rectilinear) {
        // Shader effects will not work on non-readable panels.
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

  val controlPanelPollHandler: ControlPanelPollHandler =
      ControlPanelPollHandler(exoPlayer, ipcServiceConnection)

  init {
    if (panelRenderingStyle == PanelRenderingStyle.READABLE) {
      createReadableSurfacePanel(mediaSource)
    } else if (panelRenderingStyle == PanelRenderingStyle.DIRECT_TO_SURFACE) {
      createDirectToSurfacePanel(mediaSource)
    }
  }

  /**
   * The Readable surface panel supports fetching the panel image for use in custom shaders. Less
   * performant than regular media panel
   */
  private fun createReadableSurfacePanel(
      mediaSource: MediaSource,
  ) {
    val panelSize = Vector2(mediaSource.aspectRatio * BASE_PANEL_SIZE, BASE_PANEL_SIZE)
    SpatialActivityManager.executeOnVrActivity<AppSystemActivity> { immersiveActivity ->
      immersiveActivity.registerPanel(
          ReadableVideoSurfacePanelRegistration(
              id,
              surfaceConsumer = { panelEnt, surface ->
                SurfaceUtil.paintBlack(surface)
                exoPlayer.setMediaSource(mediaSource, immersiveActivity)
                exoPlayer.prepare()
                exoPlayer.setHighQuality()
                exoPlayer.setVideoSurface(surface)
                addLinkSpatialAudioListener(exoPlayer, panelEnt)
              },
              settingsCreator = {
                ReadableMediaPanelSettings(
                    shape = QuadShapeOptions(width = panelSize.x, height = panelSize.y),
                    style = PanelStyleOptions(R.style.PanelAppThemeTransparent),
                    display =
                        PixelDisplayOptions(
                            width = mediaSource.videoDimensionsPx.x,
                            height = mediaSource.videoDimensionsPx.y,
                        ),
                    rendering =
                        ReadableMediaPanelRenderOptions(
                            mips = mediaSource.mips,
                            stereoMode = mediaSource.stereoMode,
                        ),
                    input =
                        PanelInputOptions(
                            ButtonBits.ButtonA or
                                ButtonBits.ButtonTriggerL or
                                ButtonBits.ButtonTriggerR
                        ),
                )
              },
          )
      )
    }
    entity = Entity.create(Panel(id), Transform(), Visible(false), PanelLayerAlpha(0f))
  }

  /**
   * The VideoSurfacePanelRegistration renders the exoplayer directly to the Panel Surface. This
   * approach enables DRM and is the most performant option for rendering high resolution video.
   */
  private fun createDirectToSurfacePanel(
      mediaSource: MediaSource,
  ) {
    val panelSize = Vector2(mediaSource.aspectRatio * BASE_PANEL_SIZE, BASE_PANEL_SIZE)

    SpatialActivityManager.executeOnVrActivity<AppSystemActivity> { immersiveActivity ->
      immersiveActivity.registerPanel(
          VideoSurfacePanelRegistration(
              id,
              surfaceConsumer = { panelEnt, surface ->
                SurfaceUtil.paintBlack(surface)
                exoPlayer.setMediaSource(mediaSource, immersiveActivity)
                exoPlayer.prepare()
                exoPlayer.setHighQuality()
                exoPlayer.setVideoSurface(surface)
                addLinkSpatialAudioListener(exoPlayer, panelEnt)
              },
              settingsCreator = {
                MediaPanelSettings(
                    shape =
                        when (mediaSource.videoShape) {
                          MediaSource.VideoShape.Rectilinear ->
                              QuadShapeOptions(panelSize.x, panelSize.y)
                          MediaSource.VideoShape.Equirect180 ->
                              Equirect180ShapeOptions(radius = 50f)
                        },
                    display =
                        PixelDisplayOptions(
                            width = mediaSource.videoDimensionsPx.x,
                            height = mediaSource.videoDimensionsPx.y,
                        ),
                    rendering =
                        MediaPanelRenderOptions(
                            isDRM =
                                mediaSource.videoSource is VideoSource.Url &&
                                    mediaSource.videoSource.drmLicenseUrl != null,
                            stereoMode = mediaSource.stereoMode,
                            zIndex =
                                if (mediaSource.videoShape == MediaSource.VideoShape.Equirect180) -1
                                else 0,
                        ),
                    style = PanelStyleOptions(R.style.PanelAppThemeTransparent),
                )
              },
          )
      )
    }

    entity =
        Entity.create(
            Transform(),
            Panel(id),
            Visible(false),
            PanelLayerAlpha(0f),
        )
  }

  private fun addLinkSpatialAudioListener(player: ExoPlayer, panelEnt: Entity) {
    player.addListener(
        object : Player.Listener {
          override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
              // Use a single registered audio session id (just choosing 1 as a default)
              // as we only have one audio source playing at a time.
              val registeredAudioSessionId = 1
              spatialAudioFeature.registerAudioSessionId(
                  registeredAudioSessionId,
                  player.audioSessionId,
              )

              // Determine the appropriate AudioType based on channel count
              val audioFormat = player.audioFormat
              val audioType =
                  if (audioFormat != null) {
                    when (audioFormat.channelCount) {
                      1 -> AudioType.MONO
                      2 -> AudioType.STEREO
                      else ->
                          AudioType
                              .SOUNDFIELD // Default to soundfield for multichannel (>3 channels)
                    }
                  } else {
                    AudioType.STEREO // Default to stereo if format is unknown
                  }

              panelEnt.setComponent(AudioSessionId(registeredAudioSessionId, audioType))
              Log.i(
                  EXO_VIDEO_ENTITY_TAG,
                  "Set AudioSessionId component for entity ${panelEnt.id} with type: $audioType",
              )
            }
          }
        }
    )
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
        true,
        TIMINGS.EXOPLAYER_FADE_BOTH.millisToFloat(),
        TweenEquations.Circle_In,
    ) {
      exoPlayer.playWhenReady = true
      onShowComplete?.invoke()
    }
  }

  fun hidePlayer(onHideComplete: (() -> Unit)? = null) {
    exoPlayer.pause()
    controlPanelPollHandler.stop()
    super.fadeVisibility(
        false,
        TIMINGS.EXOPLAYER_FADE_BOTH.millisToFloat(),
        TweenEquations.Circle_Out,
    ) {
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
  READABLE,
  DIRECT_TO_SURFACE,
}

const val EXO_VIDEO_ENTITY_TAG = "ExoVideoEntity"
