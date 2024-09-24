/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Transform

class SpinnerSystem() : SystemBase() {
  var prevTime: Long = 0L

  // Systems are run by calling the execute() function
  override fun execute() {
    // Track the time so that we can tell how much time
    // has passed between calls to execute()
    if (prevTime == 0L) {
      prevTime = System.currentTimeMillis()
    }
    val currentTime = System.currentTimeMillis()

    // Make a query for all Entities with the Move and Transform
    // Components attached to them.
    val q = Query.where { has(Spinner.id, Transform.id) }
    for (entity in q.eval()) {
      if (!entity.hasComponent<Transform>()) {
        continue
      }
      val transform = entity.getComponent<Transform>()
      val spinner = entity.getComponent<Spinner>()
      // Determine how much time has passed, convert milliseconds
      // to seconds
      val timeDeltaInSeconds = (currentTime - prevTime) / 1000.0f
      // Adjust the y position set on the Transform component up
      // based on the speed and how much time has passed.
      transform.transform.q =
          transform.transform.q * Quaternion(0.0f, spinner.speed * timeDeltaInSeconds, 0.0f)
      // Set the Entity's Transform component to update the position.
      entity.setComponent(transform)
    }
    prevTime = currentTime
  }
}
