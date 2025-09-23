// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.tools

import android.net.Uri
import android.widget.TextView
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkGrabbable
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.TimeComponent
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.managers.AudioManager
import com.meta.theelectricfactory.focus.utils.addDeleteButton
import com.meta.theelectricfactory.focus.utils.getDisposableID
import com.meta.theelectricfactory.focus.utils.placeInFront

// Class to create a Timer, composed by an entity with a Panel component and another entity with a Mesh component
class Timer(totalTime: Int) {

    val immA = ImmersiveActivity.getInstance()

    init {
        var id = getDisposableID()
        val _width = 0.18f
        val _height = 0.18f
        val _dp = 1150f

        immA?.registerPanel(
                PanelRegistration(id) {
                    layoutResourceId = R.layout.timer_layout
                    config {
                        themeResourceId = R.style.Theme_Focus_Transparent
                        width = _width
                        height = _height
                        layoutWidthInDp = _dp
                        layoutHeightInDp = _dp * (height / width)
                        includeGlass = false
                    }
                    panel {
                        rootView?.findViewById<TextView>(R.id.totalTime)?.text = totalTime.toString() + "'"
                    }
                })

        // Create an entity with the timer.glb model
        val timerObj =
            Entity.create(
                Mesh(mesh = Uri.parse("timer.glb")),
                Scale(0.1f),
                Grabbable(enabled = true, GrabbableType.PIVOT_Y),
                IsdkGrabbable(billboardOrientation = Vector3(0f, 180f, 0f)),
                Transform(Pose(Vector3(0f), Quaternion(0f, 180f, 0f))),
            )

        // Create an entity with a panel component
        val timerPanel: Entity =
            Entity.create(
                // hittable property should be NonCollision if we don't want to interact with it, nor
                // block the parent entity
                Panel(id).apply { hittable = MeshCollision.NoCollision },
                Transform(Pose(Vector3(0f, 0f, 0.025f), Quaternion(0f, 180f, 0f))),
            )

        // We add a TimeComponent to timer panel to be able to update it. More info in
        // UpdateTimeSystem.kt
        timerPanel.setComponent(
            TimeComponent(
                type = AssetType.TIMER, totalTime = totalTime, startTime = System.currentTimeMillis(),
            )
        )
        // We make panel entity child to the timer model entity
        timerPanel.setComponent(TransformParent(timerObj))

        // We add a ToolComponent to be able to identify it
        timerObj.setComponent(ToolComponent(-1, AssetType.TIMER, Vector3(0f, 0.13f, 0f)))

        // We place it in front of the user
        placeInFront(timerObj)
        // We add a listener to show delete button when entity is selected
        addDeleteButton(timerObj)
        AudioManager.instance.playCreationSound(timerObj.getComponent<Transform>().transform.t)
    }
}
