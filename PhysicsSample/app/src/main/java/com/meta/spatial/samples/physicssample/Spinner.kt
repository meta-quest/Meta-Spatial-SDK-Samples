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

/*
 * A Component that spins an object.
 */
class Spinner(
    speed: Float = 50.0f,
) : ComponentBase() {

  var speed by FloatAttribute("speed", R.string.Spinner_speed, this, speed)

  override fun typeID(): Int {
    return Spinner.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.Spinnerclass_class
    override val createDefaultInstance = { Spinner() }
  }
}
