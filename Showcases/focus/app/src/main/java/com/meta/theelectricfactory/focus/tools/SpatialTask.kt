// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.tools

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.AttachableComponent
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.panels.Task
import com.meta.theelectricfactory.focus.panels.TaskCard
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.utils.addDeleteButton
import com.meta.theelectricfactory.focus.utils.getDisposableID
import com.meta.theelectricfactory.focus.utils.placeInFront

// Class to create a Spatial Task
@SuppressLint("Range")
class SpatialTask(
    task: Task,
    val new: Boolean = false,
    mainTaskLabelState: MutableIntState? = null,
    mainTaskLabelPriority: MutableIntState? = null,
    mainTaskTitle: MutableState<String>? = null,
    mainTaskBody: MutableState<String>? = null
) {
    var immersiveActivity = ImmersiveActivity.getInstance()

    init {
        var id = getDisposableID()

        // Create an entity with a panel component
        val taskPanelEntity: Entity =
            Entity.createPanelEntity(id, Transform(task.pose), Grabbable(true, GrabbableType.PIVOT_Y))

        // Register the panel
        immersiveActivity?.registerPanel(
            immersiveActivity!!.PanelRegistration(id, 0.24f, 0.15f) {
                FocusTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(SpatialTheme.shapes.large)
                            .background(FocusColors.panel),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(40.dp, 0.dp)
                        ) {
                            TaskCard(
                                task = task,
                                isSpatial = true,
                                mainTaskLabelState = mainTaskLabelState,
                                mainTaskLabelPriority = mainTaskLabelPriority,
                                mainTaskTitle = mainTaskTitle,
                                mainTaskBody = mainTaskBody
                            )
                        }
                    }
                }
            // We add a listener to show delete button when entity is selected
            }.panel{ addDeleteButton(taskPanelEntity, this) }
        )

        // Place in front of user in case is new
        if (new) placeInFront(taskPanelEntity)
        taskPanelEntity.setComponent(ToolComponent(task.uuid, AssetType.TASK, Vector3(0f, 0.11f, -0.005f)))
        // We add an AttachableComponent to the object to be able to "stick" it to the boards
        taskPanelEntity.setComponent(AttachableComponent())

        if (new) immersiveActivity?.playCreationSound(taskPanelEntity.getComponent<Transform>().transform.t)
    }
}
