// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.tether

import com.meta.pixelandtexel.geovoyage.Tether
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.fromAxisAngle
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.lengthSq
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Transform
import kotlin.math.sign

private data class TetherInfo(
    val entity: Entity,
    var targetPosition: Vector3 = Vector3(0f),
    var targetRotation: Quaternion = Quaternion(),
    var snapToTarget: Boolean = false
)

class TetherSystem : SystemBase() {
  companion object {
    private const val TAG: String = "TetherSystem"
  }

  private var lastTime = System.currentTimeMillis()

  private var tetherEntityInfo = HashMap<Long, TetherInfo>()

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    val dt = ((currentTime - lastTime) / 1000f).coerceAtMost(0.1f)

    findNewObjects()
    processTethers(dt)

    lastTime = currentTime
  }

  override fun delete(entity: Entity) {
    tetherEntityInfo.remove(entity.id)
  }

  private fun findNewObjects() {
    val q = Query.where { has(Tether.id, Transform.id) and changed(Tether.id) }
    for (entity in q.eval()) {
      if (tetherEntityInfo.contains(entity.id)) {
        continue
      }

      // initialize our tether info, and flag this entity to snap to its target
      tetherEntityInfo[entity.id] = TetherInfo(entity).apply { snapToTarget = true }
    }
  }

  /**
   * For clarity, we'll process the tethers in 2 steps
   * 1. calculate the target position and rotation
   * 2. smoothly interpolate to the targets
   */
  private fun processTethers(dt: Float) {
    val headTransform = getHeadTransformComp()
    val headPose = headTransform.transform

    tetherEntityInfo.forEach { (_, info) ->
      val tetherComp = info.entity.getComponent<Tether>()

      // Invalid tether entity
      if (tetherComp.target == Entity.nullEntity()) {
        tetherComp.recycle()
        return@forEach
      }

      // Missing tether entity Transform
      if (!tetherComp.target.hasComponent<Transform>()) {
        tetherComp.recycle()
        return@forEach
      }

      // pose of the entity this one is tethered to
      val tetherEntityPose = tetherComp.target.getComponent<Transform>().transform

      // calculate our target position based on the tether entity position and the z offset
      val toHead = headPose.t - tetherEntityPose.t
      toHead.y = 0f
      var anchorPos = toHead.normalize() * tetherComp.zOffset + Vector3(0f, tetherComp.yOffset, 0f)

      // apply the tether rotation around the target entity
      if (tetherComp.rotateAround != 0f) {
        val rotator = Quaternion.fromAxisAngle(Vector3.Up, tetherComp.rotateAround)
        anchorPos = rotator.times(anchorPos)
      }

      // now calculate our rotation
      var panelFwd = Vector3.Up.cross((anchorPos * Vector3(1f, 0f, 1f)).normalize())
      panelFwd *= tetherComp.rotateAround.sign
      val rotation =
          Quaternion.lookRotationAroundY(panelFwd) * Quaternion(0f, tetherComp.yawOffset, 0f)

      info.targetPosition = tetherEntityPose.t + anchorPos
      info.targetRotation = rotation.normalize()

      tetherComp.recycle()
    }

    tetherEntityInfo.forEach { (_, info) ->
      val transformComp = info.entity.getComponent<Transform>()
      val pose = transformComp.transform

      // exit early if we're close enough to our target position and rotation
      if ((info.targetPosition - pose.t).lengthSq() < 0.00001f &&
          info.targetRotation.dot(pose.q) > 0.999f) {
        transformComp.recycle()
        return@forEach
      }

      // snap to our target if this entity was just added
      if (info.snapToTarget) {
        pose.t = info.targetPosition
        pose.q = info.targetRotation

        info.snapToTarget = false
      }
      // smoothly interpolate our position and rotation
      else {
        pose.t = pose.t.lerp(info.targetPosition, dt * 16f)
        pose.q = pose.q.slerp(info.targetRotation, dt * 12f)
      }

      info.entity.setComponent(transformComp)
      transformComp.recycle()
    }

    headTransform.recycle()
  }

  private fun getHeadTransformComp(): Transform {
    val head =
        Query.where { has(AvatarAttachment.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
            .first()
    return head.getComponent<Transform>()
  }
}
