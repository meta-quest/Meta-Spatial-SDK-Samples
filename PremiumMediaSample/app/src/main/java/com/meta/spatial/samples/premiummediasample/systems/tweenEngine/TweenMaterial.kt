/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
