// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.objectinfo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.scanner.models.ImageCopyPanelContent
import com.meta.pixelandtexel.scanner.views.components.ObjectInfoView
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun CuratedObjectImageCopyPanel(
    content: ImageCopyPanelContent,
    onResume: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
  val painter = if (content.imageResId != null) painterResource(content.imageResId) else null
  ObjectInfoView(content.title, painter, content.copy, onResume, onClose)
}

@Preview(widthDp = 592, heightDp = 604, showBackground = true, backgroundColor = 0xFF272727)
@Composable
private fun CuratedObjectTilesPanelPreview() {
  SpatialTheme {
    CuratedObjectImageCopyPanel(
        ImageCopyPanelContent(
            "Model: RF32CG5900SR/AA",
            null,
            null,
            "## 30 cu. ft. Mega Capacity\n" +
                "\n" +
                "Store more groceries with more room to stay organized.\n" +
                "\n" +
                "## Features\n" +
                "\n" +
                "- Share pictures, stream music and videos, access recipes, control your smart devices and Alexa all from the fridge.\n" +
                "- Enjoy your favorite beverage with your choice of ice. Choose from cubed ice or Ice Bites from the Dual Auto Ice Maker in the freezer drawer, or choose from curved or crushed ice from the external dispenser.\n" +
                "- A flat-front fridge design with recessed drawer handle blends beautifully into the kitchen.",
        )
    )
  }
}
