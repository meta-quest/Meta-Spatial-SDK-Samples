/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.birdseye

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.samples.uisetsample.util.view.PanelScaffold
import com.meta.spatial.uiset.button.BorderlessButton
import com.meta.spatial.uiset.button.BorderlessCircleButton
import com.meta.spatial.uiset.button.BorderlessIconButton
import com.meta.spatial.uiset.button.DestructiveButton
import com.meta.spatial.uiset.button.DestructiveCircleButton
import com.meta.spatial.uiset.button.DestructiveIconButton
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.button.PrimaryCircleButton
import com.meta.spatial.uiset.button.PrimaryIconButton
import com.meta.spatial.uiset.button.SecondaryButton
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.spatial.uiset.button.SecondaryIconButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll
import com.meta.spatial.uiset.theme.icons.regular.Chat
import com.meta.spatial.uiset.theme.icons.regular.MoreHorizontal

@Composable
fun BirdseyeButtons() {
  PanelScaffold("Buttons") {
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
          PrimaryButton(
              label = "Label",
              leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
              onClick = { /* no-op */ },
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
    // Primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.width(20.dp))
      PrimaryButton(
          label = "Label",
          onClick = { /* no-op */ },
      )
      PrimaryButton(
          label = "Label",
          leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
          onClick = { /* no-op */ },
      )
      PrimaryCircleButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
          onClick = { /* no-op */ },
      )
      PrimaryIconButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
          onClick = { /* no-op */ },
      )
      Column(modifier = Modifier.width(60.dp)) {
        Text(
            "Default",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Column(modifier = Modifier.width(300.dp)) {
        Text(
            "Primary Buttons ",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Text(
            "Use it to contain a high-emphasis action.",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.width(20.dp))
      PrimaryButton(
          label = "Label",
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      PrimaryButton(
          label = "Label",
          leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      PrimaryCircleButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      PrimaryIconButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      Column(modifier = Modifier.width(60.dp)) {
        Text(
            "Disabled",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Column(modifier = Modifier.width(300.dp)) {}

      Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
    Spacer(modifier = Modifier.height(20.dp))
    // Secondary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.width(20.dp))
      SecondaryButton(
          label = "Label",
          onClick = { /* no-op */ },
      )
      SecondaryButton(
          label = "Label",
          leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
          onClick = { /* no-op */ },
      )
      SecondaryCircleButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
          onClick = { /* no-op */ },
      )
      SecondaryIconButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
          onClick = { /* no-op */ },
      )
      Column(modifier = Modifier.width(60.dp)) {
        Text(
            "Default",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Column(modifier = Modifier.width(300.dp)) {
        Text(
            "Secondary Buttons ",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Text(
            "Use a secondary button for medium-emphasis action on a surface.",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.width(20.dp))
      SecondaryButton(
          label = "Label",
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      SecondaryButton(
          label = "Label",
          leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      SecondaryCircleButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      SecondaryIconButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      Column(modifier = Modifier.width(60.dp)) {
        Text(
            "Disabled",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Column(modifier = Modifier.width(300.dp)) {}

      Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
    Spacer(modifier = Modifier.height(20.dp))
    // Borderless
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.width(20.dp))
      BorderlessButton(
          label = "Label",
          onClick = { /* no-op */ },
      )
      BorderlessButton(
          label = "Label",
          leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
          onClick = { /* no-op */ },
      )
      BorderlessCircleButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
          onClick = { /* no-op */ },
      )
      BorderlessIconButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
          onClick = { /* no-op */ },
      )
      Column(modifier = Modifier.width(60.dp)) {
        Text(
            "Default",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Column(modifier = Modifier.width(300.dp)) {
        Text(
            "Borderless Buttons  ",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Text(
            "Borderless button doesn't have a background.",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.width(20.dp))
      BorderlessButton(
          label = "Label",
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      BorderlessButton(
          label = "Label",
          leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      BorderlessCircleButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      BorderlessIconButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      Column(modifier = Modifier.width(60.dp)) {
        Text(
            "Disabled",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Column(modifier = Modifier.width(300.dp)) {}

      Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
    Spacer(modifier = Modifier.height(20.dp))
    // Destructive
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.width(20.dp))
      DestructiveButton(
          label = "Label",
          onClick = { /* no-op */ },
      )
      DestructiveButton(
          label = "Label",
          leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
          onClick = { /* no-op */ },
      )
      DestructiveCircleButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
          onClick = { /* no-op */ },
      )
      DestructiveIconButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
          onClick = { /* no-op */ },
      )
      Column(modifier = Modifier.width(60.dp)) {
        Text(
            "Default",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Column(modifier = Modifier.width(300.dp)) {
        Text(
            "Destructive Buttons  ",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Text(
            "Use it to draw user's attention in a destructive action such as \"Delete\" or \"End\".",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(modifier = Modifier.width(20.dp))
      DestructiveButton(
          label = "Label",
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      DestructiveButton(
          label = "Label",
          leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      DestructiveCircleButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      DestructiveIconButton(
          icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
          isEnabled = false,
          onClick = { /* no-op */ },
      )
      Column(modifier = Modifier.width(60.dp)) {
        Text(
            "Disabled",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Column(modifier = Modifier.width(300.dp)) {}

      Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
    Spacer(modifier = Modifier.height(20.dp))
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 2000,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdseyeButtonsPreview() {
  BirdseyeButtons()
}
