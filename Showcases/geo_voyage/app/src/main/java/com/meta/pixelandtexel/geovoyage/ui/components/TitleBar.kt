// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageColors
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.spatial.uiset.theme.LocalTypography

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
  Box(
      modifier =
          modifier
              .shadow(
                  elevation = 16.dp,
                  shape = LocalShapes.current.small,
              )
              .background(GeoVoyageColors.navContainer, LocalShapes.current.small)
              .height(48.dp)) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()) {
              Text(
                  text = label,
                  style =
                      LocalTypography.current.headline3.copy(
                          fontWeight = FontWeight.Normal,
                      ),
                  modifier = Modifier.padding(24.dp, 12.dp).fillMaxWidth())
            }
      }
}

@Preview(showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun PreviewGeoVoyageTextInput() {
  GeoVoyageTheme { TitleBar("Speak") }
}
