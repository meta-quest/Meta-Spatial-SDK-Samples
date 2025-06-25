/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Vector3
import com.meta.spatial.samples.premiummediasample.getSize
import com.meta.spatial.samples.premiummediasample.setAbsolutePosition
import com.meta.spatial.samples.premiummediasample.setSize
import com.meta.spatial.toolkit.getAbsoluteTransform

data class PoseAndSize(val pose: Pose, val size: Vector3) {
  companion object {
    fun fromEntity(entity: Entity): PoseAndSize {
      return PoseAndSize(getAbsoluteTransform(entity), getSize(entity))
    }

    fun applyToEntity(entity: Entity, forcedSize: PoseAndSize, keepWidthAspect: Boolean = false) {
      setAbsolutePosition(entity, forcedSize.pose)
      val entitySize = getSize(entity)
      if (keepWidthAspect) {
        val aspect = entitySize.x / entitySize.y
        forcedSize.size.y = forcedSize.size.x / aspect
      }
      setSize(entity, forcedSize.size)
    }

    fun copy(copyFrom: Entity, applyTo: Entity, keepWidthAspect: Boolean = false) {
      applyToEntity(applyTo, fromEntity(copyFrom), keepWidthAspect)
    }
  }
}
