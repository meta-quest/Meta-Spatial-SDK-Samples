// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Color4
import dorkbox.tweenEngine.TweenAccessor

class TweenMaterialAccessor : TweenAccessor<TweenMaterial> {
  override fun getValues(target: TweenMaterial, tweenType: Int, returnValues: FloatArray): Int {
    return when (tweenType) {
      TweenMaterial.BASE_COLOR_COLOR4_RGBA -> {
        returnValues[0] = target.material.baseColor.red
        returnValues[1] = target.material.baseColor.green
        returnValues[2] = target.material.baseColor.blue
        returnValues[3] = target.material.baseColor.alpha
        4 // Return number of values
      }
      else -> 0
    }
  }

  override fun setValues(target: TweenMaterial, tweenType: Int, newValues: FloatArray) {
    when (tweenType) {
      TweenMaterial.BASE_COLOR_COLOR4_RGBA -> {
        target.material.baseColor =
            Color4(
                red = newValues[0], green = newValues[1], blue = newValues[2], alpha = newValues[3])
      }
    }
  }
}
