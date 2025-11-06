// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.theme.LocalTypography
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun IntroScreen(onStart: (() -> Unit)? = null) {
  val introText = stringResource(R.string.intro)

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize(),
  ) {
    SecondaryPanel {
      Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize(),
      ) {
        MarkdownText(
            markdown = introText,
            modifier = Modifier.selectable(false, false) {},
            style = LocalTypography.current.body1Strong,
        )
        PrimaryButton("Start", { onStart?.invoke() })
      }
    }
  }
}

@Preview(widthDp = 570, heightDp = 480)
@Composable
private fun IntroScreenPreview() {
  GeoVoyageTheme { IntroScreen() }
}
