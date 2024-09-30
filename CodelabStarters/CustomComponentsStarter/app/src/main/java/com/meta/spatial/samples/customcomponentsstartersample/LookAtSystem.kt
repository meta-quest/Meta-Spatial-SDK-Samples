/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.customcomponentsstartersample

import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Transform

class LookAtSystem() : SystemBase() {

  private var lastTime = System.currentTimeMillis()

  private fun smoothOver(dt: Float, convergenceFraction: Float): Float {
    // standardize frame rate for interpolation
    val smoothTime = 1f / 60f
    return 1f -
        Math.pow(1f - convergenceFraction.toDouble(), (dt / smoothTime).toDouble()).toFloat()
  }

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    // clamp the max dt if the interpolation is too large
    val deltaTime = Math.min((currentTime - lastTime) / 1000f, 0.1f)
    var headPose: Pose = getScene()!!.getViewerPose()

    val q = Query.where { has(LookAt.id, Transform.id) }
    for (entity in q.eval()) {

      val lookAt = entity.getComponent<LookAt>()
      var targetPose: Pose?

      if (lookAt.lookAtHead) {
        targetPose = headPose
      } else {
        var targetEntity = lookAt.target
        targetPose = targetEntity.getComponent<Transform>().transform
      }

      var eyePose: Pose = entity.getComponent<Transform>().transform
      var rotation: Quaternion =
          when (lookAt.axis) {
            LookAtAxis.ALL -> {
              Quaternion.lookRotation((eyePose.t - targetPose.t))
            }
            LookAtAxis.Y -> {
              Quaternion.lookRotationAroundY((eyePose.t - targetPose.t))
            }
          }

      // apply our optional offset
      rotation *= Quaternion(lookAt.offset.x, lookAt.offset.y, lookAt.offset.z)

      // if we are far away from the target, smoothly interpolate towards it,
      // otherwise just snap to it
      val dot = eyePose.q.dot(rotation)
      if (dot < 0.999f) {
        eyePose.q = eyePose.q.slerp(rotation, smoothOver(deltaTime, lookAt.speed))
      } else {
        eyePose.q = rotation
      }

      entity.setComponent(Transform(eyePose))
    }

    lastTime = currentTime
  }
}
