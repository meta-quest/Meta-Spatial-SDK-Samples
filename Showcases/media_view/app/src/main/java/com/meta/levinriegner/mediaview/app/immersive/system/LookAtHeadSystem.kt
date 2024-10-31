// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.system

import com.meta.levinriegner.mediaview.app.immersive.component.LookAtHead
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.PlayerBodyAttachmentSystem
import com.meta.spatial.toolkit.Transform
import timber.log.Timber

class LookAtHeadSystem: SystemBase() {

    override fun execute() {
        // Get Head
        val headPose = getHeadPose() ?: return

        // Query all entities that need to look at the head
        val q = Query.where { has(LookAtHead.id, Transform.id) }
        for (entity in q.eval()) {

            val lookAtHead = entity.getComponent<LookAtHead>()
            if (lookAtHead.once && lookAtHead.hasLooked) {
                continue
            }

            val forward = headPose.q * Vector3(0f, 0f, lookAtHead.zOffset)
            val panelPose = Pose(Vector3(0f, 0f, 0f), Quaternion(1f, 0f, 0f, 0f))

            // Position in front of head
            panelPose.t = headPose.t + forward
            // Rotate y axis to face head
            panelPose.q = Quaternion.lookRotationAroundY(forward)
            entity.setComponent(Transform(panelPose))

            // Mark as looked
            if (!lookAtHead.hasLooked) {
                entity.setComponent(lookAtHead.copyWith(hasLooked = true))
            }
        }

    }

    private fun getHeadPose(): Pose? {
        val head = systemManager
            .tryFindSystem<PlayerBodyAttachmentSystem>()
            ?.tryGetLocalPlayerAvatarBody()
            ?.head

        val headPose = head?.tryGetComponent<Transform>()?.transform
        if (headPose == null || headPose == Pose()) return null
        return headPose
    }
}