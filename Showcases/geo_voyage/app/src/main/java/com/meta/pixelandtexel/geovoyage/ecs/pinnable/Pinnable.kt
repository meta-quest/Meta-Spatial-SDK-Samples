// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.pinnable

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.R
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.DataModel
import com.meta.spatial.core.StringAttribute
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

class Pinnable(
    meshName: String = "pin.glb",
) : ComponentBase() {

  // currently unused, but demonstrates how you could define different pins to use on different
  // Pinnables
  var meshName by StringAttribute("meshName", R.string.Pinnable_meshName, this, meshName)

  override fun typeID(): Int {
    return id
  }

  companion object : ComponentCompanion {
    override val id = R.string.Pinnable_class
    override val createDefaultInstance = { Pinnable() }
  }
}

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
