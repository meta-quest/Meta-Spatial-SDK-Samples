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
import com.meta.spatial.uiset.button.ButtonShelf
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll

@Composable
fun BirdseyeButtonShelf() {
  PanelScaffold("Button Shelf") {
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
          ButtonShelf(
              icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
              label = "Label",
              onSelectionChange = {},
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
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Default",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(29.dp))
        ButtonShelf(
            icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
            label = "Label",
            onSelectionChange = {},
        )
      }

      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Selected",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(29.dp))
        ButtonShelf(
            icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
            label = "Label",
            selected = true,
            onSelectionChange = {},
        )
      }

      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Disabled",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(29.dp))
        ButtonShelf(
            icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
            label = "Label",
            enabled = false,
            onSelectionChange = {},
        )
      }

      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Selected Disabled",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(29.dp))
        ButtonShelf(
            icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
            label = "Label",
            enabled = false,
            selected = true,
            onSelectionChange = {},
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
            modifier = Modifier.width(100.dp).height(110.dp),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
          Text(
              text = "Label with Icon",
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
    heightDp = 2000,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdseyeButtonShelfPreview() {
  BirdseyeButtonShelf()
}
