// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.panel.PanelNavigator
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.spatial.uiset.button.TextTileButton
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll

@Composable
fun NavigationView(
    panelNavigator: PanelNavigator,
    initialView: NavigationUiItem = NavigationUiItem.Button,
) {
  val allViewIds = NavigationUiItem.entries.map { it.panelRegistrationIds }.flatten()
  // Open initial view
  LaunchedEffect(initialView) {
    panelNavigator.setPanelsVisible(initialView.panelRegistrationIds, allViewIds)
  }
  // State
  var selectedView by remember { mutableStateOf(initialView) }
  // UI
  return PanelScaffold(padding = PaddingValues(36.dp)) {
    Column {
      NavigationUiSection.entries.forEach { section ->
        Text(
            section.displayText,
            style =
                SpatialTheme.typography.headline2Strong.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 3 columns
            userScrollEnabled = false,
        ) {
          val items = NavigationUiItem.entries.filter { it.section == section }
          itemsIndexed(items) { index, item ->
            TextTileButton(
                modifier =
                    Modifier.padding(
                        end = if (index % 3 == 2) 0.dp else 12.dp,
                        bottom = if (index >= items.size - 3) 0.dp else 12.dp,
                    ),
                icon = { Icon(SpatialIcons.Regular.CategoryAll, contentDescription = "") },
                label = item.label,
                secondaryLabel = item.secondaryLabel,
                onSelectionChange = { selected ->
                  if (selected) {
                    panelNavigator.setPanelsVisible(
                        item.panelRegistrationIds,
                        allViewIds.filter { it !in item.panelRegistrationIds },
                    )
                    selectedView = item
                  }
                },
                selected = selectedView == item,
            )
          }
        }
        Spacer(Modifier.size(24.dp))
      }
    }
  }
}
