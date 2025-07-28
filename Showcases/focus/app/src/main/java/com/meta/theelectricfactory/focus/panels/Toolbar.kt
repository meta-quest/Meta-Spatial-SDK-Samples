// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.utils.FOCUS_DP
import com.meta.theelectricfactory.focus.viewmodels.FocusViewModel

@Composable
fun ToolbarPanel() {

    var immersiveActivity = ImmersiveActivity.getInstance()

    val selectedTool by FocusViewModel.instance.selectedTool.collectAsState()
    val speakerIsOn by FocusViewModel.instance.speakerIsOn.collectAsState()
    val soundIcon = if (speakerIsOn) R.drawable.sound else R.drawable.sound_off

    return FocusTheme {
        Box(
            Modifier.fillMaxSize(),
        ) {
            Row (modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box( Modifier
                    .fillMaxWidth(0.26f)
                    .fillMaxHeight()
                    .clip(LocalShapes.current.large)
                    .background(SpatialTheme.colorScheme.panel)
                    .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Row (
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                    ) {

                        ToolbarButton(
                            R.drawable.home,
                            "Home",
                            false,
                            color = FocusColors.lightGray,
                            onClick = {
                                immersiveActivity?.OpenHomePanel()
                            }
                        )

                        ToolbarButton(
                            soundIcon,
                            "Audio On/Off",
                            false,
                            color = FocusColors.lightGray,
                            onClick = {
                                immersiveActivity?.SwitchAudio()
                            }
                        )

                        ToolbarButton(
                            R.drawable.settings,
                            "Settings",
                            false,
                            color = FocusColors.lightGray,
                            onClick = {
                                immersiveActivity?.OpenSettingsPanel()
                            }
                        )
                    }
                }

                Box(Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(LocalShapes.current.large)
                    .background(SpatialTheme.colorScheme.panel)
                    .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        ) {
                        ToolbarButton(
                            R.drawable.tasks,
                            "Open Tasks Panel",
                            false,
                            color = FocusColors.lightBlue,
                            onClick = {
                                immersiveActivity?.ShowTasksPanel(true)
                            }
                        )

                        ToolbarButton(
                            R.drawable.browser,
                            "Open Browser",
                            false,
                            color = FocusColors.lightGreen,
                            onClick = {
                                immersiveActivity?.OpenWebView()
                            }
                        )

                        if (immersiveActivity != null && immersiveActivity.AIenabled) {
                            ToolbarButton(
                                R.drawable.ai,
                                "Chat with AI",
                                false,
                                color = FocusColors.aiPurple,
                                onClick = {
                                    immersiveActivity.ShowAIPanel(true)
                                }
                            )
                        }

                        VerticalDivider(
                            thickness = 1.dp,
                            color = FocusColors.darkGray
                        )

                        ToolbarButton(
                            R.drawable.sticky,
                            "Add Sticky Note",
                            selectedTool == 0,
                            onClick = {
                                if (selectedTool != 0) FocusViewModel.instance.setSelectedTool(0)
                                else FocusViewModel.instance.setSelectedTool(-1)
                                immersiveActivity?.openSubPanel(immersiveActivity.stickySubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.labels,
                            "Insert Label",
                            selectedTool == 1,
                            onClick = {
                                if (selectedTool != 1) FocusViewModel.instance.setSelectedTool(1)
                                else FocusViewModel.instance.setSelectedTool(-1)
                                immersiveActivity?.openSubPanel(immersiveActivity.labelSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.arrows,
                            "Insert Flow Arrow",
                            selectedTool == 2,
                            onClick = {
                                if (selectedTool != 2) FocusViewModel.instance.setSelectedTool(2)
                                else FocusViewModel.instance.setSelectedTool(-1)
                                immersiveActivity?.openSubPanel(immersiveActivity.arrowSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.boards,
                            "Insert Board",
                            selectedTool == 3,
                            onClick = {
                                if (selectedTool != 3) FocusViewModel.instance.setSelectedTool(3)
                                else FocusViewModel.instance.setSelectedTool(-1)
                                immersiveActivity?.openSubPanel(immersiveActivity.boardSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.shapes,
                            "Insert Shape",
                            selectedTool == 4,
                            onClick = {
                                if (selectedTool != 4) FocusViewModel.instance.setSelectedTool(4)
                                else FocusViewModel.instance.setSelectedTool(-1)
                                immersiveActivity?.openSubPanel(immersiveActivity.shapeSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.stickers,
                            "Insert Sticker",
                            selectedTool == 5,
                            onClick = {
                                if (selectedTool != 5) FocusViewModel.instance.setSelectedTool(5)
                                else FocusViewModel.instance.setSelectedTool(-1)
                                immersiveActivity?.openSubPanel(immersiveActivity.stickerSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.timer,
                            "Set Timer",
                            selectedTool == 6,
                            onClick = {
                                if (selectedTool != 6) FocusViewModel.instance.setSelectedTool(6)
                                else FocusViewModel.instance.setSelectedTool(-1)
                                immersiveActivity?.openSubPanel(immersiveActivity.timerSubPanel)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToolbarButton(
    icon: Int,
    contentDescription: String,
    selected: Boolean,
    color: Color = FocusColors.lightPurple,
    selectedColor: Color = FocusColors.selectedLightPurple,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        selected -> selectedColor
        else -> color
    }

    SpatialSideNavItem(
        Modifier
            .size(75.dp)
            .fillMaxHeight()
            .background(backgroundColor, LocalShapes.current.small)
            .aspectRatio(1f),
        icon = { Icon(
            painterResource(id = icon),
            contentDescription = contentDescription,
            tint = Color.Unspecified
        )},
        primaryLabel = "",
        collapsed = true,
        selected = selected,
        onClick = {
            onClick()
        }
    )
}

@Preview(
    widthDp = (0.65f * FOCUS_DP).toInt(),
    heightDp = (0.065f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun ToolbarPreview() {
    ToolbarPanel()
}