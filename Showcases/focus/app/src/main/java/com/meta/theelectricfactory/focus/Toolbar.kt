// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun ToolbarPanel() {

    var immersiveActivity = ImmersiveActivity.getInstance()
    val selectedTool = remember { mutableStateOf(-1) }

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
                    .clip(SpatialTheme.shapes.large)
                    .background(SpatialTheme.colorScheme.panel)
                    .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Row (
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                    ) {

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Home",
                            false,
                            color = FocusColors.lightGray,
                            onClick = {
                                immersiveActivity?.OpenHomePanel()
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Audio On/Off",
                            false,
                            color = FocusColors.lightGray,
                            onClick = {
                                immersiveActivity?.SwitchAudio()
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
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
                    .clip(SpatialTheme.shapes.large)
                    .background(SpatialTheme.colorScheme.panel)
                    .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        ) {
                        ToolbarButton(
                            R.drawable.delete_task,
                            "Open Tasks Panel",
                            false,
                            color = FocusColors.lightBlue,
                            onClick = {
                                immersiveActivity?.OpenTasksPanel()
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Open Browser",
                            false,
                            color = FocusColors.lightGreen,
                            onClick = {
                                immersiveActivity?.OpenWebView()
                            }
                        )

                        if (immersiveActivity != null && immersiveActivity.AIenabled) {
                            ToolbarButton(
                                R.drawable.delete_task,
                                "Chat with AI",
                                false,
                                color = FocusColors.aiPurple,
                                onClick = {
                                    immersiveActivity.OpenAIPanel()
                                }
                            )
                        }

                        VerticalDivider(
                            thickness = 1.dp,
                            color = Color(0xFFD4D3DC)
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Add Sticky Note",
                            selectedTool.value == 0,
                            onClick = {
                                if (selectedTool.value != 0) selectedTool.value = 0
                                else selectedTool.value = -1
                                immersiveActivity?.openSubPanel(immersiveActivity.stickySubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Insert Label",
                            selectedTool.value == 1,
                            onClick = {
                                if (selectedTool.value != 1) selectedTool.value = 1
                                else selectedTool.value = -1
                                immersiveActivity?.openSubPanel(immersiveActivity.labelSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Insert Flow Arrow",
                            selectedTool.value == 2,
                            onClick = {
                                if (selectedTool.value != 2) selectedTool.value = 2
                                else selectedTool.value = -1
                                immersiveActivity?.openSubPanel(immersiveActivity.arrowSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Insert Board",
                            selectedTool.value == 3,
                            onClick = {
                                if (selectedTool.value != 3) selectedTool.value = 3
                                else selectedTool.value = -1
                                immersiveActivity?.openSubPanel(immersiveActivity.boardSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Insert Shape",
                            selectedTool.value == 4,
                            onClick = {
                                if (selectedTool.value != 4) selectedTool.value = 4
                                else selectedTool.value = -1
                                immersiveActivity?.openSubPanel(immersiveActivity.shapeSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Insert Sticker",
                            selectedTool.value == 5,
                            onClick = {
                                if (selectedTool.value != 5) selectedTool.value = 5
                                else selectedTool.value = -1
                                immersiveActivity?.openSubPanel(immersiveActivity.stickerSubPanel)
                            }
                        )

                        ToolbarButton(
                            R.drawable.delete_task,
                            "Set Timer",
                            selectedTool.value == 6,
                            onClick = {
                                if (selectedTool.value != 6) selectedTool.value = 6
                                else selectedTool.value = -1
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
            .background(backgroundColor, RoundedCornerShape(15.dp))
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
    widthDp = (0.65f * focusDP).toInt(),
    heightDp = (0.065f * focusDP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun ToolbarPreview() {
    ToolbarPanel()
}