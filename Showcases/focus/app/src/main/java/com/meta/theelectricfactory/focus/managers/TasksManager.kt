// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.managers

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Query
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.viewmodels.FocusViewModel

data class Task(
    val uuid: Int,
    var title: String,
    val body: String,
    var state: Int,
    var priority: Int,
    val detached: Int,
    val parentUuid: Int,
    val pose: Pose,
)

class TasksManager {

  val immA: ImmersiveActivity?
    get() = ImmersiveActivity.getInstance()

  companion object {
    val instance: TasksManager by lazy { TasksManager() }
  }

  fun getSpatialTask(uuid: Int): Entity? {
    val tools = Query.where { has(ToolComponent.id) }
    for (entity in tools.eval()) {
      val entityUuid = entity.getComponent<ToolComponent>().uuid
      if (uuid == entityUuid) {
        return entity
      }
    }
    return null
  }

  fun deleteTask(uuid: Int) {
    immA?.DB?.deleteTask(uuid)
    FocusViewModel.instance.refreshTasksPanel()

    // Delete correspondent spatial task if exists
    var ent = getSpatialTask(uuid)
    if (ent != null) {
      // if current object selected is spatial task, we detach delete button first.
      if (immA?.currentObjectSelected != null && immA?.currentObjectSelected!!.equals(ent)) {
        immA?.currentObjectSelected = null
        immA?.deleteButton?.setComponent(TransformParent())
        immA?.deleteButton?.setComponent(Visible(false))
      }

      ent.destroy()
      AudioManager.instance.playDeleteSound(
          PanelManager.instance.tasksPanel.getComponent<Transform>().transform.t
      )
    }
  }
}
