/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.LongAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute

/*
 * A Component that Triggers an event when it enteres a TriggerArea component
 */
class Trigger(size: Vector3 = Vector3(0.1f, 0.1f, 0.1f), insideAreaId: Long = 0) : ComponentBase() {

  var size by Vector3Attribute("size", R.string.Trigger_size, this, size)
  var insideAreaId by
      LongAttribute("insideAreaId", R.string.Trigger_insideAreaId, this, insideAreaId)

  override fun typeID(): Int {
    return Trigger.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.Trigger_class
    override val createDefaultInstance = { Trigger() }
  }
}
