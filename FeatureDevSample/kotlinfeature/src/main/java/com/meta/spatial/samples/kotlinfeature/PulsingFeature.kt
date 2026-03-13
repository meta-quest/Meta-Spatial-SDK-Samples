/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.kotlinfeature

import com.meta.spatial.core.ComponentRegistration
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SystemBase

/**
 * SpatialFeature that provides scale-pulsing functionality.
 *
 * This feature demonstrates how to create a self-contained library module that:
 * 1. Uses pure Kotlin (no native/JNI code required)
 * 2. Registers custom components with the ECS
 * 3. Registers systems that animate entities
 * 4. Follows the SpatialFeature pattern for easy integration
 *
 * Usage in your Activity:
 * ```kotlin
 * override fun registerFeatures(): List<SpatialFeature> {
 *   return listOf(
 *     VRFeature(this),
 *     PulsingFeature(),  // Add pulsing support
 *     // ... other features
 *   )
 * }
 * ```
 *
 * Then add the Pulsing component to any entity:
 * ```kotlin
 * Entity.create(listOf(
 *   Transform(Pose(Vector3(0f, 1.5f, -2f))),
 *   Mesh(Uri.parse("mesh://box")),
 *   Box(Vector3(-0.1f, -0.1f, -0.1f), Vector3(0.1f, 0.1f, 0.1f)),
 *   Pulsing(minScale = 0.8f, maxScale = 1.2f, frequency = 0.5f)
 * ))
 * ```
 */
class PulsingFeature : SpatialFeature {

  /**
   * Returns the list of components this feature provides. The ECS will register these components at
   * startup.
   */
  override fun componentsToRegister(): List<ComponentRegistration> {
    return PulsingFeatureComponentRegistrations.all()
  }

  /**
   * Returns the list of systems this feature provides. Systems are executed every frame in the
   * order they are registered.
   */
  override fun systemsToRegister(): List<SystemBase> {
    return listOf(PulsingSystem())
  }
}
