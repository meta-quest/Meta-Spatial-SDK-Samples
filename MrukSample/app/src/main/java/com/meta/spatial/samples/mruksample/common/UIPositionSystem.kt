/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.common

import com.meta.spatial.core.Pose
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Transform

/**
 * System for managing UI panel visibility based on HMD pose initialization.
 *
 * This system monitors the HMD pose and waits until it is properly initialized before making the UI
 * panel visible. This ensures the UI panel can be correctly positioned relative to the user's head
 * position.
 *
 * @property setUIPanelVisibility Callback function to set the visibility of the UI panel
 */
class UIPositionSystem(
    private val setUIPanelVisibility: (Boolean) -> Unit,
) : SystemBase() {
  private var uiPositionInitialized = false

  override fun execute() {
    // We need to wait until the HMD pose is initialized before we can position the UI panel.
    // Keep trying until it succeeds.
    if (!uiPositionInitialized && isHmdPoseInitialized()) {
      uiPositionInitialized = true
      setUIPanelVisibility(true)
    }
  }

  private fun isHmdPoseInitialized(): Boolean {
    val head = getHmd(systemManager) ?: return false
    val headPose = head.tryGetComponent<Transform>()?.transform
    return headPose != null && headPose != Pose()
  }
}
