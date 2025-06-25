/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Vector3
import dorkbox.tweenEngine.TweenAccessor

class Vector3Accessor : TweenAccessor<Vector3> {
  companion object {
    const val XYZ = 1 // We use 1 to indicate we're tweening x, y, and z
  }

  override fun getValues(target: Vector3, tweenType: Int, returnValues: FloatArray): Int {
    return when (tweenType) {
      XYZ -> {
        returnValues[0] = target.x
        returnValues[1] = target.y
        returnValues[2] = target.z
        3 // Return number of components (x, y, z)
      }
      else -> 0
    }
  }

  override fun setValues(target: Vector3, tweenType: Int, newValues: FloatArray) {
    when (tweenType) {
      XYZ -> {
        target.x = newValues[0]
        target.y = newValues[1]
        target.z = newValues[2]
      }
    }
  }
}
