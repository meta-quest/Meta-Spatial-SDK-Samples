// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.IntAttribute

// This Component allow us to detect if an object is "attachable" to a board, or is it a board
// itself.
// In BoardParentingSystem.kt, we detect the distance between this objects and the boards, and give
// the sensation that we are sticking to them
class AttachableComponent(type: Int? = 0) : ComponentBase() {

  var type by IntAttribute("type", R.id.AttachableComponent_type, this, type)

  override fun typeID(): Int {
    return AttachableComponent.id
  }

  companion object : ComponentCompanion {
    override val id = R.id.AttachableComponent_class
    override val createDefaultInstance = { AttachableComponent() }
  }
}
