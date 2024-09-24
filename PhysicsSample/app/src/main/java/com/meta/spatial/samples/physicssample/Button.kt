/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.StringAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute

/*
 * A Component that makes an object act like a button.
 */
class Button(
    eventName: String = "trigger",
    isDown: Boolean = false,
    downOffset: Vector3 = Vector3(0.0f, -0.012f, 0.0f),
    startPosition: Vector3 = Vector3(0.0f)
) : ComponentBase() {

  var eventName by StringAttribute("eventName", R.string.Button_eventName, this, eventName)
  var isDown by BooleanAttribute("isDown", R.string.Button_isDown, this, isDown)
  var downOffset by Vector3Attribute("downOffset", R.string.Button_downOffset, this, downOffset)
  var startPosition by
      Vector3Attribute("startPosition", R.string.Button_startPosition, this, startPosition)

  override fun typeID(): Int {
    return Button.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.Button_class
    override val createDefaultInstance = { Button() }
  }
}
