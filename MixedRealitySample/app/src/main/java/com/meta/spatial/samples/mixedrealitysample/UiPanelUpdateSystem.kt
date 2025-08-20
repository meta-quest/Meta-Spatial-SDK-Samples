/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mixedrealitysample

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.physics.Physics
import com.meta.spatial.toolkit.PlayerBodyAttachmentSystem
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Transform

class UiPanelUpdateSystem() : SystemBase() {

  private var panelIsRepositioned = false

  override fun execute() {
    val activity = SpatialActivityManager.getVrActivity<MixedRealitySampleActivity>()
    // Keep trying until HMD orientation is available
    if (!panelIsRepositioned && activity.glxfLoaded)
        panelIsRepositioned = updateUiPanelPosition(activity)
  }

  private fun updateUiPanelPosition(activity: MixedRealitySampleActivity): Boolean {
    var head = getHmd()
    if (head == null) return false

    var headPose = head.tryGetComponent<Transform>()?.transform
    if (headPose == null || headPose == Pose()) return false

    val composition = activity.glXFManager.getGLXFInfo(MixedRealitySampleActivity.GLXF_SCENE)
    val panel = composition.tryGetNodeByName("Panel")
    if (panel?.entity == null) return false

    var forward = headPose.q * Vector3(0f, 0f, 1f)
    val panelPose = Pose(Vector3(0f, 0f, 0f), Quaternion(1f, 0f, 0f, 0f))

    // Position in front of HMD
    panelPose.t = headPose.t + forward * 2.5f
    // Lock y position
    panelPose.t.y = 1.1f
    // Rotate y axis to face HMD
    panelPose.q = Quaternion.lookRotationAroundY(forward)

    panel.entity.setComponent(Transform(panelPose))

    // set physics on panel after position is set (otherwise physics interferes with position)
    panel.entity.setComponent(
        Physics("box", dimensions = Vector3(1.0f, 0.75f, 0f), restitution = 0.9f)
    )

    return true
  }

  private fun getHmd(): Entity? {
    return systemManager
        .tryFindSystem<PlayerBodyAttachmentSystem>()
        ?.tryGetLocalPlayerAvatarBody()
        ?.head
  }
}
