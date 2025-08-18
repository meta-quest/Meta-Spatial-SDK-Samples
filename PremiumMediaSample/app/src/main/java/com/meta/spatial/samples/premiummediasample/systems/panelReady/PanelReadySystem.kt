/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.panelReady

import com.meta.spatial.core.Entity
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.SystemDependencies
import com.meta.spatial.core.SystemDependencyConfig
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.samples.premiummediasample.systems.tweenEngine.TweenEngineSystem
import com.meta.spatial.toolkit.MeshCreationSystem
import com.meta.spatial.toolkit.SceneObjectSystem

class PanelReadySystem() : SystemBase() {
  private val trackedEntities = HashMap<Entity, (SceneObject) -> Unit>()

  private val nextFrameQueue = ArrayDeque<Pair<SceneObject, (SceneObject) -> Unit>>()

  fun executeWhenReady(entity: Entity, callback: (SceneObject) -> Unit) {
    trackedEntities[entity] = callback
  }

  override fun execute() {
    // Compose panels add in a delay after panel creation but before the next do frame.
    // Wait one frame to kick off any animations to avoid visible stutter.
    while (nextFrameQueue.isNotEmpty()) {
      val entry = nextFrameQueue.removeFirst()
      val sceneObject = entry.first
      val callback = entry.second
      callback(sceneObject)
    }

    trackedEntities.forEach { entry ->
      val entity = entry.key
      val callback = entry.value

      val sceneObjectFuture = systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)
      if (sceneObjectFuture != null) {
        trackedEntities.remove(entity)
      }
      sceneObjectFuture?.thenAccept { sceneObject ->
        nextFrameQueue.add(Pair(sceneObject, callback))
      }
    }
  }

  override fun delete(entity: Entity) {
    trackedEntities.remove(entity)
  }

  override fun getDependencies(): SystemDependencies {
    return SystemDependencies(
        mustRunBefore = mutableSetOf(SystemDependencyConfig(MeshCreationSystem::class)),
        mustRunAfter = mutableSetOf(SystemDependencyConfig(TweenEngineSystem::class)),
    )
  }
}
