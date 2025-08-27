/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.scalable

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.samples.premiummediasample.R
import com.meta.spatial.samples.premiummediasample.entities.ImageBoxEntity
import com.meta.spatial.samples.premiummediasample.projectRayOntoPlane
import com.meta.spatial.samples.premiummediasample.systems.pointerInfo.PointerInfoSystem
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.ControllerType
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.getAbsoluteTransform

class TouchScalableSystem(private val minScale: Float = 0.5f, private val maxScale: Float = 5f) :
    SystemBase() {
  private var lastTime = System.currentTimeMillis()
  private var timePassed = 0f

  private var corners: Array<Entity>
  private var selectedEntity: Entity? = null

  private val maxHideTime = 1.5f
  private var hidingTime = 0f
  private var shownTime = 0f

  private val registeredEntities = ArrayList<Entity>()
  private val viewData = mutableMapOf<Entity, PanelData>()
  val currentlyScaling = ArrayList<Entity>()

  data class PanelData(var startPanelDimensions: Vector2, var scale: Number = 1f)

  private var lastSelectedEntity: Entity? = null

  private var cornerSize = 0.1f
  // Default is center pivot. This offsets it in physical space towards the center if positive
  private var cornerOffsetPivot = 0f

  private var isHidden = true

  init {
    corners =
        arrayOf(
            ImageBoxEntity.create(
                R.drawable.corner_round,
                cornerSize,
                cornerSize,
                Transform(),
                Visible(false),
            ),
            ImageBoxEntity.create(
                R.drawable.corner_round,
                cornerSize,
                cornerSize,
                Transform(),
                Visible(false),
            ),
            ImageBoxEntity.create(
                R.drawable.corner_round,
                cornerSize,
                cornerSize,
                Transform(),
                Visible(false),
            ),
            ImageBoxEntity.create(
                R.drawable.corner_round,
                cornerSize,
                cornerSize,
                Transform(),
                Visible(false),
            ),
        )
  }

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    val diff = (currentTime - lastTime).toDouble().toFloat() / 1000f
    lastTime = currentTime
    timePassed += diff

    hidingTime += diff
    if (selectedEntity != null) shownTime += diff

    if (hidingTime > maxHideTime) {
      hidePanel()
    }

    val pointerSystem = systemManager.findSystem<PointerInfoSystem>()
    val hoverEntity = pointerSystem.rightEntity ?: pointerSystem.leftEntity
    checkHover(hoverEntity)
    executeTouchScale()
  }

  private fun hidePanel() {
    if (isHidden) return

    isHidden = true

    shownTime = 0f
    hidingTime = 0f

    for (corner in corners) corner.setComponent(Visible(false))
  }

  private fun checkHover(entity: Entity?) {
    if (entity == null) {
      if (lastSelectedEntity != null) updatePanel(lastSelectedEntity!!, false)
      if (selectedEntity != null) firstHidePanel()
      return
    }

    // Do corner press check now
    if (corners.contains(entity)) {
      hidingTime = 0f
      return
    }

    // Selecting wrong item
    if (!registeredEntities.contains(entity)) {
      if (lastSelectedEntity != null) updatePanel(lastSelectedEntity!!, false)
      firstHidePanel()
      return
    }

    // Same entity, so update position and scale
    if (selectedEntity == entity) {
      updatePanel(selectedEntity!!)
      return
    }

    selectedEntity = entity
    lastSelectedEntity = entity
    // New entity, set visible
    showPanel(entity)
  }

  private fun updatePanel(entity: Entity, resetTime: Boolean = true) {
    val panelDimensions = entity.tryGetComponent<PanelDimensions>() ?: return

    val scale = entity.getComponent<Scale>()
    val pose = getAbsoluteTransform(entity)

    val offset =
        Vector2(
            panelDimensions.dimensions.x * scale.scale.x * 0.5f,
            panelDimensions.dimensions.y * scale.scale.y * 0.5f,
        )
    val offsets =
        arrayOf(
            pose.right() * -(offset.x - cornerOffsetPivot) +
                pose.up() * (offset.y - cornerOffsetPivot),
            pose.right() * (offset.x - cornerOffsetPivot) +
                pose.up() * (offset.y - cornerOffsetPivot),
            pose.right() * (offset.x - cornerOffsetPivot) +
                pose.up() * -(offset.y - cornerOffsetPivot),
            pose.right() * -(offset.x - cornerOffsetPivot) +
                pose.up() * -(offset.y - cornerOffsetPivot),
        )

    // Update position, and alpha of corners
    corners.forEachIndexed { index, corner ->
      corner.setComponent(
          Transform(Pose(pose.t + offsets[index], pose.q.times(cornerRotations[index])))
      )
    }

    if (resetTime) hidingTime = 0f
  }

  private fun firstHidePanel() {
    selectedEntity = null
    hidingTime = 0f
  }

  private fun showPanel(entity: Entity) {
    if (!isHidden) return
    isHidden = false
    for (corner in corners) corner.setComponent(Visible(true))

    updatePanel(entity)
  }

  private fun executeTouchScale() {
    // Grab the controllers
    var triggerIsHeldDown = false
    var triggerChangedThisFrame = false
    var controllerOrigin = Vector3(0f)
    var controllerForward = Vector3(0f)

    val controllersQ = Query.where { has(Controller.id) }
    for (controllerEntity in controllersQ.eval()) {
      if (!controllerEntity.isLocal()) {
        continue
      }
      val controllerData = controllerEntity.getComponent<Controller>()
      // Controllers can go inactive when they are not in use, so we need to check for that
      if (!controllerData.isActive) {
        continue
      }

      val clickButtonBits =
          if (controllerData.type == ControllerType.HAND) ButtonBits.ButtonA or ButtonBits.ButtonY
          else
              ButtonBits.ButtonTriggerL or
                  ButtonBits.ButtonTriggerR or
                  ButtonBits.ButtonSqueezeR or
                  ButtonBits.ButtonSqueezeL
      triggerIsHeldDown = (controllerData.buttonState and (clickButtonBits)) > 0
      triggerChangedThisFrame = (controllerData.changedButtons and (clickButtonBits)) > 0

      if (triggerIsHeldDown || triggerChangedThisFrame) {
        val controllerPose = getAbsoluteTransform(controllerEntity)
        controllerOrigin = controllerPose.t
        controllerForward = controllerPose.forward()
        break
      }
    }

    if (!triggerIsHeldDown) {
      // Trigger Up (drop item)
      if (triggerChangedThisFrame) {
        currentlyScaling.clear()
      }
      return
    }

    if (triggerChangedThisFrame) {
      // Trigger Down (possible press on scalable)
      val hitInfo = getScene().lineSegmentIntersect(controllerOrigin, controllerForward * 1000f)
      if (hitInfo != null) {
        if (corners.contains(hitInfo.entity) && lastSelectedEntity != null) {
          currentlyScaling.add(lastSelectedEntity!!)
        }
      }
    }

    for (entity in currentlyScaling) {
      // Plane
      val planeTransform = getAbsoluteTransform(entity)

      val resultPoint =
          projectRayOntoPlane(
              controllerOrigin,
              controllerForward,
              planeTransform.t,
              planeTransform.forward(),
          )
      if (resultPoint != null) {
        val distanceToPlaneCenter = (resultPoint - planeTransform.t).length()
        if (viewData.containsKey(entity)) {
          val originalCornerScale = viewData[entity]!!.startPanelDimensions.length() * 0.5f
          val percentScale = distanceToPlaneCenter / originalCornerScale
          scaleEntity(percentScale, entity, viewData[entity]!!.startPanelDimensions)
        }
      }
    }
  }

  private fun scaleEntity(wantedScale: Float, entity: Entity, startPanelDimensions: Vector2) {
    var width = wantedScale * startPanelDimensions.x
    width = width.coerceIn(minScale, maxScale)
    val clampedScale = width / startPanelDimensions.x
    val scale = Scale(Vector3(clampedScale))
    entity.setComponent(scale)
    //        cornerPanel.setComponent(scale)
    updatePanel(entity) // Updates scale
    // entity.getComponent<TransformParent>().entity.setComponent(scale)
    hidingTime = 0f
  }

  fun registerEntity(entity: Entity) {
    registeredEntities.add(entity)
    viewData[entity] = PanelData(entity.getComponent<PanelDimensions>().dimensions, 1f)
  }

  fun unregisterEntity(entity: Entity) {
    if (lastSelectedEntity == entity) lastSelectedEntity = null

    viewData.remove(entity)
    currentlyScaling.remove(entity)
    registeredEntities.remove(entity)
  }

  fun forceHide(animate: Boolean) {
    if (animate) firstHidePanel() else hidePanel()
  }

  companion object {
    private const val TAG = "ScaleableSystemTouch"

    private val cornerRotations =
        arrayOf(
            Quaternion(0f, 0f, 0f),
            Quaternion(0f, 0f, -90f),
            Quaternion(0f, 0f, -180f),
            Quaternion(0f, 0f, -270f),
        )
  }
}
