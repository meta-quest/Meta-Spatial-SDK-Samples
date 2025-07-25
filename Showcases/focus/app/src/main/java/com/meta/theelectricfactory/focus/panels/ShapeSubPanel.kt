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
import com.meta.spatial.uiset.button.BorderlessIconButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.utils.focusDP

@Composable
fun ShapeSubPanel() {

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
                    text = "Shapes",
                    color = LocalColorScheme.current.primaryButton,
//                    style = LocalTypography.current.headline2Strong.copy(
//                        fontSize = 35.sp),
                )

                ShapeButton( R.drawable.shape1, {immersiveActivity?.CreateShape(0)})
                ShapeButton( R.drawable.shape2, {immersiveActivity?.CreateShape(1)})
                ShapeButton( R.drawable.shape3, {immersiveActivity?.CreateShape(2)})
                ShapeButton( R.drawable.shape4, {immersiveActivity?.CreateShape(3)})
                ShapeButton( R.drawable.shape5, {immersiveActivity?.CreateShape(4)})
                ShapeButton( R.drawable.shape6, {immersiveActivity?.CreateShape(5)})
            }
        }
    }
}

@Composable
fun ShapeButton( //TODO evaluar si no conviene que sean todos el mismo shape/arrow/board.etc
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
fun ShapeSubPanelPreview() {
    ShapeSubPanel()
}