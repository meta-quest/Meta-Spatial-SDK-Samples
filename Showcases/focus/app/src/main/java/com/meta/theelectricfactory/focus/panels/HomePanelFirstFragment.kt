// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.spatial.uiset.button.TextTileButton
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.theelectricfactory.focus.managers.DatabaseManager
import com.meta.theelectricfactory.focus.fragments.FirstFragment
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.managers.AudioManager
import com.meta.theelectricfactory.focus.managers.ProjectManager
import com.meta.theelectricfactory.focus.ui.FocusColorSchemes
import com.meta.theelectricfactory.focus.ui.FocusShapes
import com.meta.theelectricfactory.focus.ui.focusColorScheme
import com.meta.theelectricfactory.focus.ui.focusShapes
import com.meta.theelectricfactory.focus.utils.FOCUS_DP

data class ProjectCardData(val uuid: Int, val name: String, val timeAgo: String)

@Composable
fun HomePanelFirstFragmentScreen(projects: List<ProjectCardData>) {

    val immA = ImmersiveActivity.getInstance()

    LaunchedEffect(Unit) {
        if (projects.isEmpty()) {
            FirstFragment.instance.get()?.refreshProjects()
        }
    }

    FocusTheme {
        Box {
            Column(
                modifier = Modifier
                    .clip(LocalShapes.current.large)
                    .background(LocalColorScheme.current.panel)
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SpatialTheme(
                    shapes = focusShapes(FocusShapes.Squared)
                ) {
                    PrimaryButton(
                        label = "+ New Project",
                        onClick = {
                            ProjectManager.instance.newProject()
                            FirstFragment.instance.get()?.moveToSecondFragment()
                        },
                        expanded = true
                    )
                }

                Spacer(modifier = Modifier.size(40.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = FocusColors.gray
                )

                Spacer(modifier = Modifier.size(40.dp))

                ProjectGrid(
                    projects = projects,
                    onDelete = { uuid ->
                        AudioManager.instance.playDeleteSound()

                        immA?.DB?.deleteProject(uuid)
                        FirstFragment.instance.get()?.refreshProjects()
                    }
                )
            }
        }
    }
}

@Composable
fun ProjectGrid(projects: List<ProjectCardData>, onDelete: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(projects) { project ->
            ProjectCard(project, onDelete)
        }
    }
}

@Composable
fun ProjectCard(project: ProjectCardData, onDelete: (Int) -> Unit) {
    Box {
        SpatialTheme(
            colorScheme = focusColorScheme(FocusColorSchemes.Gray),
            shapes = focusShapes(FocusShapes.Squared)
        ) {
            TextTileButton(
                modifier = Modifier
                    .height(230.dp)
                    .fillMaxSize(),
                label = project.name,
                secondaryLabel = project.timeAgo,
                onSelectionChange = {
                    ProjectManager.instance.loadProject(project.uuid)
                }
            )
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            SecondaryCircleButton(
                icon = {
                    Icon(
                        painterResource(id = R.drawable.delete_task),
                        contentDescription = "Delete project",
                        tint = Color.Unspecified
                    )
                },
                onClick = { onDelete(project.uuid) }
            )
        }
    }
}

@Suppress("Range")
fun getProjectsFromDB(): List<ProjectCardData> {
    val cursor = ImmersiveActivity.getInstance()?.DB?.getProjects()
    val projects = mutableListOf<ProjectCardData>()

    if (cursor?.moveToFirst() == true) {
        while (!cursor.isAfterLast) {
            val uuid = cursor.getInt(cursor.getColumnIndex(DatabaseManager.PROJECT_UUID))
            val name = cursor.getString(cursor.getColumnIndex(DatabaseManager.PROJECT_NAME))
            val lastOpening = cursor.getString(cursor.getColumnIndex(DatabaseManager.PROJECT_LAST_OPENING)).toLong()

            val timePassed = System.currentTimeMillis() - lastOpening
            val seconds = timePassed / 1000
            val minutes = timePassed / (1000 * 60)
            val hours = timePassed / (1000 * 60 * 60)
            val days = timePassed / (1000 * 60 * 60 * 24)

            val timeAgo = when {
                days > 0 -> "Edited $days days ago"
                hours > 0 -> "Edited $hours hours ago"
                minutes > 0 -> "Edited $minutes minutes ago"
                else -> "Edited $seconds seconds ago"
            }

            projects.add(ProjectCardData(uuid, name, timeAgo))
            cursor.moveToNext()
        }
    }

    cursor?.close()
    return projects
}

@Preview(
    widthDp = (0.58f * FOCUS_DP).toInt(),
    heightDp = (0.41f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun HomePanelFirstFragmentScreenPreview() {
    val fakeProjects = listOf(
        ProjectCardData(uuid = 1, name = "Project One", timeAgo = "time"),
        ProjectCardData(uuid = 2, name = "Project Two", timeAgo = "time"),
        ProjectCardData(uuid = 2, name = "Project Two", timeAgo = "time"),
        ProjectCardData(uuid = 2, name = "Project Two", timeAgo = "time"),
        ProjectCardData(uuid = 2, name = "Project Two", timeAgo = "time")
    )
    HomePanelFirstFragmentScreen(fakeProjects)
}
