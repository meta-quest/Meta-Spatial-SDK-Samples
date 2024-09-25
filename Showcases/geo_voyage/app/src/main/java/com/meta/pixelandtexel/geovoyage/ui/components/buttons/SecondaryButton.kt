// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
  OutlinedButton(
      onClick = onClick,
      border = BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary),
      modifier =
          modifier
              .shadow(
                  elevation = 20.dp,
                  spotColor = Color(0x40000000),
                  ambientColor = Color(0x40000000))
              .width(350.dp)
              .height(100.dp)
              .background(
                  color = MaterialTheme.colorScheme.primaryContainer,
                  shape = RoundedCornerShape(size = 50.dp)),
      enabled = enabled) {
        Text(text = text, color = color, style = MaterialTheme.typography.headlineSmall)
      }
}

@Preview
@Composable
private fun PreviewSecondaryButton() {
  GeoVoyageTheme { SecondaryButton(text = "Secondary Button") {} }
}
