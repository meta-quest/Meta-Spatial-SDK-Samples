// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

/**
 * GeoVoyage input text field.
 *
 * @param modifier the [Modifier] to be applied to this text field
 */
@Composable
fun TitleBar(
    label: String,
    modifier: Modifier = Modifier,
) {
  ElevatedCard(
      modifier =
          modifier
              .height(100.dp)
              .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(25.dp))
              .padding(0.dp)
              .shadow(
                  elevation = 16.dp,
                  shape = RoundedCornerShape(25.dp),
                  ambientColor = Color(0x40000000),
              )) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()) {
              Text(
                  text = label,
                  style =
                      MaterialTheme.typography.headlineSmall.copy(
                          fontWeight = FontWeight.Normal,
                      ),
                  modifier = Modifier.padding(30.dp, 0.dp).fillMaxWidth())
            }
      }
}

@Preview(showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun PreviewGeoVoyageTextInput() {
  GeoVoyageTheme {
    TitleBar(
        "Speak",
        modifier = Modifier.padding(16.dp),
    )
  }
}
