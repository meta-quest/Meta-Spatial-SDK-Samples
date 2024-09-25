// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.spinnable

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.PIf
import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.DataModel
import com.meta.spatial.core.FloatAttribute
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

class Spinnable(size: Float = 1f, drag: Float = 1f) : ComponentBase() {
  var size by FloatAttribute("size", R.string.Spinnable_size, this, size)
  var drag by FloatAttribute("drag", R.string.Spinnable_drag, this, drag)

  var isGrabbed by BooleanAttribute("isGrabbed", R.string.Spinnable_isGrabbed, this, false)
  var isSpinning by BooleanAttribute("isSpinning", R.string.Spinnable_isSpinning, this, false)
  var pitch by FloatAttribute("pitch", R.string.Spinnable_pitch, this, 0f)

  override fun typeID(): Int {
    return id
  }

  companion object : ComponentCompanion {
    override val id = R.string.Spinnable_class
    override val createDefaultInstance = { Spinnable() }

    const val MAX_PITCH_RAD: Float = 45f * PIf / 180f
  }
}

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
