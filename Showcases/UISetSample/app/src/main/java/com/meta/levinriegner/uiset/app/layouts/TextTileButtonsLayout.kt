// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.spatial.uiset.button.TextTileButton
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll

@Composable
fun TextTileButtonsLayout() {
  return PanelScaffold("Text Tile Button") {
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
      // Primary
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Primary",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(29.dp))
        TextTileButton(
            label = "Label",
            onSelectionChange = { /* no-op */ },
            selected = false,
        )
        Spacer(Modifier.size(24.dp))
        TextTileButton(
            label = "Label",
            onSelectionChange = { /* no-op */ },
            selected = true,
        )
      }
      // Primary + Secondary
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Primary + Secondary",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(29.dp))
        TextTileButton(
            label = "Label",
            secondaryLabel = "Secondary",
            onSelectionChange = { /* no-op */ },
            selected = false,
        )
        Spacer(Modifier.size(24.dp))
        TextTileButton(
            label = "Label",
            secondaryLabel = "Secondary",
            onSelectionChange = { /* no-op */ },
            selected = true,
        )
      }
      // With Icon
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "With Icon",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(29.dp))
        TextTileButton(
            label = "Label",
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onSelectionChange = { /* no-op */ },
            selected = false,
        )
        Spacer(Modifier.size(24.dp))
        TextTileButton(
            label = "Label",
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onSelectionChange = { /* no-op */ },
            selected = true,
        )
      }
      // Secondary with Icon
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Secondary with Icon",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(29.dp))
        TextTileButton(
            label = "Label",
            secondaryLabel = "Secondary",
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onSelectionChange = { /* no-op */ },
            selected = false,
        )
        Spacer(Modifier.size(24.dp))
        TextTileButton(
            label = "Label",
            secondaryLabel = "Secondary",
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onSelectionChange = { /* no-op */ },
            selected = true,
        )
      }

      // Rationale
      Column {
        Text(
            text = "",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(5.dp))
        Column(
            modifier = Modifier.width(59.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
          Text(
              text = "Default",
              style =
                  SpatialTheme.typography.body1.copy(
                      color = SpatialTheme.colorScheme.primaryAlphaBackground
                  ),
          )
          Text(
              text = "Selected",
              style =
                  SpatialTheme.typography.body1.copy(
                      color = SpatialTheme.colorScheme.primaryAlphaBackground
                  ),
          )
        }
      }
    }
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 558,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun TextTileButtonsLayoutPreview() {
  TextTileButtonsLayout()
}
