// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.navigation.SpatialSideNavItem
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CloseCircle
import com.meta.spatial.uiset.theme.icons.regular.PlayCircle

@Composable
fun ObjectInfoPanelHeader(
    title: String,
    onResume: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            color = SpatialColor.white100,
            style = SpatialTheme.typography.headline1Strong,
            modifier = Modifier.padding(bottom = 4.dp))
        Row {
          // using the spatial side nav item until the icon button is built
          if (true) {
            SpatialSideNavItem(
                modifier = Modifier.width(48.dp).height(48.dp),
                primaryLabel = "",
                icon = {
                  Image(
                      SpatialIcons.Regular.PlayCircle,
                      "Resume scanning",
                  )
                },
                onClick = { onResume?.invoke() },
                collapsed = true,
                dense = true,
                selected = false)
            SpatialSideNavItem(
                modifier = Modifier.width(48.dp).height(48.dp),
                primaryLabel = "",
                icon = { Image(SpatialIcons.Regular.CloseCircle, "Close panel") },
                onClick = { onClose?.invoke() },
                collapsed = true,
                dense = true,
                selected = false)
          } else {
            IconButton(onClick = { onResume?.invoke() }) {
              Image(SpatialIcons.Regular.CloseCircle, "Resume scanning")
            }
            IconButton(onClick = { onClose?.invoke() }) {
              Image(SpatialIcons.Regular.CloseCircle, "Close panel")
            }
          }
        }
      }
}
