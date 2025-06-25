/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.common

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemManager
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.PlayerBodyAttachmentSystem
import com.meta.spatial.toolkit.Transform

fun getHmd(systemManager: SystemManager): Entity? {
  return systemManager
      .tryFindSystem<PlayerBodyAttachmentSystem>()
      ?.tryGetLocalPlayerAvatarBody()
      ?.head
}

fun getLeftController(systemManager: SystemManager): Entity? {
  return systemManager
      .tryFindSystem<PlayerBodyAttachmentSystem>()
      ?.tryGetLocalPlayerAvatarBody()
      ?.leftHand
}

fun getRightController(systemManager: SystemManager): Entity? {
  return systemManager
      .tryFindSystem<PlayerBodyAttachmentSystem>()
      ?.tryGetLocalPlayerAvatarBody()
      ?.rightHand
}

fun <T> returnTo2DActivity(
    activity: AppSystemActivity,
    applicationContext: Context,
    class2DActivity: Class<T>
) {
  val panelIntent =
      Intent(applicationContext, class2DActivity).apply {
        action = Intent.ACTION_MAIN
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
  // Wrap the created Intent in a PendingIntent object
  val pendingPanelIntent =
      PendingIntent.getActivity(
          applicationContext,
          0,
          panelIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

  // Create and send the Intent to launch the Home environment, providing the
  // PendingIntent object as extra parameters
  val launchInHomeKey = "extra_launch_in_home_pending_intent"
  val homeIntent =
      Intent(Intent.ACTION_MAIN)
          .addCategory(Intent.CATEGORY_HOME)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          .putExtra(launchInHomeKey, pendingPanelIntent)
  activity.startActivity(homeIntent)
  activity.finish()
}

fun recenterElementInView(head: Entity?, element: Entity?) {
  if (head == null || element == null) {
    return
  }
  val headPose = head.tryGetComponent<Transform>()?.transform
  if (headPose == null || headPose == Pose()) {
    return
  }
  val forward = headPose.q * Vector3(0f, 0f, 1f)
  forward.y = 0f
  headPose.q = Quaternion.lookRotation(forward)
  // Rotate it to face away from the hmd
  headPose.q *= Quaternion(20f, 0f, 0f)
  // Bring it away from the hmd
  headPose.t += headPose.q * Vector3(0f, 0f, 0.8f)
  element.setComponent(Transform(headPose))
  return
}
