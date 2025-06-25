/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Vector4
import dorkbox.tweenEngine.TweenAccessor

class TweenSceneMaterialAccessor : TweenAccessor<TweenSceneMaterial> {
  override fun getValues(
      target: TweenSceneMaterial,
      tweenType: Int,
      returnValues: FloatArray
  ): Int {
    return when (tweenType) {
      TweenSceneMaterial.SET_ATTRIBUTE_VECTOR4 -> {
        returnValues[0] = target.currentValue.w
        returnValues[1] = target.currentValue.x
        returnValues[2] = target.currentValue.y
        returnValues[3] = target.currentValue.z
        4 // Return number of values
      }
      else -> 0
    }
  }

  override fun setValues(target: TweenSceneMaterial, tweenType: Int, newValues: FloatArray) {
    when (tweenType) {
      TweenSceneMaterial.SET_ATTRIBUTE_VECTOR4 -> {
        target.currentValue = Vector4(newValues[0], newValues[1], newValues[2], newValues[3])
        target.material.setAttribute(target.attributeName, target.currentValue)
      }
    }
  }
}
