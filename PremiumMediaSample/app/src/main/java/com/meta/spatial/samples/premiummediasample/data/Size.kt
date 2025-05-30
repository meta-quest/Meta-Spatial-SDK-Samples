// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.data

import com.meta.spatial.core.Vector2
import java.io.Serializable

data class Size(val x: Int, val y: Int) : Serializable {
  fun toVector2(): Vector2 {
    return Vector2(x.toFloat(), y.toFloat())
  }
}
