/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.hybridsample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

class PancakeActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    this.setTheme(R.style.PanelAppThemeTransparent)
    val activity = this
    setContent {
      HybridPanel(stringResource(R.string.switch_to_immersive_view)) {
        val immersiveIntent =
            Intent(activity, HybridSampleActivity::class.java).apply {
              action = Intent.ACTION_MAIN
              addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        startActivity(immersiveIntent)
      }
    }
  }
}

@Composable
fun getPanelTheme(): SpatialColorScheme =
    if (isSystemInDarkTheme()) darkSpatialColorScheme() else lightSpatialColorScheme()

@Composable
fun HybridPanel(buttonText: String, onButtonClick: () -> Unit) {
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
      Column(
          modifier = Modifier.widthIn(max = 400.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = stringResource(R.string.hybridsample_title),
            textAlign = TextAlign.Center,
            style =
                SpatialTheme.typography.headline1Strong.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(modifier = Modifier.size(24.dp))
        Text(
            text = stringResource(R.string.hybridsample_description),
            textAlign = TextAlign.Center,
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(modifier = Modifier.size(24.dp))
        PrimaryButton(buttonText, expanded = true, onClick = { onButtonClick() })
      }
    }
  }
}

@Preview
@Composable
fun HybridPanelPreview() {
  HybridPanel("Test") {}
}
