// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.pointerInfo

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.Transform

class PointerInfoSystem : SystemBase() {
  private var lastTime = System.currentTimeMillis()

  // max distance to ray cast into the scene for a contact
  private val kPointerDistance = 5f

  var leftEntity: Entity? = null
  var rightEntity: Entity? = null

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    lastTime = currentTime

    val controllers = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }

    for (controller in controllers) {
      val controllerData = controller.getComponent<Controller>()
      if (!controllerData.isActive) continue

      val controllerPose = controller.getComponent<Transform>().transform

      val worldOrigin = controllerPose.t
      val worldTarget = controllerPose * Vector3(0.0f, 0.0f, kPointerDistance)

      val isRight = isRightControllerOrRightHand(controller)

      val entityReference = if (isRight) ::rightEntity else ::leftEntity

      // check for a collision with the scene
      val intersection = getScene().lineSegmentIntersect(worldOrigin, worldTarget)
      if (intersection?.entity == null) {
        entityReference.set(null)
        continue
      }
      entityReference.set(intersection.entity)
    }
  }

  fun checkHover(item: Entity?): Boolean {
    return leftEntity == item || rightEntity == item
  }

  companion object {
    fun isRightControllerOrRightHand(controllerEntity: Entity): Boolean {
      val attachment = controllerEntity.tryGetComponent<AvatarAttachment>()

      return attachment != null &&
          (attachment.type == "right_controller" || attachment.type == "right_hand")
    }
  }
}
