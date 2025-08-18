/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import com.meta.spatial.core.SystemBase
import com.meta.spatial.runtime.SceneObject
import java.util.concurrent.CompletableFuture

class ManageGLXFSceneObjectsSystem(
    val animationsSampleActivity: AnimationsSampleActivity,
    val waitForGLXFSceneObjects: CompletableFuture<SceneObject>,
) : SystemBase() {

  override fun execute() {
    if (!waitForGLXFSceneObjects.isDone) {
      println("Waiting for GLXF scene objects to be created...")
      // need to wait for scene objects to be created AFTER the GLXF is inflated
      animationsSampleActivity
          .getSceneObjectByName("environment")
          ?.thenAccept({ _so: SceneObject -> waitForGLXFSceneObjects.complete(_so) })
    }
  }
}
