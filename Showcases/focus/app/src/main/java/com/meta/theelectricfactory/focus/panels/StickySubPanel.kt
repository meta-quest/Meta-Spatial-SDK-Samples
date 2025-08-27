// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.meta.spatial.uiset.navigation.SpatialSideNavItem
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.managers.ToolManager
import com.meta.theelectricfactory.focus.ui.FocusShapes
import com.meta.theelectricfactory.focus.ui.focusShapes
import com.meta.theelectricfactory.focus.utils.FOCUS_DP

@Composable
fun StickySubPanel() {
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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sticky Notes"
                )

                StickyButton("yellow", FocusColors.yellowStickyNote, { ToolManager.instance.createStickyNote(0)})
                StickyButton("green", FocusColors.greenStickyNote, { ToolManager.instance.createStickyNote(1)})
                StickyButton("pink", FocusColors.pinkStickyNote, { ToolManager.instance.createStickyNote(2)})
                StickyButton("orange", FocusColors.orangeStickyNote, { ToolManager.instance.createStickyNote(3)})
                StickyButton("blue", FocusColors.blueStickyNote, { ToolManager.instance.createStickyNote(4)})
                StickyButton("purple", FocusColors.purpleStickyNote, { ToolManager.instance.createStickyNote(5)})
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
    SpatialTheme(
        shapes = focusShapes(FocusShapes.FullRounded)
    ) {
        SpatialSideNavItem(
            Modifier
                .size(40.dp)
                .fillMaxHeight()
                .background(color, LocalShapes.current.large),
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
}

@Preview(
    widthDp = (0.26f * FOCUS_DP).toInt(),
    heightDp = (0.042f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun StickySubPanelPreview() {
    StickySubPanel()
}