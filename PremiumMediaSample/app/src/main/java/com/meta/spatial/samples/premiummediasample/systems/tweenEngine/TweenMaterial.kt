// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Vector4
import com.meta.spatial.toolkit.Material

data class TweenMaterial(
    val material: Material,
    val attributeName: String = "",
    var currentValue: Vector4
) {
  companion object {
    const val BASE_COLOR_COLOR4_RGBA = 1
  }
}
