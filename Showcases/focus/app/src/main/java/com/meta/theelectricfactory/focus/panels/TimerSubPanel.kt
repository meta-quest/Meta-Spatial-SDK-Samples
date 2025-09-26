// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.managers.ToolManager
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.utils.FOCUS_DP

@Composable
fun TimerSubPanel() {
  return FocusTheme {
    Box(
        Modifier.fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(50.dp))
            .background(SpatialTheme.colorScheme.panel),
        contentAlignment = Alignment.Center,
    ) {
      Row(
          modifier = Modifier.fillMaxHeight(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = "Timer")

        ToolbarSubpanelButton(
            R.drawable.timer1,
            80.dp,
            onClick = { ToolManager.instance.createTimer(0) },
        )
        ToolbarSubpanelButton(
            R.drawable.timer2,
            80.dp,
            onClick = { ToolManager.instance.createTimer(1) },
        )
        ToolbarSubpanelButton(
            R.drawable.timer3,
            80.dp,
            onClick = { ToolManager.instance.createTimer(2) },
        )
        ToolbarSubpanelButton(
            R.drawable.timer4,
            80.dp,
            onClick = { ToolManager.instance.createTimer(3) },
        )
        ToolbarSubpanelButton(
            R.drawable.timer5,
            80.dp,
            onClick = { ToolManager.instance.createTimer(4) },
        )
        ToolbarSubpanelButton(
            R.drawable.timer6,
            80.dp,
            onClick = { ToolManager.instance.createTimer(5) },
        )
      }
    }
  }
}

@Preview(
    widthDp = (0.35f * FOCUS_DP).toInt(),
    heightDp = (0.042f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun TimerSubPanelPreview() {
  TimerSubPanel()
}
