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
fun StickerSubPanel() {

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
                    text = "Stickers",
                    color = LocalColorScheme.current.primaryButton,
                    style = LocalTypography.current.headline2Strong.copy(
                        fontSize = 35.sp),
                )

                StickerButton( R.drawable.sticker1, {immersiveActivity?.CreateSticker(0)})
                StickerButton( R.drawable.sticker2, {immersiveActivity?.CreateSticker(1)})
                StickerButton( R.drawable.sticker3, {immersiveActivity?.CreateSticker(2)})
                StickerButton( R.drawable.sticker4, {immersiveActivity?.CreateSticker(3)})
                StickerButton( R.drawable.sticker5, {immersiveActivity?.CreateSticker(4)})
                StickerButton( R.drawable.sticker6, {immersiveActivity?.CreateSticker(5)})
            }
        }
    }
}

@Composable
fun StickerButton( //TODO evaluar si no conviene que sean todos el mismo shape/arrow/board.etc
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
    widthDp = (0.29f * 5000).toInt(),
    heightDp = (0.042f * 5000).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun StickerSubPanelPreview() {
    StickerSubPanel()
}