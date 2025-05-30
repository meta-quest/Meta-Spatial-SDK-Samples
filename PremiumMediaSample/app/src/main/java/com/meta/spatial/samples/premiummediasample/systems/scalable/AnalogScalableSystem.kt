// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.scalable

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.samples.premiummediasample.Scalable
import com.meta.spatial.samples.premiummediasample.systems.pointerInfo.PointerInfoSystem
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.Scale

class AnalogScalableSystem(
    private val pointerSystem: PointerInfoSystem,
    private var minScale: Float = 0.5f,
    private var maxScale: Float = 5f,
    private var globalScaleSpeed: Float = 1f
) : SystemBase() {

  private var lastTime = System.currentTimeMillis()

  override fun execute() {

    val currentTime = System.currentTimeMillis()
    val deltaTime = (currentTime - lastTime) * 0.001f
    lastTime = currentTime
    if (pointerSystem.leftEntity == null && pointerSystem.rightEntity == null) {
      return
    }

    val controllers = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }
    for (controller in controllers) {
      val data = controller.getComponent<Controller>()
      if (!data.isActive) continue

      val isRight = PointerInfoSystem.isRightControllerOrRightHand(controller)
      val entity = if (isRight) pointerSystem.rightEntity else pointerSystem.leftEntity

      if (entity == null) continue

      val scaleable = entity.tryGetComponent<Scalable>()
      if (scaleable == null) continue

      val upBits = if (isRight) ButtonBits.ButtonThumbRU else ButtonBits.ButtonThumbLU
      val downBits = if (isRight) ButtonBits.ButtonThumbRD else ButtonBits.ButtonThumbLD
      val scaleAmount = deltaTime * globalScaleSpeed * scaleable.speed

      if (data.buttonState.and(upBits) == upBits) {
        addScale(entity, scaleAmount)
      } else if (data.buttonState.and(downBits) == downBits) {
        addScale(entity, -scaleAmount)
      }
    }
  }

  private fun addScale(entity: Entity, scaleAddition: Float) {
    val dimensions = entity.tryGetComponent<PanelDimensions>()
    val scaleComponent = entity.getComponent<Scale>()
    if (dimensions != null) {
      // Scale based on actual size
      val scaleAdditionDim = scaleAddition / dimensions.dimensions.x
      val minBasedOnDim = minScale / dimensions.dimensions.x
      val maxBasedOnDim = maxScale / dimensions.dimensions.x
      scaleComponent.scale.x =
          (scaleComponent.scale.x + scaleAdditionDim).coerceIn(minBasedOnDim, maxBasedOnDim)
      scaleComponent.scale.y =
          (scaleComponent.scale.y + scaleAdditionDim).coerceIn(minBasedOnDim, maxBasedOnDim)
    } else {
      // Basic scale
      scaleComponent.scale.x += scaleAddition
      scaleComponent.scale.x.coerceIn(minScale, maxScale)
      scaleComponent.scale.y += scaleAddition
      scaleComponent.scale.y.coerceIn(minScale, maxScale)
    }
    entity.setComponent(scaleComponent)
  }

  companion object {
    val TAG = "AnalogScalableSystem"
  }
}
