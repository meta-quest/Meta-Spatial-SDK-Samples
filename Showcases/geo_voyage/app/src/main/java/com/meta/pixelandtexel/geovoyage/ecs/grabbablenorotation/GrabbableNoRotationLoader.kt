// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.grabbablenorotation

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.GrabbableNoRotation
import com.meta.spatial.core.DataModel
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

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
