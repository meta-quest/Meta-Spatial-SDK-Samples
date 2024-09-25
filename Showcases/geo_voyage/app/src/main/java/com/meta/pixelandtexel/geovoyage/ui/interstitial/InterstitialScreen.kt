// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.interstitial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.PrimaryButton
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun InterstitialScreen(onAccepted: () -> Unit) {
  val noticeText = stringResource(id = R.string.notice)

  Column(
      verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(50.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          MarkdownText(
              markdown = noticeText,
              modifier = Modifier.selectable(false, false) {},
              style = MaterialTheme.typography.headlineSmall)
          PrimaryButton(text = stringResource(id = R.string._continue), onClick = onAccepted)
        }
      }
}

@Preview(widthDp = 932, heightDp = 650, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
fun InterstitialScreenPreview() {
  GeoVoyageTheme { InterstitialScreen() {} }
}
