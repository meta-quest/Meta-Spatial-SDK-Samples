/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.FloatAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute

/*
 * A Component that Animates a transform up and down.
 */
class UpAndDown(
    amount: Float = 0.1f,
    speed: Float = 0.5f,
    offset: Float = 0.0f,
    startPosition: Vector3 = Vector3(0f)
) : ComponentBase() {

  var amount by FloatAttribute("amount", R.string.UpAndDown_amount, this, amount)
  var speed by FloatAttribute("speed", R.string.UpAndDown_speed, this, speed)
  var offset by FloatAttribute("offset", R.string.UpAndDown_offset, this, offset)
  var startPosition by
      Vector3Attribute("startPosition", R.string.Button_startPosition, this, startPosition)

  override fun typeID(): Int {
    return UpAndDown.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.UpAndDown_class
    override val createDefaultInstance = { UpAndDown() }
  }
}
