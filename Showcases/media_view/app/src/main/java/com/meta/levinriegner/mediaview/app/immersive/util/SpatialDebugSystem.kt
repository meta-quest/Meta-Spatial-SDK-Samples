// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.util

import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Transform
import java.lang.Math.min
import timber.log.Timber

class SpatialDebugSystem : SystemBase() {

  private var lastTime = System.currentTimeMillis()

  private fun getHeadPose(): Pose {
    val head =
        Query.where { has(AvatarAttachment.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
            .first()
    return head.getComponent<Transform>().transform
  }

  private fun smoothOver(dt: Float, convergenceFraction: Float): Float {
    // standardize frame rate for interpolation
    val smoothTime = 1f / 60f
    return 1f -
        Math.pow(1f - convergenceFraction.toDouble(), (dt / smoothTime).toDouble()).toFloat()
  }

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    // clamp the max dt if the interpolation is too large
    val deltaTime = min((currentTime - lastTime) / 1000f, 0.1f)
    var headPose: Pose? = null

    // Query all entities that need to log transforms and look at the head
    val entities = Query.where { has(SpatialDebugComponent.id, Transform.id) }.eval()

    for (entity in entities) {
      val spatialDebugComponent = entity.getComponent<SpatialDebugComponent>()

      if (headPose == null) {
        headPose = getHeadPose()
      }
      val targetPose = headPose

      var eyePose: Pose = entity.getComponent<Transform>().transform

      // Position the entity in front of the user
      if (spatialDebugComponent.followCamera) {
        val forward = targetPose.q * Vector3(0.0f, 0.0f, 1.0f)
        val desiredPosition =
            targetPose.t +
                forward * spatialDebugComponent.distance +
                spatialDebugComponent.followOffset
        eyePose.t += (desiredPosition - eyePose.t) * spatialDebugComponent.followSpeed
      }

      // Calculate rotation to always face the head (billboarding)
      if (spatialDebugComponent.billboard) {
        val lookAtDirection = targetPose.t - eyePose.t
        var rotation: Quaternion =
            when (spatialDebugComponent.axis) {
              LookAtAxis.ALL -> Quaternion.lookRotation(lookAtDirection)
              LookAtAxis.Y -> {
                // Project the lookAtDirection onto the XZ plane for Y axis rotation
                val lookAtDirectionY =
                    Vector3(lookAtDirection.x, 0.0f, lookAtDirection.z).normalize()
                Quaternion.lookRotation(lookAtDirectionY)
              }
            }

        rotation *=
            Quaternion(
                spatialDebugComponent.rotationOffset.x,
                spatialDebugComponent.rotationOffset.y,
                spatialDebugComponent.rotationOffset.z)
        val dot = eyePose.q.dot(rotation)
        if (dot > 0.999f) {
          eyePose.q =
              eyePose.q.slerp(rotation, smoothOver(deltaTime, spatialDebugComponent.rotationSpeed))
        } else {
          eyePose.q = rotation
        }
      }

      entity.setComponent(Transform(eyePose))

      // Logging the transform
      val position = eyePose.t
      val rotationLog = eyePose.q

      Timber.d("Entity ID: ${entity.id}, Position: $position, Rotation: $rotationLog")
    }

    // log time
    lastTime = currentTime
  }
}
