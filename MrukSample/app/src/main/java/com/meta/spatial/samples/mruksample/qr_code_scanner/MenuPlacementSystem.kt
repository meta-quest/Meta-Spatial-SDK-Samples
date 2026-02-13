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
import kotlin.math.abs

/**
 * System that handles menu placement and animation relative to QR codes and parent entities. This
 * system manages the positioning and scaling of UI panels based on their parent transforms,
 * ensuring proper placement whether QR codes are on walls or floors.
 */
class MenuPlacementSystem : SystemBase() {
  /** Stores base poses for entities being animated to enable smooth interpolation. */
  private val basePoses = mutableMapOf<Entity, Pose>()

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
                      .lerp(Vector3(targetScale.target), targetScale.speed)
              )
          )
        }
    Query.where { has(TransformParentFollow.id) }
        .eval()
        .forEach { child ->
          val parent = child.getComponent<TransformParentFollow>().parent
          val childTransform = child.getComponent<Transform>().transform
          val parentTransform = parent.tryGetComponent<Transform>()?.transform ?: Pose()
          val basePose =
              basePoses
                  .getOrPut(child) { parentTransform }
                  .lerp(parentTransform, INTERPOLATION_SPEED)
          basePoses[child] = basePose
          // 0 when QR is on wall (vertical), 1 when QR is on floor (laying down)
          val parentForward = parentTransform.q * Vector3(0.0f, 0.0f, 1.0f)
          val worldUp = Vector3(0.0f, 1.0f, 0.0f)
          val amountY = abs(parentForward.dot(worldUp))

          val scale = child.getComponent<Scale>().scale
          val panelHeight =
              QrCodeScannerSampleActivity.PANEL_HEIGHT / PANEL_HEIGHT_DIVISOR * scale.y
          // position the panel so it won't clip into the floor if the QR is on the floor
          // and otherwise position it 2cm off the wall it is on
          val offsetDistance = -(amountY * panelHeight + WALL_OFFSET)
          val panelOffset = Pose(Vector3(0.0f, 0.0f, 1.0f) * offsetDistance)
          val offsetPose = basePose * panelOffset

          val offsetPosition = (parentTransform * panelOffset).t
          val lookDirection = offsetPosition - head.t
          val parentLookQuat = Quaternion.lookRotationAroundY(lookDirection)

          // parent rotation about the Y axis
          val parentForwardDirection = parentTransform.q * Vector3(0.0f, 0.0f, 1.0f)
          val parentYQuat = Quaternion.lookRotationAroundY(parentForwardDirection)
          // if the QR code is vertical, keep it roughly on the wall
          // otherwise, rotate it to face the user
          val desiredQuat = parentLookQuat.slerp(parentYQuat, 1.0f - amountY)
          val finalPose =
              Pose(offsetPose.t, childTransform.q.slerp(desiredQuat, INTERPOLATION_SPEED))
          child.setComponent(Transform(finalPose))
        }
  }

  companion object {
    /** Offset distance from wall in meters (2cm). */
    private const val WALL_OFFSET = 0.02f

    /** Divisor for panel height calculation. */
    private const val PANEL_HEIGHT_DIVISOR = 2

    /** Interpolation speed for animations and transform updates. */
    private const val INTERPOLATION_SPEED = 0.1f
  }
}
