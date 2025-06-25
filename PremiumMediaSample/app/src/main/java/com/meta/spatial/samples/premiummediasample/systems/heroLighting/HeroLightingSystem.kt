/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.heroLighting

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector4
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.samples.premiummediasample.HeroLighting
import com.meta.spatial.samples.premiummediasample.ReceiveLighting
import com.meta.spatial.samples.premiummediasample.getSize
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.getAbsoluteTransform
import dorkbox.tweenEngine.TweenAccessor

class HeroLightingSystem(
    private val autoDetectTexture: Boolean = true,
    private var isProcessingShaders: Boolean = true
) : SystemBase() {

  // Data
  private val screenPositionData = Vector4(0f)
  private val screenDirectionData = Vector4(0f, 0f, 0f, 0.5f)

  private val registeredMaterials: MutableList<SceneMaterial> = mutableListOf()
  private val registeredMaterialsCustom: MutableList<SceneMaterial> = mutableListOf()
  private val unprocessedEntities: MutableList<Entity> = mutableListOf()
  private val materialsToGrabLater = mutableListOf<Entity>()

  private var stereoMode: StereoMode = StereoMode.LeftRight
  private var texture: SceneTexture? = null

  private val _videoLightingData = Vector4(0.5f, 0.5f, 0.5f, 0.5f)

  var lightingAlpha: Float
    get() = _videoLightingData.x
    set(value) {
      _videoLightingData.x = value
      forceUpdateMaterials()
    }

  var lightingDebugFarPlane: Float
    get() = _videoLightingData.y
    set(value) {
      _videoLightingData.y = value
      forceUpdateMaterials()
    }

  // Effective setter for texture property
  private fun setTexture(newTexture: SceneTexture?) {
    texture = newTexture
    if (texture != null) {
      for (mat in registeredMaterials) {
        mat.setTexture("emissive", texture!!)
      }
      for (mat in registeredMaterialsCustom) {
        mat.setTexture("emissive", texture!!)
      }
    }
  }

  override fun execute() {
    var forcePositionUpdate = false

    if (autoDetectTexture) {
      // if we find a new texture, we update the position regardless if the transform has moved
      forcePositionUpdate = checkNewHeroLightingTexture()
    }
    checkNewReceiveLightingMaterials()
    checkCurrentHeroLightingUpdates(forcePositionUpdate)
  }

  private fun checkNewHeroLightingTexture(): Boolean {
    var newTextureFound = false
    // Find and grab texture if 'HeroLighting' is added to it
    val lightingEntities = Query.where { changed(HeroLighting.id) }.eval()
    for (lightingEntity in lightingEntities) {
      val heroLighting = lightingEntity.getComponent<HeroLighting>()
      if (heroLighting.isEnabled) {
        systemManager.findSystem<SceneObjectSystem>().getSceneObject(lightingEntity)?.thenAccept {
            so ->
          setTexture(so.mesh?.materials?.get(0)?.texture)
          newTextureFound = true
        }
      }
    }
    return newTextureFound
  }

  private fun checkNewReceiveLightingMaterials() {
    val receiveLightingMaterials = Query.where { changed(ReceiveLighting.id) }.eval()

    for (addedEntity in materialsToGrabLater) {
      // Find the material and register/unregister
      registerOrUnregisterMaterials(addedEntity)
    }

    // Clear list
    materialsToGrabLater.clear()

    for (receiveLightingEntity in receiveLightingMaterials) {
      if (!isProcessingShaders) {
        unprocessedEntities.add(receiveLightingEntity)
      } else setShaderAndProcessLater(receiveLightingEntity)
    }
  }

  private fun registerOrUnregisterMaterials(entity: Entity) {
    systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)?.thenAccept { so ->
      val materials = so?.mesh?.materials
      if (materials != null) {
        val receiveLighting = entity.getComponent<ReceiveLighting>()
        if (receiveLighting.isEnabled) {
          if (receiveLighting.hasProcessed) {
            // Unregister materials
            receiveLighting.hasProcessed = false
            entity.setComponent(receiveLighting)
            materials.forEach { unregisterMaterial(it) }
          } else {
            // Register materials
            receiveLighting.hasProcessed = true
            entity.setComponent(receiveLighting)
            materials.forEach { registerMaterial(it) }
          }
        }
      }
    }
  }

  // Adding a material that has pre-defined attributes that we want/need to work
  fun registerMaterial(material: SceneMaterial, custom: Boolean = false) {
    if (texture != null) {
      material.setTexture("emissive", texture!!)
    }
    material.setStereoMode(stereoMode)
    updateMaterial(material, custom)
    if (custom) registeredMaterialsCustom.add(material) else registeredMaterials.add(material)
  }

  // Adding a material that has pre-defined attributes that we want/need to work
  private fun unregisterMaterial(material: SceneMaterial, custom: Boolean = false) {
    if (custom) registeredMaterialsCustom.remove(material) else registeredMaterials.remove(material)
  }

  private fun checkCurrentHeroLightingUpdates(forceUpdate: Boolean = false) {
    // Loop through and find moved HeroLighting and grab new position/angle/scale values
    val query =
        Query.where {
          if (forceUpdate) {
            has(HeroLighting.id, Transform.id, Scale.id)
          } else {
            has(HeroLighting.id, Transform.id, Scale.id) and
                (changed(Transform.id) or changed(Scale.id))
          }
        }

    var updatedPositionOrScale = false

    for (entity in query.eval()) {
      if (!entity.getComponent<HeroLighting>().isEnabled) continue

      val transform = getAbsoluteTransform(entity)
      val screenDirection = transform.q.toEuler()
      val scale = getSize(entity)

      screenPositionData.x = transform.t.x
      screenPositionData.y = transform.t.y
      screenPositionData.z = transform.t.z
      screenPositionData.w = scale.x

      screenDirectionData.x = screenDirection.x
      screenDirectionData.y = screenDirection.y
      screenDirectionData.z = screenDirection.z
      screenDirectionData.w = scale.y

      updatedPositionOrScale = true
    }

    if (updatedPositionOrScale) forceUpdateMaterials()
  }

  private fun forceUpdateMaterials() {
    for (material in registeredMaterials) {
      updateMaterial(material, false)
    }
    for (material in registeredMaterialsCustom) {
      updateMaterial(material, true)
    }
  }

  private fun updateMaterial(material: SceneMaterial, custom: Boolean) {
    material.setAttribute("emissiveFactor", screenPositionData)
    material.setAttribute("albedoFactor", screenDirectionData)
    if (custom) {
      material.setAttribute("matParams", _videoLightingData)
    } else {
      material.setRoughnessMetallicness(_videoLightingData.x, _videoLightingData.y)
    }
  }

  private fun updateShader(entity: Entity) {
    val receiveLighting = entity.getComponent<ReceiveLighting>()
    if (receiveLighting.customShader != "") {
      val mesh = entity.getComponent<Mesh>()
      mesh.defaultShaderOverride = receiveLighting.customShader
      entity.setComponent(mesh)
    }
  }

  private fun setShaderAndProcessLater(entity: Entity) {
    materialsToGrabLater.add(entity) // to process next frame when mesh had loaded default shader
    updateShader(entity)
  }

  fun hasRegisteredMaterial(material: SceneMaterial): Boolean {
    if (registeredMaterials.contains(material)) return true

    if (registeredMaterialsCustom.contains(material)) return true

    return false
  }

  companion object {
    val TAG = "HeroLightingSystem"
  }
}

data class TweenFarPlane(val heroLightingSystem: HeroLightingSystem) {
  companion object {
    const val FAR_PLANE_PERCENT_LERP = 1
  }
}

class TweenFarPlaneAccessor : TweenAccessor<TweenFarPlane> {
  override fun getValues(target: TweenFarPlane, tweenType: Int, returnValues: FloatArray): Int {
    return when (tweenType) {
      TweenFarPlane.FAR_PLANE_PERCENT_LERP -> {
        returnValues[0] = target.heroLightingSystem.lightingDebugFarPlane
        1 // Return number of values
      }
      else -> 0
    }
  }

  override fun setValues(target: TweenFarPlane, tweenType: Int, newValues: FloatArray) {
    when (tweenType) {
      TweenFarPlane.FAR_PLANE_PERCENT_LERP -> {
        target.heroLightingSystem.lightingDebugFarPlane = newValues[0]
      }
    }
  }
}
