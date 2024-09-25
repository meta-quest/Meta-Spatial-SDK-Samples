// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

/**
 * Primary GeoVoyage panel to display content.
 *
 * @param content Content to display within the [PrimaryPanel]
 */
@Composable
fun PrimaryPanel(
    content: @Composable () -> Unit,
) {
  Surface(
      shape = RoundedCornerShape(26.dp),
      color = MaterialTheme.colorScheme.surfaceContainer,
  ) {
    Column { content.invoke() }
  }
}

@Preview(widthDp = 800, heightDp = 600)
@Composable
fun PreviewGeoVoyagePrimaryPanel() {
  GeoVoyageTheme { PrimaryPanel {} }
}
