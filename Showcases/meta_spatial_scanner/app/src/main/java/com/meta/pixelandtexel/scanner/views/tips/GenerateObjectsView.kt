// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.tips

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.R
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun GenerateObjectsView(
    titleCopy: String,
    bodyCopy: String,
    onGenerate: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Image(
        painter = painterResource(R.drawable.tip_refrigerator),
        contentDescription = stringResource(R.string.refrigerator_image_description),
        modifier = Modifier.fillMaxWidth(),
        colorFilter = ColorFilter.tint(Color(0x88000000), BlendMode.Darken),
    )
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f).fillMaxWidth().padding(20.dp),
    ) {
      Text(
          titleCopy,
          color = Color.White,
          style = SpatialTheme.typography.headline3Strong,
          modifier = Modifier.padding(top = 6.dp),
      )
      Text(
          bodyCopy,
          textAlign = TextAlign.Center,
          color = Color.White,
          style = SpatialTheme.typography.body2,
          modifier = Modifier.padding(bottom = 8.dp),
      )
      PrimaryButton(
          stringResource(R.string.btn_dismiss),
          expanded = true,
          onClick = { onDismiss?.invoke() },
      )
      PrimaryButton(
          stringResource(R.string.btn_generate_objects),
          expanded = true,
          onClick = { onGenerate?.invoke() },
      )
    }
  }
}

@Preview(widthDp = 368, heightDp = 440, showBackground = true, backgroundColor = 0xFF272727)
@Composable
fun GenerateObjectsViewPreview() {
  GenerateObjectsView(
      "Title Copy",
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc porttitor egestas libero, at egestas lacus sollicitudin nec.",
  )
}
