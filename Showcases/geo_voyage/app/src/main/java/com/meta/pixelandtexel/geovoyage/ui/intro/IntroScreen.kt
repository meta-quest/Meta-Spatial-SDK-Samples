// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun IntroScreen() {
  val introText = stringResource(R.string.intro)

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        SecondaryPanel(modifier = Modifier.fillMaxWidth().height(510.dp)) {
          Column(
              verticalArrangement = Arrangement.SpaceAround,
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.fillMaxSize()) {
                MarkdownText(
                    markdown = introText,
                    modifier = Modifier.selectable(false, false) {},
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                        ))
              }
        }
      }
}

@Preview(widthDp = 932, heightDp = 650, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun IntroScreenPreview() {
  GeoVoyageTheme { IntroScreen() }
}
