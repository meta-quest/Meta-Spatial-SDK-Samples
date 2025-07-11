// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.uiset.button.PrimaryIconButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme

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
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Labels",
                    color = LocalColorScheme.current.primaryButton,
//                    style = LocalTypography.current.headline2Strong.copy(
//                        fontSize = 35.sp),
                )

                LabelButton("To do", R.drawable.label_to_do, {immersiveActivity?.CreateLabelTool(0)})
                LabelButton("In progress", R.drawable.label_in_progress, {immersiveActivity?.CreateLabelTool(1)})
                LabelButton("Done", R.drawable.label_done, {immersiveActivity?.CreateLabelTool(2)})
                LabelButton("Low priority", R.drawable.label_low, {immersiveActivity?.CreateLabelTool(3)})
                LabelButton("Medium priority", R.drawable.label_medium, {immersiveActivity?.CreateLabelTool(4)})
                LabelButton("High priority", R.drawable.label_high, {immersiveActivity?.CreateLabelTool(5)})
            }
        }
    }
}

@Composable
fun LabelButton(
    contentDescription: String,
    icon: Int,
    onClick: () -> Unit
) {
    PrimaryIconButton(
        icon = { Icon(
            painterResource(id = icon),
            contentDescription = contentDescription,
            tint = Color.Unspecified
        )},
        onClick = onClick,
    )
}

@Preview(
    widthDp = (0.47f * focusDP).toInt(),
    heightDp = (0.042f * focusDP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun LabelSubPanelPreview() {
    LabelSubPanel()
}