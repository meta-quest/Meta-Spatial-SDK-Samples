// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.util

import com.meta.levinriegner.mediaview.R
import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.EnumAttribute
import com.meta.spatial.core.FloatAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute

enum class LookAtAxis {
  ALL,
  Y,
}

class SpatialDebugComponent(
    axis: LookAtAxis = LookAtAxis.Y,
    rotationSpeed: Float = 0.15f,
    followSpeed: Float = 0.15f,
    rotationOffset: Vector3 = Vector3(0.0f, 0.0f, 0.0f),
    followOffset: Vector3 = Vector3(0.0f, 0.0f, 0.0f),
    followCamera: Boolean = true,
    billboard: Boolean = true,
    distance: Float = 2.0f
) : ComponentBase() {

  var axis by
      EnumAttribute("axis", R.string.SpatialDebugComponent_axis, this, LookAtAxis::class.java, axis)
  var rotationSpeed by
      FloatAttribute(
          "rotationSpeed", R.string.SpatialDebugComponent_rotationSpeed, this, rotationSpeed)
  var followSpeed by
      FloatAttribute("followSpeed", R.string.SpatialDebugComponent_followSpeed, this, followSpeed)
  var rotationOffset by
      Vector3Attribute(
          "rotationOffset", R.string.SpatialDebugComponent_rotationOffset, this, rotationOffset)
  var followOffset by
      Vector3Attribute(
          "followOffset", R.string.SpatialDebugComponent_followOffset, this, followOffset)
  var followCamera by
      BooleanAttribute(
          "followCamera", R.string.SpatialDebugComponent_followCamera, this, followCamera)
  var billboard by
      BooleanAttribute("billboard", R.string.SpatialDebugComponent_billboard, this, billboard)
  var distance by
      FloatAttribute("distance", R.string.SpatialDebugComponent_distance, this, distance)

  override fun typeID(): Int {
    return id
  }

  companion object {
    val id = R.string.SpatialDebugComponent_class
  }
}
