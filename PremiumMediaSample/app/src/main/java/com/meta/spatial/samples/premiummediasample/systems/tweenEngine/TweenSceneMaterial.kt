// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Vector4
import com.meta.spatial.runtime.SceneMaterial

data class TweenSceneMaterial(
    val material: SceneMaterial,
    val attributeName: String = "",
    var currentValue: Vector4
) {
  companion object {
    const val SET_ATTRIBUTE_VECTOR4 = 1
  }
}
