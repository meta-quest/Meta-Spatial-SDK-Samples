// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.entities

import android.net.Uri
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Box
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh

class ImageBoxEntity {
  companion object {
    fun create(resourceId: Int, width: Float, height: Float, vararg args: ComponentBase): Entity {
      val hWidth = width * 0.5f
      val hHeight = height * 0.5f

      val entity =
          Entity.create(
              Mesh(Uri.parse("mesh://box")),
              Box(Vector3(-hWidth, -hHeight, 0f), Vector3(hWidth, hHeight, 0f)),
              Material().apply {
                baseTextureAndroidResourceId = resourceId
                alphaMode = 1
                unlit = true
              },
          )
      entity.setComponents(*args)
      return entity
    }
  }
}
