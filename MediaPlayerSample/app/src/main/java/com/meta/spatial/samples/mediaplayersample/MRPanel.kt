/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mediaplayersample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meta.spatial.toolkit.SpatialActivityManager

@Composable
fun MRApp() {
  val (mrCheckedState, setMRCheckedState) = remember { mutableStateOf(false) }

  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier =
          Modifier.fillMaxSize()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF0f0f0f))
              .padding(8.dp),
  ) {
    MRSwitch(mrCheckedState) { state ->
      setMRCheckedState(state)
      SpatialActivityManager.executeOnVrActivity<MediaPlayerSampleActivity> { activity ->
        activity.scene.enablePassthrough(state)
        activity.scene.enableEnvironmentDepth(state)

        activity.mrState = state
      }
    }
  }
}

@Composable
fun MRSwitch(MRCheckedState: Boolean, onMR: (state: Boolean) -> Unit) {
  Column() {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
          text = "Passthrough",
          minLines = 1,
          style = MaterialTheme.typography.subtitle2,
          color = Color.White,
      )
      Spacer(modifier = Modifier.width(8.dp))
      Switch(checked = MRCheckedState, onCheckedChange = { onMR(it) })
    }
  }
}
