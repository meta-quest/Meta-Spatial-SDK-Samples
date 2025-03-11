// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.spin

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.Spin
import com.meta.spatial.core.DataModel
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

class SpinLoader : BaseLoader() {
  override fun parseAttributes(ctx: LoaderContext, dm: DataModel, parser: XmlResourceParser) {
    val speedValue = parser.getAttributeValue(null, "speed")?.toFloat()

    val axisString: String? = parser.getAttributeValue(null, "axis")
    val axisArray = axisString?.split(",".toRegex(), 3)?.toTypedArray()

    ctx.entity_!!.setComponent(
        Spin().apply {
          if (speedValue != null) {
            speed = speedValue
          }

          axisArray?.let {
            axis = Vector3(it[0].toFloat(), it[1].toFloat(), it[2].toFloat()).normalize()
          }
        })
  }
}
