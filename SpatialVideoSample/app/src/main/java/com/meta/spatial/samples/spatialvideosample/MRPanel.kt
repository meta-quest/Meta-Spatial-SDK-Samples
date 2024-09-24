/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.spatialvideosample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.toolkit.SpatialActivityManager

@Composable
fun MRApp(isMrModeDefault: Boolean) {
  val (MRCheckedState, setMRCheckedState) = remember { mutableStateOf(isMrModeDefault) }
  val context = LocalContext.current
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier =
          Modifier.fillMaxSize()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF1C2E33))
              .padding(10.dp)) {
        MRSwitch(
            MRCheckedState,
            { isMrMode ->
              setMRCheckedState(isMrMode)
              SpatialActivityManager.executeOnVrActivity<SpatialVideoSampleActivity> { activity ->
                activity.setMrMode(isMrMode)
              }
            })
      }
}

@Composable
fun MRSwitch(MRCheckedState: Boolean, onMR: (state: Boolean) -> Unit) {
  Column() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.width(184.dp)
                .height(40.dp)
                .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)) {
          Text(
              text = "Passthrough",
              style =
                  TextStyle(
                      fontSize = 12.sp,
                      lineHeight = 20.sp,
                      fontFamily = FontFamily(Font(R.font.noto_sans_regular)),
                      fontWeight = FontWeight(400),
                      color = Color(0xFFF1F4F7),
                  ))
          Spacer(modifier = Modifier.width(2.dp))
          Switch(checked = MRCheckedState, onCheckedChange = { onMR(it) })
        }
  }
}

class MRPanel : ComponentActivity() {
  override fun onCreate(savedInstanceBundle: Bundle?) {
    super.onCreate(savedInstanceBundle)
    setContent { MRApp(intent.getStringExtra("isMrMode").toBoolean()) }
  }
}
