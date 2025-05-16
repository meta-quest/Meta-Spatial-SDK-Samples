// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.layouts

// import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.CameraRollFilled
// import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.Info
// import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.MicrophoneOn
// import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.Notifications
// import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.People
// import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.StoreCart
// import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.Time
// import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.Trash
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
import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.CategoryBasic
import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.CheckCircle
import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.Destination
import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.DoNotDisturbOn
import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.HeartOn
import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.People2
import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.StarFull
import com.meta.levinriegner.uiset.app.layouts.ButtonShelvesItem.Warning
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.levinriegner.uiset.app.util.view.StatefulWrapper
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
