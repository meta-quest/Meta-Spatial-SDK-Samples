// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Vector4
import dorkbox.tweenEngine.TweenAccessor

class Vector4Accessor : TweenAccessor<Vector4> {
  companion object {
    const val WXYZ = 1 // We use 1 to indicate we're tweening x, y, and z
  }

  override fun getValues(target: Vector4, tweenType: Int, returnValues: FloatArray): Int {
    return when (tweenType) {
      WXYZ -> {
        returnValues[0] = target.w
        returnValues[1] = target.x
        returnValues[2] = target.y
        returnValues[3] = target.z
        4 // Return number of components (x, y, z)
      }
      else -> 0
    }
  }

  override fun setValues(target: Vector4, tweenType: Int, newValues: FloatArray) {
    when (tweenType) {
      WXYZ -> {
        target.w = newValues[0]
        target.x = newValues[1]
        target.y = newValues[2]
        target.z = newValues[3]
      }
    }
  }
}
