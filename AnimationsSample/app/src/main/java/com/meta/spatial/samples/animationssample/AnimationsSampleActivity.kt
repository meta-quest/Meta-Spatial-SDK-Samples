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
import com.meta.spatial.animation.PanelAnimationFeature
import com.meta.spatial.animation.PanelQuadCylinderAnimation
import com.meta.spatial.animation.PanelQuadCylinderAnimationType
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.DataModel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SpatialSDKExperimentalAPI
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.runtime.PanelShapeType
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.runtime.panel.PanelConfigOptions2
import com.meta.spatial.runtime.panel.cylinder
import com.meta.spatial.runtime.panel.layer
import com.meta.spatial.runtime.panel.onPanelCreation
import com.meta.spatial.runtime.panel.resolution
import com.meta.spatial.runtime.panel.shapeType
import com.meta.spatial.runtime.panel.style
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
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

/**
 * This sample uses experimental Panel Animation APIs that are subject to change. Please use at your
 * own risk.
 */
@OptIn(SpatialSDKExperimentalAPI::class)
class AnimationsSampleActivity : AppSystemActivity() {
  public val GLXF_DRONE_SCENE = "GLXF_DRONE_SCENE"

  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private val waitForGLXFSceneObjects = CompletableFuture<SceneObject>()
  private var droneSceneController: DroneSceneController? = null

  override fun registerFeatures(): List<SpatialFeature> {
    @OptIn(SpatialSDKExperimentalAPI::class)
    val features =
        mutableListOf<SpatialFeature>(
            VRFeature(this),
            IsdkFeature(this, spatial, systemManager),
            PanelAnimationFeature(),
        )
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
      features.add(HotReloadFeature(this))
      features.add(OVRMetricsFeature(this, OVRMetricsDataModel() { numberOfMeshes() }))
      features.add(DataModelInspectorFeature(spatial, this.componentManager))
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

    loadGLXF { composition ->
      waitForGLXFSceneObjects.thenAccept { _ -> droneSceneController = DroneSceneController() }

      // get the environment mesh from Meta Spatial Editor and set it to use an unlit shader.
      val environmentEntity: Entity? = composition.getNodeByName("environment").entity
      val environmentMesh = environmentEntity?.getComponent<Mesh>()
      environmentMesh?.defaultShaderOverride = SceneMaterial.UNLIT_SHADER
      environmentEntity?.setComponent(environmentMesh!!)
    }
  }

  override fun registerPanels(): List<PanelRegistration> {
    val defaultOptions =
        PanelConfigOptions2.style(themeResourceId = R.style.PanelAppThemeTransparent).layer()
    return listOf(
        PanelRegistration(R.layout.ui_panel).fromConfigOptions2 { ent ->
          defaultOptions.cylinder(1.0f).resolution(widthInDp = 720f).onPanelCreation { ent ->
            val transformButton = rootView?.findViewById<Button>(R.id.transform_button)!!
            transformButton.setOnClickListener {
              if (shapeType == PanelShapeType.CYLINDER) {
                transformButton.text = "Change to Cylinder Shape"
                ent.setComponent(
                    PanelQuadCylinderAnimation(
                        startTime = DataModel.getLocalDataModelTime(),
                        animationType = PanelQuadCylinderAnimationType.CYLINDER_TO_QUAD,
                    ))
              } else {
                transformButton.text = "Change to Quad Shape"
                ent.setComponent(
                    PanelQuadCylinderAnimation(
                        startTime = DataModel.getLocalDataModelTime(),
                        animationType = PanelQuadCylinderAnimationType.QUAD_TO_CYLINDER,
                        targetRadius = Random.nextFloat() * 2f + 0.5f,
                    ))
              }
            }
          }
        },
        PanelRegistration(R.layout.ui_about).fromConfigOptions2 { defaultOptions },
        PanelRegistration(R.layout.ui_info).fromConfigOptions2 { defaultOptions },
        PanelRegistration(R.layout.ui_grab).fromConfigOptions2 { ent ->
          defaultOptions.onPanelCreation { ent ->
            val followModeButton = rootView?.findViewById<Button>(R.id.follow_mode_button)!!
            followModeButton.setOnClickListener {
              if (droneSceneController != null) {
                droneSceneController!!.toggleFollowTargetMode()
                if (droneSceneController!!.getFollowTargetIsBuiltInFollower()) {
                  followModeButton.text = "Change to Custom"
                } else {
                  followModeButton.text = "Change to Builtin"
                }
              }
            }
          }
        },
    )
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // set the reference space to enable recentering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f,
    )
    scene.updateIBLEnvironment("environment.env")

    scene.setViewOrigin(0.0f, 0.0f, 0.0f, 0.0f)

    Entity.create(
        listOf(
            Mesh(Uri.parse("mesh://skybox"), hittable = MeshCollision.NoCollision),
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.skydome
              unlit = true
            },
            Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
        ))
  }

  private fun loadGLXF(onLoaded: ((GLXFInfo) -> Unit) = {}): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("apk:///scenes/droneScene.glxf"),
          rootEntity = gltfxEntity!!,
          keyName = GLXF_DRONE_SCENE,
          onLoaded = onLoaded,
      )
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
