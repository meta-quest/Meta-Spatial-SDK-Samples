/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll
import com.meta.spatial.uiset.theme.icons.regular.Chat
import com.meta.spatial.uiset.theme.icons.regular.MoreHorizontal

@Composable
fun ButtonsLayout() {
  return PanelScaffold("Buttons") {
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
      // Label Only
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Label Only",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(35.dp))
        PrimaryButton(
            label = "Label",
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        SecondaryButton(
            label = "Label",
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        BorderlessButton(
            label = "Label",
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        DestructiveButton(
            label = "Label",
            onClick = { /* no-op */ },
        )
      }
      // Icon & Label
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Icon & Label",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(35.dp))
        PrimaryButton(
            label = "Label",
            leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        SecondaryButton(
            label = "Label",
            leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        BorderlessButton(
            label = "Label",
            leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        DestructiveButton(
            label = "Label",
            leading = { Icon(imageVector = SpatialIcons.Regular.Chat, "") },
            onClick = { /* no-op */ },
        )
      }
      // Icon Only
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Icon Only",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(35.dp))
        PrimaryCircleButton(
            icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        SecondaryCircleButton(
            icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        BorderlessCircleButton(
            icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        DestructiveCircleButton(
            icon = { Icon(imageVector = SpatialIcons.Regular.MoreHorizontal, "") },
            onClick = { /* no-op */ },
        )
      }
      // Icon Selected
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Icon Selected",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(35.dp))
        PrimaryIconButton(
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        SecondaryIconButton(
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        BorderlessIconButton(
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onClick = { /* no-op */ },
        )
        Spacer(Modifier.size(28.dp))
        DestructiveIconButton(
            icon = { Icon(imageVector = SpatialIcons.Regular.CategoryAll, "") },
            onClick = { /* no-op */ },
        )
      }
      // Rationale
      Column(
          modifier = Modifier.width(330.dp),
          verticalArrangement = Arrangement.Top,
      ) {
        Text(
            text = "",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.size(35.dp))
        Text(
            text = "Primary Buttons",
            style =
                SpatialTheme.typography.body1Strong.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Text(
            text = "Use it to contain a high-emphasis action.",
            style =
                SpatialTheme.typography.body1.copy(
                    color =
                        SpatialTheme.colorScheme.primaryAlphaBackground.copy(
                            alpha = 0.6f,
                        ),
                ),
        )
        Spacer(Modifier.size(25.dp))
        Text(
            text = "Secondary Buttons",
            style =
                SpatialTheme.typography.body1Strong.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Text(
            text = "Use a secondary button for medium-emphasis action on a surface.",
            style =
                SpatialTheme.typography.body1.copy(
                    color =
                        SpatialTheme.colorScheme.primaryAlphaBackground.copy(
                            alpha = 0.6f,
                        ),
                ),
        )
        Spacer(Modifier.size(25.dp))
        Text(
            text = "Borderless Buttons",
            style =
                SpatialTheme.typography.body1Strong.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Text(
            text = "Borderless button doesn't have a background.",
            style =
                SpatialTheme.typography.body1.copy(
                    color =
                        SpatialTheme.colorScheme.primaryAlphaBackground.copy(
                            alpha = 0.6f,
                        ),
                ),
        )
        Spacer(Modifier.size(25.dp))
        Text(
            text = "Destructive Buttons",
            style =
                SpatialTheme.typography.body1Strong.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Text(
            text =
                "Use it to draw user's attention in a destructive action such as \"Delete\" or \"End\".",
            style =
                SpatialTheme.typography.body1.copy(
                    color =
                        SpatialTheme.colorScheme.primaryAlphaBackground.copy(
                            alpha = 0.6f,
                        ),
                ),
        )
      }
    }
    // endregion
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 568,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun ButtonsLayoutPreview() {
  ButtonsLayout()
}
