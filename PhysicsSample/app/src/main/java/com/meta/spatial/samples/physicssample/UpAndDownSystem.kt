/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import kotlin.math.sin

class UpAndDownSystem() : SystemBase() {

  private var lastTime = System.currentTimeMillis()
  private var entities = listOf<Entity>()

  private fun findNewObjects() {

    val q = Query.where { changed(Mesh.id) and has(UpAndDown.id, Transform.id) }
    for (entity in q.eval()) {

      if (entities.contains(entity)) {
        continue
      }

      val upAndDown = entity.getComponent<UpAndDown>()
      val transform = entity.getComponent<Transform>()
      upAndDown.startPosition = transform.transform.t
      entity.setComponent(upAndDown)
      entities += entity
    }
  }

  private fun animate(deltaTimeSeconds: Float) {

    for (entity in entities) {
      val transform = entity.getComponent<Transform>()
      val upAndDown = entity.getComponent<UpAndDown>()

      // Calculate new y offset based on speed and time passed.
      upAndDown.offset += (upAndDown.speed * deltaTimeSeconds)
      upAndDown.offset %= 1f

      // Update the y position set on the Transform component.
      transform.transform.t.y =
          upAndDown.startPosition.y - (sin(upAndDown.offset * Math.PI.toFloat()) * upAndDown.amount)

      // update the components on the entity
      entity.setComponent(transform)
      entity.setComponent(upAndDown)
    }
  }

  // Systems are run by calling the execute() function
  override fun execute() {
    val currentTime = System.currentTimeMillis()
    // clamp the max dt if the interpolation is too large
    val deltaTimeSeconds = Math.min((currentTime - lastTime) / 1000f, 0.1f)
    findNewObjects()
    animate(deltaTimeSeconds)
    lastTime = currentTime
  }
}
