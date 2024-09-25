// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.askearth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.PrimaryButton
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

/**
 * Display speech input interface.
 *
 * @param amplitude Amplitude value to determine which sound generation image to display.
 */
@Composable
fun ListeningScreen(amplitude: Int = 0, onStopListeningClicked: () -> Unit) {
  var imageId: Int = R.drawable.askearth_thinking_darr

  // TODO add these to a config instead of hard-coding here
  if (amplitude > 100) {
    imageId = R.drawable.askearth_speaking_darr_1
  }
  if (amplitude > 800) {
    imageId = R.drawable.askearth_speaking_darr_2
  }
  if (amplitude > 2000) {
    imageId = R.drawable.askearth_speaking_darr_3
  }

  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        SecondaryPanel(modifier = Modifier.width(452.dp).height(430.dp)) {
          Column(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.SpaceEvenly,
              horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                  Image(
                      painterResource(id = imageId),
                      contentDescription = "Ask Earth Amplitude",
                      contentScale = ContentScale.FillWidth,
                      colorFilter = ColorFilter.tint(Color.Black),
                      modifier = Modifier.graphicsLayer(rotationZ = 90f).width(150.dp))
                  Image(
                      painterResource(id = R.drawable.askearth_speaking_mic),
                      contentDescription = "Ask Earth Mic",
                      colorFilter = ColorFilter.tint(Color.Black),
                      contentScale = ContentScale.FillWidth,
                      modifier = Modifier.padding(12.dp).width(56.dp))
                  Image(
                      painterResource(id = imageId),
                      contentDescription = "Ask Earth Amplitude",
                      contentScale = ContentScale.FillWidth,
                      colorFilter = ColorFilter.tint(Color.Black),
                      modifier = Modifier.graphicsLayer(rotationZ = -90f).width(150.dp))
                }
            PrimaryButton(
                text = stringResource(id = R.string.finished_speaking),
                onClick = onStopListeningClicked)
          }
        }
      }
}

@Preview(widthDp = 932, heightDp = 650, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun SpeakingScreenPreview() {
  GeoVoyageTheme { ListeningScreen {} }
}
