/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.FloatAttribute
import com.meta.spatial.core.StringAttribute

class DroneComponent(
    targetName: String = "",
    enabled: Boolean = true,
    rotationSpeed: Float = .05f,
    tiltFactor: Float = .25f,
) : ComponentBase() {

  var targetName: String by
      StringAttribute("targetName", R.string.follower_target_name, this, targetName)
  var enabled: Boolean by BooleanAttribute("enabled", R.string.drone_enabled, this, enabled)
  var rotationSpeed: Float by
      FloatAttribute("rotationSpeed", R.string.drone_rotation_speed, this, rotationSpeed)
  var tiltFactor: Float by
      FloatAttribute("tiltFactor", R.string.drone_tilt_factor, this, tiltFactor)

  override fun typeID(): Int {
    return DroneComponent.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.drone_class
    override val createDefaultInstance = { DroneComponent() }
  }
}
