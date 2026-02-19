/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

@Composable
fun UISetSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    overrideColorScheme: SpatialColorScheme? = null,
    content: @Composable () -> Unit,
) {
  val colorScheme =
      when {
        overrideColorScheme != null -> overrideColorScheme
        darkTheme -> darkSpatialColorScheme()
        else -> lightSpatialColorScheme()
      }
  SpatialTheme(colorScheme = colorScheme, content = content)
}
