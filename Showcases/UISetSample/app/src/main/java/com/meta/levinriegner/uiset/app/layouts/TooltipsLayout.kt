// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.R
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.Basic
import com.meta.spatial.uiset.tooltip.SpatialTooltipContent

@Composable
fun TooltipsLayout() {
  return PanelScaffold("Tooltip") {
    Spacer(Modifier.size(37.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(Modifier.width(90.dp))
      SpatialTooltipContent(
          title = "Primary",
      )
      Spacer(Modifier.width(133.dp))
      Text(
          text = "Default",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(Modifier.width(226.dp))
      SpatialTooltipContent(
          icon = {
            Icon(
                imageVector = SpatialIcons.Regular.Basic,
                "",
                tint = LocalColorScheme.current.primaryAlphaBackground,
            )
          },
          title = "Primary",
          subtitle = "Secondary",
      )
      Spacer(Modifier.width(64.dp))
      Text(
          text = "Tooltip with Icon & Subtitle",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
    }
    Spacer(Modifier.height(46.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(Modifier.width(90.dp))
      SpatialTooltipContent(
          title = "Primary",
          subtitle = "Secondary",
      )
      Spacer(Modifier.width(112.dp))
      Text(
          text = "Default with Subtitle",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(Modifier.width(142.dp))
      SpatialTooltipContent(
          icon = {
            Image(
                painterResource(R.drawable.sample_avatar),
                contentScale = ContentScale.Crop,
                contentDescription = "Avatar",
            )
          },
          title = "Primary",
      )
      Spacer(Modifier.width(85.dp))
      Text(
          text = "Tooltip with Image",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
    }
    Spacer(Modifier.height(46.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(Modifier.width(90.dp))
      SpatialTooltipContent(
          title = "Primary",
          icon = {
            Icon(
                imageVector = SpatialIcons.Regular.Basic,
                "",
                tint = LocalColorScheme.current.primaryAlphaBackground,
            )
          },
      )
      Spacer(Modifier.width(85.dp))
      Text(
          text = "Tooltip with Icon",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(Modifier.width(164.dp))
      SpatialTooltipContent(
          icon = {
            Image(
                painterResource(R.drawable.sample_avatar),
                contentScale = ContentScale.Crop,
                contentDescription = "Avatar",
            )
          },
          title = "Primary",
          subtitle = "Secondary",
      )
      Spacer(Modifier.width(65.dp))
      Text(
          text = "Tooltip with Image & Subtitle",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
    }
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 671,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun TooltipsLayoutPreview() {
  TooltipsLayout()
}
