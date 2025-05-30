// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Entity

data class TweenScale(val entity: Entity) {
  companion object {
    const val SCALE_VALUE = 1
    const val SCALE_XYZ = 2
  }
}
