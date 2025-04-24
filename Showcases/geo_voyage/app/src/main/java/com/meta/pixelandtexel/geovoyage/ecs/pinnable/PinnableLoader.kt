// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.pinnable

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.Pinnable
import com.meta.spatial.core.DataModel
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

class PinnableLoader : BaseLoader() {
  override fun parseAttributes(ctx: LoaderContext, dm: DataModel, parser: XmlResourceParser) {
    val meshNameValue: String? = parser.getAttributeValue(null, "meshName")

    ctx.entity_!!.setComponent(
        Pinnable().apply {
          if (meshNameValue != null) {
            meshName = meshNameValue
          }
        })
  }
}
