// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.tips

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.views.components.Panel
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun HelpScreen(
    onGenerate: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
  SpatialTheme {
    Panel(outerPadding = false) {
      GenerateObjectsView(
          stringResource(R.string.help_title),
          stringResource(R.string.help_body),
          onGenerate,
          onDismiss,
      )
    }
  }
}

@Preview(widthDp = 368, heightDp = 440)
@Composable
fun HelpScreenPreview() {
  HelpScreen()
}
