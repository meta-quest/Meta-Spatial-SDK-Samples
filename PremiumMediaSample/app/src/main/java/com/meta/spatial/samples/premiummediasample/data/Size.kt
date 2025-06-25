/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data

import com.meta.spatial.core.Vector2
import java.io.Serializable

data class Size(val x: Int, val y: Int) : Serializable {
  fun toVector2(): Vector2 {
    return Vector2(x.toFloat(), y.toFloat())
  }
}
