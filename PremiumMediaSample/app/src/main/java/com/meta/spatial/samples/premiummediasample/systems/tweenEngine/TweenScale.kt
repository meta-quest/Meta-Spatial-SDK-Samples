/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Entity

data class TweenScale(val entity: Entity) {
  companion object {
    const val SCALE_VALUE = 1
    const val SCALE_XYZ = 2
  }
}
