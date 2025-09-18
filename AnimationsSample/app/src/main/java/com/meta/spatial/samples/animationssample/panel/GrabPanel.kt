/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.samples.animationssample.R
import com.meta.spatial.toolkit.PanelConstants.DEFAULT_DP_PER_METER
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme

const val GRAB_PANEL_WIDTH = 0.8f
const val GRAB_PANEL_HEIGHT = 0.384f

@Composable
fun GrabPanel(onButtonClick: () -> Unit) {
  val changeToFollowableText = stringResource(R.string.change_to_followable)
  val changeToCustomText = stringResource(R.string.change_to_custom)
  var buttonText by remember { mutableStateOf(changeToFollowableText) }
  SpatialTheme(colorScheme = getPanelTheme()) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .clip(SpatialTheme.shapes.large)
                .background(brush = LocalColorScheme.current.panel)
                .padding(20.dp)
    ) {
      Text(
          text = stringResource(R.string.grab_panel_title),
          style =
              SpatialTheme.typography.body1Strong.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(modifier = Modifier.size(8.dp))
      Text(
          text = stringResource(R.string.grab_panel),
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(modifier = Modifier.size(24.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        PrimaryButton(
            buttonText,
            onClick = {
              buttonText =
                  if (buttonText == changeToFollowableText) {
                    changeToCustomText
                  } else {
                    changeToFollowableText
                  }
              onButtonClick()
            },
        )
      }
    }
  }
}

@Composable
@Preview(
    widthDp = (DEFAULT_DP_PER_METER * GRAB_PANEL_WIDTH).toInt(),
    heightDp = (DEFAULT_DP_PER_METER * GRAB_PANEL_HEIGHT).toInt(),
)
fun GrabPanelPreview() {
  GrabPanel {}
}
