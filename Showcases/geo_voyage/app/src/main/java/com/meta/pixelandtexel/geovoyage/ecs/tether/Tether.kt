// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.tether

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.PIf
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.yawAngle
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.DataModel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.EntityAttribute
import com.meta.spatial.core.FloatAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

class Tether(
    entity: Entity = Entity.nullEntity(),
    rotateAround: Float = 90f,
    yawOffset: Float = 0f,
    zOffset: Float = 1f,
    yOffset: Float = 0f,
) : ComponentBase() {

  var target: Entity by EntityAttribute("entity", R.string.Tether_entity, this, entity)
  var rotateAround by
      FloatAttribute("rotateAround", R.string.Tether_rotateAround, this, rotateAround)
  var yawOffset by FloatAttribute("yawOffset", R.string.Tether_yawOffset, this, yawOffset)
  var zOffset by FloatAttribute("zOffset", R.string.Tether_zOffset, this, zOffset)
  var yOffset by FloatAttribute("yOffset", R.string.Tether_yOffset, this, yOffset)

  override fun typeID(): Int {
    return Tether.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.Tether_class
    override val createDefaultInstance = { Tether() }

    /** Helper function to calculate the yaw, z, and y offsets from the entities' current poses */
    fun fromEntities(entity: Entity, tetherEntity: Entity): Tether {
      val entityTransform = entity.getComponent<Transform>().transform
      val tetherEntityTransform = tetherEntity.getComponent<Transform>().transform

      val toTetherEntity = entityTransform.t - tetherEntityTransform.t
      val toTetherEntityXY = toTetherEntity * Vector3(1f, 0f, 1f)
      val zOffset = toTetherEntityXY.length()
      val yOffset = toTetherEntity.y

      // instead of getting the head pose to compute the rotate around, assume the view origin
      // is 0,0,0 and use the tetherEntity's position
      var angle = (-tetherEntityTransform.t).yawAngle(toTetherEntityXY)
      angle *= 180f / PIf

      return Tether(tetherEntity, angle, zOffset, yOffset)
    }
  }
}

class TetherLoader : BaseLoader() {
  override fun parseAttributes(ctx: LoaderContext, dm: DataModel, parser: XmlResourceParser) {
    val entityIdValue = parser.getAttributeValue(null, "entity")
    val rotateAroundValue = parser.getAttributeValue(null, "rotateAround")?.toFloat()
    val yawOffsetValue = parser.getAttributeValue(null, "yawOffset")?.toFloat()
    val zOffsetValue = parser.getAttributeValue(null, "zOffset")?.toFloat()
    val yOffsetValue = parser.getAttributeValue(null, "yOffset")?.toFloat()

    ctx.entity_!!.setComponent(
        Tether().apply {
          if (!entityIdValue.isNullOrEmpty()) {
            val id = entityIdValue.substring(1).toLong()
            target = Entity(dm, id)
          }
          if (rotateAroundValue != null) {
            rotateAround = rotateAroundValue
          }
          if (yawOffsetValue != null) {
            yawOffset = yawOffsetValue
          }
          if (zOffsetValue != null) {
            zOffset = zOffsetValue
          }
          if (yOffsetValue != null) {
            yOffset = yOffsetValue
          }
        })
  }
}
