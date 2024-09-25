// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.entity

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Query
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.AvatarBody
import com.meta.spatial.toolkit.Transform

class EnvironmentEntities {

  fun getHeadPose(): Pose {
    val head =
        Query.where { has(AvatarAttachment.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
            .first()
    return head.getComponent<Transform>().transform
  }

  private fun getRightController(): Entity {
    val avatarBody =
        Query.where { has(AvatarBody.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarBody>().isPlayerControlled }
            .first()
    val rightController = avatarBody.getComponent<AvatarBody>().rightHand
    return rightController
  }

  private fun getLeftController(): Entity {
    val avatarBody =
        Query.where { has(AvatarBody.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarBody>().isPlayerControlled }
            .first()
    val leftController = avatarBody.getComponent<AvatarBody>().leftHand
    return leftController
  }
}
