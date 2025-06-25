/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Scale
import dorkbox.tweenEngine.TweenAccessor

class TweenScaleAccessor : TweenAccessor<TweenScale> {
  override fun getValues(target: TweenScale, tweenType: Int, returnValues: FloatArray): Int {
    val component = target.entity.getComponent<Scale>()
    return when (tweenType) {
      TweenScale.SCALE_VALUE -> {
        returnValues[0] = component.scale.x
        1 // Return number of values
      }
      TweenScale.SCALE_XYZ -> {
        returnValues[0] = component.scale.x
        returnValues[1] = component.scale.y
        returnValues[2] = component.scale.z
        3 // Return number of values
      }
      else -> 0
    }
  }

  override fun setValues(target: TweenScale, tweenType: Int, newValues: FloatArray) {
    val component = target.entity.getComponent<Scale>()
    when (tweenType) {
      TweenScale.SCALE_VALUE -> {
        component.scale = Vector3(newValues[0])
      }
      TweenScale.SCALE_XYZ -> {
        component.scale.x = newValues[0]
        component.scale.y = newValues[1]
        component.scale.z = newValues[2]
      }
    }
    target.entity.setComponent(component)
  }
}
