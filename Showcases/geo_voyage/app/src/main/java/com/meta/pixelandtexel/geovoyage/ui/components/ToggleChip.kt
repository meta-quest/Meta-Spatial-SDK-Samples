// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.button.SecondaryButton
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CheckAlt

@Composable
fun ToggleChip(
    selected: Boolean = true,
    enabled: Boolean = true,
    text: String = "",
    onClick: () -> Unit,
) {
  if (enabled && !selected) {
    SecondaryButton(label = text, leading = null, isEnabled = true, onClick = onClick)
  } else {
    PrimaryButton(
        label = text,
        leading =
            if (selected) {
              {
                Icon(
                    imageVector = SpatialIcons.Regular.CheckAlt,
                    contentDescription = "Done icon",
                )
              }
            } else {
              null
            },
        isEnabled = enabled,
        onClick = onClick,
    )
  }
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
