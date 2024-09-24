/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.customcomponentssample

import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.Entity
import com.meta.spatial.core.EntityAttribute
import com.meta.spatial.core.EnumAttribute
import com.meta.spatial.core.FloatAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute

enum class LookAtAxis {
  ALL,
  Y,
}

class LookAt(
    target: Entity = Entity.nullEntity(),
    lookAtHead: Boolean = false,
    axis: LookAtAxis = LookAtAxis.ALL,
    speed: Float = 0.15f,
    offset: Vector3 = Vector3(0.0f, 0.0f, 0.0f)
) : ComponentBase() {

  var target: Entity by EntityAttribute("target", R.id.LookAt_target, this, target)
  var lookAtHead by BooleanAttribute("lookAtHead", R.id.LookAt_lookAtHead, this, lookAtHead)
  var axis by EnumAttribute("axis", R.id.LookAt_axis, this, LookAtAxis::class.java, axis)
  var speed by FloatAttribute("speed", R.id.LookAt_speed, this, speed)
  var offset by Vector3Attribute("offset", R.id.LookAt_offset, this, offset)

  override fun typeID(): Int {
    return LookAt.id
  }

  companion object : ComponentCompanion {
    override val id = R.id.LookAt_class
    override val createDefaultInstance = { LookAt() }
  }
}
