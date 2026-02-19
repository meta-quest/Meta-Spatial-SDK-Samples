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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.samples.uisetsample.util.view.PanelScaffold
import com.meta.spatial.samples.uisetsample.util.view.StatefulWrapper
import com.meta.spatial.uiset.input.SpatialSearchBar
import com.meta.spatial.uiset.input.SpatialTextField
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.Basic

@Composable
fun TextFieldsLayout() {
  return PanelScaffold("Text Field & Search Bar") {
    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxSize()) {
      Spacer(Modifier.width(22.dp))
      Column(
          Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Default",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.height(48.dp))
        StatefulWrapper(initialValue = "") { query, onQueryChange ->
          SpatialSearchBar(
              enableAudio = false,
              query = query,
              onQueryChange = onQueryChange,
              onQuerySubmit = {},
          )
        }
      }
      Spacer(Modifier.width(65.dp))
      Column(
          Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "With Microphone",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.height(48.dp))
        StatefulWrapper(initialValue = "") { query, onQueryChange ->
          SpatialSearchBar(
              enableAudio = true,
              query = query,
              onQueryChange = onQueryChange,
              onQuerySubmit = {},
          )
        }
      }
      Spacer(Modifier.width(65.dp))
      Column(
          Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "Default",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.height(20.dp))
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Label",
              placeholder = "Value",
              value = value,
              onValueChange = onChanged,
              autoValidate = false,
          )
        }
      }
      Spacer(Modifier.width(65.dp))
      Column(
          Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "With Icon",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
        Spacer(Modifier.height(20.dp))
        StatefulWrapper(initialValue = "") { value, onChanged ->
          SpatialTextField(
              label = "Label",
              placeholder = "Value",
              leadingIcon = { Icon(SpatialIcons.Regular.Basic, "") },
              value = value,
              onValueChange = onChanged,
              autoValidate = false,
          )
        }
      }
    }
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 402,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun TextFieldsLayoutPreview() {
  TextFieldsLayout()
}
