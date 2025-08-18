/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.keyboard_tracker

import android.net.Uri
import android.util.Log
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector4
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKVolume
import com.meta.spatial.mruk.TrackedKeyboard
import com.meta.spatial.runtime.BlendMode
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMaterialAttribute
import com.meta.spatial.runtime.SceneMaterialDataType
import com.meta.spatial.runtime.SceneMesh
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCreationSystem
import com.meta.spatial.toolkit.Transform

class SpawnKeyboardSystem(private val mrukFeature: MRUKFeature) : SystemBase() {

  private var isInit = false
  private val trackableMeshId: String = "mesh://trackable"
  private lateinit var ptCutoutMaterial: SceneMaterial

  override fun execute() {
    if (!isInit) {
      init()
      isInit = true
    }

    // 1. Query all entities that have TrackedKeyboard
    Query.where { has(TrackedKeyboard.id) }
        .eval()
        .forEach { entity ->
          val mesh = entity.tryGetComponent<Mesh>()
          // 2. Check if they have already the Mesh component
          if (mesh == null) {
            // 3. If not, spawn it
            entity.setComponent(Mesh(Uri.parse(trackableMeshId)))
          }
        }
  }

  private fun init() {
    // Create a material to cutout the keyboard and show passthrough to the user
    ptCutoutMaterial =
        SceneMaterial.custom(
                "pt_cutout",
                arrayOf<SceneMaterialAttribute>(
                    SceneMaterialAttribute("roughnessMetallicUnlit", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("minAlphaAlphaCutoff", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("stereoParams", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("emissiveFactor", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("albedoFactor", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("albedoUVTransformM00", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("albedoUVTransformM10", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "roughnessMetallicUVTransformM00",
                        SceneMaterialDataType.Vector4,
                    ),
                    SceneMaterialAttribute(
                        "roughnessMetallicUVTransformM10",
                        SceneMaterialDataType.Vector4,
                    ),
                    SceneMaterialAttribute(
                        "emissiveMetallicUVTransformM00",
                        SceneMaterialDataType.Vector4,
                    ),
                    SceneMaterialAttribute(
                        "emissiveMetallicUVTransformM10",
                        SceneMaterialDataType.Vector4,
                    ),
                    SceneMaterialAttribute(
                        "occlusionMetallicUVTransformM00",
                        SceneMaterialDataType.Vector4,
                    ),
                    SceneMaterialAttribute(
                        "occlusionMetallicUVTransformM10",
                        SceneMaterialDataType.Vector4,
                    ),
                    SceneMaterialAttribute(
                        "normalMetallicUVTransformM00",
                        SceneMaterialDataType.Vector4,
                    ),
                    SceneMaterialAttribute(
                        "normalMetallicUVTransformM10",
                        SceneMaterialDataType.Vector4,
                    ),
                ),
            )
            .apply {
              setAttribute("albedoUVTransformM00", Vector4(1f, 0f, 0f, 0f))
              setAttribute("albedoUVTransformM10", Vector4(0f, 1f, 0f, 0f))
              setAttribute("stereoParams", Vector4(0f, 0f, 1f, 1f))
            }
    ptCutoutMaterial.setBlendMode(BlendMode.ADDITIVE)

    // Create a mesh creator for creating a mesh for each tracked keyboard
    val meshManager = systemManager.findSystem<MeshCreationSystem>().meshManager
    meshManager.meshCreators.put(trackableMeshId) { entity ->
      Log.i("MRUK SpawnKeyboardSystem", "Create mesh in mesh creation system")
      val transform = entity.getComponent<Transform>().transform
      val volume = entity.getComponent<MRUKVolume>()
      val keyboardScale = 1.2f
      val volumeMin = volume.min * keyboardScale
      val volumeMax = volume.max * keyboardScale
      SceneMesh.box(
          volumeMin.x,
          volumeMin.y,
          volumeMin.z,
          volumeMax.x,
          volumeMax.y,
          volumeMax.z,
          ptCutoutMaterial,
      )
    }
  }
}
