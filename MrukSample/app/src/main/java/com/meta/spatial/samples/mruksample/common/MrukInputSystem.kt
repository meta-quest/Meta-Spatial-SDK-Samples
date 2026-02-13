/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.common

import android.util.Log
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.toolkit.AvatarBody
import com.meta.spatial.toolkit.Controller

/**
 * Input system for handling VR controller interactions in the MRUK sample application.
 *
 * This system monitors the left controller for menu button presses and releases. When the menu
 * button is pressed, it triggers the provided callback function. Button releases are logged for
 * debugging purposes.
 *
 * @property onMenuButtonPressed Callback function that is invoked when the menu button is pressed
 */
class MrukInputSystem(private val onMenuButtonPressed: () -> Unit) : SystemBase() {

  override fun execute() {
    // Query for local player-controlled avatar bodies
    val avatarBodies = Query.where { has(AvatarBody.id) }.eval().toList()
    val localPlayerAvatar =
        avatarBodies.first { it.isLocal() && it.getComponent<AvatarBody>().isPlayerControlled }

    // Get the left controller from the avatar's left hand
    val avatarBody = localPlayerAvatar.getComponent<AvatarBody>()
    val leftController = avatarBody.leftHand.tryGetComponent<Controller>()

    // Handle menu button interactions if controller is available
    leftController?.let { controller ->
      if (controller.getPressed(ButtonBits.ButtonMenu)) {
        onMenuButtonPressed()
      }

      if (controller.getReleased(ButtonBits.ButtonMenu)) {
        Log.i("MrukInputSystem", "Menu button released")
      }
    }
  }
}

/**
 * Returns true if the button was pressed this frame.
 *
 * @param button The button bit to check (e.g., ButtonBits.ButtonMenu)
 * @return true if the button was pressed this frame, false otherwise
 */
fun Controller.getPressed(button: Int): Boolean = (buttonState and changedButtons and button) != 0

/**
 * Returns true if the button was released this frame.
 *
 * @param button The button bit to check (e.g., ButtonBits.ButtonMenu)
 * @return true if the button was released this frame, false otherwise
 */
fun Controller.getReleased(button: Int): Boolean =
    (buttonState and button) == 0 && (changedButtons and button) != 0
