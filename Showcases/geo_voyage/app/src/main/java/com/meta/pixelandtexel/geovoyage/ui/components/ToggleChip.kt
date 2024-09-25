// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

@Composable
fun ToggleChip(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    enabled: Boolean = true,
    text: String = "",
    onClick: () -> Unit
) {
  FilterChip(
      selected = selected,
      enabled = enabled,
      onClick = onClick,
      modifier = modifier.width(232.dp).height(68.dp),
      label = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()) {
              Text(text = text, style = MaterialTheme.typography.headlineSmall)
            }
      },
      leadingIcon =
          if (selected) {
            {
              Icon(
                  imageVector = Icons.Filled.Check,
                  contentDescription = "Done icon",
                  modifier = Modifier.size(40.dp))
            }
          } else {
            null
          },
      trailingIcon =
          if (selected) {
            { Spacer(modifier = Modifier.width(20.dp)) }
          } else {
            null
          },
      colors =
          FilterChipDefaults.filterChipColors()
              .copy(
                  containerColor = Color.Transparent,
                  labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                  selectedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                  selectedLabelColor = Color.White,
                  selectedLeadingIconColor = Color.White,
                  disabledLabelColor = Color.White,
                  disabledContainerColor = Color(0x664D644F),
                  disabledSelectedContainerColor = MaterialTheme.colorScheme.tertiary),
      border =
          FilterChipDefaults.filterChipBorder(
              enabled = enabled,
              selected = selected,
              borderColor = MaterialTheme.colorScheme.onPrimaryContainer))
}

@Preview
@Composable
private fun ToggleChipPreview() {
  GeoVoyageTheme { ToggleChip(selected = false, enabled = true, text = "Unselected") {} }
}

@Preview
@Composable
private fun ToggleChipPreviewSelected() {
  GeoVoyageTheme { ToggleChip(selected = true, enabled = true, text = "Selected") {} }
}

@Preview
@Composable
private fun ToggleChipPreviewDisabled() {
  GeoVoyageTheme { ToggleChip(selected = false, enabled = false, text = "Disabled") {} }
}
