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
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.managers.PanelManager
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.managers.ProjectManager
import com.meta.theelectricfactory.focus.panels.WebViewPanel
import com.meta.theelectricfactory.focus.utils.getDisposableID
import com.meta.theelectricfactory.focus.utils.getNewUUID
import com.meta.theelectricfactory.focus.utils.placeInFront

// Class to create a Web View panel tool
class WebView(
    url: String = "https://www.google.com/",
    var uuid: Int = -1,
    pose: Pose = Pose()
) {

    var id = getDisposableID()
    var immA = ImmersiveActivity.getInstance()

    // Create a grabbable entity with a Panel
    init {

        val ent: Entity =
            Entity.createPanelEntity(id, Transform(pose), Grabbable(true, GrabbableType.FACE))

        // If this is a new Web View, we create it in the database as well and place it in front of user
        if (uuid == -1) {
            if (pose == Pose()) placeInFront(ent, bigPanel = true)
            uuid = getNewUUID()
            immA?.DB?.createToolAsset(
                uuid,
                ProjectManager.instance.currentProject?.uuid,
                AssetType.WEB_VIEW,
                url,
                0f, // not relevant
                0f, // not relevant
                ent.getComponent<Transform>().transform
            )
        }

        // Register the panel
        immA?.registerPanel(
            PanelManager.instance.panelRegistration(id, 0.56f, 0.4f) { WebViewPanel(url, uuid, ent) }
        )

        // ToolComponent is added to web view to save properties and identify it
        ent.setComponent(ToolComponent(uuid, AssetType.WEB_VIEW, Vector3(0f, 0.3f, -0.005f)))
    }
}
