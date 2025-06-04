// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.askearth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.SpatialTheme

/**
 * Display error.
 *
 * @param errorMessage Error message to display.
 */
@Composable
fun ErrorScreen(errorMessage: String) {
  SecondaryPanel {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight()) {
                Text(
                    text = stringResource(id = R.string.default_error_title),
                    style = SpatialTheme.typography.headline3Strong)
                if (errorMessage.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(20.dp))
                  Text(text = errorMessage, style = SpatialTheme.typography.body1)
                }
              }
        }
  }
}

@Preview(widthDp = 570, heightDp = 480)
@Composable
private fun ErrorScreenPreview() {
  GeoVoyageTheme { ErrorScreen("THIS ERROR HAS OCCURED BECAUSE YOU DID SOMETHING INCORRECTLY") }
}
