// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.utils

import android.app.Activity
import android.util.Log
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkGrabbable
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.ButtonDownEventArgs
import com.meta.spatial.runtime.ControllerButton
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.managers.PanelManager
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.data.arrowHeightArray
import com.meta.theelectricfactory.focus.data.boardHeightArray
import com.meta.theelectricfactory.focus.data.boardSizeArray
import com.meta.theelectricfactory.focus.data.shape2DSizeArray
import com.meta.theelectricfactory.focus.data.shape3DSizeArray
import com.meta.theelectricfactory.focus.managers.AudioManager
import com.meta.theelectricfactory.focus.viewmodels.FocusViewModel

var temporalID: Int = 0
const val FOCUS_DP: Int = 1800

// Creates temporal ids to identify entities during runtime
fun getDisposableID(): Int {
    temporalID += 1
    return temporalID
}

// Creates ids to identify objects in projects. This is not temporal
fun getNewUUID(): Int {
    val savedData =
        ImmersiveActivity.getInstance()!!.getSharedPreferences(
        "com.meta.theelectricfactory.focus.MySavedData", Activity.MODE_PRIVATE)
    val uuid = savedData.getInt("currentUUID", -1)

    with(savedData.edit()) {
        putInt("currentUUID", uuid + 1)
        apply()
    }

    Log.i("Focus", "Focus> NEW UUID GENERATED: ${uuid + 1}")
    return (uuid + 1)
}

// This function adds an input listener to an entity, to detect when has been selected and show the delete button on it
fun addDeleteButton(entity: Entity, panel: PanelSceneObject? = null) {
    // If entity has a Panel, we need to detect the input in the panel
    if (panel != null) {
        panel.addInputListener(
            object : InputListener {
                override fun onInput(
                    receiver: SceneObject,
                    hitInfo: HitInfo,
                    sourceOfInput: Entity,
                    changed: Int,
                    clicked: Int,
                    downTime: Long
                ): Boolean {
                    // Check if panel has been clicked/tapped with button A, X, triggers o squeezers
                    if ((changed and ButtonBits.ButtonA) != 0 ||
                        (changed and ButtonBits.ButtonX) != 0 ||
                        (changed and ButtonBits.ButtonTriggerR) != 0 ||
                        (changed and ButtonBits.ButtonTriggerL) != 0 ||
                        (changed and ButtonBits.ButtonSqueezeR) != 0 ||
                        (changed and ButtonBits.ButtonSqueezeL) != 0) {

                        if ((clicked and ButtonBits.ButtonA) != 0 ||
                            (clicked and ButtonBits.ButtonX) != 0 ||
                            (clicked and ButtonBits.ButtonTriggerR) != 0 ||
                            (clicked and ButtonBits.ButtonTriggerL) != 0 ||
                            (clicked and ButtonBits.ButtonSqueezeR) != 0 ||
                            (clicked and ButtonBits.ButtonSqueezeL) != 0) {

                            selectElement(entity)
                            return true
                        }
                    }
                    return false
                }
            })
        // If entity has a Mesh component, object is selected with a registerEventListener
    } else {
        addOnSelectListener(
            entity,
            fun() {
                selectElement(entity)
            })
    }
}

// Function to detect when an entity has been selected or grabbed
fun addOnSelectListener(entity: Entity, onClick: () -> (Unit)) {
    entity.registerEventListener<ButtonDownEventArgs>(ButtonDownEventArgs.EVENT_NAME) {
            ent,
            eventArgs ->
        if (eventArgs.button == ControllerButton.A ||
            eventArgs.button == ControllerButton.X ||
            eventArgs.button == ControllerButton.RightTrigger ||
            eventArgs.button == ControllerButton.LeftTrigger ||
            eventArgs.button == ControllerButton.RightSqueeze ||
            eventArgs.button == ControllerButton.LeftSqueeze) {
            onClick()
        }
    }
}

// Function to get the correct size for some tools.
fun getAssetSize(type: AssetType, index: Int): Float {
    var sizeArray = arrayOf(0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f) // Default size

    when (type) {
        AssetType.BOARD -> {
            sizeArray = boardSizeArray
        }
        AssetType.SHAPE_2D -> {
            sizeArray = shape2DSizeArray
        }
        AssetType.SHAPE_3D -> {
            sizeArray = shape3DSizeArray
        }
        else -> {
            //
        }
    }
    return sizeArray[index]
}

// Function to get the correct height for the delete button for some tools.
fun getDeleteButtonHeight(type: AssetType, index: Int): Float {
    var heightArray = arrayOf(0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f) // Default height

    when (type) {
        AssetType.ARROW -> {
            heightArray = arrowHeightArray
        }
        AssetType.BOARD -> {
            heightArray = boardHeightArray
        }
        else -> {
            //
        }
    }
    return heightArray[index]
}

// Function to get all children of an entity
fun getChildren(parent: Entity): MutableList<Entity> {
    var children: MutableList<Entity> = mutableListOf()

    val allChildren = Query.where { has(TransformParent.id) }
    for (child in allChildren.eval()) {
        val _parent = child.getComponent<TransformParent>().entity
        if (parent == _parent) {
            children.add(child)
        }
    }
    return children
}

// Function to get the pose of the user's head
fun getHeadPose(): Pose {
    val head =
        Query.where { has(AvatarAttachment.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
            .first()
    return head.getComponent<Transform>().transform
}

// Function to place an entity in front of the user, at the same height and facing it
fun placeInFront(entity: Entity?, offset: Vector3 = Vector3(0f), bigPanel: Boolean = false) {
    val headPose: Pose = getHeadPose()

    val isToolbar = entity!! == PanelManager.instance.toolbarPanel

    // We treat toolbar and big panels differently from other objects.
    val height: Float = if (isToolbar) 0.35f else 0.1f
    var distanceFromUser: Float = if (bigPanel) 0.9f else 0.7f

    // Having the users position, we place the entity in front of it, at a particular distance and height
    var newPos = headPose.t + headPose.q * Vector3.Forward * distanceFromUser
    newPos.y = headPose.t.y - height

    // If there is an offset vector, we place the object at the vector position (using user's position as reference)
    if (offset != Vector3(0f)) {
        newPos =
            headPose.t + headPose.q * Vector3.Right * offset.x + headPose.q * Vector3.Forward * offset.z

        newPos.y = headPose.t.y + offset.y
    }

    // Add rotation to look in same vector direction as user
    var newRot = Quaternion.lookRotation(newPos - headPose.t)
    val billboardOrientationEuler: Vector3 =
        entity.tryGetComponent<IsdkGrabbable>()?.billboardOrientation ?: Vector3(0f, 0f, 0f)

    newRot *=
        Quaternion(
            billboardOrientationEuler.x, billboardOrientationEuler.y, billboardOrientationEuler.z)

    entity.setComponent(Transform(Pose(newPos, newRot)))
}

// When an object is selected, the delete button is shown in the specified position
fun selectElement(ent: Entity) {
    ImmersiveActivity.getInstance()?.currentObjectSelected = ent
    val billboardOrientationEuler: Vector3 =
        ent.tryGetComponent<IsdkGrabbable>()?.billboardOrientation ?: Vector3(0f, 0f, 0f)
    ImmersiveActivity.getInstance()?.deleteButton?.setComponent(
        Transform(
            Pose(
                ent.getComponent<ToolComponent>().deleteButtonPosition,
                Quaternion(
                    billboardOrientationEuler.x,
                    billboardOrientationEuler.y,
                    billboardOrientationEuler.z))))

    // Change icon if the object is a task
    if (ent.getComponent<ToolComponent>().type == AssetType.TASK) {
        ImmersiveActivity.getInstance()?.deleteButton?.setComponent(
            Material().apply {
            baseTextureAndroidResourceId = R.drawable.close_task
            alphaMode = 1
            unlit = true
        })
    } else {
        ImmersiveActivity.getInstance()?.deleteButton?.setComponent(
            Material().apply {
                baseTextureAndroidResourceId = R.drawable.delete
                alphaMode = 1
                unlit = true
            })
    }
    ImmersiveActivity.getInstance()?.deleteButton?.setComponent(TransformParent(ent))
    ImmersiveActivity.getInstance()?.deleteButton?.setComponent(Visible(true))
}

// Delete object depending on the type
fun deleteObject(
    entity: Entity?,
    deleteFromDB: Boolean = false,
    cleaningProject: Boolean = false
) {
    Log.i("Focus", "Focus> Deleting object: ${entity?.getComponent<ToolComponent>()?.uuid}")

    val immA = ImmersiveActivity.getInstance()
    val position = entity!!.getComponent<Transform>().transform.t

    if (immA?.currentObjectSelected != null && immA?.currentObjectSelected!!.equals(entity)) {
        immA.currentObjectSelected = null
    }

    // deleteButton is detached from the object
    immA?.deleteButton?.setComponent(TransformParent())
    immA?.deleteButton?.setComponent(Visible(false))

    // Checking type of object to know how to delete it.
    val asset = entity.getComponent<ToolComponent>()
    // Check if we have to delete it from database as well or we are just cleaning the space to show a different project
    if (deleteFromDB && asset.type != AssetType.TASK && asset.type != AssetType.TIMER) {
        when (asset.type) {
            AssetType.STICKY_NOTE -> {
                immA?.DB?.deleteSticky(asset.uuid)
            }
            AssetType.BOARD -> {
                var children = getChildren(entity)
                for (i in children.count() - 1 downTo 0) {
                    deleteObject(children[i], true)
                }
                immA?.DB?.deleteToolAsset(asset.uuid)
            }
            else -> {
                immA?.DB?.deleteToolAsset(asset.uuid)
            }
        }
    } else if (asset.type == AssetType.TASK && !cleaningProject) {
        immA?.DB?.updateTaskData(asset.uuid, detach = 0)
        FocusViewModel.instance.refreshTasksPanel()

        // In case of some object, we need to delete its children too
    } else if (asset.type == AssetType.TIMER || asset.type == AssetType.BOARD) {
        var children = getChildren(entity)
        for (i in children.count() - 1 downTo 0) {
            children[i].destroy()
        }
    }
    entity.destroy()
    if (!cleaningProject) AudioManager.instance.playDeleteSound(position)
}
