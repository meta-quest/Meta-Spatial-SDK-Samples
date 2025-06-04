// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.askearth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.SpatialTheme

/**
 * Screen that displays when query is submitted and response is pending.
 *
 * @param transcription User-input query text which will display on the thinking view.
 */
@Composable
fun ThinkingScreen(transcription: String) {
  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        SecondaryPanel(
            modifier =
                Modifier.width(dimensionResource(R.dimen.centered_panel_width))
                    .height(dimensionResource(R.dimen.centered_panel_height))) {
              Box {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()) {
                      Image(
                          painter = painterResource(id = R.drawable.askearth_thinking_darr),
                          contentDescription = stringResource(id = R.string.thinking),
                          colorFilter = ColorFilter.tint(Color.Black),
                          contentScale = ContentScale.FillWidth,
                          modifier = Modifier.padding(30.dp).width(120.dp))
                      Text(
                          text = stringResource(id = R.string.thinking),
                          style = SpatialTheme.typography.headline3)
                    }
              }
            }
      }
}

@Preview(widthDp = 570, heightDp = 480, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun ThinkingScreenPreview() {
  GeoVoyageTheme { ThinkingScreen(transcription = "What is the oldest rainforest on earth?") }
}
