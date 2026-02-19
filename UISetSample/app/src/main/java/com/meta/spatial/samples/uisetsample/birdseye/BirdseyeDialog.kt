/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.birdseye

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.samples.uisetsample.R
import com.meta.spatial.samples.uisetsample.util.view.PanelScaffold
import com.meta.spatial.uiset.dialog.SpatialBasicDialog
import com.meta.spatial.uiset.dialog.SpatialIconDialog
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.Warning

@Composable
fun BirdseyeDialog() {
  PanelScaffold("Dialog") {
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.background(SpatialTheme.colorScheme.hover).fillMaxWidth()) {
      Column {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "  Component",
            style =
                LocalTypography.current.headline1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
          SpatialBasicDialog(
              title = "Title goes here",
              description =
                  "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua temet.",
              primaryChoiceActionLabel = "Label",
              onPrimaryChoiceActionClick = { /* no-op */ },
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
                color = LocalColorScheme.current.primaryAlphaBackground
            ),
    )
    Spacer(modifier = Modifier.height(40.dp))

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
    heightDp = 2000,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdseyeDialogPreview() {
  BirdseyeDialog()
}
