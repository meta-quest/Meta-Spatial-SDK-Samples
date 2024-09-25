// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.grabbablenorotation

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.R
import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.DataModel
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

class GrabbableNoRotation(enabled: Boolean = true) : ComponentBase() {
  var enabled by BooleanAttribute("enabled", R.string.GrabbableNoRotation_enabled, this, enabled)
  var isGrabbed by
      BooleanAttribute("isGrabbed", R.string.GrabbableNoRotation_isGrabbed, this, false)

  override fun typeID(): Int {
    return id
  }

  companion object : ComponentCompanion {
    override val id = R.string.GrabbableNoRotation_class
    override val createDefaultInstance = { GrabbableNoRotation() }
  }
}

class GrabbableNoRotationLoader : BaseLoader() {
  override fun parseAttributes(ctx: LoaderContext, dm: DataModel, parser: XmlResourceParser) {
    val enabledValue = parser.getAttributeValue(null, "enabled")?.toBoolean()

    ctx.entity_!!.setComponent(
        GrabbableNoRotation().apply {
          if (enabledValue != null) {
            enabled = enabledValue
          }
        })
  }
}
