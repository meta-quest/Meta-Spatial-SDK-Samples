/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mixedrealitysample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.toolkit.PanelConstants
import com.meta.spatial.uiset.button.SecondaryButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

const val ABOUT_PANEL_WIDTH = 2.048f
const val ABOUT_PANEL_HEIGHT = 1.254f

internal val CIRCLE_DARK_MODE = Color(0xFF494949)
internal val CIRCLE_LIGHT_MODE = SpatialColor.gray20

@Composable
fun getPanelTheme(): SpatialColorScheme =
    if (isSystemInDarkTheme()) darkSpatialColorScheme() else lightSpatialColorScheme()

@Composable
fun NumberedCircleIcon(number: Int) {
  val circleIconColor = if (isSystemInDarkTheme()) CIRCLE_DARK_MODE else CIRCLE_LIGHT_MODE
  Box(
      modifier = Modifier.size(28.dp).background(circleIconColor, CircleShape),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = number.toString(),
        style = SpatialTheme.typography.body1,
        color = SpatialTheme.colorScheme.secondaryAlphaBackground,
        textAlign = TextAlign.Center,
    )
  }
}

@Composable
@Preview(
    widthDp = (PanelConstants.DEFAULT_DP_PER_METER * ABOUT_PANEL_WIDTH).toInt(),
    heightDp = (PanelConstants.DEFAULT_DP_PER_METER * ABOUT_PANEL_HEIGHT).toInt(),
)
fun AboutPanelPreview() {
  AboutPanelLayout(
      onConfigureRoomClick = {},
      onToggleDebugClick = {},
  )
}

@Composable
fun AboutPanelLayout(
    onConfigureRoomClick: () -> Unit,
    onToggleDebugClick: () -> Unit,
) {
  SpatialTheme(colorScheme = getPanelTheme()) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .clip(SpatialTheme.shapes.large)
                .background(brush = LocalColorScheme.current.panel),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
          modifier = Modifier.widthIn(max = 520.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = stringResource(R.string.mixedrealitysample_title),
            textAlign = TextAlign.Center,
            style =
                SpatialTheme.typography.headline1Strong.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(modifier = Modifier.size(24.dp))

        // Body text
        Text(
            text = stringResource(R.string.mixedrealitysample_description),
            textAlign = TextAlign.Center,
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
            modifier = Modifier.widthIn(max = 300.dp),
        )
        Spacer(modifier = Modifier.size(48.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // First row: Setup room instruction + button
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              NumberedCircleIcon(number = 1)
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                  text = stringResource(R.string.setup_room_instruction),
                  textAlign = TextAlign.Center,
                  style =
                      SpatialTheme.typography.body1.copy(
                          color = SpatialTheme.colorScheme.primaryAlphaBackground
                      ),
              )
            }
            Box(modifier = Modifier.width(140.dp)) {
              SecondaryButton(
                  label = stringResource(R.string.setup_room),
                  onClick = onConfigureRoomClick,
                  expanded = true,
              )
            }
          }

          // Second row: Toggle debug instruction + button
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              NumberedCircleIcon(number = 2)
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                  text = stringResource(R.string.toggle_debug_instruction),
                  textAlign = TextAlign.Center,
                  style =
                      SpatialTheme.typography.body1.copy(
                          color = SpatialTheme.colorScheme.primaryAlphaBackground
                      ),
              )
            }
            Box(modifier = Modifier.width(140.dp)) {
              SecondaryButton(
                  label = stringResource(R.string.toggle_debug),
                  onClick = onToggleDebugClick,
                  expanded = true,
              )
            }
          }

          // Third row: Fire balls instruction + trigger gif
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              NumberedCircleIcon(number = 3)
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                  text = stringResource(R.string.fire_balls),
                  textAlign = TextAlign.Center,
                  style =
                      SpatialTheme.typography.body1.copy(
                          color = SpatialTheme.colorScheme.primaryAlphaBackground
                      ),
              )
            }
            Box(
                modifier = Modifier.width(132.dp).height(48.dp),
                contentAlignment = Alignment.Center,
            ) {
              Image(
                  painter = painterResource(id = R.drawable.trigger),
                  contentDescription = "Trigger animation",
                  contentScale = ContentScale.Fit,
              )
            }
          }
        }
      }
    }
  }
}
