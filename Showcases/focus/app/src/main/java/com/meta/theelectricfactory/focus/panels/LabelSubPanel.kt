// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.managers.ToolManager
import com.meta.theelectricfactory.focus.data.Label
import com.meta.theelectricfactory.focus.utils.FOCUS_DP
import com.meta.theelectricfactory.focus.ui.focusFont
import com.meta.theelectricfactory.focus.data.priorityLabels
import com.meta.theelectricfactory.focus.data.stateLabels

@Composable
fun LabelSubPanel() {
    return FocusTheme {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(50.dp))
                .background(SpatialTheme.colorScheme.panel),
            contentAlignment = Alignment.Center
        ) {
            Row (
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Labels"
                )

                LabelButton(stateLabels[0], onClick = { ToolManager.instance.createLabelTool(0)})
                LabelButton(stateLabels[1], onClick = { ToolManager.instance.createLabelTool(1)})
                LabelButton(stateLabels[2], onClick = { ToolManager.instance.createLabelTool(2)})
                LabelButton(priorityLabels[0], onClick = { ToolManager.instance.createLabelTool(3)})
                LabelButton(priorityLabels[1], onClick = { ToolManager.instance.createLabelTool(4)})
                LabelButton(priorityLabels[2], onClick =  { ToolManager.instance.createLabelTool(5)})
            }
        }
    }
}

@Composable
fun LabelButton(
    label: Label,
    fontSize: TextUnit = 12.sp,
    height: Dp = 40.dp,
    onClick: () -> Unit
) {

    Button(
        modifier = Modifier.height(height),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = label.containerColor,
            contentColor = label.contentColor
        )

    ) {
        Text(
            text = label.description,
            fontSize = fontSize,
            fontFamily = focusFont
        )
    }
}

@Preview(
    widthDp = (0.46f * FOCUS_DP).toInt(),
    heightDp = (0.042f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun LabelSubPanelPreview() {
    LabelSubPanel()
}