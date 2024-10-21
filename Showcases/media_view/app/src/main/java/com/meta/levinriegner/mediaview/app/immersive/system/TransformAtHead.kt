// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.system

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.PlayerBodyAttachmentSystem
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible

// Position the panel in front of the avatar's head
// once it becomes available
class TransformAtHead(
    private val compositionName: String,
    private val panelNodeName: String,
    private val zOffset: Float,
) : SystemBase() {

    private var isHeadPoseSet = false

    // Reset the system to reposition the panel
    fun resetHeadPose() {
        isHeadPoseSet = false
    }

    override fun execute() {
        if (isHeadPoseSet) return
        val activity = SpatialActivityManager.getVrActivity<AppSystemActivity>()
        isHeadPoseSet = updatePanelPosition(activity)
    }

    private fun updatePanelPosition(activity: AppSystemActivity): Boolean {
        val head = getHead() ?: return false
        val headPose = head.tryGetComponent<Transform>()?.transform
        if (headPose == null || headPose == Pose()) return false

        val composition = activity.glXFManager.getGLXFInfo(compositionName)
        val panel = composition.tryGetNodeByName(panelNodeName)
        if (panel?.entity == null) return false

        val forward = headPose.q * Vector3(0f, 0f, 1f)
        val panelPose = Pose(Vector3(0f, 0f, 0f), Quaternion(1f, 0f, 0f, 0f))

        // Position in front of head
        panelPose.t = headPose.t + forward * zOffset
        // Rotate y axis to face head
        panelPose.q = Quaternion.lookRotationAroundY(forward)
        panel.entity.setComponent(Transform(panelPose))

        // Set Visible
        panel.entity.setComponent(Visible(true))

        return true
    }

    private fun getHead(): Entity? {
        return systemManager
            .tryFindSystem<PlayerBodyAttachmentSystem>()
            ?.tryGetLocalPlayerAvatarBody()
            ?.head
    }
}