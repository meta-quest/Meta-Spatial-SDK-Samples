/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.util.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun PanelToolbar(
    backgroundColor: Color = Color.Unspecified,
    title: String? = null,
    centerTitle: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
  Row(
      horizontalArrangement = if (centerTitle) Arrangement.SpaceBetween else Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth().height(48.dp).background(color = backgroundColor).padding(4.dp),
  ) {
    if (leading != null) {
      leading()
    } else {
      Box(modifier = Modifier.size(1.dp)) {}
    }

    title?.let {
      Text(
          it,
          style =
              TextStyle(
                  fontSize = 14.sp,
              ),
          color = SpatialTheme.colorScheme.primaryAlphaBackground,
          textAlign = TextAlign.Center,
      )
    }

    actions?.let {
      Row(
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        it()
      }
    }
  }
}
