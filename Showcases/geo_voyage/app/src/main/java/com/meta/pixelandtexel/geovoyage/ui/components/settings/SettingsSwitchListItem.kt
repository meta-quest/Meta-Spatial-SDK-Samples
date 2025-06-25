// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.control.SpatialSwitch
import com.meta.spatial.uiset.theme.LocalTypography

@Composable
fun SettingsSwitchListItem(
    text: String,
    enabled: MutableState<Boolean>,
    onSettingChanged: (newValue: Boolean) -> Unit
) {
  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(12.dp, 0.dp).fillMaxWidth()) {
        Text(
            text = text,
            style = LocalTypography.current.headline3,
        )
        SpatialSwitch(
            checked = enabled.value,
            onCheckedChange = { checked ->
              enabled.value = checked
              onSettingChanged(checked)
            })
      }
  HorizontalDivider(color = Color.White)
}
