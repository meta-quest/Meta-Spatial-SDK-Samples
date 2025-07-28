// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.button.PrimaryButton
import com.meta.spatial.uiset.theme.SpatialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3
import com.meta.spatial.uiset.button.BorderlessIconButton
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.spatial.uiset.tooltip.SpatialTooltipContent
import com.meta.theelectricfactory.focus.db.DatabaseManager
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.tools.SpatialTask
import com.meta.theelectricfactory.focus.utils.FOCUS_DP
import com.meta.theelectricfactory.focus.utils.getNewUUID
import com.meta.theelectricfactory.focus.ui.focusFont
import com.meta.theelectricfactory.focus.priorityLabels
import com.meta.theelectricfactory.focus.stateLabels
import com.meta.theelectricfactory.focus.ui.FocusColorSchemes
import com.meta.theelectricfactory.focus.ui.FocusShapes
import com.meta.theelectricfactory.focus.ui.focusColorScheme
import com.meta.theelectricfactory.focus.ui.focusShapes
import com.meta.theelectricfactory.focus.utils.FocusViewModel

data class Task(val uuid: Int, var title: String, val body: String, var state: Int, var priority: Int, val detached: Int, val pose: Pose)

@SuppressLint("Range")
@Composable
fun TasksPanel() {
    var immersiveActivity = ImmersiveActivity.getInstance()
    var titleInput = remember { mutableStateOf("") }
    var textInput = remember { mutableStateOf("") }
    val tasksList = remember { mutableStateListOf<Task>() }

    val templateTaskState = remember { mutableIntStateOf(0) }
    val templateTaskPriority = remember { mutableIntStateOf(0) }

    val tasksListHasChanged by FocusViewModel.instance.tasksListsHasChanged.collectAsState()
    LaunchedEffect(tasksListHasChanged) {
        tasksList.clear()
        val cursor = immersiveActivity?.DB?.getTasks(immersiveActivity.currentProject?.uuid)
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val uuid = cursor.getInt(cursor.getColumnIndex(DatabaseManager.TASK_UUID))
                val title = cursor.getString(cursor.getColumnIndex(DatabaseManager.TASK_TITLE))
                val body = cursor.getString(cursor.getColumnIndex(DatabaseManager.TASK_BODY))
                val state = cursor.getInt(cursor.getColumnIndex(DatabaseManager.TASK_STATE))
                val priority = cursor.getInt(cursor.getColumnIndex(DatabaseManager.TASK_PRIORITY))
                val detached = cursor.getInt(cursor.getColumnIndex(DatabaseManager.TASK_DETACH))

                val posX = cursor.getFloat(cursor.getColumnIndex(DatabaseManager.TASK_POSITION_X))
                val posY = cursor.getFloat(cursor.getColumnIndex(DatabaseManager.TASK_POSITION_Y))
                val posZ = cursor.getFloat(cursor.getColumnIndex(DatabaseManager.TASK_POSITION_Z))

                val rotW = cursor.getFloat(cursor.getColumnIndex(DatabaseManager.TASK_ROTATION_W))
                val rotX = cursor.getFloat(cursor.getColumnIndex(DatabaseManager.TASK_ROTATION_X))
                val rotY = cursor.getFloat(cursor.getColumnIndex(DatabaseManager.TASK_ROTATION_Y))
                val rotZ = cursor.getFloat(cursor.getColumnIndex(DatabaseManager.TASK_ROTATION_Z))

                val pose = Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ))

                tasksList.add(Task(uuid, title, body, state, priority, detached, pose))
                cursor.moveToNext()
            }
        }
    }

    return FocusTheme {
        Column(
            modifier = Modifier
                .padding(5.dp),
        ) {
            Row (modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(50.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpatialTheme(
                    colorScheme = focusColorScheme(FocusColorSchemes.BlueTooltip)
                ) {
                    SpatialTooltipContent(
                        modifier = Modifier
                            .clip(SpatialTheme.shapes.large),
                        title = "Tasks",
                    )
                }

                Box(modifier = Modifier
                    .height(40.dp)
                    .aspectRatio(1f)
                ) {
                    SecondaryCircleButton(
                        onClick = {
                            immersiveActivity?.ShowTasksPanel(false)
                        },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.close),
                                contentDescription = "Close",
                                tint = Color.Unspecified
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.size(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(SpatialTheme.shapes.large)
                    .background(FocusColors.panel),
                contentAlignment = Alignment.Center
            ) {

                Column (
                    modifier = Modifier
                        .padding(30.dp)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    )
                    {
                        LabelButton(stateLabels[templateTaskState.intValue], { switchLabel(templateTaskState) })
                        LabelButton(priorityLabels[templateTaskPriority.intValue], { switchLabel(templateTaskPriority) })
                    }

                    Spacer(modifier = Modifier.size(20.dp))

                    //                SpatialTextField(
//                    label = "",
//                    placeholder = "Add text",
//                    value = "",
//                    onValueChange = { },
//                    autoValidate = false,
//                )

//                BasicTextField(
//                    value = "",
//                    onValueChange = { },
//                    modifier = Modifier.fillMaxWidth()
//                )

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = titleInput.value,
                        onValueChange = { titleInput.value = it },
                        placeholder = {
                            Text(
                                text ="Task title",
                                fontFamily = focusFont,
                                fontSize = 28.sp,
                            )},
                        textStyle = TextStyle(
                            fontSize = 28.sp,
                            fontFamily = focusFont
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                    )

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.55f),
                        value = textInput.value,
                        onValueChange = { textInput.value = it },
                        placeholder = {
                            Text(
                                text ="Add text",
                                fontFamily = focusFont,
                                fontSize = 20.sp,
                                color = FocusColors.darkGray
                            )},
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = focusFont,
                            color = FocusColors.darkGray
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )

                    Spacer(modifier = Modifier.size(20.dp))

                    SpatialTheme(
                        shapes = focusShapes(FocusShapes.Squared)
                    ) {
                        PrimaryButton(
                            label = "+ Add Task",
                            onClick = {
                                val _uuid = getNewUUID()
                                immersiveActivity?.DB?.createTask(
                                    _uuid,
                                    immersiveActivity.currentProject?.uuid,
                                    titleInput.value,
                                    textInput.value,
                                    templateTaskState.intValue,
                                    templateTaskPriority.intValue
                                )
                                titleInput.value = ""
                                textInput.value = ""
                                templateTaskState.intValue = 0
                                templateTaskPriority.intValue = 0
                                FocusViewModel.instance.refreshTasksPanel()
                            },
                            isEnabled = titleInput.value.isNotEmpty(),
                            expanded = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(SpatialTheme.shapes.large)
                    .background(FocusColors.panel),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(30.dp, 10.dp),
                ) {
                    items(tasksList, key = { it.uuid }) { task ->
                        TaskCard(task)
                    }
                }
            }
        }
    }
}

fun switchLabel(label: MutableIntState) {
    if (label.intValue == 2) label.intValue = 0
    else label.intValue++
}

@SuppressLint("Range")
@Composable
fun TaskCard(
    task: Task,
    isSpatial: Boolean = false,
    mainTaskLabelState: MutableIntState? = null,
    mainTaskLabelPriority: MutableIntState? = null,
    mainTaskTitle: MutableState<String>? = null,
    mainTaskBody: MutableState<String>? = null
) {
    val key = "init_${task.uuid}" //TODO

    var taskLabelState = remember(key) { mutableIntStateOf(task.state) }
    var taskLabelPriority = remember(key) { mutableIntStateOf(task.priority) }

    var taskTitleInput = remember(key) { mutableStateOf(task.title) }
    var taskBodyInput = remember(key) { mutableStateOf(task.body) }

    val taskUpdated by FocusViewModel.instance.currentTaskUpdated.collectAsState()
    LaunchedEffect(taskUpdated) {
        if (isSpatial && task.uuid == FocusViewModel.instance.currentTaskUuid.value) {
            val cursor = ImmersiveActivity.getInstance()?.DB?.getTaskData(FocusViewModel.instance.currentTaskUuid.value)
            if (cursor != null && cursor.moveToFirst()) {
                val taskState = cursor.getInt(cursor.getColumnIndex(DatabaseManager.TASK_STATE))
                val taskPriority = cursor.getInt(cursor.getColumnIndex(DatabaseManager.TASK_PRIORITY))
                val taskTitle = cursor.getString(cursor.getColumnIndex(DatabaseManager.TASK_TITLE))
                val taskBody = cursor.getString(cursor.getColumnIndex(DatabaseManager.TASK_BODY))

                taskLabelState.intValue = taskState
                taskLabelPriority.intValue = taskPriority
                taskTitleInput.value = taskTitle
                taskBodyInput.value = taskBody
            }
            cursor?.close()
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box() {
            Box(modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LabelButton(stateLabels[taskLabelState.intValue], {
                        var newState = taskLabelState.intValue + 1
                        if (newState > 2) newState = 0
                        taskLabelState.intValue = newState
                        ImmersiveActivity.getInstance()?.DB?.updateTaskData(
                            task.uuid,
                            state = newState
                        )
                        if (isSpatial) {
                            mainTaskLabelState!!.intValue = newState
                        } else {
                            FocusViewModel.instance.setCurrentTaskUuid(
                                task.uuid
                            )
                            FocusViewModel.instance.updateCurrentSpatialTask()
                        }
                    })

                    LabelButton(priorityLabels[taskLabelPriority.intValue], {
                        var newPriority = taskLabelPriority.intValue + 1
                        if (newPriority > 2) newPriority = 0
                        taskLabelPriority.intValue = newPriority
                        ImmersiveActivity.getInstance()?.DB?.updateTaskData(
                            task.uuid,
                            priority = newPriority
                        )
                        if (isSpatial) {
                            mainTaskLabelPriority!!.intValue = newPriority
                        } else {
                            FocusViewModel.instance.setCurrentTaskUuid(
                                task.uuid
                            )
                            FocusViewModel.instance.updateCurrentSpatialTask()
                        }
                    })
                }
            }

            Box(modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (task.detached == 0 && !isSpatial) {
                        BorderlessIconButton(
                            icon = {
                                Icon(
                                    painterResource(id = R.drawable.detach),
                                    contentDescription = "Detach",
                                    tint = Color.Unspecified
                                )
                            },
                            onClick = {
                                // Spatial task is created and saved in database when detach button is pressed
                                ImmersiveActivity.getInstance()?.DB?.updateTaskData(
                                    task.uuid,
                                    detach = 1
                                )
                                FocusViewModel.instance.refreshTasksPanel()
                                SpatialTask(
                                    task = task,
                                    new = true,
                                    mainTaskLabelState = taskLabelState,
                                    mainTaskLabelPriority = taskLabelPriority,
                                    mainTaskTitle = taskTitleInput,
                                    mainTaskBody = taskBodyInput
                                )
                            }
                        )
                    }

                    BorderlessIconButton(
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.delete_task),
                                contentDescription = "Delete",
                                tint = Color.Unspecified
                            )
                        },
                        onClick = {
                            ImmersiveActivity.getInstance()!!.deleteTask(task.uuid)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(20.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = taskTitleInput.value,
            onValueChange = { taskTitleInput.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    ImmersiveActivity.getInstance()!!.DB.updateTaskData(task.uuid, title = taskTitleInput.value, body = taskBodyInput.value)
                    if (isSpatial) {
                        mainTaskTitle!!.value = taskTitleInput.value
                    } else {
                        FocusViewModel.instance.setCurrentTaskUuid(task.uuid)
                        FocusViewModel.instance.updateCurrentSpatialTask()
                    }
                }
            ),
            textStyle = TextStyle(
                fontSize = 23.sp,
                fontFamily = focusFont
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            value = taskBodyInput.value,
            onValueChange = { taskBodyInput.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    ImmersiveActivity.getInstance()!!.DB.updateTaskData(task.uuid, title = taskTitleInput.value, body = taskBodyInput.value)
                    if (isSpatial) {
                        mainTaskBody!!.value = taskBodyInput.value
                    } else {
                        FocusViewModel.instance.setCurrentTaskUuid(task.uuid)
                        FocusViewModel.instance.updateCurrentSpatialTask()
                    }
                }
            ),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontFamily = focusFont,
                color = FocusColors.darkGray
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )

        Spacer(modifier = Modifier.size(20.dp))

        if (!isSpatial) HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFD4D3DC)
        )
    }

    if (task.detached == 1 && !isSpatial) {
        if (ImmersiveActivity.getInstance()?.getSpatialTask(task.uuid) == null) {
            SpatialTask(
                task = task,
                mainTaskLabelState = taskLabelState,
                mainTaskLabelPriority = taskLabelPriority,
                mainTaskTitle = taskTitleInput,
                mainTaskBody = taskBodyInput
            )
        }
    }
}

@Preview(
    widthDp = (0.275f * FOCUS_DP).toInt(),
    heightDp = (0.5f * FOCUS_DP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun TasksPanelPreview() {
    TasksPanel()
}