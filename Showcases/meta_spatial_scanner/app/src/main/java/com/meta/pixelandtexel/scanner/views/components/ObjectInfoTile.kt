// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.R
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun ObjectInfoTile(
    title: String,
    subTitle: String = "",
    painter: Painter? = null,
    onClick: () -> Unit,
) {
  Button(
      onClick = onClick,
      shape = SpatialTheme.shapes.large,
      contentPadding = PaddingValues(0.dp),
      colors =
          ButtonColors(
              containerColor = Color.Black,
              contentColor = Color.White,
              disabledContainerColor = Color.Black,
              disabledContentColor = Color.White,
          ),
      modifier = Modifier.padding(0.dp).fillMaxWidth().aspectRatio(1.333f),
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      if (painter != null) {
        Image(
            painter = painter,
            contentDescription = "Button tile $title",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
      }
      Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
        Text(
            title,
            color = SpatialColor.white100,
            style =
                SpatialTheme.typography.body1Strong.merge(
                    TextStyle(shadow = Shadow(blurRadius = 4.dp.value))
                ),
        )

        Spacer(Modifier.height(2.dp))
        Text(
            subTitle,
            color = SpatialColor.gray30,
            style =
                SpatialTheme.typography.body2.merge(
                    TextStyle(shadow = Shadow(blurRadius = 4.dp.value))
                ),
        )
      }
    }
  }
}

@Preview(widthDp = 253, heightDp = 190)
@Composable
private fun ObjectInfoTilePreview() {
  ObjectInfoTile("How To:", "Connect Your Apps", painterResource(R.drawable.tv_apps)) {}
}
