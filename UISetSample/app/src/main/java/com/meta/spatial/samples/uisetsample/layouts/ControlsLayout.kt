/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.samples.uisetsample.util.view.PanelScaffold
import com.meta.spatial.samples.uisetsample.util.view.StatefulWrapper
import com.meta.spatial.uiset.control.SpatialCheckbox
import com.meta.spatial.uiset.control.SpatialRadioButton
import com.meta.spatial.uiset.control.SpatialSwitch
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.spatial.uiset.theme.LocalTypography

@Composable
fun ControlsLayout() {
  return PanelScaffold("Control") {
    Spacer(Modifier.size(8.5.dp))
    Row(
        Modifier.fillMaxSize(),
    ) {
      Column(Modifier.width(330.dp)) {
        Text(
            text = "Toggle",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground,
                ),
        )
        Text(
            text = "A Toggle is a switch that can be activated between two modes, ON versus OFF.",
            style =
                LocalTypography.current.body1.copy(
                    color =
                        LocalColorScheme.current.primaryAlphaBackground.copy(
                            alpha = 0.6f,
                        ),
                ),
        )
        Spacer(Modifier.size(32.dp))
        StatefulWrapper(false) { value, onChanged ->
          SpatialSwitch(
              checked = value,
              onCheckedChange = onChanged,
          )
        }
        Spacer(Modifier.size(48.dp))
        Text(
            text = "Toggle Switch with Text Label",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground,
                ),
        )
        Spacer(Modifier.size(12.dp))
        ControlsList {
          StatefulWrapper(false) { value, onChanged ->
            SpatialSwitch(
                checked = value,
                onCheckedChange = onChanged,
            )
          }
        }
      }
      Spacer(Modifier.size(24.dp))
      Column(Modifier.width(330.dp)) {
        Text(
            text = "Checkbox",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground,
                ),
        )
        Text(
            text =
                "A checkbox is a UI input that allows users to select one or multiple choices presented.",
            style =
                LocalTypography.current.body1.copy(
                    color =
                        LocalColorScheme.current.primaryAlphaBackground.copy(
                            alpha = 0.6f,
                        ),
                ),
        )
        Spacer(Modifier.size(32.dp))
        StatefulWrapper(false) { value, onChanged ->
          SpatialCheckbox(
              checked = value,
              onCheckedChange = onChanged,
          )
        }
        Spacer(Modifier.size(48.dp))
        Text(
            text = "Checkbox Buttons with Text Label",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground,
                ),
        )
        Spacer(Modifier.size(12.dp))
        ControlsList {
          StatefulWrapper(false) { value, onChanged ->
            SpatialCheckbox(
                checked = value,
                onCheckedChange = onChanged,
            )
          }
        }
      }
      Spacer(Modifier.size(24.dp))
      Column(Modifier.width(330.dp)) {
        Text(
            text = "Radio",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground,
                ),
        )
        Text(
            text =
                "Radios are used when there are multiple choices present, but users are only to select one option.",
            style =
                LocalTypography.current.body1.copy(
                    color =
                        LocalColorScheme.current.primaryAlphaBackground.copy(
                            alpha = 0.6f,
                        ),
                ),
        )
        Spacer(Modifier.size(32.dp))
        StatefulWrapper(false) { value, onChanged ->
          SpatialRadioButton(
              selected = value,
              onClick = { onChanged(!value) },
          )
        }
        Spacer(Modifier.size(48.dp))
        Text(
            text = "Radio Buttons with Text Label",
            style =
                LocalTypography.current.body1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground,
                ),
        )
        Spacer(Modifier.size(12.dp))
        RadioGroupDemo()
      }
    }
    // endregion
  }
}

@Composable
private fun RadioGroupDemo() {
  val deviceTypes = listOf("Quest 3", "Quest 3S", "Ray Ban Meta")
  val currentSelection = remember { mutableStateOf(deviceTypes.first()) }

  RadioGroup(
      modifier =
          Modifier.background(
                  color = LocalColorScheme.current.secondaryButton,
                  LocalShapes.current.medium,
              )
              .padding(12.dp)
              .fillMaxWidth(),
      items = deviceTypes,
      selection = currentSelection.value,
      onItemClick = { clickedItem -> currentSelection.value = clickedItem },
  )
}

@Composable
private fun RadioGroup(
    modifier: Modifier,
    items: List<String>,
    selection: String,
    onItemClick: ((String) -> Unit),
) {
  Column(modifier = modifier) {
    items.forEach { item ->
      ControlWithTextLabel {
        SpatialRadioButton(selected = item == selection, onClick = { onItemClick(item) })
      }
    }
  }
}

@Composable
private fun ControlsList(
    control: @Composable () -> Unit,
) {
  Column(
      Modifier.background(
              color = LocalColorScheme.current.secondaryButton,
              LocalShapes.current.medium,
          )
          .padding(12.dp)
          .fillMaxWidth(),
  ) {
    ControlWithTextLabel { control() }
    Spacer(Modifier.size(12.dp))
    ControlWithTextLabel { control() }
    Spacer(Modifier.size(12.dp))
    ControlWithTextLabel { control() }
  }
}

@Composable
private fun ControlWithTextLabel(
    label: String = "Label",
    secondaryLabel: String = "Secondary Text",
    control: @Composable () -> Unit,
) {
  Row(
      modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Column {
      Text(
          text = label,
          style =
              LocalTypography.current.body1.copy(
                  color = LocalColorScheme.current.primaryAlphaBackground,
              ),
      )
      Text(
          text = secondaryLabel,
          style =
              LocalTypography.current.body2.copy(
                  color = LocalColorScheme.current.primaryAlphaBackground,
              ),
      )
    }
    control()
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 650,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun ControlsLayoutPreview() {
  ControlsLayout()
}
