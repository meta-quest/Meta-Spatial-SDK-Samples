/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.entities

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.samples.premiummediasample.PanelLayerAlpha
import com.meta.spatial.samples.premiummediasample.R
import com.meta.spatial.samples.premiummediasample.ScaledChild
import com.meta.spatial.samples.premiummediasample.TIMINGS
import com.meta.spatial.samples.premiummediasample.getHeadPose
import com.meta.spatial.samples.premiummediasample.millisToFloat
import com.meta.spatial.samples.premiummediasample.panels.controlsPanel.ControlsPanelActivity
import com.meta.spatial.samples.premiummediasample.panels.controlsPanel.ControlsPanelConstants
import com.meta.spatial.samples.premiummediasample.setDistanceFov
import com.meta.spatial.samples.premiummediasample.systems.scaleChildren.ScaleChildrenSystem
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.getAbsoluteTransform
import dorkbox.tweenEngine.TweenEngine
import dorkbox.tweenEngine.TweenEquations

class ControlsPanelEntity(tweenEngine: TweenEngine) : FadingPanel(tweenEngine) {
  companion object {
    private val widthDp = ControlsPanelConstants.PANEL_WIDTH_DP
    private val heightDp = ControlsPanelConstants.PANEL_HEIGHT_DP
    private const val scale = 0.001f

    fun panelRegistration(): PanelRegistration {
      return PanelRegistration(R.id.ControlsPanel) {
        config {
          mips = 1
          layerConfig = LayerConfig()
          layoutWidthInDp = widthDp
          layoutHeightInDp = heightDp
          width = widthDp * scale
          height = heightDp * scale
          enableTransparent = true
          includeGlass = false
          themeResourceId = R.style.PanelAppThemeTransparent
        }
        activityClass = ControlsPanelActivity::class.java
      }
    }
  }

  override val entity: Entity
  var parentEntity: Entity? = null

  var attachedCinema: Entity? = null

  // The three variables are modified by Debug Panel
  var controlsFov = 33f
    set(value) {
      if (field != value) {
        field = value
        attachedCinema?.let { movePanelForCinema(it) }
      }
    }

  var controlsAngle = 24f
    set(value) {
      if (field != value) {
        field = value
        attachedCinema?.let { movePanelForCinema(it) }
      }
    }

  var controlsDistance = 1.25f
    set(value) {
      if (field != value) {
        field = value
        attachedCinema?.let { movePanelForCinema(it) }
      }
    }

  private var controlsOffset = Vector3(0f, 0f, 0f)
    set(value) {
      if (field != value) {
        field = value
        updateTransform()
      }
    }

  private val pivotOffset = Vector3(0f, -0.16f, 0f)

  init {
    entity =
        Entity.create(
            PanelDimensions(Vector2(widthDp * scale, heightDp * scale)),
            Panel(R.id.ControlsPanel),
            Transform(),
            PanelLayerAlpha(0f),
            Visible(false),
            TransformParent(Entity.nullEntity()))
  }

  fun fadeVisibility(isVisible: Boolean, onComplete: (() -> Unit)? = null) {
    val easing = if (isVisible) TweenEquations.Circle_In else TweenEquations.Circle_Out
    super.fadeVisibility(
        isVisible, TIMINGS.CONTROL_PANEL_FADE_BOTH.millisToFloat(), easing, onComplete)
  }

  fun movePanelForCinema(videoEntity: Entity) {
    attachedCinema = videoEntity
    if (parentEntity != null) detachFromEntity()

    val videoPosition = videoEntity.getComponent<Transform>()
    val difference = (videoPosition.transform.t - getHeadPose().t)
    difference.y = 0f
    val headPose = getHeadPose()
    headPose.q = Quaternion.lookRotation(difference.normalize())

    setDistanceFov(
        entity,
        controlsDistance,
        controlsFov,
        true,
        -controlsAngle,
        vector = headPose,
        angleContent = false)
    // Force same rotation as screen
    val controlsPose = entity.getComponent<Transform>()
    controlsPose.transform.q = videoPosition.transform.q
    entity.setComponent(controlsPose)
  }

  fun attachToEntity(target: Entity) {
    attachedCinema = null
    val panelDimensions = target.tryGetComponent<PanelDimensions>()
    if (panelDimensions != null) {
      // Place beneath the panel
      controlsOffset.y = panelDimensions.dimensions.y * -0.5f
    }

    if (parentEntity == null) {
      reparent(target)
    }

    SpatialActivityManager.executeOnVrActivity<AppSystemActivity> { activity ->
      activity.systemManager.findSystem<ScaleChildrenSystem>().forceUpdateChildren(target)
    }
  }

  private fun reparent(parent: Entity) {
    parentEntity = parent

    val transformParent = entity.getComponent<TransformParent>()
    if (transformParent.entity != parent) {
      transformParent.entity = parent
      updateTransform()
      entity.setComponent(transformParent)
      entity.setComponent(ScaledChild(localPosition = controlsOffset, pivotOffset = pivotOffset))
    }
    entity.setComponent(Scale(1f))
  }

  fun detachFromEntity() {
    val scaledChild = entity.tryGetComponent<ScaledChild>()
    scaledChild?.isEnabled = false
    val globalPosition = getAbsoluteTransform(entity)
    entity.setComponent(TransformParent(Entity.nullEntity()))
    entity.setComponent(Transform(globalPosition))
    if (scaledChild != null) {
      entity.setComponent(scaledChild)
    }
    parentEntity = null
  }

  private fun updateTransform() {
    entity.setComponent(Transform(Pose(controlsOffset + pivotOffset)))
  }

  fun destroy() {
    entity.destroy()
    currTween?.cancel()
  }
}
