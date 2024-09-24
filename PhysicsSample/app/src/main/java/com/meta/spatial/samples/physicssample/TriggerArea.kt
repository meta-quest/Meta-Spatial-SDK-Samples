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
import com.meta.spatial.core.StringAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute

/*
 * A Component that is triggered when an entity with a Trigger component enters the trigger area.
 */
class TriggerArea(
    eventName: String = "trigger",
    value: Float = 10.0f,
    size: Vector3 = Vector3(0.1f, 0.1f, 0.1f),
) : ComponentBase() {

  var eventName by StringAttribute("eventName", R.string.TriggerArea_eventName, this, eventName)
  var value by FloatAttribute("value", R.string.TriggerArea_value, this, value)
  var size by Vector3Attribute("size", R.string.TriggerArea_size, this, size)

  override fun typeID(): Int {
    return TriggerArea.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.TriggerArea_class
    override val createDefaultInstance = { TriggerArea() }
  }
}
