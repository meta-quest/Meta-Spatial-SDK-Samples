// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.shared.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    CircularProgressIndicator(color = color)
  }
}
