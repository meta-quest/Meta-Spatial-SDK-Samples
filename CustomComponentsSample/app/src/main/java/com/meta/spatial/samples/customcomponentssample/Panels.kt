/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.customcomponentssample

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.toolkit.PanelConstants.DEFAULT_DP_PER_METER
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

const val PANEL_WIDTH = 2.048f
const val PANEL_HEIGHT = 1.254f

@Composable
fun getPanelTheme(): SpatialColorScheme {
  if (isSystemInDarkTheme()) {
    return darkSpatialColorScheme()
  } else {
    return lightSpatialColorScheme()
  }
}

@Composable
@Preview(
    widthDp = (DEFAULT_DP_PER_METER * PANEL_WIDTH).toInt(),
    heightDp = (DEFAULT_DP_PER_METER * PANEL_HEIGHT).toInt(),
)
fun WelcomePanel() {
  SpatialTheme(colorScheme = getPanelTheme()) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .clip(SpatialTheme.shapes.large)
                .background(brush = LocalColorScheme.current.panel)
                .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Text(
          text = stringResource(R.string.panel_title),
          modifier = Modifier.widthIn(max = 400.dp),
          textAlign = TextAlign.Center,
          style =
              SpatialTheme.typography.headline1Strong.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(modifier = Modifier.size(24.dp))
      Text(
          text = stringResource(R.string.panel_description),
          modifier = Modifier.widthIn(max = 400.dp),
          textAlign = TextAlign.Center,
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
    }
  }
}
