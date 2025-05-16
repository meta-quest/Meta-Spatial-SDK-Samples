// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.birdseye

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll

@Composable
fun BirdseyeTextTileButton() {
  PanelScaffold("Text Tile Button") {
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.background(SpatialTheme.colorScheme.hover).fillMaxWidth()) {
      Column {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "  Component",
            style =
                LocalTypography.current.headline1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground))
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
          TextTileButton(
              label = "Label",
              onSelectionChange = { /* no-op */ },
              selected = false,
          )
        }
        Spacer(modifier = Modifier.height(20.dp))
      }
      Spacer(modifier = Modifier.height(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        "  Component Definitions & Variations",
        style =
            LocalTypography.current.headline1Strong.copy(
                color = LocalColorScheme.current.primaryAlphaBackground))
    Spacer(modifier = Modifier.height(40.dp))
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
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
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
        Spacer(Modifier.size(24.dp))
        TextTileButton(
            label = "Label",
            onSelectionChange = { /* no-op */ },
            enabled = false,
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
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
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
        Spacer(Modifier.size(24.dp))
        TextTileButton(
            label = "Label",
            secondaryLabel = "Secondary",
            onSelectionChange = { /* no-op */ },
            enabled = false,
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
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
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
        Spacer(Modifier.size(24.dp))
        TextTileButton(
            label = "Label",
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onSelectionChange = { /* no-op */ },
            enabled = false,
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
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
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
        Spacer(Modifier.size(24.dp))
        TextTileButton(
            label = "Label",
            secondaryLabel = "Secondary",
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onSelectionChange = { /* no-op */ },
            enabled = false,
        )
      }

      // Rationale
      Column {
        Text(
            text = "",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(5.dp))
        Column(
            modifier = Modifier.width(59.dp).height(370.dp),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
          Text(
              text = "Default",
              style =
                  SpatialTheme.typography.body1.copy(
                      color = SpatialTheme.colorScheme.primaryAlphaBackground),
          )
          Text(
              text = "Selected",
              style =
                  SpatialTheme.typography.body1.copy(
                      color = SpatialTheme.colorScheme.primaryAlphaBackground),
          )
          Text(
              text = "Disabled",
              style =
                  SpatialTheme.typography.body1.copy(
                      color = SpatialTheme.colorScheme.primaryAlphaBackground),
          )
        }
      }
    }
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 1700,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdseyeTextTileButtonPreview() {
  BirdseyeTextTileButton()
}
