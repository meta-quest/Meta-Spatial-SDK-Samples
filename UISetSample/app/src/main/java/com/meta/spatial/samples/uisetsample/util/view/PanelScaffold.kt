/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.util.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.samples.uisetsample.navigation.theme_selector.ThemeHolder
import com.meta.spatial.samples.uisetsample.theme.UISetSampleTheme
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.interFontFamily

@Composable
fun PanelScaffold(
    title: String? = null,
    padding: PaddingValues = PaddingValues(48.dp),
    overrideColorScheme: SpatialColorScheme? = null,
    content: @Composable () -> Unit,
) {

  UISetSampleTheme(
      overrideColorScheme = overrideColorScheme ?: ThemeHolder.theme.colorScheme,
  ) {
    Column(
        Modifier.fillMaxSize()
            .clip(SpatialTheme.shapes.large)
            .background(brush = LocalColorScheme.current.panel)
            .padding(padding),
    ) {
      if (title != null) {
        Text(
            title,
            style =
                TextStyle(
                    fontFamily = interFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 72.sp,
                    lineHeight = 76.sp,
                    color = SpatialTheme.colorScheme.primaryAlphaBackground,
                ),
        )
        Spacer(Modifier.size(40.dp))
        HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
        Spacer(Modifier.size(48.dp))
      }
      content()
    }
  }
}
