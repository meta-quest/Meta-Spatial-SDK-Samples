/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.panels.controlsPanel

import android.os.Bundle
import com.meta.spatial.samples.premiummediasample.data.CinemaState
import com.meta.spatial.samples.premiummediasample.immersive.ImmersiveActivity.Companion.ImmersiveActivityCodes
import com.meta.spatial.samples.premiummediasample.service.IPCService
import com.meta.spatial.samples.premiummediasample.service.IPCServiceConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ControlsPanelViewModel(private val ipcServiceConnection: IPCServiceConnection? = null) {
  // States
  private val _mediaState = MutableStateFlow(MediaState())
  val mediaState: StateFlow<MediaState>
    get() = _mediaState

  private val _controlButtons = MutableStateFlow<List<ControlsPanelButton>>(emptyList())
  val controlButtons: StateFlow<List<ControlsPanelButton>>
    get() = _controlButtons

  private val _passthroughState = MutableStateFlow<SliderState>(SliderState(1f, true, true))
  val passthroughState: StateFlow<SliderState>
    get() = _passthroughState

  private val _lightingState = MutableStateFlow<SliderState>(SliderState(0.5f, true, true))
  val lightingState: StateFlow<SliderState>
    get() = _lightingState

  fun updateCinemaState(cinemaState: CinemaState) {
    ipcServiceConnection?.messageProcess(
        IPCService.IMMERSIVE_CHANNEL,
        ImmersiveActivityCodes.CONTROL_PANEL_SET_CINEMA_STATE.ordinal,
        Bundle().apply { putSerializable("cinemaState", cinemaState) })
  }

  // Function to update Passthrough level
  fun updatePassthroughLevel(level: Float) {
    _passthroughState.update { _passthroughState.value.copy(value = level) }
  }

  // Function to update Lighting level
  fun updateLightingLevel(level: Float) {
    _lightingState.update { _lightingState.value.copy(value = level) }
  }

  // Set control buttons
  fun updateControlButtons(buttons: List<ControlsPanelButton>) {
    _controlButtons.value = buttons
  }

  fun onPlayPauseToggle(shouldPlay: Boolean) {
    ipcServiceConnection?.messageProcess(
        IPCService.IMMERSIVE_CHANNEL,
        ImmersiveActivityCodes.CONTROL_PANEL_TOGGLE_PLAY.ordinal,
        Bundle().apply { putBoolean("isPlaying", shouldPlay) })
  }

  fun onMuteToggle(shouldMute: Boolean) {
    ipcServiceConnection?.messageProcess(
        IPCService.IMMERSIVE_CHANNEL,
        ImmersiveActivityCodes.CONTROL_PANEL_TOGGLE_MUTE.ordinal,
        Bundle().apply { putBoolean("isMute", shouldMute) })
  }

  fun onSeekTo(position: Float) {
    ipcServiceConnection?.messageProcess(
        IPCService.IMMERSIVE_CHANNEL,
        ImmersiveActivityCodes.CONTROL_PANEL_SEEK_TO.ordinal,
        Bundle().apply { putFloat("position", position) })
  }

  fun onUpdatePassthrough(passthrough: Float): Float {
    ipcServiceConnection?.messageProcess(
        IPCService.IMMERSIVE_CHANNEL,
        ImmersiveActivityCodes.CONTROL_PANEL_SET_PASSTHROUGH.ordinal,
        Bundle().apply { putFloat("passthrough", passthrough) })

    updatePassthroughLevel(passthrough)
    return passthrough
  }

  fun onUpdateLighting(lighting: Float): Float {
    ipcServiceConnection?.messageProcess(
        IPCService.IMMERSIVE_CHANNEL,
        ImmersiveActivityCodes.CONTROL_PANEL_SET_LIGHTING.ordinal,
        Bundle().apply { putFloat("value", lighting) })
    updateLightingLevel(lighting)
    return lighting
  }

  fun onStopWatching() {
    ipcServiceConnection?.messageProcess(
        IPCService.IMMERSIVE_CHANNEL, ImmersiveActivityCodes.CONTROL_PANEL_CLOSE_PLAYER.ordinal)
  }

  fun updateState(
      isPlaying: Boolean,
      isBuffering: Boolean,
      isMuted: Boolean,
      progress: Float,
      duration: Float
  ) {
    _mediaState.value =
        MediaState(
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            isMuted = isMuted,
            progress = progress,
            duration = duration,
        )
  }

  fun passthroughSetInteractable(enabled: Boolean) {
    _passthroughState.update { _passthroughState.value.copy(isInteractable = enabled) }
  }

  fun lightingSetInteractable(enabled: Boolean) {
    _lightingState.update { _lightingState.value.copy(isInteractable = enabled) }
  }
}

data class MediaState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isMuted: Boolean = false,
    val progress: Float = 0f,
    val duration: Float = 0f
)

data class SliderState(var value: Float, var isInteractable: Boolean, var isVisible: Boolean)
