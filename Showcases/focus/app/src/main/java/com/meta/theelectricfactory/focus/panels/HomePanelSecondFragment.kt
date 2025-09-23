// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.input.SpatialTextField
import com.meta.spatial.uiset.theme.SpatialTheme
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.spatial.uiset.tooltip.SpatialTooltipContent
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.managers.PanelManager
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.fragments.SecondFragment
import com.meta.theelectricfactory.focus.managers.ProjectManager
import com.meta.theelectricfactory.focus.ui.FocusColorSchemes
import com.meta.theelectricfactory.focus.ui.FocusShapes
import com.meta.theelectricfactory.focus.ui.focusColorScheme
import com.meta.theelectricfactory.focus.ui.focusShapes
import com.meta.theelectricfactory.focus.utils.FOCUS_DP

@Composable
fun HomePanelSecondFragmentScreen() {

    var saveButtonLabel = if (ProjectManager.instance.currentProject != null) "Save project" else "Create project"
    var projectNameInput = remember {
        mutableStateOf(
            if (ProjectManager.instance.currentProject != null) ProjectManager.instance.currentProject!!.name
            else ""
        )
    }
    var envSelected = remember {
        mutableIntStateOf(
            if (ProjectManager.instance.currentProject != null && !ProjectManager.instance.currentProject!!.MR) ProjectManager.instance.currentProject?.environment!!
            else 3
        )
    }
    selectEnv(envSelected.intValue)

    return FocusTheme {
        Box {
            Column(
                modifier = Modifier
                    .clip(SpatialTheme.shapes.large)
                    .background(SpatialTheme.colorScheme.panel)
                    .padding(40.dp),
            ) {
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Project Settings",
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                    )

                    SecondaryCircleButton(
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.close),
                                contentDescription = "Close",
                                tint = Color.Unspecified
                            )
                        },
                        onClick = {
                            // If we don't have a current project open, we return Home Panel first view.
                            if (ProjectManager.instance.currentProject == null) {
                                projectNameInput.value = ""
                                ProjectManager.instance.newProject()
                                SecondFragment.instance.get()?.moveToFirstFragment()
                            } else {
                                // If it's an old project, we update project info each time the user closes or changes a property
                                saveCurrentProject(projectNameInput, envSelected.intValue)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.size(30.dp))

                SpatialTheme(
                    colorScheme = focusColorScheme(FocusColorSchemes.Gray),
                    shapes = focusShapes(FocusShapes.FullRounded)
                ) {
                    SpatialTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = "Project name",
                        placeholder = "Enter project name",
                        value = projectNameInput.value,
                        onValueChange = { projectNameInput.value = it },
                        singleLine = true,
                    )
                }

                Spacer(modifier = Modifier.size(25.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = FocusColors.gray
                )

                Spacer(modifier = Modifier.size(25.dp))

                Text(
                    text = "Select your environment",
                    color = FocusColors.darkGray,
                    fontSize = 15.sp,
                )

                Spacer(modifier = Modifier.size(5.dp))

                LazyVerticalGrid(
                    modifier = Modifier.height(375.dp),
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    data class Environment(val name: String, val img: Int, val index: Int, val label: String)
                    val environments = listOf(
                        Environment("Desert Retreat", R.drawable.env1, 0, "VR"),
                        Environment("Concrete Sanctuary", R.drawable.env2, 1, "VR"),
                        Environment("Blush Oasis", R.drawable.env3, 2, "VR"),
                        Environment("Passthrough", R.drawable.env4, 3, "MR"),
                    );

                    items(environments) { environment ->
                        Box(
                            modifier = Modifier
                                .height(375.dp)
                                .clip(SpatialTheme.shapes.small)
                                .selectable(
                                    selected = envSelected.intValue == environment.index,
                                    onClick = {
                                        if (ProjectManager.instance.currentProject != null) {
                                            ProjectManager.instance.currentProject!!.environment = environment.index
                                            ProjectManager.instance.saveProjectSettings(envSelected.intValue != 3, projectNameInput.value)
                                        }
                                        envSelected.intValue = environment.index
                                    },
                                ),
                        ) {
                            Image(
                                painter = painterResource(id = environment.img),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Selected border
                            if (envSelected.intValue == environment.index) {
                                Image(
                                    painter = painterResource(id = R.drawable.border),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Text(
                                text = environment.name,
                                color = Color.White,
                                lineHeight = 25.sp,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(25.dp).width(120.dp),
                            )

                            Row(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(90.dp)
                                    .padding(25.dp)
                                    .align(Alignment.TopEnd),
                            ) {

                                SpatialTheme(
                                    colorScheme = focusColorScheme(FocusColorSchemes.GrayTooltip),
                                ) {
                                    SpatialTooltipContent(
                                        modifier = Modifier
                                            .clip(LocalShapes.current.xxSmall),
                                        title = environment.label,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                Box (
                    modifier = Modifier.align(Alignment.End),
                ) {
                    SpatialTheme(
                        shapes = focusShapes(FocusShapes.Squared)
                    ) {
                        PrimaryButton(
                            label = saveButtonLabel,
                            onClick = {
                                saveCurrentProject(projectNameInput, envSelected.intValue)
                            },
                        )
                    }
                }
            }
        }
    }
}

fun selectEnv(env: Int) {
    if (env != 3) ImmersiveActivity.getInstance()?.selectEnvironment(env)
    else ImmersiveActivity.getInstance()?.selectMRMode()
}

fun saveCurrentProject(
    projectNameInput: MutableState<String>,
    envSelected: Int
) {
    if (projectNameInput.value.isEmpty()) {
        projectNameInput.value = "Untitled"
    }

    ProjectManager.instance.saveProjectSettings(envSelected == 3, projectNameInput.value)
    PanelManager.instance.homePanel.setComponent(Visible(false))
}

@Preview(
    widthDp = (0.58f * FOCUS_DP).toInt(),
    heightDp = (0.41f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun HomePanelSecondFragmentScreenPreview() {
    HomePanelSecondFragmentScreen()
}



