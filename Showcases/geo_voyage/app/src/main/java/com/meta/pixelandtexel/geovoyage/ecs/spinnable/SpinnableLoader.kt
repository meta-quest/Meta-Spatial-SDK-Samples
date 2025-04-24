// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.spinnable

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.Spinnable
import com.meta.spatial.core.DataModel
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

class SpinnableLoader : BaseLoader() {
  override fun parseAttributes(ctx: LoaderContext, dm: DataModel, parser: XmlResourceParser) {
    val sizeValue = parser.getAttributeValue(null, "size")?.toFloat()
    val dragValue = parser.getAttributeValue(null, "drag")?.toFloat()

    ctx.entity_!!.setComponent(
        Spinnable().apply {
          if (sizeValue != null) {
            size = sizeValue
          }
          if (dragValue != null) {
            drag = dragValue
          }
        })
  }
}
