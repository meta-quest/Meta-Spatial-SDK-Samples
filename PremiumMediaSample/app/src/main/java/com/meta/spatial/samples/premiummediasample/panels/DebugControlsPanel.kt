/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.samples.premiummediasample.data.debug.DebugButtonItem
import com.meta.spatial.samples.premiummediasample.data.debug.DebugData
import com.meta.spatial.samples.premiummediasample.data.debug.DebugEnumItem
import com.meta.spatial.samples.premiummediasample.data.debug.DebugItem
import com.meta.spatial.samples.premiummediasample.data.debug.DebugLabelItem
import com.meta.spatial.samples.premiummediasample.data.debug.DebugSliderItem
import com.meta.spatial.samples.premiummediasample.data.debug.DebugStringArrayItem
import com.meta.spatial.samples.premiummediasample.data.debug.DebugToggleItem
import kotlin.math.roundToInt
import kotlin.reflect.KClass

object DebugControlsPanelConstants {
  val BackgroundColor: Color = Color(0xFF303D46)
}

@Composable
fun DebugPanel(debugData: DebugData) {
  Column(
      modifier =
          Modifier.clip(RoundedCornerShape(20.dp))
              .background(DebugControlsPanelConstants.BackgroundColor)
              .wrapContentHeight()
              .fillMaxWidth()) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier =
                Modifier.fillMaxWidth().wrapContentHeight().padding(start = 16.dp, end = 16.dp)) {
              items(items = debugData.items) { item -> DebugPanelItem(item) }
            }
      }
}

@Composable
fun DebugPanelItem(item: DebugItem) {
  when (item) {
    is DebugEnumItem -> DebugPanelEnumSlider(item)
    is DebugSliderItem -> DebugPanelSlider(item)
    is DebugButtonItem -> DebugPanelButton(item)
    is DebugToggleItem -> DebugPanelToggle(item)
    is DebugStringArrayItem -> DebugPanelStringArray(item)
    is DebugLabelItem -> DebugPanelLabel(item)
  }
}

@Composable fun DebugPanelLabel(item: DebugLabelItem) {}

@Composable
fun DebugPanelStringArray(slider: DebugStringArrayItem) {
  var debugValue by remember { mutableStateOf(slider.initialValue) }
  val values = slider.values
  val count = values.size

  Column() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.height(40.dp)
                .fillMaxWidth()
                .background(DebugControlsPanelConstants.BackgroundColor)) {
          Column(modifier = Modifier.width(110.dp)) {
            Text(
                text = slider.label,
                style =
                    TextStyle(
                        fontSize = 12.sp,
                    ),
                color = Color.White,
            )
            Text(
                text = debugValue,
                style =
                    TextStyle(
                        fontSize = 12.sp,
                    ),
                color = Color.White,
            )
          }
          Slider(
              value = values.indexOf(debugValue).toFloat(),
              steps = count - 2,
              valueRange = 0f..(count - 1).toFloat(),
              onValueChange = { value ->
                debugValue = values[value.roundToInt()]
                slider.onValueChanged.invoke(debugValue)
              },
              modifier = Modifier.width(240.dp))
        }
  }
}

@Composable
fun DebugPanelEnumSlider(slider: DebugEnumItem) {
  var debugValue by remember { mutableStateOf(slider.initialValue) }
  val values = (slider.initialValue::class as KClass<Enum<*>>).java.enumConstants
  var count = 0

  val intToValue = mutableMapOf<Int, Enum<*>>()
  if (values != null) {
    for (value in values) {
      intToValue[count] = value
      count++
    }
  }

  Column() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.height(40.dp)
                .fillMaxWidth()
                .background(DebugControlsPanelConstants.BackgroundColor)) {
          Column(modifier = Modifier.width(110.dp)) {
            Text(
                text = slider.label,
                style =
                    TextStyle(
                        fontSize = 12.sp,
                    ),
                color = Color.White,
            )
            Text(
                text = debugValue.name,
                style =
                    TextStyle(
                        fontSize = 12.sp,
                    ),
                color = Color.White,
            )
          }
          Slider(
              value = debugValue.ordinal.toFloat(),
              steps = count - 2,
              valueRange = 0f..(count - 1).toFloat(),
              onValueChange = { value ->
                debugValue = intToValue[value.roundToInt()] ?: debugValue
                slider.onValueChanged.invoke(debugValue)
              },
              modifier = Modifier.width(240.dp))
        }
  }
}

@Composable
fun DebugPanelSlider(slider: DebugSliderItem) {
  var debugValue by remember { mutableStateOf(slider.initialValue) }

  Column() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.height(40.dp)
                .fillMaxWidth()
                .background(DebugControlsPanelConstants.BackgroundColor)) {
          Text(
              text = slider.label,
              style =
                  TextStyle(
                      fontSize = 12.sp,
                  ),
              color = Color.White,
              modifier = Modifier.width(110.dp))
          Slider(
              value = debugValue,
              steps = slider.steps,
              valueRange = slider.range,
              onValueChange = { value ->
                debugValue = value
                if (slider.roundToInt) debugValue.roundToInt().toFloat()
                slider.onValueChanged(debugValue)
              },
              modifier = Modifier.width(190.dp))

          Text(
              text = (if (slider.roundToInt) "%.0f" else "%.2f").format(debugValue),
              style =
                  TextStyle(
                      fontSize = 12.sp,
                  ),
              color = Color.White,
              modifier = Modifier.width(50.dp))
        }
  }
}

@Composable
fun DebugPanelToggle(toggle: DebugToggleItem) {
  var debugValue by remember { mutableStateOf(toggle.initialValue) }

  Column() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.height(40.dp)
                .fillMaxWidth()
                .background(DebugControlsPanelConstants.BackgroundColor)) {
          Text(
              text = toggle.label,
              style =
                  TextStyle(
                      fontSize = 12.sp,
                  ),
              color = Color.White,
              modifier = Modifier.width(110.dp))
          Switch(
              checked = debugValue,
              onCheckedChange = {
                debugValue = it
                toggle.onValueChanged(debugValue)
              })
        }
  }
}

@Composable
fun DebugPanelButton(button: DebugButtonItem) {
  Column() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.height(40.dp)
                .fillMaxWidth()
                .background(DebugControlsPanelConstants.BackgroundColor)) {
          Button(onClick = button.onClick) {
            Text(
                text = button.label,
                style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center),
                color = Color.White,
                modifier = Modifier.width(180.dp))
          }
        }
  }
}

@Preview
@Composable
fun DebugPanelPreview() {

  val debugData =
      DebugData(
          items =
              mutableListOf(
                  DebugSliderItem(
                      label = "Test 1",
                      initialValue = 0.5f,
                      onValueChanged = { value -> println(" item 1 value changed $value") }),
                  DebugSliderItem(
                      label = "Test 2",
                      initialValue = 0.5f,
                      onValueChanged = { value -> println(" item 2 value changed $value") }),
                  DebugSliderItem(
                      label = "Test 3",
                      initialValue = 1f,
                      range = 1f..10f,
                      steps = 8,
                      roundToInt = true,
                      onValueChanged = { value -> println(" item 3 value changed $value") }),
                  DebugToggleItem(
                      label = "Toggle Test 1",
                      initialValue = true,
                      onValueChanged = { value -> println(" toggle 1 value changed $value") }),
                  DebugToggleItem(
                      label = "Toggle Test 2",
                      initialValue = false,
                      onValueChanged = { value -> println(" toggle 2 value changed $value") }),
                  DebugButtonItem(
                      label = "Button 1",
                      onClick = { println("clicked button 1") },
                  ),
                  DebugButtonItem(
                      label = "Button 2",
                      onClick = { println("clicked button 2") },
                  )))

  DebugPanel(debugData)
}
