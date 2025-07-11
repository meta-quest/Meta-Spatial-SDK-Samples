// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.meta.spatial.uiset.button.BorderlessIconButton
import com.meta.spatial.uiset.button.PrimaryIconButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun ArrowSubPanel() {

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
                    text = "Arrows",
                    color = LocalColorScheme.current.primaryButton,
//                    style = LocalTypography.current.headline2Strong.copy(
//                        fontSize = 35.sp),
                )

                ArrowButton( R.drawable.button_arrow1, {immersiveActivity?.CreateArrowTool(0)})
                ArrowButton( R.drawable.button_arrow2, {immersiveActivity?.CreateArrowTool(1)})
                ArrowButton( R.drawable.button_arrow3, {immersiveActivity?.CreateArrowTool(2)})
                ArrowButton( R.drawable.button_arrow4, {immersiveActivity?.CreateArrowTool(3)})
                ArrowButton( R.drawable.button_arrow5, {immersiveActivity?.CreateArrowTool(4)})
                ArrowButton( R.drawable.button_arrow6, {immersiveActivity?.CreateArrowTool(5)})
            }
        }
    }
}

@Composable
fun ArrowButton(
    icon: Int,
    onClick: () -> Unit
) {
    BorderlessIconButton(
        icon = { Icon(
            painterResource(id = icon),
            contentDescription = "",
            tint = Color.Unspecified
        )},
        onClick = onClick,
    )
}

@Preview(
    widthDp = (0.28f * focusDP).toInt(),
    heightDp = (0.042f * focusDP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun ArrowSubPanelPreview() {
    ArrowSubPanel()
}