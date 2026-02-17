/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import com.meta.spatial.animation.PanelAnimationFeature
import com.meta.spatial.animation.PanelQuadCylinderAnimation
import com.meta.spatial.animation.PanelQuadCylinderAnimationType
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.ComposeViewPanelRegistration
import com.meta.spatial.core.DataModel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SpatialSDKExperimentalAPI
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.runtime.PanelShapeType
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.samples.animationssample.panel.ABOUT_PANEL_HEIGHT
import com.meta.spatial.samples.animationssample.panel.ABOUT_PANEL_WIDTH
import com.meta.spatial.samples.animationssample.panel.ANIMATION_PANEL_HEIGHT
import com.meta.spatial.samples.animationssample.panel.ANIMATION_PANEL_WIDTH
import com.meta.spatial.samples.animationssample.panel.AboutPanel
import com.meta.spatial.samples.animationssample.panel.GRAB_PANEL_HEIGHT
import com.meta.spatial.samples.animationssample.panel.GRAB_PANEL_WIDTH
import com.meta.spatial.samples.animationssample.panel.GrabPanel
import com.meta.spatial.samples.animationssample.panel.INFO_PANEL_HEIGHT
import com.meta.spatial.samples.animationssample.panel.INFO_PANEL_WIDTH
import com.meta.spatial.samples.animationssample.panel.InfoPanel
import com.meta.spatial.samples.animationssample.panel.PanelAnimationPanel
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
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
  val GLXF_DRONE_SCENE = "GLXF_DRONE_SCENE"

  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private val waitForGLXFSceneObjects = CompletableFuture<SceneObject>()
  private var droneSceneController: DroneSceneController? = null

  override fun registerFeatures(): List<SpatialFeature> {
    @OptIn(SpatialSDKExperimentalAPI::class)
    val features =
        mutableListOf<SpatialFeature>(
            VRFeature(this),
            ComposeFeature(),
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
    return listOf(
        ComposeViewPanelRegistration(
            R.id.ui_panel,
            composeViewCreator = { _, context ->
              // Create ComposeView without content
              ComposeView(context)
            },
            panelSetupWithComposeView = { composeView, panelSceneObject, entity ->
              // Set content here where you have access to panelSceneObject!
              composeView.setContent {
                PanelAnimationPanel {
                  val animationDurationMs =
                      resources.getInteger(R.integer.animation_duration_ms).toLong()
                  val startTime = entity.tryGetComponent<PanelQuadCylinderAnimation>()?.startTime
                  if (
                      startTime != null &&
                          startTime + animationDurationMs > DataModel.getLocalDataModelTime()
                  ) {
                    false
                  } else {
                    if (
                        panelSceneObject.getPanelShapeConfig()?.panelShapeType ==
                            PanelShapeType.CYLINDER
                    ) {
                      entity.setComponent(
                          PanelQuadCylinderAnimation(
                              startTime = DataModel.getLocalDataModelTime(),
                              animationType = PanelQuadCylinderAnimationType.CYLINDER_TO_QUAD,
                              durationInMs = animationDurationMs,
                          )
                      )
                    } else {
                      entity.setComponent(
                          PanelQuadCylinderAnimation(
                              startTime = DataModel.getLocalDataModelTime(),
                              animationType = PanelQuadCylinderAnimationType.QUAD_TO_CYLINDER,
                              // Random radius in [0.67, 2.67]. Angular extent = width / radius,
                              // so with panel width = 2.048 this gives ~44° to ~175°
                              targetRadius = Random.nextFloat() * 2f + 0.67f,
                              durationInMs = animationDurationMs,
                          )
                      )
                    }
                    true
                  }
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape =
                      QuadShapeOptions(
                          width = ANIMATION_PANEL_WIDTH,
                          height = ANIMATION_PANEL_HEIGHT,
                      ),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        ),
        createSimpleComposePanel(R.id.about_panel, ABOUT_PANEL_WIDTH, ABOUT_PANEL_HEIGHT) {
          AboutPanel()
        },
        createSimpleComposePanel(R.id.info_panel, INFO_PANEL_WIDTH, INFO_PANEL_HEIGHT) {
          InfoPanel()
        },
        createSimpleComposePanel(R.id.grab_panel, GRAB_PANEL_WIDTH, GRAB_PANEL_HEIGHT) {
          GrabPanel {
            if (droneSceneController != null) {
              droneSceneController!!.toggleFollowTargetMode()
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
        )
    )
  }

  private fun createSimpleComposePanel(
      panelId: Int,
      width: Float,
      height: Float,
      content: @Composable () -> Unit,
  ): ComposeViewPanelRegistration {
    return ComposeViewPanelRegistration(
        panelId,
        composeViewCreator = { _, ctx -> ComposeView(ctx).apply { setContent { content() } } },
        settingsCreator = {
          UIPanelSettings(
              shape = QuadShapeOptions(width = width, height = height),
              style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
              display = DpPerMeterDisplayOptions(),
          )
        },
    )
  }

  private fun loadGLXF(onLoaded: ((GLXFInfo) -> Unit) = {}): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          "apk:///scenes/droneScene.glxf".toUri(),
          rootEntity = gltfxEntity!!,
          keyName = GLXF_DRONE_SCENE,
          onLoaded = onLoaded,
      )
    }
  }

  fun getSceneObjectByName(name: String): CompletableFuture<SceneObject>? {
    try {
      val sos = systemManager.findSystem<SceneObjectSystem>()
      val glxf = glXFManager.getGLXFInfo(GLXF_DRONE_SCENE)
      val ent = glxf.getNodeByName(name).entity
      return sos.getSceneObject(ent)
    } catch (e: Exception) {}
    return null
  }
}
