// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.EnumAttribute
import com.meta.spatial.core.IntAttribute
import com.meta.spatial.core.LongAttribute

// Time Component allow us to identify and save properties of Clock and Timer tools, to update them
// later in UpdateTimeSystem.kt

class TimeComponent(
    type: AssetType = AssetType.CLOCK,
    totalTime: Int = 0,
    startTime: Long = System.currentTimeMillis(),
    complete: Boolean = false,
    loop: Int = 0,
) : ComponentBase() {

  var type by EnumAttribute("type", R.id.TimeComponent_type, this, AssetType::class.java, type)
  var totalTime by IntAttribute("totalTime", R.id.TimeComponent_total_time, this, totalTime)
  var startTime by LongAttribute("startTime", R.id.TimeComponent_start_time, this, startTime)
  var complete by BooleanAttribute("complete", R.id.TimeComponent_complete, this, complete)
  var loop by IntAttribute("loop", R.id.TimeComponent_loop, this, loop)

  override fun typeID(): Int {
    return TimeComponent.id
  }

  companion object : ComponentCompanion {
    override val id = R.id.TimeComponent_class
    override val createDefaultInstance = { TimeComponent() }
  }
}
