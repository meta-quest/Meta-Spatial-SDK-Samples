// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.control.SpatialRadioButton
import com.meta.spatial.uiset.theme.LocalTypography

@Composable
fun SettingsRadioButtonGroupListItem(
    headlineText: String,
    selectedIdx: MutableIntState,
    options: List<String>,
    onOptionSelected: (idx: Int) -> Unit,
) {
  Column(verticalArrangement = Arrangement.SpaceBetween) {
    Row(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp).fillMaxWidth()) {
      Text(text = headlineText, style = LocalTypography.current.headline3)
    }
    options.forEachIndexed { index, label ->
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.padding(24.dp, 8.dp).fillMaxWidth(),
      ) {
        Text(label, style = LocalTypography.current.body1)
        SpatialRadioButton(
            selected = index == selectedIdx.intValue,
            onClick = {
              selectedIdx.intValue = index
              onOptionSelected(index)
            },
        )
      }
    }
  }
  HorizontalDivider(color = Color.White)
}
