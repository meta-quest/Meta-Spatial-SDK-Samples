/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.panels.controlsPanel

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meta.spatial.samples.premiummediasample.data.CinemaState
import com.meta.spatial.samples.premiummediasample.service.IPCMessageHandler
import com.meta.spatial.samples.premiummediasample.service.IPCService
import com.meta.spatial.samples.premiummediasample.service.IPCServiceConnection

class ControlsPanelActivity : ComponentActivity(), IPCMessageHandler {
  companion object {
    enum class ControlsPanelCodes {
      IMMERSIVE_UPDATE_PLAYER_STATE,
      IMMERSIVE_UPDATE_CONTROL_PANEL,
    }
  }

  private val ipcServiceConnection: IPCServiceConnection =
      IPCServiceConnection(this, this, IPCService.CONTROL_PANEL_CHANNEL)

  private val controlsPanelViewModel = ControlsPanelViewModel(ipcServiceConnection)

  override fun onCreate(savedInstanceBundle: Bundle?) {
    super.onCreate(savedInstanceBundle)
    ipcServiceConnection.bindService()
    setContent { ControlsPanel(controlsPanelViewModel) }
  }

  override fun handleIPCMessage(msg: Message) {
    when (msg.what) {
      ControlsPanelCodes.IMMERSIVE_UPDATE_PLAYER_STATE.ordinal -> {
        val isPlaying: Boolean = msg.data.getBoolean("isPlaying", true)
        val isBuffering: Boolean = msg.data.getBoolean("isBuffering", true)
        val isMuted: Boolean = msg.data.getBoolean("isMuted", true)
        val progress: Float = msg.data.getFloat("progress", 0.0f)
        val duration: Float = msg.data.getFloat("duration", 0.0f)
        controlsPanelViewModel.updateState(isPlaying, isBuffering, isMuted, progress, duration)
      }
      ControlsPanelCodes.IMMERSIVE_UPDATE_CONTROL_PANEL.ordinal -> {
        val cinemaState: CinemaState = msg.data.getSerializable("cinemaState") as CinemaState
        controlsPanelViewModel.updateControlButtons(getControlsButtons(cinemaState))
        // Re-enable passthrough slider
        controlsPanelViewModel.passthroughSetInteractable(cinemaState != CinemaState.Cinema)
        if (msg.data.containsKey("lightingEnabled")) {
          val isLightingEnabled: Boolean = msg.data.getBoolean("lightingEnabled", true)
          controlsPanelViewModel.lightingSetInteractable(isLightingEnabled)
        }
      }
      else -> Log.i("IPCService", "Unknown message code received from ControlPanel Activity")
    }
  }

  override fun onDestroy() {
    ipcServiceConnection.unbindService()
    super.onDestroy()
  }

  fun getControlsButtons(cinemaState: CinemaState): List<ControlsPanelButton> {
    val cinemaAndTV =
        listOf(
            ControlsPanelButton("Cinema") {
              controlsPanelViewModel.updateCinemaState(CinemaState.Cinema)
              controlsPanelViewModel.passthroughSetInteractable(false)
            },
            ControlsPanelButton("TV") {
              controlsPanelViewModel.updateCinemaState(CinemaState.TV)
              controlsPanelViewModel.passthroughSetInteractable(true)
            })
    val noButtons = listOf<ControlsPanelButton>()
    return when (cinemaState) {
      CinemaState.Cinema -> cinemaAndTV
      CinemaState.TV -> cinemaAndTV
      CinemaState.Equirect180 -> noButtons
      CinemaState.Home -> noButtons
    }
  }
}
