/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.ecs.system

import com.meta.spatial.core.Entity
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.SystemDependencies
import com.meta.spatial.core.SystemDependencyConfig
import com.meta.spatial.runtime.PanelDimensionsOverrides
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelCreationSystem
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.SceneObjectSystem

class BatchedPanelCreationSystem(
    private val maxPanelsPerTick: Int = 1,
) : SystemBase() {

  private data class PendingPanel(
      val entity: Entity,
      val panel: Panel,
  )

  private val queuedPanelEntities = ArrayDeque<Entity>()
  private val pendingPanels = ArrayDeque<PendingPanel>()
  private val trackedPanels = mutableSetOf<Entity>()
  private val deferredCallbacks = ArrayDeque<() -> Unit>()

  private var onComplete: (() -> Unit)? = null
  private var waitingForCompletion = false

  fun enqueuePanels(entities: List<Entity>, onComplete: () -> Unit = {}) {
    recyclePendingPanels()
    queuedPanelEntities.clear()
    trackedPanels.clear()
    deferredCallbacks.clear()
    this.onComplete = onComplete

    queuedPanelEntities.addAll(entities)
    waitingForCompletion = queuedPanelEntities.isNotEmpty()
    if (!waitingForCompletion) {
      completeOnNextTick()
    }
  }

  override fun getDependencies(): SystemDependencies? {
    return SystemDependencies(
        mustRunAfter = mutableSetOf(SystemDependencyConfig(PanelCreationSystem::class))
    )
  }

  override fun execute() {
    while (deferredCallbacks.isNotEmpty()) {
      deferredCallbacks.removeFirst().invoke()
    }

    captureQueuedPanels()

    repeat(maxPanelsPerTick.coerceAtLeast(1)) {
      if (pendingPanels.isEmpty()) {
        return@repeat
      }

      val pendingPanel = pendingPanels.removeFirst()
      if (pendingPanel.entity.willBeDeleted()) {
        trackedPanels.remove(pendingPanel.entity)
        pendingPanel.panel.recycle()
      } else {
        applyPanelDimensionsOverride(pendingPanel.entity)
        pendingPanel.entity.setComponent(pendingPanel.panel)
        pendingPanel.panel.recycle()
      }
    }

    if (
        waitingForCompletion &&
            queuedPanelEntities.isEmpty() &&
            pendingPanels.isEmpty() &&
            allTrackedPanelsCreated()
    ) {
      completeOnNextTick()
    }
  }

  override fun delete(entity: Entity) {
    queuedPanelEntities.removeAll { it == entity }
    removePendingPanelsFor(entity)
    trackedPanels.remove(entity)
  }

  override fun destroy() {
    recyclePendingPanels()
    queuedPanelEntities.clear()
    trackedPanels.clear()
    deferredCallbacks.clear()
    onComplete = null
    waitingForCompletion = false
    super.destroy()
  }

  private fun captureQueuedPanels() {
    while (queuedPanelEntities.isNotEmpty()) {
      val entity = queuedPanelEntities.removeFirst()
      if (entity.willBeDeleted()) {
        continue
      }

      val panel = entity.tryGetComponent<Panel>() ?: continue
      if (entity.tryRemoveComponent<Panel>()) {
        pendingPanels.addLast(PendingPanel(entity, panel))
        trackedPanels.add(entity)
      } else {
        panel.recycle()
      }
    }
  }

  private fun allTrackedPanelsCreated(): Boolean {
    val sceneObjectSystem = systemManager.findSystem<SceneObjectSystem>()
    val iterator = trackedPanels.iterator()
    while (iterator.hasNext()) {
      val entity = iterator.next()
      if (entity.willBeDeleted()) {
        iterator.remove()
        continue
      }

      val future = sceneObjectSystem.getSceneObject(entity) ?: return false
      if (future.isCancelled || future.isCompletedExceptionally) {
        iterator.remove()
      } else if (!future.isDone) {
        return false
      }
    }
    return true
  }

  private fun applyPanelDimensionsOverride(entity: Entity) {
    val panelDimensions = entity.tryGetComponent<PanelDimensions>() ?: return
    PanelDimensionsOverrides.set(entity, panelDimensions.dimensions)
    panelDimensions.recycle()
  }

  private fun removePendingPanelsFor(entity: Entity) {
    val retainedPanels = pendingPanels.filter { it.entity != entity }
    pendingPanels.filter { it.entity == entity }.forEach { it.panel.recycle() }
    pendingPanels.clear()
    pendingPanels.addAll(retainedPanels)
  }

  private fun recyclePendingPanels() {
    while (pendingPanels.isNotEmpty()) {
      pendingPanels.removeFirst().panel.recycle()
    }
  }

  private fun completeOnNextTick() {
    waitingForCompletion = false
    val callback = onComplete
    onComplete = null
    if (callback != null) {
      deferredCallbacks.addLast(callback)
    }
  }
}
