// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.samples.premiummediasample.PanelLayerAlpha
import dorkbox.tweenEngine.TweenAccessor

class TweenPanelLayerAlphaAccessor : TweenAccessor<TweenPanelLayerAlpha> {
  override fun getValues(
      target: TweenPanelLayerAlpha,
      tweenType: Int,
      returnValues: FloatArray
  ): Int {
    val component = target.entity.getComponent<PanelLayerAlpha>()
    return when (tweenType) {
      TweenPanelLayerAlpha.ALPHA_VALUE -> {
        returnValues[0] = component.layerAlpha
        1 // Return number of values
      }
      else -> 0
    }
  }

  override fun setValues(target: TweenPanelLayerAlpha, tweenType: Int, newValues: FloatArray) {
    when (tweenType) {
      TweenPanelLayerAlpha.ALPHA_VALUE -> {
        target.entity.setComponent(PanelLayerAlpha(newValues[0]))
      }
    }
  }
}
