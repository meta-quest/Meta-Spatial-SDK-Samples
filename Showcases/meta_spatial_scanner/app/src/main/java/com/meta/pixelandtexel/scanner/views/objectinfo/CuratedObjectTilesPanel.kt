// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.objectinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.models.TileContent
import com.meta.pixelandtexel.scanner.models.TilesPanelContent
import com.meta.pixelandtexel.scanner.views.components.ObjectInfoPanelHeader
import com.meta.pixelandtexel.scanner.views.components.ObjectInfoTile
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun CuratedObjectTilesPanel(
    content: TilesPanelContent,
    tileSelected: ((Int) -> Unit)? = null,
    onResume: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
  Column {
    ObjectInfoPanelHeader(content.title, onResume = onResume, onClose = onClose)
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        thickness = 1.dp,
        color = Color.White,
    )
    Spacer(Modifier.height(24.dp))
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
      itemsIndexed(content.tiles) { i, tileContent ->
        val painter =
            if (tileContent.imageResId != null) painterResource(tileContent.imageResId) else null
        ObjectInfoTile(tileContent.title, tileContent.subTitle, painter) { tileSelected?.invoke(i) }
      }
    }
  }
}

@Preview(widthDp = 592, heightDp = 604, showBackground = true, backgroundColor = 0xFF272727)
@Composable
private fun CuratedObjectTilesPanelPreview() {
  SpatialTheme {
    CuratedObjectTilesPanel(
        TilesPanelContent(
            "Refrigerator",
            null,
            listOf(
                TileContent("How To:", "Troubleshoot Wifi Connection", R.drawable.fridge_light),
                TileContent("How To:", "Connect Your Apps", R.drawable.tv_apps),
                TileContent("How To:", "Prepare for Wall Mounting", R.drawable.tv_mount),
                TileContent("How To:", "Connect a Sound System", R.drawable.tv_sound),
            ),
        ))
  }
}
