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
import com.meta.spatial.mruk.MRUKRoom
import com.meta.spatial.mruk.SurfaceType
import com.meta.spatial.samples.mruksample.common.getRightController
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible

/** Defines the different modes for raycasting operations. */
enum class RaycastMode {
  SINGLE,
  ALL,
  GLOBAL_MESH,
  DEPTH,
}

/**
 * System responsible for updating raycast operations and managing arrow entities to visualize
 * raycast hit results in the MRUK environment.
 *
 * @property mrukFeature The MRUK feature instance for performing raycast operations
 * @property arrowEntities Mutable list of arrow entities used to visualize raycast hits
 * @property raycastMode The current raycast mode determining the type of raycast operation
 */
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

    val rightHandController = getRightController(mrukFeature.systemManager)
    val rightHandPose = rightHandController?.tryGetComponent<Transform>()?.transform
    val currentRoom = mrukFeature.getCurrentRoom()

    if (rightHandPose != null) {
      val rightHandDirection = (rightHandPose.q * Vector3(0f, 0f, 1f)).normalize()

      if (raycastMode == RaycastMode.DEPTH) {
        handleDepthRaycast(rightHandPose, rightHandDirection)
      } else if (currentRoom != null) {
        val maxDistance = Float.POSITIVE_INFINITY
        val raycastHits =
            performRoomRaycast(
                currentRoom,
                rightHandPose,
                rightHandDirection,
                maxDistance,
            )
        updateArrowEntitiesFromHits(raycastHits)
      }
    }
  }

  private fun handleDepthRaycast(rightHandPose: Pose, rightHandDirection: Vector3) {
    val depthRaycastResult = mrukFeature.raycastEnvironment(rightHandPose.t, rightHandDirection)
    if (depthRaycastResult.result == MRUKEnvironmentRaycastHitResult.SUCCESS) {
      if (arrowEntities.isEmpty()) {
        arrowEntities.add(
            Entity.create(
                listOf(
                    Mesh(Uri.parse("arrow.glb")),
                    Transform(Pose()),
                ),
            ),
        )
      }
      val arrowPose =
          Pose(
              depthRaycastResult.point,
              Quaternion.lookRotation(depthRaycastResult.normal.normalize()),
          )
      val arrowEntity = arrowEntities[0]
      arrowEntity.setComponent(Transform(arrowPose))
      arrowEntity.setComponent(Visible(true))
    }
  }

  private fun performRoomRaycast(
      currentRoom: MRUKRoom,
      rightHandPose: Pose,
      rightHandDirection: Vector3,
      maxDistance: Float,
  ): Array<MRUKHit> {
    return when (raycastMode) {
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
  }

  private fun updateArrowEntitiesFromHits(raycastHits: Array<MRUKHit>) {
    for (index in raycastHits.indices) {
      if (index >= arrowEntities.size) {
        arrowEntities.add(
            Entity.create(
                listOf(
                    Mesh(Uri.parse("arrow.glb")),
                    Transform(Pose()),
                ),
            ),
        )
      }
      val hit = raycastHits[index]
      val arrowEntity = arrowEntities[index]
      val arrowPose =
          Pose(
              hit.hitPosition,
              Quaternion.lookRotation(hit.hitNormal.normalize()),
          )
      arrowEntity.setComponent(Transform(arrowPose))
      arrowEntity.setComponent(Visible(true))
    }
  }
}
