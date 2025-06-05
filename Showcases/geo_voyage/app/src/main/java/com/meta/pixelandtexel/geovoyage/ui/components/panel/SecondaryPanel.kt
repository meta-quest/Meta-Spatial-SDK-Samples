// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.panel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageColors
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalShapes

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
      shape = LocalShapes.current.small,
      color = GeoVoyageColors.textContainer,
      modifier =
          modifier.shadow(
              elevation = 16.dp,
              shape = LocalShapes.current.small,
          )) {
        Box(modifier = Modifier.padding(dimensionResource(R.dimen.standard_margin))) {
          content.invoke()
        }
      }
}

@Preview(widthDp = 600, heightDp = 400)
@Composable
fun SecondaryPanelPreview() {
  GeoVoyageTheme { SecondaryPanel {} }
}
