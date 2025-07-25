// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.tools

import android.net.Uri
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Animated
import com.meta.spatial.toolkit.Box
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.AttachableComponent
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.utils.addDeleteButton
import com.meta.theelectricfactory.focus.utils.getNewUUID
import com.meta.theelectricfactory.focus.utils.placeInFront

// Class to create a Tool, could be a Sticker, Label, Shape or Board
class Tool(
    val type: AssetType?,
    val source: String = "",
    val size: Float = 1f,
    var uuid: Int = -1,
    var pose: Pose = Pose(),
    var deleteButtonHeight: Float = 0.08f
) {
    init {

        var obj: Entity

        // In case it is a 3D shape
        if (type == AssetType.SHAPE_3D) {
            obj =
                Entity.create(
                    Mesh(mesh = Uri.parse(source)),
                    Scale(Vector3(size)),
                    Grabbable(true, GrabbableType.PIVOT_Y),
                    Transform(pose),
                    Animated(System.currentTimeMillis()))
            // The rest of the tools are all 2D
        } else {
            obj =
                Entity.create(
                    Mesh(Uri.parse("mesh://box")),
                    Box(Vector3(-size, -size, 0f), Vector3(size, size, 0f)),
                    Material().apply {
                        // We set the correct texture in each case
                        baseTextureAndroidResourceId = source.toInt()
                        alphaMode = 1
                        unlit = true
                    },
                    Grabbable(true, GrabbableType.PIVOT_Y),
                    Transform(pose))
        }

        // We add a listener to show delete button when entity is selected
        addDeleteButton(obj)

        // We add an AttachableComponent to the object to be able to "stick" it to the boards
        if (type == AssetType.BOARD) {
            obj.setComponent(AttachableComponent(1))
        } else if (type == AssetType.STICKY_NOTE ||
            type == AssetType.LABEL ||
            type == AssetType.ARROW ||
            type == AssetType.SHAPE_2D ||
            type == AssetType.STICKER
        ) {

            obj.setComponent(AttachableComponent())
        }

        // If tool is new, we save it in database
        if (uuid == -1) {
            if (type == AssetType.BOARD) placeInFront(obj, bigPanel = true) else placeInFront(obj)
            uuid = getNewUUID()
            ImmersiveActivity.instance
                .get()
                ?.DB
                ?.createToolAsset(
                    uuid,
                    ImmersiveActivity.instance.get()?.currentProject?.uuid,
                    type,
                    source,
                    size,
                    deleteButtonHeight,
                    obj.getComponent<Transform>().transform)
            ImmersiveActivity.instance.get()?.playCreationSound(obj.getComponent<Transform>().transform.t)
        }

        // We add it a ToolComponent to be able to identify it and get the type and uuid of the entity
        obj.setComponent(ToolComponent(uuid, type!!, Vector3(0f, deleteButtonHeight, -0.005f)))
    }
}
