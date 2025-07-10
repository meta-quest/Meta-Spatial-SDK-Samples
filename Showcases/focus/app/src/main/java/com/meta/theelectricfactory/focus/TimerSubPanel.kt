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
import com.meta.spatial.uiset.button.BorderlessIconButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun TimerSubPanel() {

    var immersiveActivity = ImmersiveActivity.getInstance()

    return FocusTheme {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(100.dp))
                .background(SpatialTheme.colorScheme.panel)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row (
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = "Shapes",
                    color = LocalColorScheme.current.primaryButton,
                    style = LocalTypography.current.headline2Strong.copy(
                        fontSize = 35.sp),
                )

                TimerButton( R.drawable.timer1, {immersiveActivity?.CreateTimer(0)})
                TimerButton( R.drawable.timer2, {immersiveActivity?.CreateTimer(1)})
                TimerButton( R.drawable.timer3, {immersiveActivity?.CreateTimer(2)})
                TimerButton( R.drawable.timer4, {immersiveActivity?.CreateTimer(3)})
                TimerButton( R.drawable.timer5, {immersiveActivity?.CreateTimer(4)})
                TimerButton( R.drawable.timer6, {immersiveActivity?.CreateTimer(5)})
            }
        }
    }
}

@Composable
fun TimerButton( //TODO evaluar si no conviene que sean todos el mismo shape/arrow/board.etc
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
    widthDp = (0.38f * 5000).toInt(),
    heightDp = (0.042f * 5000).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun TimerSubPanelPreview() {
    TimerSubPanel()
}