/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
