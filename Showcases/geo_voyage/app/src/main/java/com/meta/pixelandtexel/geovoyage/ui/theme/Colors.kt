// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

object GeoVoyageColors {
  val navContainer = Color(0xFFEBF5E9)
  val navContainerBottom = Color(0xFF8d938c)
  val textContainer = Color(0xFFB6F1BB)
  val textContainerBottom = Color(0xFF6d9170)
  val button = Color(0xFF29666E)
  val navIcons = Color(0xFF001F24)
  val navSelected = Color(0xFFBDEAF4)
  val settingsCog = Color(0xFF727970)
  val textColor = Color(0xFF423F47)
  val errorColor = Color(0xFFB91A1A)
}

val GeoVoyageColorScheme =
    lightSpatialColorScheme()
        .copy(
            primaryButton = GeoVoyageColors.button,
            secondaryButton = Color(0xFFECEFE8),
            panel =
                Brush.verticalGradient(
                    colors =
                        listOf(
                            GeoVoyageColors.navContainer,
                            GeoVoyageColors.navContainerBottom,
                        ),
                ),
            dialog =
                Brush.verticalGradient(
                    colors =
                        listOf(
                            GeoVoyageColors.textContainer,
                            GeoVoyageColors.textContainerBottom,
                        ),
                ),
        )
