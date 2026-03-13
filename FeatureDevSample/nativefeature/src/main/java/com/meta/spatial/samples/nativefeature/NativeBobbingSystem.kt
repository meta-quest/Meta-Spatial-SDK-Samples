/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.nativefeature

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Transform

/**
 * System that applies native-calculated bobbing motion to entities.
 *
 * This system demonstrates how to:
 * 1. Query entities with specific components using the ECS Query API
 * 2. Call native C++ code via JNI for per-frame calculations
 * 3. Update entity transforms based on native computation results
 * 4. Maintain per-entity state (start time, initial position)
 *
 * The bobbing motion moves entities up and down using a sine wave, with the calculation performed
 * in native C++ code for demonstration.
 *
 * Usage: systemManager.registerSystem(NativeBobbingSystem())
 */
class NativeBobbingSystem : SystemBase() {

  /**
   * Native method that calculates the Y-offset for bobbing motion.
   *
   * The calculation uses a sine wave formula: offset = amplitude * sin(2π * frequency * time)
   *
   * @param elapsedTimeMs Time since bobbing started, in milliseconds
   * @param amplitude Maximum vertical displacement in meters
   * @param frequency Oscillation frequency in Hz (cycles per second)
   * @return The Y-axis offset to apply to the entity's position
   */
  private external fun nativeCalculateBobbingOffset(
      elapsedTimeMs: Long,
      amplitude: Float,
      frequency: Float,
  ): Float

  /** Tracks when bobbing started for each entity. Maps entity ID to start time in milliseconds. */
  private val startTimes = mutableMapOf<Long, Long>()

  /**
   * Stores the initial Y position of each entity. This allows bobbing to be relative to the
   * original position.
   */
  private val initialYPositions = mutableMapOf<Long, Float>()

  /**
   * Called every frame by the ECS system manager.
   *
   * This method:
   * 1. Queries for entities with NativeBobbing and Transform components
   * 2. For each entity, calculates elapsed time
   * 3. Calls native code to compute the bobbing offset
   * 4. Applies the offset to the entity's Y position
   */
  override fun execute() {
    val currentTime = System.currentTimeMillis()

    // Query for entities that have both NativeBobbing and Transform components
    val query = Query.where { has(NativeBobbing.id, Transform.id) }

    for (entity in query.eval()) {
      val bobbing = entity.getComponent<NativeBobbing>()
      val transform = entity.getComponent<Transform>()

      // Initialize start time on first execution for this entity
      val startTime = startTimes.getOrPut(entity.id) { currentTime }
      val elapsedTime = currentTime - startTime

      // Store initial Y position on first encounter
      val initialY = initialYPositions.getOrPut(entity.id) { transform.transform.t.y }

      // Call native C++ code to calculate the bobbing offset
      val yOffset =
          nativeCalculateBobbingOffset(
              elapsedTimeMs = elapsedTime,
              amplitude = bobbing.amplitude,
              frequency = bobbing.frequency,
          )

      // Apply the offset to the transform's Y position
      transform.transform.t.y = initialY + yOffset

      // Set the updated transform back to the entity
      entity.setComponent(transform)
    }
  }

  /** Called when an entity with NativeBobbing component is deleted. Cleans up tracked state. */
  override fun delete(entity: Entity) {
    super.delete(entity)
    startTimes.remove(entity.id)
    initialYPositions.remove(entity.id)
  }
}
