// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.navigation.theme_selector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.theme.UISetSampleTheme
import com.meta.spatial.uiset.button.SecondaryButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.BrightnessOn
import com.meta.spatial.uiset.theme.icons.regular.HomeEdit
import com.meta.spatial.uiset.theme.icons.regular.NightMode

@Composable
fun ThemeSelectorView() {
  return UISetSampleTheme(
      overrideColorScheme = ThemeHolder.theme.colorScheme,
  ) {
    Box(
        Modifier.fillMaxSize(),
    ) {
      Column(
          Modifier.fillMaxWidth(0.6f)
              .fillMaxHeight(0.5f)
              .clip(SpatialTheme.shapes.large)
              .background(SpatialTheme.colorScheme.panel)
              .padding(36.dp),
      ) {
        Text(
            text = "Themes",
            style =
                LocalTypography.current.headline2Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(24.dp))
        AppTheme.entries.forEachIndexed { index, theme ->
          SecondaryButton(
              expanded = true,
              leading = {
                Icon(
                    when (theme.type) {
                      AppThemeType.DARK -> SpatialIcons.Regular.NightMode
                      AppThemeType.LIGHT -> SpatialIcons.Regular.BrightnessOn
                      AppThemeType.CUSTOM -> SpatialIcons.Regular.HomeEdit
                    },
                    contentDescription = "",
                )
              },
              label = theme.displayName,
              onClick = {
                if (ThemeHolder.theme != theme) {
                  ThemeHolder.theme = theme
                }
              },
          )
          if (index < AppTheme.entries.size - 1) {
            Spacer(Modifier.size(12.dp))
          }
        }
      }
    }
  }
}
