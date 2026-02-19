/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.navigation.theme_selector

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

enum class AppThemeType {
  DARK,
  LIGHT,
  CUSTOM,
}

enum class AppTheme(
    val colorScheme: SpatialColorScheme,
    val type: AppThemeType,
    val displayName: String,
) {
  DARK(
      darkSpatialColorScheme(),
      AppThemeType.DARK,
      "Dark Theme",
  ),
  LIGHT(
      lightSpatialColorScheme(),
      AppThemeType.LIGHT,
      "Light Theme",
  ),
  CUSTOM1(
      darkSpatialColorScheme()
          .copy(
              primaryButton = Color(0xFFA000D3),
              primaryOpaqueButton = SpatialColor.RLDSWhite100,
              secondaryButton = Color(0x33A000D3),
              menu = Color(0xFF35003C),
              panel =
                  Brush.verticalGradient(
                      colors =
                          listOf(
                              Color(0xFF53005E),
                              Color(0xFF35003C),
                          ),
                  ),
              dialog =
                  Brush.verticalGradient(
                      colors =
                          listOf(
                              Color(0xFF53005E),
                              Color(0xFF35003C),
                          ),
                  ),
          ),
      AppThemeType.CUSTOM,
      "Custom Theme 1",
  ),
  CUSTOM2(
      darkSpatialColorScheme()
          .copy(
              primaryButton = Color(0xFF00B98B),
              primaryOpaqueButton = SpatialColor.RLDSWhite100,
              secondaryButton = Color(0x3300B98B),
              menu = Color(0xFF00322E),
              panel =
                  Brush.verticalGradient(
                      colors =
                          listOf(
                              Color(0xFF005850),
                              Color(0xFF00322E),
                          ),
                  ),
              dialog =
                  Brush.verticalGradient(
                      colors =
                          listOf(
                              Color(0xFF005850),
                              Color(0xFF00322E),
                          ),
                  ),
          ),
      AppThemeType.CUSTOM,
      "Custom Theme 2",
  ),
}
