/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.heroLighting

import android.graphics.Color
import android.graphics.ColorSpace
import android.net.Uri
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector4
import com.meta.spatial.mruk.MRUKAnchor
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.mruk.MRUKPlane
import com.meta.spatial.mruk.getSize
import com.meta.spatial.mruk.hasLabel
import com.meta.spatial.runtime.BlendMode
import com.meta.spatial.runtime.DepthWrite
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMaterialAttribute
import com.meta.spatial.runtime.SceneMaterialDataType
import com.meta.spatial.runtime.SceneMesh
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.samples.premiummediasample.quadTriangleMesh
import com.meta.spatial.samples.premiummediasample.systems.tweenEngine.value
import com.meta.spatial.toolkit.Hittable
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import java.util.UUID

class WallLightingSystem(
    private var materialsMap: Map<WallLightingFace, SceneMaterial>? = null,
    private var _isVisible: Boolean = true,
) : SystemBase() {
  private var planes = mutableListOf<Entity>()
  private var planeScales = mutableListOf<Vector3>()
  private var roomUuid: UUID? = null

  private val meshNames: Array<MeshNameLabel>
  private val validLabels =
      arrayOf(
          MRUKLabel.WALL_FACE,
          MRUKLabel.FLOOR,
          MRUKLabel.CEILING,
          MRUKLabel.WALL_ART,
          MRUKLabel.SCREEN,
          MRUKLabel.DOOR_FRAME,
          MRUKLabel.WINDOW_FRAME,
      )

  val isVisible: Boolean
    get() = _isVisible

  init {
    val activity = SpatialActivityManager.getAppSystemActivity()
    if (materialsMap == null || materialsMap!!.isEmpty()) materialsMap = getDefaultMaterialsMap()

    val meshNamesList = mutableListOf<MeshNameLabel>()
    val heroLightingSystem = activity.getSystemManager().findSystem<HeroLightingSystem>()
    val labels = mutableListOf<MRUKLabel>()
    for (materialEntry in materialsMap!!) {
      if (!validLabels.contains(materialEntry.key.label)) {
        throw IllegalArgumentException(
            "Cannot use a material map with label type: ${materialEntry.key.label}"
        )
      }

      // Add labels for quick search later
      if (!labels.contains(materialEntry.key.label)) labels.add(materialEntry.key.label)

      // Register Name+Direction, for finding later
      val meshName = MESH_PREFIX + materialEntry.key.toString()
      var meshNameLabel = meshNamesList.find { it.label == materialEntry.key.label }
      if (meshNameLabel == null) {
        meshNameLabel = MeshNameLabel(materialEntry.key.label, null, mutableMapOf())
        meshNamesList.add(meshNameLabel)
      }
      if (materialEntry.key.direction == null) meshNameLabel.defaultMesh = meshName
      else meshNameLabel.directions[materialEntry.key.direction!!] = meshName

      // Allow creation of custom mesh with material
      activity.registerMeshCreator(meshName) {
        SceneMesh.fromTriangleMesh(
            quadTriangleMesh(1f, 1f, materialEntry.value, 4, 4),
            false,
        ) // Crazy subdivided mesh
      }

      // Register material with lighting
      if (!heroLightingSystem.hasRegisteredMaterial(materialEntry.value))
          heroLightingSystem.registerMaterial(materialEntry.value, true)
    }

    // Save names array map for search
    meshNames = meshNamesList.toTypedArray()
  }

  override fun execute() {
    val anchorQuery = Query.where { changed(MRUKPlane.id) }
    for (mrukPlane in anchorQuery.eval()) {
      val anchor = mrukPlane.getComponent<MRUKAnchor>()

      if (roomUuid == null)
          roomUuid =
              SpatialActivityManager.getAppSystemActivity()
                  .tryFindFeature<MRUKFeature>()
                  ?.getCurrentRoom()
                  ?.anchor
                  ?.uuid

      if (anchor.roomUuid != roomUuid) continue

      val transform = mrukPlane.getComponent<Transform>()
      val normal = transform.transform.forward()
      var meshName: String? = null
      meshName = getMeshName(anchor, normal)

      if (meshName == null) continue

      val plane = mrukPlane.getComponent<MRUKPlane>()
      val size = plane.getSize()
      val scale = Vector3(size.x, size.y, 1f)
      planeScales.add(scale)
      planes.add(
          Entity.create(
              Mesh(Uri.parse(meshName)),
              Transform(transform.transform),
              Hittable(MeshCollision.NoCollision),
              Scale(if (_isVisible) scale else Vector3(0f)),
              Visible(_isVisible),
          )
      )
    }
  }

  private fun getMeshName(anchor: MRUKAnchor, normal: Vector3): String? {
    for (meshNameLabel in meshNames) {
      if (!anchor.hasLabel(meshNameLabel.label)) continue

      if (meshNameLabel.directions.isNotEmpty()) {
        for (directionName in meshNameLabel.directions) {
          if (normal.dot(directionName.key) >= 0.67f) // 45 degree
          {
            return directionName.value
          }
        }
      }
      return meshNameLabel.defaultMesh
    }
    return null
  }

  fun transitionInstant(visible: Boolean) {
    _isVisible = visible
    planes.forEachIndexed { index, entity ->
      val targetScale =
          if (visible) planeScales[index]
          else Vector3(0f, planeScales[index].y, planeScales[index].z)
      entity.setComponent(Scale(targetScale))
      entity.setComponent(Visible(visible))
    }
  }

  data class MeshNameLabel(
      var label: MRUKLabel,
      var defaultMesh: String?,
      var directions: MutableMap<Vector3, String>,
  )

  companion object {
    const val TAG = "WallLightingSystem"
    const val MESH_PREFIX = "mesh://WallLightingSystem_"

    fun getDefaultMaterialsMap(): Map<WallLightingFace, SceneMaterial> {
      val p3: ColorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
      val color = Color.valueOf(1.0f, 1.0f, 1.0f, 1.0f, p3) // Opaque red in Display P3 color space
      val wallMaterial = getDefaultCustomShader("mruk_hero_lighting", SceneTexture(color))

      return mapOf(
          Pair(WallLightingFace(MRUKLabel.WALL_FACE, null), wallMaterial),
          Pair(WallLightingFace(MRUKLabel.CEILING, null), wallMaterial),
          Pair(WallLightingFace(MRUKLabel.FLOOR, null), wallMaterial),
      )
    }

    private fun getDefaultCustomShader(
        shaderName: String,
        albedoTexture: SceneTexture = SceneTexture(Color()),
        depthWrite: DepthWrite = DepthWrite.DISABLE,
    ): SceneMaterial {
      return SceneMaterial.custom(
              shaderName,
              arrayOf(
                  SceneMaterialAttribute("albedoSampler", SceneMaterialDataType.Texture2D),
                  SceneMaterialAttribute(
                      "roughnessMetallicTexture",
                      SceneMaterialDataType.Texture2D,
                  ),
                  SceneMaterialAttribute("emissive", SceneMaterialDataType.Texture2D),
                  SceneMaterialAttribute("occlusion", SceneMaterialDataType.Texture2D),
                  SceneMaterialAttribute("emissiveFactor", SceneMaterialDataType.Vector4),
                  SceneMaterialAttribute("albedoFactor", SceneMaterialDataType.Vector4),
                  SceneMaterialAttribute("matParams", SceneMaterialDataType.Vector4),
                  SceneMaterialAttribute("stereoParams", SceneMaterialDataType.Vector4),
              ),
          )
          .apply {
            // Render behind everything
            setDepthWrite(depthWrite)
            setAttribute("emissiveFactor", Vector4(1.0f, 1.0f, 0f, 0f))
            setAttribute("albedoFactor", Vector4(0.0f, 0.0f, 1f, 0f))
            setAttribute("matParams", Vector4(1.0f, 0.0f, 0f, 0f))
            setAttribute("stereoParams", Vector4(1.0f, 0.0f, 0f, 0f))
            setTexture("albedoSampler", albedoTexture)
            setTexture("roughnessMetallicTexture", SceneTexture(Color()))
            setTexture("emissive", SceneTexture(Color()))
            setTexture("occlusion", SceneTexture(Color()))
            setBlendMode(BlendMode.OPAQUE)
            setStereoMode(StereoMode.None)
          }
    }
  }
}
