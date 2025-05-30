// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import dorkbox.tweenEngine.TweenAccessor

class TweenFloatAccessor : TweenAccessor<TweenFloat> {
  companion object {
    const val TYPE_VALUE = 1
  }

  override fun getValues(target: TweenFloat, tweenType: Int, returnValues: FloatArray): Int {
    if (tweenType == TYPE_VALUE) {
      returnValues[0] = target.value
      return 1
    }
    return 0
  }

  override fun setValues(target: TweenFloat, tweenType: Int, newValues: FloatArray) {
    if (tweenType == TYPE_VALUE) {
      target.value = newValues[0]
    }
  }
}
