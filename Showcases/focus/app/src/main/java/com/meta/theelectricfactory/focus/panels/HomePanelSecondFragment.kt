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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.unit.sp
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.SecondFragment
import com.meta.theelectricfactory.focus.ui.focusColorScheme
import com.meta.theelectricfactory.focus.ui.squareShapes
import com.meta.theelectricfactory.focus.utils.focusDP

@Composable
fun HomePanelSecondFragmentScreen() {

    var immersiveActivity = ImmersiveActivity.getInstance()

    var saveButtonLabel = if (immersiveActivity?.currentProject != null) "Save project" else "Create project"
    var projectNameInput = remember {
        mutableStateOf(
            if (immersiveActivity?.currentProject != null) immersiveActivity.currentProject!!.name
            else ""
        )
    }
    var envSelected = remember {
        mutableIntStateOf(
            if (immersiveActivity?.currentProject != null && !immersiveActivity.currentProject!!.MR) immersiveActivity.currentProject?.environment!!
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
                    )

                    Box(modifier = Modifier
                        .aspectRatio(1f)
                    ) {
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
                                if (ImmersiveActivity.getInstance()?.currentProject == null) {
                                    projectNameInput.value = ""
                                    ImmersiveActivity.getInstance()?.newProject()
                                    SecondFragment.instance.get()?.moveToFirstFragment()
                                } else {
                                    // If it's an old project, we update project info each time the user closes or changes a property
                                    saveCurrentProject(projectNameInput, envSelected.intValue)
                                }
                            }
                        )

                    }


                }

                Spacer(modifier = Modifier.size(30.dp))

                SpatialTheme(
                    colorScheme = focusColorScheme(true)
                ) {
                    SpatialTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = "Project name",
                        placeholder = "Enter project name",
                        value = projectNameInput.value,
                        onValueChange = { projectNameInput.value = it }
                    )
                }

                Spacer(modifier = Modifier.size(30.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = FocusColors.lightGray
                )

                Spacer(modifier = Modifier.size(30.dp))

                Text(
                    text = "Select your environment",
                    color = FocusColors.darkGray,
                    fontSize = 20.sp,
                )

                Spacer(modifier = Modifier.size(15.dp))

                LazyVerticalGrid(
                    modifier = Modifier.height(480.dp),
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
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
                                .height(480.dp)
                                .clip(SpatialTheme.shapes.medium)
                                .selectable(
                                    selected = envSelected.intValue == environment.index,
                                    onClick = {
                                        if (ImmersiveActivity.getInstance()?.currentProject != null) {
                                            ImmersiveActivity.getInstance()?.currentProject!!.environment = environment.index
                                            ImmersiveActivity.getInstance()
                                                ?.saveProjectSettings(envSelected.intValue != 3, projectNameInput.value)
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
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(30.dp).width(130.dp),
                            )

                            Row(
                                modifier = Modifier
                                    .height(110.dp)
                                    .padding(30.dp)
                                    .align(Alignment.TopEnd),
                            ) {

                                SpatialTheme(
                                    colorScheme = focusColorScheme(true)
                                ) {
                                    PrimaryButton(
                                        label = environment.label,
                                        onClick = {}
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
                        shapes = squareShapes()
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

// Show a border for selected environment image
fun selectEnv(env: Int) {
    if (env != 3) ImmersiveActivity.getInstance()?.selectEnvironment(env)
    else ImmersiveActivity.getInstance()?.selectMRMode()

    // TODO Show correct UI
}

fun saveCurrentProject(
    projectNameInput: MutableState<String>,
    envSelected: Int
) {
    if (projectNameInput.value.isEmpty()) {
        projectNameInput.value = "Untitled"
    }

    ImmersiveActivity.getInstance()?.saveProjectSettings(envSelected == 3, projectNameInput.value)
    ImmersiveActivity.getInstance()?.homePanel?.setComponent(Visible(false))
}

@Preview(
    widthDp = (0.58f * focusDP).toInt(),
    heightDp = (0.41f * focusDP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun HomePanelSecondFragmentScreenPreview() {
    HomePanelSecondFragmentScreen()
}



