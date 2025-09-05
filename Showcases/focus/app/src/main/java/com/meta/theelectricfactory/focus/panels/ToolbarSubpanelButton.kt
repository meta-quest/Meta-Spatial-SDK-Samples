// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.utils.FOCUS_DP

@Composable
fun ToolbarSubpanelButton(
    icon: Int,
    width: Dp = 45.dp,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxHeight()
            .width(width),
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = "",
            tint = Color.Unspecified
        )
    }
}

@Preview(
    widthDp = (0.1f * FOCUS_DP).toInt(),
    heightDp = (0.1f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun ToolbarSubpanelButtonPreview() {
    ToolbarSubpanelButton(R.drawable.sticker1, onClick = {})
}