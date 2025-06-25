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

class MrukInputSystem(private val onMenuButtonPressed: () -> Unit) : SystemBase() {

  override fun execute() {
    val leftController =
        Query.where { has(AvatarBody.id) }
            .eval()
            .toMutableList()
            .first { it.isLocal() && it.getComponent<AvatarBody>().isPlayerControlled }
            .getComponent<AvatarBody>()
            .leftHand
            .tryGetComponent<Controller>()

    if (leftController != null && leftController.getPressed(ButtonBits.ButtonMenu)) {
      onMenuButtonPressed()
    }

    if (leftController != null && leftController.getReleased(ButtonBits.ButtonMenu)) {
      Log.i("MrukInputSystem", "Menu button released")
    }
  }
}

/** returns true if the button was pressed this frame */
fun Controller.getPressed(button: Int): Boolean {
  return (buttonState and changedButtons and button) != 0
}

/** returns true if the button was released this frame */
fun Controller.getReleased(button: Int): Boolean {
  return buttonState and button == 0 && changedButtons and button != 0
}
