// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalShapes

/**
 * Primary GeoVoyage panel to display content.
 *
 * @param content Content to display within the [PrimaryPanel]
 */
@Composable
fun PrimaryPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
  Box(
      modifier =
          modifier
              .clip(LocalShapes.current.large)
              .background(brush = LocalColorScheme.current.panel, shape = LocalShapes.current.large)
              .padding(dimensionResource(R.dimen.standard_margin))
  ) {
    content.invoke()
  }
}

@Preview(widthDp = 600, heightDp = 400)
@Composable
fun PreviewGeoVoyagePrimaryPanel() {
  GeoVoyageTheme { PrimaryPanel {} }
}
