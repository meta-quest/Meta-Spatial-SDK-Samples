/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
