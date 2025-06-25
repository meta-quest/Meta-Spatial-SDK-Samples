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

class UIPositionSystem(
    private val setUIPanelVisibility: (Boolean) -> Unit,
) : SystemBase() {

  private var uiPositionInitialized = false

  override fun execute() {
    // We need to wait until the HMD pose is initialized before we can position the UI panel.
    // Keep trying until it succeeds.
    if (!uiPositionInitialized) {
      if (isHDMPoseInitialized()) {
        uiPositionInitialized = true
        setUIPanelVisibility(true)
      }
    }
  }

  private fun isHDMPoseInitialized(): Boolean {
    val head = getHmd(systemManager) ?: return false
    val headPose = head.tryGetComponent<Transform>()?.transform
    return !(headPose == null || headPose == Pose())
  }
}
