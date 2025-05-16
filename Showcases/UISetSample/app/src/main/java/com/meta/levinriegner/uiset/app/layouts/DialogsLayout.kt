// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.R
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.spatial.uiset.dialog.SpatialBasicDialog
import com.meta.spatial.uiset.dialog.SpatialIconDialog
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.Warning

@Composable
fun DialogsLayout() {
  PanelScaffold("Dialog") {
    Column(
        Modifier.padding(24.dp),
    ) {
      Row {
        SpatialBasicDialog(
            title = "Title goes here",
            description =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua temet.",
            primaryChoiceActionLabel = "Label",
            onPrimaryChoiceActionClick = { /* no-op */ },
        )
        Spacer(Modifier.width(76.dp))
        SpatialIconDialog(
            icon = {
              Icon(
                  SpatialIcons.Regular.Warning,
                  "",
                  tint = SpatialColor.y60,
                  modifier = Modifier.size(40.dp),
              )
            },
            title = "Title goes here",
            description =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua temet.",
            primaryChoiceActionLabel = "Label",
            onPrimaryChoiceActionClick = { /* no-op */ },
        )
        Spacer(Modifier.width(76.dp))
        SpatialBasicDialog(
            thumbnail = {
              Image(
                  painterResource(R.drawable.sample_thumbnail),
                  "",
                  contentScale = ContentScale.FillBounds,
              )
            },
            title = "Title goes here",
            description =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua temet.",
            primaryChoiceActionLabel = "Label",
            onPrimaryChoiceActionClick = { /* no-op */ },
        )
      }
    }
    Spacer(Modifier.height(76.dp))
    Row {
      SpatialBasicDialog(
          title = "Title goes here",
          description =
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua temet.",
          primaryChoiceActionLabel = "Label",
          onPrimaryChoiceActionClick = { /* no-op */ },
          secondaryChoiceActionLabel = "Label",
          onSecondaryChoiceActionClick = { /* no-op */ },
      )
      Spacer(Modifier.width(76.dp))
      SpatialIconDialog(
          icon = {
            Icon(
                SpatialIcons.Regular.Warning,
                "",
                tint = SpatialColor.y60,
                modifier = Modifier.size(40.dp),
            )
          },
          title = "Title goes here",
          description =
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua temet.",
          primaryChoiceActionLabel = "Label",
          onPrimaryChoiceActionClick = { /* no-op */ },
          secondaryChoiceActionLabel = "Label",
          onSecondaryChoiceActionClick = { /* no-op */ },
      )
      Spacer(Modifier.width(76.dp))
      SpatialBasicDialog(
          thumbnail = {
            Image(
                painterResource(R.drawable.sample_thumbnail),
                "",
                contentScale = ContentScale.FillBounds,
            )
          },
          title = "Title goes here",
          description =
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua temet.",
          primaryChoiceActionLabel = "Label",
          onPrimaryChoiceActionClick = { /* no-op */ },
          secondaryChoiceActionLabel = "Label",
          onSecondaryChoiceActionClick = { /* no-op */ },
      )
    }
  }
}

@Preview(
    widthDp = 1400,
    heightDp = 1263,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun DialogsLayoutPreview() {
  DialogsLayout()
}
