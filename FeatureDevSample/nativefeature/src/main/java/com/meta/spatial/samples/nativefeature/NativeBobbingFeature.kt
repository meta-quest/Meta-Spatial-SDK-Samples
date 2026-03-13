/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.nativefeature

import android.os.Bundle
import android.util.Log
import com.meta.spatial.core.ComponentRegistration
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SystemBase

/**
 * SpatialFeature that provides native-calculated bobbing functionality.
 *
 * This feature demonstrates how to create a self-contained library module that:
 * 1. Loads a native library via JNI
 * 2. Registers custom components with the ECS
 * 3. Registers systems that use native code
 * 4. Follows the SpatialFeature pattern for easy integration
 *
 * Usage in your Activity:
 * ```kotlin
 * override fun registerFeatures(): List<SpatialFeature> {
 *   return listOf(
 *     VRFeature(this),
 *     NativeBobbingFeature(),  // Add native bobbing support
 *     // ... other features
 *   )
 * }
 * ```
 *
 * Then add the NativeBobbing component to any entity:
 * ```kotlin
 * Entity.create(listOf(
 *   Transform(Pose(Vector3(0f, 1.5f, -2f))),
 *   Mesh(Uri.parse("mesh://sphere")),
 *   NativeBobbing(amplitude = 0.3f, frequency = 1.5f)
 * ))
 * ```
 */
class NativeBobbingFeature : SpatialFeature {

  companion object {
    private const val TAG = "NativeBobbingFeature"
  }

  /** Called before the runtime is initialized. Use this to load native libraries. */
  override fun preRuntimeOnCreate(savedInstanceState: Bundle?) {
    loadLibrary("native_bobbing")
    Log.i(TAG, "Native bobbing library loaded successfully")
  }

  /**
   * Returns the list of components this feature provides. The ECS will register these components at
   * startup.
   */
  override fun componentsToRegister(): List<ComponentRegistration> {
    return NativeFeatureComponentRegistrations.all()
  }

  /**
   * Returns the list of systems this feature provides. Systems are executed every frame in the
   * order they are registered.
   */
  override fun systemsToRegister(): List<SystemBase> {
    return listOf(NativeBobbingSystem())
  }
}
