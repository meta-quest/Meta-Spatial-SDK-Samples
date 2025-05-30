// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.heroLighting

import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKLabel

data class WallLightingFace(
    val label: MRUKLabel,
    val direction: Vector3? = null,
) {
  override fun toString(): String {
    if (direction == null) return label.name
    return label.name + "_" + direction.toString()
  }
}
