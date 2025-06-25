// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.theme

import androidx.compose.runtime.Composable
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun GeoVoyageTheme(content: @Composable () -> Unit) {
  SpatialTheme(
      colorScheme = GeoVoyageColorScheme,
      typography = GeoVoyageTypography,
      shapes = GeoVoyageShapes,
      content = content)
}
