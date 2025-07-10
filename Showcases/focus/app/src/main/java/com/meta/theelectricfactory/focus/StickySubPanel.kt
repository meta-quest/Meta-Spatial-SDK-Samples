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
import com.meta.spatial.uiset.navigation.SpatialSideNavItem
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun StickySubPanel() {

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
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sticky Notes",
                    color = LocalColorScheme.current.primaryButton,
                    style = LocalTypography.current.headline2Strong.copy(
                        fontSize = 35.sp),
                )

                StickyButton("yellow", FocusColors.yellowStickyNote, {immersiveActivity?.CreateStickyNote(0)})
                StickyButton("green", FocusColors.greenStickyNote, {immersiveActivity?.CreateStickyNote(1)})
                StickyButton("pink", FocusColors.pinkStickyNote, {immersiveActivity?.CreateStickyNote(2)})
                StickyButton("orange", FocusColors.orangeStickyNote, {immersiveActivity?.CreateStickyNote(3)})
                StickyButton("blue", FocusColors.blueStickyNote, {immersiveActivity?.CreateStickyNote(4)})
                StickyButton("purple", FocusColors.purpleStickyNote, {immersiveActivity?.CreateStickyNote(5)})
            }
        }
    }
}

@Composable
fun StickyButton(
    contentDescription: String,
    color: Color,
    onClick: () -> Unit
) {
    SpatialSideNavItem(
        Modifier
            .size(100.dp)
            .fillMaxHeight()
            .background(color, RoundedCornerShape(80.dp)),
        icon = { Icon(
            painterResource(id = R.drawable.transparent),
            contentDescription = contentDescription,
            tint = Color.Unspecified
        )},
        primaryLabel = "",
        collapsed = true,
        onClick = {
            onClick()
        }
    )
}

@Preview(
    widthDp = (0.26f * 5000).toInt(),
    heightDp = (0.042f * 5000).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun StickySubPanelPreview() {
    StickySubPanel()
}