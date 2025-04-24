// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.spin

import com.meta.pixelandtexel.geovoyage.Spin
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.fromAxisAngle
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Transform

class SpinSystem : SystemBase() {
  companion object {
    private const val TAG: String = "SpinSystem"
  }

  private var lastTime = System.currentTimeMillis()

  private val spinningEntities = HashSet<Entity>()

  override fun execute() {
    // calculate our delta time
    val currentTime = System.currentTimeMillis()
    val dt = ((currentTime - lastTime) / 1000f).coerceAtMost(0.1f)
    lastTime = currentTime

    findNewObjects()
    processSpinningEntities(dt)
  }

  override fun delete(entity: Entity) {
    super.delete(entity)

    spinningEntities.remove(entity)
  }

  private fun findNewObjects() {
    val query = Query.where { has(Spin.id, Transform.id) and changed(Spin.id) }
    for (entity in query.eval()) {
      if (spinningEntities.contains(entity)) {
        continue
      }

      spinningEntities.add(entity)
    }
  }

  private fun processSpinningEntities(dt: Float) {
    for (entity in spinningEntities) {
      val spin = entity.getComponent<Spin>()

      if (spin.speed == 0f || spin.axis == Vector3(0f, 0f, 0f)) {
        spin.recycle()
        continue
      }

      // Log.d(TAG, "Spinning ${spin.speed}, ${spin.axis}, $dt")

      val transform = entity.getComponent<Transform>()
      val pose = transform.transform

      // rotate around the axis
      val rotationStep = Quaternion.fromAxisAngle(spin.axis, spin.speed * dt)
      pose.q = (rotationStep * pose.q).normalize()

      // update the transform
      transform.transform = pose
      entity.setComponent(transform)

      spin.recycle()
      transform.recycle()
    }
  }
}
