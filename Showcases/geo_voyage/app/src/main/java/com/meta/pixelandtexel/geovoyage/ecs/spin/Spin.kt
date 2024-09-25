// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.spin

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.R
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.DataModel
import com.meta.spatial.core.FloatAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

/**
 * A simple component to steadily rotate an entity around an axis
 *
 * @param speed degrees per second
 * @param axis the vector direction around which to rotate the object
 */
class Spin(speed: Float = 15f, axis: Vector3 = Vector3(0f, 1f, 0f)) : ComponentBase() {

  var speed by FloatAttribute("speed", R.string.Spin_speed, this, speed)
  var axis by Vector3Attribute("axis", R.string.Spin_axis, this, axis.normalize())

  override fun typeID(): Int {
    return Spin.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.Spin_class
    override val createDefaultInstance = { Spin() }
  }
}

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
