// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.birdseye

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.levinriegner.uiset.app.util.view.StatefulWrapper
import com.meta.spatial.uiset.control.SpatialCheckbox
import com.meta.spatial.uiset.control.SpatialRadioButton
import com.meta.spatial.uiset.control.SpatialSwitch
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun BirdseyeControl() {
  PanelScaffold("Control") {
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.background(SpatialTheme.colorScheme.hover).fillMaxWidth()) {
      Column {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "  Component",
            style =
                LocalTypography.current.headline1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
          StatefulWrapper(true) { value, onChanged ->
            SpatialSwitch(
                checked = value,
                onCheckedChange = onChanged,
            )
          }
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
                color = LocalColorScheme.current.primaryAlphaBackground),
    )
    Spacer(modifier = Modifier.height(40.dp))

    Row() {
      Spacer(Modifier.width(20.dp))
      Column(Modifier.width(330.dp)) {
        Box(Modifier.height(80.dp)) {
          Column {
            Text(
                text = "Toggle",
                style =
                    LocalTypography.current.body1Strong.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground,
                    ),
            )
            Text(
                text =
                    "A Toggle is a switch that can be activated between two modes, ON versus OFF.",
                style =
                    LocalTypography.current.body1.copy(
                        color =
                            LocalColorScheme.current.primaryAlphaBackground.copy(
                                alpha = 0.6f,
                            ),
                    ),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(false) { value, onChanged ->
            SpatialSwitch(
                checked = value,
                onCheckedChange = onChanged,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "OFF",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(true) { value, onChanged ->
            SpatialSwitch(
                checked = value,
                onCheckedChange = onChanged,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ON",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(false) { value, onChanged ->
            SpatialSwitch(checked = value, onCheckedChange = onChanged, enabled = false)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "OFF Disabled",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(true) { value, onChanged ->
            SpatialSwitch(checked = value, onCheckedChange = onChanged, enabled = false)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ON Disabled",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
      }
      Spacer(Modifier.size(24.dp))
      Column(Modifier.width(330.dp)) {
        Box(Modifier.height(80.dp)) {
          Column {
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
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(false) { value, onChanged ->
            SpatialCheckbox(
                checked = value,
                onCheckedChange = onChanged,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "OFF",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(true) { value, onChanged ->
            SpatialCheckbox(
                checked = value,
                onCheckedChange = onChanged,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ON",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(false) { value, onChanged ->
            SpatialCheckbox(checked = value, onCheckedChange = onChanged, enabled = false)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "OFF Disabled",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(true) { value, onChanged ->
            SpatialCheckbox(checked = value, onCheckedChange = onChanged, enabled = false)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ON Disabled",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
      }
      Spacer(Modifier.size(24.dp))
      Column(Modifier.width(330.dp)) {
        Box(Modifier.height(80.dp)) {
          Column {
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
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(false) { value, onChanged ->
            SpatialRadioButton(
                selected = value,
                onClick = { onChanged(!value) },
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "OFF",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(true) { value, onChanged ->
            SpatialRadioButton(
                selected = value,
                onClick = { onChanged(!value) },
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ON",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(false) { value, onChanged ->
            SpatialRadioButton(selected = value, onClick = { onChanged(!value) }, enabled = false)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "OFF Disabled",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
        Spacer(Modifier.size(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatefulWrapper(true) { value, onChanged ->
            SpatialRadioButton(selected = value, onClick = { onChanged(!value) }, enabled = false)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ON Disabled",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground),
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        "  Radio Group",
        style =
            LocalTypography.current.headline1Strong.copy(
                color = LocalColorScheme.current.primaryAlphaBackground),
    )
    Row() { RadioGroupDemo() }
  }
}

@Composable
private fun RadioGroupDemo() {
  val deviceTypes = listOf("All", "Quest 3", "Quest 3S", "Ray Ban Meta")
  val currentSelection = remember { mutableStateOf(deviceTypes.first()) }

  RadioGroup(
      modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
      LabelledRadioButton(
          label = item,
          selected = item == selection,
          onClick = { onItemClick(item) },
      )
    }
  }
}

@Composable
private fun LabelledRadioButton(
    label: String = "Label",
    selected: Boolean = false,
    onClick: (() -> Unit)?,
) {
  Row(
      modifier = Modifier.height(48.dp).padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    SpatialRadioButton(
        selected = selected,
        onClick = onClick,
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
        text = label,
        style =
            LocalTypography.current.body1.copy(
                color = LocalColorScheme.current.primaryAlphaBackground,
            ),
    )
  }
}

@Preview(
    widthDp = 1136,
    heightDp = 2000,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdseyeControlPreview() {
  BirdseyeControl()
}
