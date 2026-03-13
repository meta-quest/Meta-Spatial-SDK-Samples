/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.kotlinfeature

import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Scale
import kotlin.math.sin

/**
 * System that applies pulsing scale animation to entities.
 *
 * This system demonstrates how to:
 * 1. Query entities with specific components using the ECS Query API
 * 2. Perform per-frame calculations in pure Kotlin (no native code)
 * 3. Update entity scale based on time-based animation
 * 4. Maintain per-entity state (start time)
 *
 * The pulsing effect smoothly oscillates an entity's scale between minScale and maxScale using a
 * sine wave.
 *
 * Usage: systemManager.registerSystem(PulsingSystem())
 */
class PulsingSystem : SystemBase() {

  /** Tracks when pulsing started for each entity. Maps entity ID to start time in milliseconds. */
  private val startTimes = mutableMapOf<Long, Long>()

  /** Two times PI for sine wave calculations. */
  private val TWO_PI = 6.28318530718f

  /**
   * Called every frame by the ECS system manager.
   *
   * This method:
   * 1. Queries for entities with Pulsing component
   * 2. For each entity, calculates elapsed time
   * 3. Computes the current scale using a sine wave
   * 4. Applies the scale to the entity
   */
  override fun execute() {
    val currentTime = System.currentTimeMillis()

    // Query for entities that have the Pulsing component
    val query = Query.where { has(Pulsing.id) }

    for (entity in query.eval()) {
      val pulsing = entity.getComponent<Pulsing>()

      // Initialize start time on first execution for this entity
      val startTime = startTimes.getOrPut(entity.id) { currentTime }
      val elapsedTimeMs = currentTime - startTime

      // Convert to seconds for frequency calculation
      val elapsedSeconds = elapsedTimeMs / 1000.0f

      // Calculate phase using sine wave: ranges from -1 to 1
      val phase = sin(TWO_PI * pulsing.frequency * elapsedSeconds)

      // Normalize phase to 0-1 range: (phase + 1) / 2
      val normalizedPhase = (phase + 1f) / 2f

      // Interpolate between minScale and maxScale
      val currentScale = pulsing.minScale + (pulsing.maxScale - pulsing.minScale) * normalizedPhase

      // Apply uniform scale to the entity
      entity.setComponent(Scale(currentScale))
    }
  }
}
