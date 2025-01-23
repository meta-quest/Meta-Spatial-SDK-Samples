/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample

import android.net.Uri
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKHit
import com.meta.spatial.mruk.SurfaceType
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible

class UpdateRaycastSystem(
    private val mrukFeature: MRUKFeature,
    private val arrowEntities: MutableList<Entity>,
    var showAllHits: Boolean = true
) : SystemBase() {

  override fun execute() {
    // Make all arrows invisible
    for (entity in arrowEntities) {
      entity.setComponent(Visible(false))
    }

    val rightHand = getRightController(mrukFeature.systemManager)
    val rightHandPose = rightHand?.tryGetComponent<Transform>()?.transform
    val currentRoom = mrukFeature.getCurrentRoom()
    if (currentRoom != null && rightHandPose != null) {
      val rightHandDirection = (rightHandPose.q * Vector3(0f, 0f, 1f)).normalize()
      val surfaceMask = SurfaceType.PLANE_VOLUME
      val maxDistance = Float.POSITIVE_INFINITY

      val hits: Array<MRUKHit>
      if (showAllHits) {
        hits =
            mrukFeature.raycastRoomAll(
                currentRoom.anchor.uuid,
                rightHandPose.t,
                rightHandDirection,
                maxDistance,
                surfaceMask)
      } else {
        val hit =
            mrukFeature.raycastRoom(
                currentRoom.anchor.uuid,
                rightHandPose.t,
                rightHandDirection,
                maxDistance,
                surfaceMask)
        hits =
            if (hit != null) {
              arrayOf(hit)
            } else {
              emptyArray()
            }
      }
      for (i in hits.indices) {
        if (i >= arrowEntities.size) {
          arrowEntities.add(Entity.create(listOf(Mesh(Uri.parse("arrow.glb")), Transform(Pose()))))
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
