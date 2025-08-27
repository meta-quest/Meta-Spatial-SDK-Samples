/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.controlPanelVisibility

import com.meta.spatial.core.SystemBase
import com.meta.spatial.samples.premiummediasample.entities.ControlsPanelEntity
import com.meta.spatial.samples.premiummediasample.getAnyKeyDown
import com.meta.spatial.samples.premiummediasample.systems.pointerInfo.PointerInfoSystem
import com.meta.spatial.samples.premiummediasample.systems.scalable.TouchScalableSystem
import com.meta.spatial.toolkit.Grabbable

class ControlPanelVisibilitySystem(private val controlsPanel: ControlsPanelEntity) : SystemBase() {
  // Not directly tracking visible component due to fading in/out
  private var controlsVisible: Boolean = false
  private var controlsActiveTime = 0L
  private val controlsShowDuration = 3000L
  private var wasGrabbingVideo = false

  private var activelyTracking = false

  fun fadeAndStartTracking() {
    activelyTracking = true
    setControlsVisibility(isVisible = true, fade = true)
  }

  fun fadeAndStopTracking() {
    activelyTracking = false
    setControlsVisibility(isVisible = false, fade = true)
  }

  override fun execute() {
    // Hide controls after inactivity
    val now = System.currentTimeMillis()
    handleInactivity(now)
    handleKeyPress()
    updateControlsActiveTime()
    handleGrabbing()
  }

  private fun handleInactivity(now: Long) {
    if (controlsVisible && now > (controlsActiveTime + controlsShowDuration)) {
      setControlsVisibility(isVisible = false, fade = false)
    }
  }

  private fun handleKeyPress() {
    if (getAnyKeyDown()) {
      if (activelyTracking) {
        // If we are scaling, hide controls
        if (
            controlsPanel.parentEntity !== null &&
                systemManager
                    .findSystem<TouchScalableSystem>()
                    .currentlyScaling
                    .contains(controlsPanel.parentEntity)
        ) {
          setControlsVisibility(isVisible = false, fade = false)
        } else {
          // Show on click if controls are hidden or pointing at controls
          // Otherwise hide controls.
          setControlsVisibility(
              systemManager.findSystem<PointerInfoSystem>().checkHover(controlsPanel.entity) ||
                  !controlsVisible,
              false,
          )
        }
      }
    }
  }

  private fun updateControlsActiveTime() {
    // If we're pointing at something, then keep controls alive
    val pointerInfoSystem = systemManager.findSystem<PointerInfoSystem>()
    if (pointerInfoSystem.checkHover(controlsPanel.entity)) {
      controlsActiveTime = System.currentTimeMillis()
    }
  }

  private fun handleGrabbing() {
    if (activelyTracking) {
      // If we're grabbing
      val grabComponent = controlsPanel.parentEntity?.tryGetComponent<Grabbable>()
      if (grabComponent !== null) {
        val isGrabbingVideo = grabComponent.isGrabbed
        if (isGrabbingVideo != wasGrabbingVideo) {
          if (isGrabbingVideo) {
            setControlsVisibility(isVisible = false, fade = false)
          }
          wasGrabbingVideo = isGrabbingVideo
        }
      }
    }
  }

  fun setControlsVisibility(isVisible: Boolean, fade: Boolean) {
    if (isVisible) {
      controlsActiveTime = System.currentTimeMillis()
    }
    controlsVisible = isVisible

    if (fade) {
      controlsPanel.fadeVisibility(isVisible)
    } else {
      controlsPanel.setVisible(isVisible)
    }
  }
}
