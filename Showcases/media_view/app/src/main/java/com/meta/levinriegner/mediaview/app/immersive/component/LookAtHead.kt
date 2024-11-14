// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.component

import com.meta.levinriegner.mediaview.R
import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.FloatAttribute

class LookAtHead(
    zOffset: Float = 1f,
    once: Boolean = true,
    hasLooked: Boolean = false,
) : ComponentBase() {

  var zOffset by FloatAttribute("zOffset", R.id.LookAtHead_zOffset, this, zOffset)
  var once by BooleanAttribute("once", R.id.LookAtHead_once, this, once)
  var hasLooked by BooleanAttribute("hasLooked", R.id.LookAtHead_hasLooked, this, hasLooked)

  fun copyWith(
      zOffset: Float = this.zOffset,
      once: Boolean = this.once,
      hasLooked: Boolean = this.hasLooked,
  ): LookAtHead {
    return LookAtHead(zOffset, once, hasLooked)
  }

  override fun typeID(): Int {
    return id
  }

  companion object : ComponentCompanion {
    override val id = R.id.LookAtHead_class
    override val createDefaultInstance = { LookAtHead() }
  }
}
