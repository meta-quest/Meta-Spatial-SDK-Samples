package com.meta.pixelandtexel.scanner.feature.mrukraycasting.system

import com.meta.pixelandtexel.scanner.FollowHead
import com.meta.pixelandtexel.scanner.RotationMode
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Transform


class FollowHeadSystem : SystemBase() {
    override fun execute() {
        val q = Query.Companion.where { has(FollowHead.id, Transform.Companion.id) }

        val headPose: Pose = getScene().getViewerPose()

        for (entity in q.eval()) {
            var targetPose = headPose

            val eyePose = entity.getComponent<Transform>().transform
            val followHead = entity.getComponent<FollowHead>()

            val rotationOfEntity = when (followHead.rotationMode) {
                RotationMode.FULL -> Quaternion.lookRotation((eyePose.t - targetPose.t))
                RotationMode.Y_ROTATION -> Quaternion.lookRotationAroundY((eyePose.t - targetPose.t))
            }

            eyePose.q = rotationOfEntity

            entity.setComponent(Transform(eyePose))
        }
    }
}