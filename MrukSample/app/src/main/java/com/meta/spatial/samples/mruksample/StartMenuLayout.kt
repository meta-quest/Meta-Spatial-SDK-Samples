/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.meta.spatial.toolkit.PanelConstants
import com.meta.spatial.uiset.button.SecondaryButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

const val START_MENU_PANEL_WIDTH = 1.6f
const val START_MENU_PANEL_HEIGHT = 1.1f

@Composable
fun getPanelTheme(): SpatialColorScheme =
    if (isSystemInDarkTheme()) darkSpatialColorScheme() else lightSpatialColorScheme()

@Composable
@Preview(
    widthDp = (PanelConstants.DEFAULT_DP_PER_METER * START_MENU_PANEL_WIDTH).toInt(),
    heightDp = (PanelConstants.DEFAULT_DP_PER_METER * START_MENU_PANEL_HEIGHT).toInt(),
)
fun StartMenuPreview() {
  StartMenuLayout(
      onAnchorMeshClick = {},
      onRaycastClick = {},
      onKeyboardTrackerClick = {},
      onQrCodeScannerClick = {},
  )
}

@Composable
fun StartMenuLayout(
    onAnchorMeshClick: () -> Unit,
    onRaycastClick: () -> Unit,
    onKeyboardTrackerClick: () -> Unit,
    onQrCodeScannerClick: () -> Unit,
) {
  SpatialTheme(colorScheme = getPanelTheme()) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .clip(SpatialTheme.shapes.large)
                .background(brush = LocalColorScheme.current.panel)
                .padding(36.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
          modifier = Modifier.widthIn(max = 400.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = stringResource(R.string.mruksample_title),
            textAlign = TextAlign.Center,
            style =
                SpatialTheme.typography.headline1Strong.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 24.dp),
        ) {
          SecondaryButton(
              label = stringResource(R.string.anchor_mesh_sample),
              onClick = onAnchorMeshClick,
              expanded = true,
          )
          SecondaryButton(
              label = stringResource(R.string.raycast_sample),
              onClick = onRaycastClick,
              expanded = true,
          )
          SecondaryButton(
              label = stringResource(R.string.keyboard_tracker_sample),
              onClick = onKeyboardTrackerClick,
              expanded = true,
          )
          SecondaryButton(
              label = stringResource(R.string.qr_code_scanner_sample),
              onClick = onQrCodeScannerClick,
              expanded = true,
          )
        }
      }
    }
  }
}
