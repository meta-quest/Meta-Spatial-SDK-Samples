// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.panel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Secondary GeoVoyage panel to display content. To be used within Primary GeoVoyage Panel.
 *
 * @param content Content to display within the [SecondaryPanel]
 */
@Composable
fun SecondaryPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
  Surface(
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.primaryContainer,
      modifier =
          modifier.shadow(
              elevation = 16.dp,
              shape = RoundedCornerShape(8.dp),
              ambientColor = Color(0x00000040),
          ),
  ) {
    Box(modifier = Modifier.padding(26.dp)) { content.invoke() }
  }
}
