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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.Label
import com.meta.theelectricfactory.focus.utils.FOCUS_DP
import com.meta.theelectricfactory.focus.ui.focusFont
import com.meta.theelectricfactory.focus.priorityLabels
import com.meta.theelectricfactory.focus.stateLabels

@Composable
fun LabelSubPanel() {

    var immersiveActivity = ImmersiveActivity.getInstance()

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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Labels",
                    color = FocusColors.black,
                )

                LabelButton(stateLabels[0], {immersiveActivity?.CreateLabelTool(0)})
                LabelButton(stateLabels[1], {immersiveActivity?.CreateLabelTool(1)})
                LabelButton(stateLabels[2], {immersiveActivity?.CreateLabelTool(2)})
                LabelButton(priorityLabels[0], {immersiveActivity?.CreateLabelTool(3)})
                LabelButton(priorityLabels[1], {immersiveActivity?.CreateLabelTool(4)})
                LabelButton(priorityLabels[2], {immersiveActivity?.CreateLabelTool(5)})
            }
        }
    }
}

@Composable
fun LabelButton(
    label: Label,
    onClick: () -> Unit
) {

    Button(
        modifier = Modifier.height(50.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = label.containerColor,
            contentColor = label.contentColor
        )

    ) {
        Text(
            text = label.description,
            fontSize = 16.sp,
            fontFamily = focusFont
        )
    }
}

@Preview(
    widthDp = (0.44f * FOCUS_DP).toInt(),
    heightDp = (0.042f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun LabelSubPanelPreview() {
    LabelSubPanel()
}