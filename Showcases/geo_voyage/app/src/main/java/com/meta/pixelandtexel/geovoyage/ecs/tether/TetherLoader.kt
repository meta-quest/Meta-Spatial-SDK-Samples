// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.tether

import android.content.res.XmlResourceParser
import com.meta.pixelandtexel.geovoyage.Tether
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.PIf
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.yawAngle
import com.meta.spatial.core.DataModel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.inflate.BaseLoader
import com.meta.spatial.toolkit.inflate.LoaderContext

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

  companion object {
    // Helper function to calculate the yaw, z, and y offsets from the entities' current poses
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
