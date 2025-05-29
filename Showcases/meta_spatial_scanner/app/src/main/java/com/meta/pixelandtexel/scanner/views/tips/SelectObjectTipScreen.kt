// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.tips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.views.components.Panel
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun SelectObjectTipScreen(onDismiss: (() -> Unit)? = null) {
  SpatialTheme {
    Panel {
      Column(
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize()) {
            Text(
                stringResource(R.string.select_object_title),
                color = Color.White,
                style = SpatialTheme.typography.headline3Strong,
                modifier = Modifier.padding(top = 6.dp))
            Text(
                stringResource(R.string.select_object_copy),
                color = Color.White,
                style = SpatialTheme.typography.body2,
                modifier = Modifier.padding(bottom = 8.dp))
            PrimaryButton(
                stringResource(R.string.btn_dismiss),
                expanded = true,
                onClick = { onDismiss?.invoke() })
          }
    }
  }
}

@Preview(widthDp = 368, heightDp = 164)
@Composable
fun SelectObjectTipScreenPreview() {
  SelectObjectTipScreen()
}
