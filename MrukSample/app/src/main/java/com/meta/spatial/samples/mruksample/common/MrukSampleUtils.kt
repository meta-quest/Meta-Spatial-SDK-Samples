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

/**
 * Utility functions for the MRUK (Mixed Reality Utility Kit) sample application.
 *
 * This file provides helper functions for accessing player body entities (HMD and controllers),
 * managing activity transitions between spatial and 2D experiences, and repositioning UI elements
 * relative to the user's head position.
 */

/**
 * The rotation angle in degrees applied to UI elements when positioning them in front of the HMD.
 */
private const val ELEMENT_ROTATION_DEGREES = 20f

/** The distance in meters from the HMD at which UI elements are positioned. */
private const val ELEMENT_DISTANCE_FROM_HMD = 0.8f

/**
 * Retrieves the head-mounted display (HMD) entity for the local player.
 *
 * @param systemManager The system manager to query for the player body attachment system.
 * @return The HMD entity if available, null otherwise.
 */
fun getHmd(systemManager: SystemManager): Entity? {
  return systemManager
      .tryFindSystem<PlayerBodyAttachmentSystem>()
      ?.tryGetLocalPlayerAvatarBody()
      ?.head
}

/**
 * Retrieves the left controller entity for the local player.
 *
 * @param systemManager The system manager to query for the player body attachment system.
 * @return The left controller entity if available, null otherwise.
 */
fun getLeftController(systemManager: SystemManager): Entity? {
  return systemManager
      .tryFindSystem<PlayerBodyAttachmentSystem>()
      ?.tryGetLocalPlayerAvatarBody()
      ?.leftHand
}

/**
 * Retrieves the right controller entity for the local player.
 *
 * @param systemManager The system manager to query for the player body attachment system.
 * @return The right controller entity if available, null otherwise.
 */
fun getRightController(systemManager: SystemManager): Entity? {
  return systemManager
      .tryFindSystem<PlayerBodyAttachmentSystem>()
      ?.tryGetLocalPlayerAvatarBody()
      ?.rightHand
}

/**
 * Returns from the spatial experience to a 2D Android activity.
 *
 * This function finishes the current spatial activity and launches a specified 2D activity in the
 * Home environment. It creates the necessary intents and pending intents to ensure the transition
 * happens correctly.
 *
 * @param T The type of the target activity class.
 * @param activity The current spatial activity to finish.
 * @param applicationContext The application context for creating intents.
 * @param targetActivityClass The class of the 2D activity to launch.
 */
fun <T> returnTo2DActivity(
    activity: AppSystemActivity,
    applicationContext: Context,
    targetActivityClass: Class<T>,
) {
  val panelIntent =
      Intent(applicationContext, targetActivityClass).apply {
        action = Intent.ACTION_MAIN
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
  // Wrap the created Intent in a PendingIntent object
  val pendingPanelIntent =
      PendingIntent.getActivity(
          applicationContext,
          0,
          panelIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )

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

/**
 * Recenters a UI element in front of the user's head position.
 *
 * This function positions the specified element at a fixed distance in front of the user's head,
 * facing the user. It applies a rotation to tilt the element and ensures it's positioned on the
 * horizontal plane (ignoring vertical head rotation).
 *
 * @param head The head entity to use as reference, or null if unavailable.
 * @param element The element entity to reposition, or null if unavailable.
 */
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
  headPose.q *= Quaternion(ELEMENT_ROTATION_DEGREES, 0f, 0f)
  // Bring it away from the hmd
  headPose.t += headPose.q * Vector3(0f, 0f, ELEMENT_DISTANCE_FROM_HMD)
  element.setComponent(Transform(headPose))
}
