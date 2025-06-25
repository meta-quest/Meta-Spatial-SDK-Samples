/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.qr_code_scanner

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.samples.mruksample.TargetScale
import com.meta.spatial.samples.mruksample.TransformParentFollow
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform

class MenuPlacementSystem() : SystemBase() {

  val speed: Float = 0.1f

  val basePoses = mutableMapOf<Entity, Pose>()

  override fun execute() {
    val head = getScene().getViewerPose()
    Query.where { has(TargetScale.id) }
        .eval()
        .forEach { entity ->
          val targetScale = entity.getComponent<TargetScale>()
          entity.setComponent(
              Scale(
                  entity
                      .getComponent<Scale>()
                      .scale
                      .lerp(Vector3(targetScale.target), targetScale.speed)))
        }
    Query.where { has(TransformParentFollow.id) }
        .eval()
        .forEach { child ->
          val parent = child.getComponent<TransformParentFollow>().parent
          if (!basePoses.containsKey(child)) {
            basePoses[child] = parent.tryGetComponent<Transform>()?.transform ?: Pose()
          }
          val childTransform = child.getComponent<Transform>().transform
          val parentTransform = parent.tryGetComponent<Transform>()?.transform ?: Pose()
          val basePose = basePoses.getValue(child).lerp(parentTransform, speed)
          basePoses[child] = basePose
          // 0 when qr is on wall (vertical), 1 when qr is on floor (laying down)
          val amountY: Float =
              Math.abs(
                  (parentTransform.q * Vector3(0.0f, 0.0f, 1.0f)).dot(Vector3(0.0f, 1.0f, 0.0f)))
          val scale: Vector3 = child.getComponent<Scale>().scale
          val panelHeight: Float = QrCodeScannerSampleActivity.panelHeight / 2 * scale.y
          // position the panel so it won't clip into the floor if the QR is on the floor
          // and otherwise position it 2cm off the wall it is on
          val panelOffset = Pose(Vector3(0.0f, 0.0f, 1.0f) * -(amountY * panelHeight + 0.02f))
          val offsetPose = basePose * panelOffset
          val parentLookQuat =
              Quaternion.lookRotationAroundY(-(head.t - ((parentTransform * panelOffset).t)))
          // parent rotation about the Y axis
          val parentYQuat =
              Quaternion.lookRotationAroundY(parentTransform.q * Vector3(0.0f, 0.0f, 1.0f))
          // if the QR code is vertical, keep it roughly on the wall
          // otherwise, rotate it to face the user
          val desiredQuat = parentLookQuat.slerp(parentYQuat, (1.0f - amountY))
          val finalPose = Pose(offsetPose.t, childTransform.q.slerp(desiredQuat, speed))
          child.setComponent(Transform(finalPose))
        }
  }
}
