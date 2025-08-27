/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package com.meta.spatial.samples.premiummediasample.entities

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.dash.DashChunkSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.source.MediaLoadData
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import com.meta.spatial.samples.premiummediasample.data.VideoSource
import java.lang.Exception

fun buildCustomExoPlayer(context: Context, isRemote: Boolean): ExoPlayer {
  val renderersFactory =
      DefaultRenderersFactory(context)
          .forceEnableMediaCodecAsynchronousQueueing()
          .setEnableDecoderFallback(true)

  val exoBuilder = ExoPlayer.Builder(context, renderersFactory)
  if (isRemote) {
    val loadControl =
        DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                10_000, // Minimum buffer before playback can start or resume
                30_000, // Maximum buffer size to hold
                1_000, // Buffer required to start playback after user action
                2_000, // Buffer required to resume after a rebuffer
            )
            .build()
    exoBuilder.setLoadControl(loadControl)
  }

  val exoPlayer = exoBuilder.build()
  exoPlayer.addAnalyticLogs()
  return exoPlayer
}

fun ExoPlayer.setMediaSource(
    mediaItem: com.meta.spatial.samples.premiummediasample.data.MediaSource,
    context: Context,
) {
  when (mediaItem.videoSource) {
    is VideoSource.Raw -> {
      val mediaUrl =
          "android.resource://" + context.packageName + "/" + mediaItem.videoSource.videoRaw
      setMediaSource(Uri.parse(mediaUrl), context, null, mediaItem.position)
    }
    is VideoSource.Url -> {
      setMediaSource(
          Uri.parse(mediaItem.videoSource.videoUrl),
          context,
          mediaItem.videoSource.drmLicenseUrl,
          mediaItem.position,
      )
    }
  }
}

fun ExoPlayer.setMediaSource(
    uri: Uri,
    context: Context,
    licenseServer: String? = null,
    position: Long = 0L,
) {
  if (uri.toString().endsWith(".mpd")) {
    // Setup Dash Sources
    val userAgent = "ExoPlayer-Drm"
    val defaultHttpDataSourceFactory =
        DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setTransferListener(
                DefaultBandwidthMeter.Builder(context).setResetOnNetworkTypeChange(false).build()
            )
    val dashChunkSourceFactory: DashChunkSource.Factory =
        DefaultDashChunkSource.Factory(defaultHttpDataSourceFactory)
    val manifestDataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent(userAgent)

    // Media item
    val mediaItemBuilder = MediaItem.Builder().setUri(uri)

    Log.d(EXO_PLAYER_TAG, "Loading uri ${uri.toString()}")
    if (licenseServer != null) {
      Log.d(EXO_PLAYER_TAG, "Setting global drm license server ${licenseServer}")
      mediaItemBuilder.setDrmConfiguration(
          MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
              .setLicenseUri(Uri.parse(licenseServer))
              .build()
      )
    }

    // Set dash factory with settings for DRM
    val mediaSource =
        DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
            .createMediaSource(mediaItemBuilder.build())
    this.setMediaSource(mediaSource)
  } else {
    // Load basic media item
    val mediaItem = MediaItem.fromUri(uri)
    this.setMediaItem(mediaItem)
  }
  if (position > 0L) seekTo(position)
}

private var globalAnalyticsId = 0

fun ExoPlayer.addAnalyticLogs(logId: Int? = null) {
  this.addAnalyticsListener(
      object : AnalyticsListener {
        val id: Int

        init {
          if (logId != null) {
            id = logId
          } else {
            id = globalAnalyticsId
            globalAnalyticsId++
          }
        }

        override fun onVideoSizeChanged(
            eventTime: AnalyticsListener.EventTime,
            videoSize: VideoSize,
        ) {
          Log.d(
              EXO_PLAYER_TAG,
              "[Video${id}] Video size changed: ${videoSize.width}x${videoSize.height}",
          )
        }

        override fun onDroppedVideoFrames(
            eventTime: AnalyticsListener.EventTime,
            droppedFrames: Int,
            elapsedMs: Long,
        ) {
          Log.d(EXO_PLAYER_TAG, "[Video${id}] On Dropped Video Frames: $droppedFrames")
        }

        override fun onIsPlayingChanged(
            eventTime: AnalyticsListener.EventTime,
            isPlaying: Boolean,
        ) {
          Log.d(EXO_PLAYER_TAG, "[Video${id}] IsPlayingChanged: $isPlaying")
        }

        override fun onDownstreamFormatChanged(
            eventTime: AnalyticsListener.EventTime,
            mediaLoadData: MediaLoadData,
        ) {
          if (mediaLoadData.trackType == C.TRACK_TYPE_VIDEO) {
            val bitrate = mediaLoadData.trackFormat?.bitrate ?: 0
            Log.d(
                EXO_PLAYER_TAG,
                "[Video${id}] Quality changed: New video bitrate = ${bitrate / 1000} kbps",
            )
          }
        }

        override fun onTracksChanged(eventTime: AnalyticsListener.EventTime, tracks: Tracks) {
          Log.d(EXO_PLAYER_TAG, "[Video${id}] onTracksChanged: $tracks")
          val trackGroups = tracks.groups
          for (i in 0 until trackGroups.count()) {
            val trackGroup = trackGroups[i]
            for (j in 0 until trackGroup.length) {
              val format = trackGroup.getTrackFormat(j)
              Log.d(
                  EXO_PLAYER_TAG,
                  "[Video${id}] Track ${j + 1} in group ${i + 1}: Bitrate = ${format.bitrate}, Resolution = ${format.width}x${format.height}, Language = ${format.language}",
              )
            }
          }
        }

        override fun onTrackSelectionParametersChanged(
            eventTime: AnalyticsListener.EventTime,
            trackSelectionParameters: TrackSelectionParameters,
        ) {
          Log.d(
              EXO_PLAYER_TAG,
              "[Video${id}] track selection parameters changed $trackSelectionParameters",
          )
        }

        override fun onAudioDecoderInitialized(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long,
        ) {
          super.onAudioDecoderInitialized(
              eventTime,
              decoderName,
              initializedTimestampMs,
              initializationDurationMs,
          )
          Log.d(EXO_PLAYER_TAG, "Using audio codec: $decoderName")
        }

        override fun onVideoCodecError(
            eventTime: AnalyticsListener.EventTime,
            videoCodecError: Exception,
        ) {
          Log.d(EXO_PLAYER_TAG, "onVideoCodecError: $videoCodecError")
          super.onVideoCodecError(eventTime, videoCodecError)
        }

        override fun onPlayerError(
            eventTime: AnalyticsListener.EventTime,
            error: PlaybackException,
        ) {
          Log.d(EXO_PLAYER_TAG, "onPlayerError: $error")
          super.onPlayerError(eventTime, error)
        }

        override fun onVideoDecoderInitialized(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long,
        ) {
          super.onVideoDecoderInitialized(
              eventTime,
              decoderName,
              initializedTimestampMs,
              initializationDurationMs,
          )
          Log.d(EXO_PLAYER_TAG, "Using video codec: $decoderName")
        }

        override fun onDrmSessionManagerError(
            eventTime: AnalyticsListener.EventTime,
            error: Exception,
        ) {
          super.onDrmSessionManagerError(eventTime, error)
          Log.d(EXO_PLAYER_TAG, "onDrmSessionManagerError: $error")
        }
      }
  )
}

fun ExoPlayer.setHighQuality() {
  val groupIndex = 0
  val trackIndex =
      getLastTrackIndex(groupIndex) // In the Sintel video, higher track index is higher resolution
  Log.d(
      EXO_PLAYER_TAG,
      "Setting highQuality, setting group ${groupIndex + 1} to track ${trackIndex + 1}",
  )

  setQualityForTrackGroup(groupIndex, trackIndex)
}

fun ExoPlayer.getLastTrackIndex(groupIndex: Int): Int {
  val trackGroups = currentTracks.groups
  // Ensure the requested group and track indices are within bounds
  if (groupIndex < trackGroups.size) {
    val trackGroupInfo = trackGroups[groupIndex]
    val trackGroup = trackGroupInfo.mediaTrackGroup
    return trackGroup.length - 1
  }
  return 0
}

fun ExoPlayer.setQualityForTrackGroup(groupIndex: Int, trackIndex: Int) {
  val trackSelector = this.trackSelector as DefaultTrackSelector
  val trackGroups = currentTracks.groups

  val parameters = trackSelector.buildUponParameters().clearOverrides()

  // Ensure the requested group and track indices are within bounds
  if (groupIndex < trackGroups.size) {
    val trackGroupInfo = trackGroups[groupIndex]
    val trackGroup = trackGroupInfo.mediaTrackGroup

    if (trackIndex < trackGroup.length) {
      val trackOverride = TrackSelectionOverride(trackGroup, trackIndex)
      parameters.addOverride(trackOverride)
      parameters.build()
      trackSelector.setParameters(parameters)

      Log.d("TrackSelection", "Applied quality settings for group $groupIndex, track $trackIndex")
    } else {
      Log.w("TrackSelection", "Track index $trackIndex is out of bounds for group $groupIndex.")
    }
  } else {
    Log.w("TrackSelection", "Group index $groupIndex is out of bounds.")
  }
}

const val EXO_PLAYER_TAG = "ExoPlayer"
