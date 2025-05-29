// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.views.components.Panel
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun NoticeView(onContinue: (() -> Unit)) {
  Panel {
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
      Column(
          verticalArrangement = Arrangement.spacedBy(30.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        MarkdownText(
            stringResource(R.string.notice),
            style = SpatialTheme.typography.body1.merge(TextStyle(color = SpatialColor.white100)))
        PrimaryButton(stringResource(R.string.btn_continue), onClick = onContinue, expanded = true)
      }
    }
  }
}

@Preview(widthDp = 368, heightDp = 404)
@Composable
private fun InterstitialViewPreview() {
  NoticeView {}
}
