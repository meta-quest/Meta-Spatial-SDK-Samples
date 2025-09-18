/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample.panel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun CurvedPanelIcon(color: Color) {
  Canvas(modifier = Modifier.width(16.dp).height(12.dp)) {
    val iconWidth = size.width
    val iconHeight = size.height
    val curveHeight = iconHeight * 0.2f

    // Draw curved rectangle with straight sides and upward-bent top/bottom
    val path =
        Path().apply {
          val left = 0f
          val right = iconWidth
          val top = 0f
          val bottom = iconHeight

          // Start at bottom-left
          moveTo(left, bottom)

          // Left side (straight)
          lineTo(left, top)

          // Top side (curved upward - concave)
          quadraticTo(iconWidth / 2f, top - curveHeight, right, top)

          // Right side (straight)
          lineTo(right, bottom)

          // Bottom side (curved upward - concave)
          quadraticTo(iconWidth / 2f, bottom - curveHeight, left, bottom)

          close()
        }

    drawPath(
        path = path,
        color = color,
    )
  }
}

@Composable
fun FlatPanelIcon(color: Color) {
  Canvas(modifier = Modifier.width(16.dp).height(12.dp)) {
    val iconWidth = size.width
    val iconHeight = size.height
    // Draw straight rectangle
    drawRect(
        color = color,
        topLeft = Offset(0f, 0f),
        size = Size(iconWidth, iconHeight),
    )
  }
}
