/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.util.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.meta.spatial.samples.uisetsample.navigation.theme_selector.ThemeHolder
import com.meta.spatial.samples.uisetsample.theme.UISetSampleTheme
import com.meta.spatial.uiset.button.BorderlessCircleButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.Close
import com.meta.spatial.uiset.theme.icons.regular.Minimize

@Composable
fun PatternScaffold(
    isVideo: Boolean = false,
    overrideColorScheme: SpatialColorScheme? = null,
    leading: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {

  UISetSampleTheme(
      overrideColorScheme = overrideColorScheme ?: ThemeHolder.theme.colorScheme,
  ) {
    Column(
        Modifier.fillMaxSize()
            .clip(SpatialTheme.shapes.large)
            .background(
                brush =
                    if (isVideo) {
                      Brush.verticalGradient(
                          colors =
                              listOf(
                                  SpatialColor.b50,
                                  SpatialColor.b50,
                              ),
                      )
                    } else {
                      LocalColorScheme.current.panel
                    }
            )
    ) {
      PanelToolbar(
          leading = leading,
          title = "Title",
          actions = {
            BorderlessCircleButton(
                icon = { Icon(SpatialIcons.Regular.Minimize, "", modifier = Modifier.size(16.dp)) },
                onClick = {},
            )

            BorderlessCircleButton(
                icon = { Icon(SpatialIcons.Regular.Close, "", modifier = Modifier.size(16.dp)) },
                onClick = {},
            )
          },
      )

      Box(
          modifier = Modifier.padding(24.dp),
      ) {
        content()
      }
    }
  }
}
