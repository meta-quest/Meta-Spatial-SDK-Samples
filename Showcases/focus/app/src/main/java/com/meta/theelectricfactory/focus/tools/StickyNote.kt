// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.tools

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.AttachableComponent
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.managers.PanelManager
import com.meta.theelectricfactory.focus.data.StickyColor
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.managers.AudioManager
import com.meta.theelectricfactory.focus.managers.ProjectManager
import com.meta.theelectricfactory.focus.panels.StickyNotePanel
import com.meta.theelectricfactory.focus.utils.addDeleteButton
import com.meta.theelectricfactory.focus.utils.getDisposableID
import com.meta.theelectricfactory.focus.utils.getNewUUID
import com.meta.theelectricfactory.focus.utils.placeInFront

// Class to create a Sticky Note
class StickyNote(
    var uuid: Int = -1,
    var message: String = "",
    var color: StickyColor,
    var pose: Pose = Pose(),
    var parentUuid: Int = -1
) {
    var immA = ImmersiveActivity.getInstance()

    init {
        var id = getDisposableID()

        // Create a grabbable entity with a Panel
        val sticky: Entity =
            Entity.createPanelEntity(id, Transform(pose), Grabbable(true, GrabbableType.PIVOT_Y))

        // If Sticky is new, we save it in database as well
        if (uuid == -1) {
            placeInFront(sticky)
            uuid = getNewUUID()
            immA?.DB?.createSticky(
                uuid,
                ProjectManager.instance.currentProject?.uuid,
                color,
                message,
                sticky.getComponent<Transform>().transform
            )
            AudioManager.instance.playCreationSound(sticky.getComponent<Transform>().transform.t)
        }

        // Register the panel
        immA?.registerPanel(
            PanelManager.instance.panelRegistration(id, 0.14f, 0.14f) {
                StickyNotePanel(
                    uuid = uuid,
                    message = message,
                    color = color
                )
                // We add a listener to show delete button when entity is selected
            }.panel{ addDeleteButton(sticky, this) }
        )

        // We add it a ToolComponent to be able to identify it and get the type and uuid of the entity
        sticky.setComponent(ToolComponent(uuid, AssetType.STICKY_NOTE, Vector3(0f, 0.115f, -0.005f)))
        // We add an AttachableComponent to the object to be able to "stick" it to the boards
        sticky.setComponent(AttachableComponent(parentUuid = parentUuid))
    }
}
