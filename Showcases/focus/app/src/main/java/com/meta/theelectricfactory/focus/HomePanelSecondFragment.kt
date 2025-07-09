// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.meta.spatial.uiset.button.PrimaryCircleButton
import com.meta.spatial.uiset.button.TextTileButton
import com.meta.spatial.uiset.input.SpatialTextField
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography

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
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Project Settings",
                        color = LocalColorScheme.current.primaryButton //TODO get the color of the theme
                    )

                    Box(modifier = Modifier
                        .height(40.dp)          //TODO
                        .aspectRatio(1f)
                    ) {
                        PrimaryCircleButton(
                            icon = {
                                Icon(
                                    painterResource(id = R.drawable.close),
                                    contentDescription = "Close"
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

                Spacer(modifier = Modifier.size(40.dp))

                SpatialTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Project name",
                    placeholder = "Enter project name",
                    value = projectNameInput.value,
                    onValueChange = { projectNameInput.value = it }
                )

                Spacer(modifier = Modifier.size(40.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFD4D3DC)
                )

                Spacer(modifier = Modifier.size(40.dp))

                Text(
                    text = "Select your environment",
                    color = LocalColorScheme.current.primaryButton //TODO get the color of the theme
                )

                LazyVerticalGrid(
                    modifier = Modifier.height(380.dp),
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    data class Environment(val name: String, val index: Int)
                    val environments = listOf(
                        Environment("Desert Retreat", 0),
                        Environment("Concrete Sanctuary", 1),
                        Environment("Blush Oasis", 2),
                        Environment("Passthrough", 3),
                    );

                    items(environments) { environment ->
                        var reality = "VR"
                        if (environment.index == 3) {
                            reality = "MR"
                        }
                        TextTileButton(
                            modifier = Modifier.fillMaxHeight(),
                            icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                            label = environment.name,
                            secondaryLabel = reality,
                            selected =  envSelected.intValue == environment.index,
                            onSelectionChange = {
                                if (ImmersiveActivity.getInstance()?.currentProject != null) {
                                    ImmersiveActivity.getInstance()?.currentProject!!.environment = environment.index
                                    ImmersiveActivity.getInstance()
                                        ?.saveProjectSettings(envSelected.intValue != 3, projectNameInput.value)
                                }
                                envSelected.intValue = environment.index
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                Box (
                    modifier = Modifier.align(Alignment.End),
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
    widthDp = 1450,
    heightDp = 900,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun HomePanelSecondFragmentScreenPreview() {
    HomePanelSecondFragmentScreen()
}



