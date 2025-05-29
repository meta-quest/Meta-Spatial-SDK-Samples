// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.R
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ObjectInfoView(
    title: String,
    painter: Painter? = null,
    copy: String? = null,
    onResume: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null
) {
  Column {
    ObjectInfoPanelHeader(title, onResume = onResume, onClose = onClose)
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        thickness = 1.dp,
        color = Color.White)
    ScrollableColumn {
      if (painter != null) {
        Spacer(Modifier.height(24.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Image(
              painter = painter,
              contentDescription = title,
              contentScale = ContentScale.FillWidth,
              modifier =
                  Modifier.fillMaxWidth()
                      .clip(RoundedCornerShape(SpatialTheme.shapes.medium.topEnd)))
        }
      }

      if (copy != null) {
        if (copy.isEmpty()) {
          Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator(
                color = SpatialTheme.colorScheme.progressOnBackground,
                trackColor = SpatialTheme.colorScheme.progressBarOnBackground,
                modifier = Modifier.padding(40.dp))
          }
        } else {
          Spacer(Modifier.height(24.dp))
          MarkdownText(
              copy,
              style = SpatialTheme.typography.body1.merge(TextStyle(color = SpatialColor.white100)))
        }
      }
    }
  }
}

@Preview(widthDp = 592, heightDp = 604, showBackground = true, backgroundColor = 0xFF272727)
@Composable
private fun ObjectInfoViewPreview() {
  SpatialTheme {
    ObjectInfoView(
        "Model: RF32CG5900SR/AA",
        painterResource(R.drawable.fridge_hero),
        "## 30 cu. ft. Mega Capacity\n" +
            "\n" +
            "Store more groceries with more room to stay organized.\n" +
            "\n" +
            "## Features\n" +
            "\n" +
            "- Share pictures, stream music and videos, access recipes, control your smart devices and Alexa all from the fridge.\n" +
            "- Enjoy your favorite beverage with your choice of ice. Choose from cubed ice or Ice Bites from the Dual Auto Ice Maker in the freezer drawer, or choose from curved or crushed ice from the external dispenser.\n" +
            "- A flat-front fridge design with recessed drawer handle blends beautifully into the kitchen.")
  }
}
