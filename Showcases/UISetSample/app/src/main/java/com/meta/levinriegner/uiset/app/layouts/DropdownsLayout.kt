// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.spatial.uiset.dropdown.SpatialDropdown
import com.meta.spatial.uiset.dropdown.foundation.SpatialDropdownItem
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll

@Composable
fun DropdownsLayout() {
  return PanelScaffold("Dropdown") {
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Standard",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(24.dp))
        Box {
          val items = (1..3).map { index -> SpatialDropdownItem(title = "Title $index") }
          var selectedItem by remember { mutableStateOf<SpatialDropdownItem?>(null) }
          SpatialDropdown(
              title = "Select",
              items = items,
              selectedItem = selectedItem,
              onItemSelected = { selectedItem = it },
          )
        }
        Spacer(Modifier.size(38.dp))
        Box {
          val items = (1..3).map { index -> SpatialDropdownItem(title = "Title $index") }
          var selectedItem by remember { mutableStateOf<SpatialDropdownItem?>(items[0]) }
          SpatialDropdown(
              title = "Title",
              items = items,
              selectedItem = selectedItem,
              onItemSelected = { selectedItem = it },
          )
        }
      }
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Standard with Icon",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(24.dp))
        Box {
          val items = (1..3).map { index -> SpatialDropdownItem(title = "Title $index") }
          var selectedItem by remember { mutableStateOf<SpatialDropdownItem?>(null) }
          SpatialDropdown(
              title = "Select",
              leading = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
              items = items,
              selectedItem = selectedItem,
              onItemSelected = { selectedItem = it },
          )
        }
        Spacer(Modifier.size(38.dp))
        Box {
          val items = (1..3).map { index -> SpatialDropdownItem(title = "Title $index") }
          var selectedItem by remember { mutableStateOf<SpatialDropdownItem?>(items[0]) }
          SpatialDropdown(
              title = "Title",
              leading = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
              items = items,
              selectedItem = selectedItem,
              onItemSelected = { selectedItem = it },
          )
        }
      }
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Rounded Standard",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(24.dp))
        Box {
          val items =
              (1..3).map { index ->
                SpatialDropdownItem(title = "Title $index", subtitle = "Subtitle")
              }
          var selectedItem by remember { mutableStateOf<SpatialDropdownItem?>(null) }
          SpatialDropdown(
              title = "Select",
              subtitle = "Subtitle",
              items = items,
              selectedItem = selectedItem,
              onItemSelected = { selectedItem = it },
          )
        }
        Spacer(Modifier.size(30.dp))
        Box {
          val items =
              (1..3).map { index ->
                SpatialDropdownItem(title = "Title $index", subtitle = "Subtitle")
              }
          var selectedItem by remember { mutableStateOf<SpatialDropdownItem?>(items[0]) }
          SpatialDropdown(
              title = "Title",
              subtitle = "Subtitle",
              items = items,
              selectedItem = selectedItem,
              onItemSelected = { selectedItem = it },
          )
        }
      }
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Rounded with Icon",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(24.dp))
        Box {
          val items =
              (1..3).map { index ->
                SpatialDropdownItem(title = "Title $index", subtitle = "Subtitle")
              }
          var selectedItem by remember { mutableStateOf<SpatialDropdownItem?>(null) }
          SpatialDropdown(
              title = "Select",
              subtitle = "Subtitle",
              leading = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
              items = items,
              selectedItem = selectedItem,
              onItemSelected = { selectedItem = it },
          )
        }
        Spacer(Modifier.size(30.dp))
        Box {
          val items =
              (1..3).map { index ->
                SpatialDropdownItem(title = "Title $index", subtitle = "Subtitle")
              }
          var selectedItem by remember { mutableStateOf<SpatialDropdownItem?>(items[0]) }
          SpatialDropdown(
              title = "Title",
              subtitle = "Subtitle",
              leading = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
              items = items,
              selectedItem = selectedItem,
              onItemSelected = { selectedItem = it },
          )
        }
      }
      // Rationale
      Column {
        Text(
            text = "",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(38.dp))
        Text(
            text = "Context Menu Default",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
        Spacer(Modifier.size(65.dp))
        Text(
            text = "Context Menu Selected",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground),
        )
      }
    }
  }
}

@Preview(
    widthDp = 1300,
    heightDp = 443,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun DropdownsLayoutPreview() {
  DropdownsLayout()
}
