/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample.panel

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme
import kotlin.math.roundToInt

enum class ToggleState {
  LEFT,
  RIGHT,
}

@Composable
fun CustomToggle(
    toggleState: ToggleState,
    onToggleStateChange: (ToggleState) -> Unit,
    modifier: Modifier = Modifier,
) {
  val toggleWidth = 100.dp
  val toggleHeight = 44.dp
  val thumbPadding = 8.dp

  val animatedOffset by
      animateFloatAsState(
          targetValue = if (toggleState == ToggleState.RIGHT) 1f else 0f,
          animationSpec = tween(durationMillis = 300),
          label = "toggle_offset",
      )

  val toggleColor = if (isSystemInDarkTheme()) SpatialColor.black10 else SpatialColor.white100
  val thumbColor = SpatialTheme.colorScheme.controlButton

  // Colors for selected vs unselected states
  val selectedIconColor = if (isSystemInDarkTheme()) SpatialColor.black100 else SpatialColor.white90
  val unselectedIconColor =
      if (isSystemInDarkTheme()) SpatialColor.white60 else SpatialColor.black100

  Box(
      modifier =
          modifier
              .width(100.dp)
              .height(toggleHeight)
              .clip(RoundedCornerShape(toggleHeight / 4))
              .shadow(
                  elevation = 1.dp,
                  shape = RoundedCornerShape(toggleHeight / 4),
                  ambientColor = SpatialColor.white10,
                  spotColor = SpatialColor.white10,
              )
              .shadow(
                  elevation = 0.5.dp,
                  shape = RoundedCornerShape(toggleHeight / 4),
                  ambientColor = SpatialColor.black10,
                  spotColor = SpatialColor.black10,
              )
              .background(toggleColor)
              .clickable {
                onToggleStateChange(
                    if (toggleState == ToggleState.LEFT) ToggleState.RIGHT else ToggleState.LEFT
                )
              }
              .padding(thumbPadding),
      contentAlignment = Alignment.CenterStart,
  ) {
    // Sliding thumb
    Box(
        modifier =
            Modifier.width(42.dp)
                .height(32.dp)
                .offset {
                  IntOffset(
                      x =
                          (animatedOffset *
                                  (toggleWidth.toPx() - 42.dp.toPx() - (thumbPadding * 2).toPx()))
                              .roundToInt(),
                      y = 0,
                  )
                }
                .background(thumbColor, RoundedCornerShape(8.dp))
    )

    // Left icon (Curved Panel)
    Box(
        modifier = Modifier.align(Alignment.CenterStart).padding(start = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
      val color = if (toggleState == ToggleState.LEFT) selectedIconColor else unselectedIconColor
      CurvedPanelIcon(color = color)
    }

    // Right icon (Flat Panel)
    Box(
        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
      val color = if (toggleState == ToggleState.RIGHT) selectedIconColor else unselectedIconColor
      FlatPanelIcon(color = color)
    }
  }
}
