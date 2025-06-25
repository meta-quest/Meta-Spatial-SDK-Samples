/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import com.meta.spatial.core.Entity
import com.meta.spatial.core.EntityContext
import com.meta.spatial.core.EventArgs
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform

class ButtonSystem() : SystemBase() {

  public var triggerButtons: Int = ButtonBits.ButtonTriggerL or ButtonBits.ButtonTriggerR
  private var lastTime = System.currentTimeMillis()
  private var entitiesWithListener = HashSet<Entity>()

  private fun findNewObjects() {

    val dataModel = EntityContext.getDataModel()
    val q = Query.where { changed(Mesh.id) and has(Button.id, Transform.id) }
    for (entity in q.eval()) {
      val systemObject =
          systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity) ?: continue
      if (entitiesWithListener.contains(entity)) {
        continue
      }
      systemObject.thenAccept { so ->
        entitiesWithListener.add(entity)

        // store the initial position
        val button = entity.getComponent<Button>()
        val transform = entity.getComponent<Transform>()
        button.startPosition = transform.transform.t
        entity.setComponent(button)

        // listen for input
        so.addInputListener(
            object : InputListener {
              override fun onClickDown(
                  receiver: SceneObject,
                  hitInfo: HitInfo,
                  sourceOfInput: Entity,
              ) {
                button.isDown = true
                entity.setComponent(button)
              }

              override fun onClick(
                  receiver: SceneObject,
                  hitInfo: HitInfo,
                  sourceOfInput: Entity,
              ) {
                dataModel?.sendEvent(entity, "button", EventArgs("click", dataModel))
              }
            })
      }
    }
  }

  private fun roughlyEqual(v1: Vector3, v2: Vector3, tollerence: Float = 0.001f): Boolean {
    return (v1 - v2).length() < tollerence
  }

  private fun animateButtons(deltaTimeSeconds: Float) {
    entitiesWithListener.forEach { entity ->
      val button = entity.getComponent<Button>()
      val transform = entity.getComponent<Transform>()

      var targetPosition =
          if (button.isDown) button.startPosition + button.downOffset else button.startPosition

      // animate the button position up and down
      transform.transform.t =
          transform.transform.t.lerp(targetPosition, smoothOver(deltaTimeSeconds, 0.15f))
      entity.setComponent(transform)

      // reset the button after it's gone down
      if (button.isDown && roughlyEqual(targetPosition, transform.transform.t)) {
        button.isDown = false
        entity.setComponent(button)
      }
    }
  }

  private fun smoothOver(dt: Float, convergenceFraction: Float): Float {
    // standardize frame rate for interpolation
    val smoothTime = 1f / 60f
    return 1f -
        Math.pow(1f - convergenceFraction.toDouble(), (dt / smoothTime).toDouble()).toFloat()
  }

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    // clamp the max dt if the interpolation is too large
    val deltaTimeSeconds = Math.min((currentTime - lastTime) / 1000f, 0.1f)
    findNewObjects()
    animateButtons(deltaTimeSeconds)
    lastTime = currentTime
  }

  override fun delete(entity: Entity) {
    entitiesWithListener.remove(entity)
  }
}
