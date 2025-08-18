// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.annotation.SuppressLint
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialContext
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.Scene
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity

// Class to create a Spatial Task
@SuppressLint("Range")
class SpatialTask(
    scene: Scene,
    ctx: SpatialContext,
    var uuid: Int,
    val new: Boolean,
    var pose: Pose = Pose(),
) {

  init {

    var id = getDisposableID()
    val _width = 0.23f
    val _height = 0.15f
    val _widthInPx = 850
    val _dpi = 270

    ImmersiveActivity.instance
        .get()
        ?.registerPanel(
            PanelRegistration(id) {
              layoutResourceId = R.layout.task_layout
              config {
                themeResourceId = R.style.Theme_Focus_Transparent
                width = _width
                height = _height
                layoutWidthInPx = _widthInPx
                layoutHeightInPx = (_widthInPx * (_height / _width)).toInt()
                layoutDpi = _dpi
                includeGlass = false
                //                    enableLayer = true
                //                    enableTransparent = true
              }
              panel {
                // delete detach and delete buttons from task (this should be only visible in UI
                // tasks, not in spatial tasks)
                val parentLayout =
                    rootView
                        ?.findViewById<ConstraintLayout>(R.id.taskLayout)
                        ?.findViewById<ConstraintLayout>(R.id.taskContainer)
                val detachButton = rootView?.findViewById<ImageButton>(R.id.buttonDetach)
                val deleteButton = rootView?.findViewById<ImageButton>(R.id.buttonDeleteTask)
                parentLayout?.removeView(detachButton)
                parentLayout?.removeView(deleteButton)

                ImmersiveActivity.instance.get()?.updateTask(uuid, this)
                // We add a listener to show delete button when entity is selected
                addDeleteButton(this.entity!!, this)

                val titleTextInput: EditText? = rootView?.findViewById(R.id.editTaskTitle)
                val bodyTextInput: EditText? = rootView?.findViewById(R.id.editTaskBody)

                // Update info in database and tasks panel if the text or labels are modify from
                // this spatial task
                val buttonLabel1: ImageButton? = rootView?.findViewById(R.id.label1)
                buttonLabel1?.setOnClickListener {
                  val taskCursor = ImmersiveActivity.instance.get()?.DB?.getTaskData(uuid)
                  if (taskCursor!!.moveToFirst()) {
                    val dbState =
                        taskCursor.getInt(taskCursor.getColumnIndex(DatabaseManager.TASK_STATE))
                    val newState = if (dbState == 2) 0 else (dbState + 1)

                    buttonLabel1.setImageResource(stateLabels[newState])
                    buttonLabel1.layoutParams.width =
                        ImmersiveActivity.instance
                            .get()
                            ?.resources!!
                            .getDimension(
                                if (newState == 1) R.dimen.max_label1_size
                                else R.dimen.min_label1_size)
                            .toInt()
                    ImmersiveActivity.instance.get()?.DB?.updateTaskData(uuid, state = newState)
                    ImmersiveActivity.instance.get()?.cleanAndLoadTasks(true)

                    ImmersiveActivity.instance.get()?.strikeThroughText(titleTextInput!!, newState)
                    ImmersiveActivity.instance.get()?.strikeThroughText(bodyTextInput!!, newState)
                  }
                  taskCursor.close()
                }

                val buttonLabel2: ImageButton? = rootView?.findViewById(R.id.label2)
                buttonLabel2?.setOnClickListener {
                  val taskCursor = ImmersiveActivity.instance.get()?.DB?.getTaskData(uuid)
                  if (taskCursor!!.moveToFirst()) {
                    val dbPriority =
                        taskCursor.getInt(taskCursor.getColumnIndex(DatabaseManager.TASK_PRIORITY))
                    val newPriority = if (dbPriority == 2) 0 else (dbPriority + 1)

                    buttonLabel2.setImageResource(priorityLabels[newPriority])
                    ImmersiveActivity.instance
                        .get()
                        ?.DB
                        ?.updateTaskData(uuid, priority = newPriority)
                    ImmersiveActivity.instance.get()?.cleanAndLoadTasks(true)
                  }
                  taskCursor.close()
                }

                fun onComplete() {
                  ImmersiveActivity.instance
                      .get()
                      ?.DB
                      ?.updateTaskData(
                          uuid,
                          title = titleTextInput?.text.toString(),
                          body = bodyTextInput?.text.toString(),
                      )
                  ImmersiveActivity.instance.get()?.cleanAndLoadTasks(true)
                }
                addEditTextListeners(titleTextInput, ::onComplete)
                addEditTextListeners(bodyTextInput, ::onComplete, true)
              }
            })

    // Create an entity with a panel component
    val task: Entity =
        Entity.createPanelEntity(id, Transform(pose), Grabbable(true, GrabbableType.PIVOT_Y))

    // Place in front of user in case is new
    if (new) placeInFront(task)
    task.setComponent(ToolComponent(uuid, AssetType.TASK, Vector3(0f, 0.11f, -0.005f)))
    // We add an AttachableComponent to the object to be able to "stick" it to the boards
    task.setComponent(AttachableComponent())

    if (new)
        ImmersiveActivity.instance
            .get()
            ?.playCreationSound(task.getComponent<Transform>().transform.t)
  }
}
