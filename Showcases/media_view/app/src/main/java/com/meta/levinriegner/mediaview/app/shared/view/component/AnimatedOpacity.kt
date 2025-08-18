// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.shared.view.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AnimatedOpacity(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = 300,
    content: @Composable () -> Unit,
) {
  val targetAlpha = if (visible) 1f else 0f

  val alpha by
      animateFloatAsState(
          targetValue = targetAlpha,
          animationSpec = tween(durationMillis),
          label = "Opacity State",
      )

  Box(modifier = modifier.graphicsLayer(alpha = alpha)) { content() }
}
