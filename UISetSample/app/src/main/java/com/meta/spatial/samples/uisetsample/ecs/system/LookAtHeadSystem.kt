/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.ecs.system

import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.samples.uisetsample.LookAtHead
import com.meta.spatial.toolkit.PlayerBodyAttachmentSystem
import com.meta.spatial.toolkit.Transform
import timber.log.Timber

class LookAtHeadSystem : SystemBase() {

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
      Timber.d(
          "Entity ${entity.id} is looking at head. Once: ${lookAtHead.once}, HasLooked: ${lookAtHead.hasLooked}",
      )

      val forward = headPose.q * Vector3(lookAtHead.xOffset, lookAtHead.yOffset, lookAtHead.zOffset)
      val panelPose = Pose(Vector3(0f, 0f, 0f), Quaternion(1f, 0f, 0f, 0f))

      // Position in front of head
      panelPose.t = headPose.t + forward
      // Rotate y axis to face head
      panelPose.q = Quaternion.lookRotationAroundY(forward)
      entity.setComponent(Transform(panelPose))

      // Mark as looked
      if (!lookAtHead.hasLooked) {
        entity.setComponent(lookAtHead.apply { hasLooked = true })
      }
    }
  }

  private fun getHeadPose(): Pose? {
    val head =
        systemManager
            .tryFindSystem<PlayerBodyAttachmentSystem>()
            ?.tryGetLocalPlayerAvatarBody()
            ?.head

    val headPose = head?.tryGetComponent<Transform>()?.transform
    if (headPose == null || headPose == Pose()) return null
    return headPose
  }
}
