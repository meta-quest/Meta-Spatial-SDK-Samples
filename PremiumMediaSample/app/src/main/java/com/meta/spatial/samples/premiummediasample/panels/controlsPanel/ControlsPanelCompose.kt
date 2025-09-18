/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.panels.controlsPanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.samples.premiummediasample.R
import com.meta.spatial.samples.premiummediasample.panels.FadeIcon
import com.meta.spatial.samples.premiummediasample.panels.HoverButton
import com.meta.spatial.samples.premiummediasample.panels.HoverIconButton
import com.meta.spatial.samples.premiummediasample.panels.MetaButton
import com.meta.spatial.samples.premiummediasample.panels.formatTime
import kotlin.math.max

object ControlsPanelConstants {
  const val PANEL_WIDTH_DP = 800f
  const val PANEL_HEIGHT_DP = 220f
  const val PANEL_DP_PER_METER = 1000f

  val controlsBackgroundColor = Color(0xFF1c2b33)
  val controlsBackgroundSecondary = Color(0xFF283943)
  val controlsBlueColor = Color(0xFF0064e0)
  val controlsDisabledColor = Color(0xFF324047)

  val controlsDebugHover = false
}

@Composable
fun ControlsPanel(viewModel: ControlsPanelViewModel) {
  val mediaState by viewModel.mediaState.collectAsState()
  val controlButtons by viewModel.controlButtons.collectAsState()

  val passthroughState by viewModel.passthroughState.collectAsState()
  val lightingState by viewModel.lightingState.collectAsState()

  ControlsPanel(
      mediaState = mediaState,
      controlButtons = controlButtons,
      passthroughState = passthroughState,
      lightingState = lightingState,
      onPlayPauseToggle = viewModel::onPlayPauseToggle,
      onMuteToggle = viewModel::onMuteToggle,
      onSeekTo = viewModel::onSeekTo,
      onStopWatching = viewModel::onStopWatching,
      onUpdatePassthrough = viewModel::onUpdatePassthrough,
      onUpdateLighting = viewModel::onUpdateLighting,
  )
}

@Composable
fun ControlsPanel(
    mediaState: MediaState,
    controlButtons: List<ControlsPanelButton>,
    passthroughState: SliderState,
    lightingState: SliderState,
    onPlayPauseToggle: (Boolean) -> Unit,
    onMuteToggle: (Boolean) -> Unit,
    onSeekTo: (Float) -> Unit,
    onStopWatching: () -> Unit,
    onUpdatePassthrough: (Float) -> Float,
    onUpdateLighting: (Float) -> Float,
) {
  var isScrubbing by remember { mutableStateOf(false) }
  var displayProgress by remember {
    mutableStateOf(mediaState.progress)
  } // Temporary progress state

  var lightingValue by remember { mutableStateOf(lightingState.value) }
  var passthroughValue by remember { mutableFloatStateOf(passthroughState.value) }

  val sliderWidth = 100.dp
  val cornerRadius = 24.dp
  val boxPadding = 20.dp // Outer padding for all elements
  val sliderPaddingNegate = 7.5.dp // to modify this one slider to be smaller in padding
  val slidersSpacingRow = 34.dp
  val spacingBetweenIconAndSlider = 4.dp
  val buttonSpacing = 10.dp

  LaunchedEffect(mediaState.progress) {
    // Only update scrubProgress when not scrubbing
    if (!isScrubbing) {
      displayProgress = mediaState.progress
    }
  }

  Column(
      modifier =
          Modifier.fillMaxSize()
              .clip(RoundedCornerShape(cornerRadius))
              .background(ControlsPanelConstants.controlsBackgroundColor)
              .padding(0.dp, boxPadding),
      verticalArrangement = Arrangement.SpaceBetween,
  ) {
    // Top Controls (Audio, Rewind, Play/Pause, Stop Watching)
    Row(
        modifier = Modifier.fillMaxWidth().padding(boxPadding, 0.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
          HoverIconButton(
              iconPainter =
                  painterResource(
                      id =
                          if (mediaState.isMuted) R.drawable.controls_volume_off
                          else R.drawable.controls_volume_on
                  ),
              onClick = { onMuteToggle(!mediaState.isMuted) },
          )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
        ) {
          HoverIconButton(
              iconPainter = painterResource(id = R.drawable.controls_seek_back_10),
              onClick = { onSeekTo(displayProgress - 10f) },
          )
          HoverButton(
              glowInside = true,
              haloOpacity = 0.1f,
              haloSize = 39.5.dp,
              haloBorderWidth = 10.dp,
              iconTint = Color.White,
              onClick = { onPlayPauseToggle(!mediaState.isPlaying) },
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                  modifier = Modifier.align(Alignment.Center),
                  tint = Color.White,
                  painter =
                      painterResource(
                          id =
                              if (mediaState.isPlaying) R.drawable.controls_btn_pause
                              else R.drawable.controls_btn_play
                      ),
                  contentDescription = "",
              )
              if (mediaState.isBuffering) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.size(32.dp) // Adjust the size of the progress circle
                            .align(Alignment.Center)
                            .offset(-0.dp, 0.25.dp),
                    color = Color(0x33FFFFFF),
                    strokeWidth = 1.5.dp,
                )
              }
            }
          }
          HoverIconButton(
              iconPainter = painterResource(id = R.drawable.controls_seek_forward_10),
              onClick = { onSeekTo(displayProgress + 10f) },
          )
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopEnd) {
          MetaButton("Stop Watching", Modifier, onClick = onStopWatching)
        }
      }
    }

    // Slider (Progress Bar)
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
      Slider(
          value = displayProgress,
          onValueChange = { newValue ->
            isScrubbing = true
            displayProgress = newValue
          },
          onValueChangeFinished = {
            isScrubbing = false
            onSeekTo(displayProgress) // Seek when scrubbing stops
          },
          modifier = Modifier.fillMaxWidth().padding(boxPadding - sliderPaddingNegate, 0.dp),
          colors =
              SliderDefaults.colors(
                  thumbColor = Color.White,
                  activeTrackColor = ControlsPanelConstants.controlsBlueColor,
                  inactiveTrackColor = ControlsPanelConstants.controlsDisabledColor,
              ),
          valueRange = 0f..max(mediaState.duration, 0f),
      )
      Row(
          modifier =
              Modifier.offset(0.dp, -8.dp)
                  .padding(boxPadding, 0.dp, boxPadding, 10.dp)
                  .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(formatTime(displayProgress), fontSize = 11.sp, color = Color.Gray)
        Text(formatTime(mediaState.duration), fontSize = 11.sp, color = Color.Gray)
      }
    }

    // Toggle Options (Curve Screen, Passthrough, Lighting + Buttons)
    Box(modifier = Modifier.height(48.dp).padding(horizontal = boxPadding).fillMaxWidth()) {
      Box(
          modifier =
              Modifier.clip(RoundedCornerShape(cornerRadius))
                  .background(ControlsPanelConstants.controlsBackgroundSecondary)
                  .fillMaxWidth()
                  .padding(horizontal = cornerRadius),
          contentAlignment = Alignment.TopStart,
      ) {
        Row(
            modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(slidersSpacingRow, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          FadeSlider(
              passthroughValue,
              passthroughState,
              R.drawable.controls_passthrough_off,
              R.drawable.controls_passthrough_on,
              spacingBetweenIconAndSlider,
              sliderWidth,
              flipIconFade = true,
          ) {
            passthroughValue = onUpdatePassthrough(it)
          }
          FadeSlider(
              lightingValue,
              lightingState,
              R.drawable.controls_lighting_off_v2,
              R.drawable.controls_lighting_on_v2,
              spacingBetweenIconAndSlider,
              sliderWidth,
              minimumOpacity = 0.25f,
          ) {
            lightingValue = onUpdateLighting(it)
          }
        }
        Row(
            modifier = Modifier.fillMaxHeight().offset(x = cornerRadius - 4.dp).fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(buttonSpacing, Alignment.End), // Space evenly
            verticalAlignment = Alignment.CenterVertically,
        ) {
          for (buttonData in controlButtons) {
            MetaButton(
                buttonData.buttonName,
                color = Color(0xFF3d4c55),
                blurSize = 0.dp,
                modifier = Modifier.height(40.dp),
                onClick = buttonData.onClick,
            )
          }
        }
      }
    }
  }
}

@Composable
fun FadeSlider(
    sliderValue: Float,
    state: SliderState,
    iconBG: Int,
    iconFade: Int,
    spacing: Dp,
    sliderWidth: Dp,
    minimumOpacity: Float = 0f,
    flipIconFade: Boolean = false,
    onValueChange: (Float) -> Unit,
) {
  if (state.isVisible) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      FadeIcon(
          iconBG = painterResource(iconBG),
          iconFade = painterResource(iconFade),
          fadeOpacity =
              ((if (flipIconFade) 1f - sliderValue else sliderValue) * (1f - minimumOpacity)) +
                  minimumOpacity,
          color = if (state.isInteractable) Color.White else Color(0.5f, 0.5f, 0.5f, 1f),
      )
      Slider(
          enabled = state.isInteractable,
          value = sliderValue,
          onValueChange = onValueChange,
          modifier = Modifier.width(sliderWidth),
          colors =
              SliderDefaults.colors(
                  thumbColor = Color.White,
                  activeTrackColor = ControlsPanelConstants.controlsBlueColor,
                  inactiveTrackColor = ControlsPanelConstants.controlsDisabledColor,
              ),
      )
    }
  }
}

@Preview(
    widthDp = ControlsPanelConstants.PANEL_WIDTH_DP.toInt(),
    heightDp = ControlsPanelConstants.PANEL_HEIGHT_DP.toInt(),
)
@Composable
fun ControlsPanelPreview() {
  val viewModel = ControlsPanelViewModel()
  viewModel.updateControlButtons(
      listOf(ControlsPanelButton("Cinema") {}, ControlsPanelButton("TV") {})
  )
  ControlsPanel(viewModel = viewModel)
}

// Data classes
data class ControlsPanelButton(val buttonName: String, val onClick: () -> Unit)
