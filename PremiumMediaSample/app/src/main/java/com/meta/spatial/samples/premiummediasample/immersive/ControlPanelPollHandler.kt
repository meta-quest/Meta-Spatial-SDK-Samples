/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.immersive

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.meta.spatial.samples.premiummediasample.panels.controlsPanel.ControlsPanelActivity.Companion.ControlsPanelCodes
import com.meta.spatial.samples.premiummediasample.service.IPCService
import com.meta.spatial.samples.premiummediasample.service.IPCServiceConnection

class ControlPanelPollHandler(
    private val exoPlayer: ExoPlayer,
    private val ipcServiceConnection: IPCServiceConnection,
) {
  // Send out IPC message every 11ms to update state of ControlPanel
  private val exoPlayerLooper = Handler(Looper.getMainLooper())
  private val exoPlayerStateUpdatesRunnable: Runnable =
      object : Runnable {
        override fun run() {
          updateState(exoPlayer)
          exoPlayerLooper.postDelayed(this, 11)
        }
      }

  fun start() {
    exoPlayerLooper.post(exoPlayerStateUpdatesRunnable)
  }

  fun stop() {
    exoPlayerLooper.removeCallbacks(exoPlayerStateUpdatesRunnable)
  }

  private fun updateState(player: ExoPlayer) {
    ipcServiceConnection.messageProcess(
        IPCService.CONTROL_PANEL_CHANNEL,
        ControlsPanelCodes.IMMERSIVE_UPDATE_PLAYER_STATE.ordinal,
        Bundle().apply {
          putBoolean("isPlaying", player.isPlaying || player.playWhenReady)
          putBoolean("isBuffering", player.playbackState == Player.STATE_BUFFERING)
          putBoolean("isMuted", player.volume == 0f)
          putFloat("progress", player.currentPosition.toFloat() * 0.001f)
          putFloat("duration", player.duration.toFloat() * 0.001f)
        },
    )
  }
}
