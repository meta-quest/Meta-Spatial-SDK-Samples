/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesItem.CategoryBasic
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesItem.CheckCircle
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesItem.Destination
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesItem.DoNotDisturbOn
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesItem.HeartOn
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesItem.People2
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesItem.StarFull
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesItem.Warning
import com.meta.spatial.samples.uisetsample.util.view.PanelScaffold
import com.meta.spatial.samples.uisetsample.util.view.StatefulWrapper
import com.meta.spatial.uiset.button.ButtonShelf
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll
import com.meta.spatial.uiset.theme.icons.regular.CheckCircle
import com.meta.spatial.uiset.theme.icons.regular.DoNotDisturb
import com.meta.spatial.uiset.theme.icons.regular.HeartOn
import com.meta.spatial.uiset.theme.icons.regular.SidebarPin
import com.meta.spatial.uiset.theme.icons.regular.StarFull
import com.meta.spatial.uiset.theme.icons.regular.ThreePeople
import com.meta.spatial.uiset.theme.icons.regular.Warning

@Composable
fun ButtonShelvesLayout() {

  @Composable
  fun getIcon(item: ButtonShelvesItem) =
      when (item) {
        CheckCircle -> SpatialIcons.Regular.CheckCircle
        CategoryBasic -> SpatialIcons.Regular.CategoryAll
        Warning -> SpatialIcons.Regular.Warning
        DoNotDisturbOn -> SpatialIcons.Regular.DoNotDisturb
        // CameraRollFilled -> SpatialIcons.Regular.CameraRoll
        // MicrophoneOn -> SpatialIcons.Regular.MicrophoneOn
        // People -> SpatialIcons.Regular.ThreePeople
        // Time -> SpatialIcons.Regular.Time

        Destination -> SpatialIcons.Regular.SidebarPin
        HeartOn -> SpatialIcons.Regular.HeartOn
        StarFull -> SpatialIcons.Regular.StarFull
        People2 -> SpatialIcons.Regular.ThreePeople
      // StoreCart -> SpatialIcons.Regular.StoreCart
      // Notifications -> SpatialIcons.Regular.Notifications
      // Info -> SpatialIcons.Regular.Info
      // Trash -> SpatialIcons.Regular.Trash
      }
  return PanelScaffold("Button Shelf") {
    Column(
        Modifier.fillMaxWidth().padding(24.dp),
    ) {
      Row {
        // List all items
        ButtonShelvesItem.entries.take(4).forEachIndexed { index, item ->
          StatefulWrapper(initialValue = false) { isSelected, onSelectionChange ->
            ButtonShelf(
                modifier = Modifier.padding(end = if (index == 7) 0.dp else 26.dp),
                icon = { Icon(getIcon(item), contentDescription = "") },
                label = "Label",
                onSelectionChange = onSelectionChange,
                selected = isSelected,
            )
          }
        }
      }
      Spacer(Modifier.size(26.dp))
      Row {
        ButtonShelvesItem.entries.takeLast(4).forEachIndexed { index, item ->
          StatefulWrapper(initialValue = false) { isSelected, onSelectionChange ->
            ButtonShelf(
                modifier = Modifier.padding(end = if (index == 7) 0.dp else 26.dp),
                icon = { Icon(getIcon(item), contentDescription = "") },
                label = "Label",
                onSelectionChange = onSelectionChange,
                selected = isSelected,
            )
          }
        }
      }
    }
  }
}

private enum class ButtonShelvesItem {
  CheckCircle,
  CategoryBasic,
  Warning,
  DoNotDisturbOn,
  // CameraRollFilled,
  // MicrophoneOn,
  // People,
  // Time,
  Destination,
  HeartOn,
  StarFull,
  People2,
  // StoreCart,
  // Notifications,
  // Info,
  // Trash,

}

@Preview(
    widthDp = 630,
    heightDp = 480,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun ButtonShelvesLayoutPreview() {
  ButtonShelvesLayout()
}
