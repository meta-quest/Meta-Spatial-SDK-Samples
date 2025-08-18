/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.raycast

import android.net.Uri
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKEnvironmentRaycastHitResult
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKHit
import com.meta.spatial.mruk.SurfaceType
import com.meta.spatial.samples.mruksample.common.getRightController
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible

enum class RaycastMode {
  SINGLE,
  ALL,
  GLOBAL_MESH,
  DEPTH,
}

class UpdateRaycastSystem(
    private val mrukFeature: MRUKFeature,
    private val arrowEntities: MutableList<Entity>,
    var raycastMode: RaycastMode = RaycastMode.ALL,
) : SystemBase() {

  override fun execute() {
    // Make all arrows invisible
    for (entity in arrowEntities) {
      entity.setComponent(Visible(false))
    }

    val rightHand = getRightController(mrukFeature.systemManager)
    val rightHandPose = rightHand?.tryGetComponent<Transform>()?.transform
    val currentRoom = mrukFeature.getCurrentRoom()
    if (rightHandPose != null) {
      val rightHandDirection = (rightHandPose.q * Vector3(0f, 0f, 1f)).normalize()
      val maxDistance = Float.POSITIVE_INFINITY

      if (raycastMode == RaycastMode.DEPTH) {
        val depthRaycastResult = mrukFeature.raycastEnvironment(rightHandPose.t, rightHandDirection)
        if (depthRaycastResult.result == MRUKEnvironmentRaycastHitResult.SUCCESS) {
          if (arrowEntities.isEmpty()) {
            arrowEntities.add(
                Entity.create(listOf(Mesh(Uri.parse("arrow.glb")), Transform(Pose()))))
          }
          val arrowPose =
              Pose(
                  depthRaycastResult.point,
                  Quaternion.lookRotation(depthRaycastResult.normal.normalize()),
              )
          val entity = arrowEntities[0]
          entity.setComponent(Transform(arrowPose))
          entity.setComponent(Visible(true))
        }
      } else if (currentRoom != null) {
        val hits: Array<MRUKHit> =
            when (raycastMode) {
              RaycastMode.SINGLE -> {
                val hit =
                    mrukFeature.raycastRoom(
                        currentRoom.anchor.uuid,
                        rightHandPose.t,
                        rightHandDirection,
                        maxDistance,
                        SurfaceType.PLANE_VOLUME,
                    )
                if (hit != null) arrayOf(hit) else emptyArray()
              }
              RaycastMode.ALL -> {
                mrukFeature.raycastRoomAll(
                    currentRoom.anchor.uuid,
                    rightHandPose.t,
                    rightHandDirection,
                    maxDistance,
                    SurfaceType.PLANE_VOLUME,
                )
              }
              RaycastMode.GLOBAL_MESH -> {
                val hit =
                    mrukFeature.raycastRoom(
                        currentRoom.anchor.uuid,
                        rightHandPose.t,
                        rightHandDirection,
                        maxDistance,
                        SurfaceType.MESH,
                    )
                if (hit != null) arrayOf(hit) else emptyArray()
              }
              RaycastMode.DEPTH -> {
                emptyArray()
              }
            }

        for (i in hits.indices) {
          if (i >= arrowEntities.size) {
            arrowEntities.add(
                Entity.create(listOf(Mesh(Uri.parse("arrow.glb")), Transform(Pose()))))
          }
          val hit = hits[i]
          val entity = arrowEntities[i]
          val arrowPose = Pose(hit.hitPosition, Quaternion.lookRotation(hit.hitNormal.normalize()))
          entity.setComponent(Transform(arrowPose))
          entity.setComponent(Visible(true))
        }
      }
    }
  }
}
