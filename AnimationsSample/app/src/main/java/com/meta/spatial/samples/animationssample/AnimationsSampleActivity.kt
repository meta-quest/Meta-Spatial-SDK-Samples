/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import com.meta.spatial.animation.PanelAnimation
import com.meta.spatial.animation.PanelAnimationFeature
import com.meta.spatial.animation.PanelQuadCylinderAnimator
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.PanelShapeType
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.vr.VRFeature
import java.util.concurrent.CompletableFuture
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AnimationsSampleActivity : AppSystemActivity() {
  public val GLXF_DRONE_SCENE = "GLXF_DRONE_SCENE"

  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private val waitForGLXFSceneObjects = CompletableFuture<SceneObject>()
  private var droneSceneController: DroneSceneController? = null

  override fun registerFeatures(): List<SpatialFeature> {
    val features = mutableListOf<SpatialFeature>(VRFeature(this), PanelAnimationFeature())
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
    }
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    componentManager.registerComponent<FollowerComponent>(FollowerComponent.Companion)
    componentManager.registerComponent<FollowerTarget>(FollowerTarget.Companion)
    componentManager.registerComponent<DroneComponent>(DroneComponent.Companion)
    systemManager.registerSystem(FollowerSystem())
    systemManager.registerSystem(DroneSystem(droneSceneController))
    systemManager.registerSystem(ManageGLXFSceneObjectsSystem(this, waitForGLXFSceneObjects))

    loadGLXF().invokeOnCompletion {
      waitForGLXFSceneObjects.thenAccept { _ -> droneSceneController = DroneSceneController() }

      // get the environment mesh from Meta Spatial Editor and set it to use an unlit shader.
      val composition = glXFManager.getGLXFInfo(GLXF_DRONE_SCENE)
      val environmentEntity: Entity? = composition.getNodeByName("environment").entity
      val environmentMesh = environmentEntity?.getComponent<Mesh>()
      environmentMesh?.defaultShaderOverride = SceneMaterial.UNLIT_SHADER
      environmentEntity?.setComponent(environmentMesh!!)
    }
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.ui_panel) { ent ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layoutWidthInDp = 720f
            layerConfig = LayerConfig()

            // Defaults for panel config
            panelShapeType = PanelShapeType.CYLINDER
            radiusForCylinderOrSphere = 1.0f
            enableTransparent = true
          }
          panel {
            val button = rootView?.findViewById<Button>(R.id.transform_button)!!
            button.setOnClickListener {
              val shapeConfig = getPanelShapeConfig()
              if (shapeConfig.panelShapeType == PanelShapeType.CYLINDER) {
                button.text = "Change to Cylinder Shape"
                ent.setComponent(PanelAnimation(PanelQuadCylinderAnimator()))
              } else {
                button.text = "Change to Quad Shape"
                ent.setComponent(
                    PanelAnimation(
                        PanelQuadCylinderAnimator(targetRadius = Random.nextFloat() * 2f + 0.5f)))
              }
            }
          }
        },
        PanelRegistration(R.layout.ui_about) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
          }
        },
        PanelRegistration(R.layout.ui_info) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
          }
        },
        PanelRegistration(R.layout.ui_grab) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
          }
        })
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // set the reference space to enable recentering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f)
    scene.updateIBLEnvironment("environment.env")

    scene.setViewOrigin(0.0f, 0.0f, 0.0f, 0.0f)

    Entity.create(
        listOf(
            Mesh(Uri.parse("mesh://skybox")),
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.skydome
              unlit = true
            },
            Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f)))))
  }

  private fun loadGLXF(): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("apk:///scenes/droneScene.glxf"),
          rootEntity = gltfxEntity!!,
          keyName = GLXF_DRONE_SCENE)
    }
  }

  public fun getSceneObjectByName(name: String): CompletableFuture<SceneObject>? {
    try {
      val sos = systemManager.findSystem<SceneObjectSystem>()
      val glxf = glXFManager.getGLXFInfo(GLXF_DRONE_SCENE)
      val ent = glxf.getNodeByName(name).entity
      return sos.getSceneObject(ent)
    } catch (e: Exception) {}
    return null
  }
}
