// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.askearth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.SecondaryButton
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun PermissionsScreen(
    onEnableClicked: () -> Unit,
    onNotNowClicked: () -> Unit,
) {
  val noticeText = stringResource(id = R.string.permissions)

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        SecondaryPanel(modifier = Modifier.fillMaxWidth().height(510.dp)) {
          Column(
              modifier = Modifier.fillMaxSize(),
          ) {
            MarkdownText(
                markdown = noticeText,
                modifier = Modifier.selectable(false, false) {}.weight(1f),
                style = MaterialTheme.typography.headlineSmall)
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
              SecondaryButton(text = "Not now", onClick = onNotNowClicked)
              PrimaryButton(
                  text = "Enable",
                  modifier = Modifier.padding(start = 26.dp),
                  onClick = onEnableClicked)
            }
          }
        }
      }
}

@Preview(widthDp = 932, heightDp = 650, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
fun PermissionsScreenPreview() {
  GeoVoyageTheme { PermissionsScreen({}, {}) }
}
