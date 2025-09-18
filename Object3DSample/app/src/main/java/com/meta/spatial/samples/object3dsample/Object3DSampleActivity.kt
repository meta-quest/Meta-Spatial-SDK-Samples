/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.object3dsample

import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.ComposeViewPanelRegistration
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.physics.PhysicsWorldBounds
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.vr.VRFeature
import com.meta.spatial.vr.VrInputSystemType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Object3DSampleActivity : AppSystemActivity() {

  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var gltfxEntity: Entity? = null
  private var passthroughEnabled = false

  private var robot: Entity? = null
  private var drone: Entity? = null
  private var plant: Entity? = null
  private var deskLamp: Entity? = null
  private var easyChair: Entity? = null
  private var sculpture: Entity? = null
  private var skybox: Entity? = null

  override fun registerFeatures(): List<SpatialFeature> {
    val features =
        mutableListOf<SpatialFeature>(
            PhysicsFeature(spatial, worldBounds = PhysicsWorldBounds(minY = -100.0f)),
            // Demonstration of how to opt-out of the InteractionSDK, and use controller-only
            // simplified input.
            VRFeature(this, inputSystemType = VrInputSystemType.SIMPLE_CONTROLLER),
            ComposeFeature(),
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

    loadGLXF { composition ->
      robot = composition.getNodeByName("robot").entity
      drone = composition.getNodeByName("drone").entity
      plant = composition.getNodeByName("plant").entity
      deskLamp = composition.getNodeByName("deskLamp").entity
      easyChair = composition.getNodeByName("easyChair").entity
      sculpture = composition.getNodeByName("sculpture").entity

      // set the environment to unlit
      val environmentEntity: Entity? = composition.getNodeByName("Environment").entity
      val environmentMesh = environmentEntity?.getComponent<Mesh>()
      environmentMesh?.defaultShaderOverride = SceneMaterial.UNLIT_SHADER
      environmentEntity?.setComponent(environmentMesh!!)
    }
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

    scene.setViewOrigin(0f, 0.0f, 0.0f, 0.0f)

    skybox =
        Entity.create(
            listOf(
                Mesh("mesh://skybox".toUri(), hittable = MeshCollision.NoCollision),
                Material().apply {
                  baseTextureAndroidResourceId = R.drawable.skydome
                  unlit = true
                },
                Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
            )
        )

    // uncomment to see the physics debug lines
    // spatial.enablePhysicsDebugLines(true)
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        ComposeViewPanelRegistration(
            R.id.library_panel,
            composeViewCreator = { _, context ->
              ComposeView(context).apply {
                setContent {
                  ObjectLibraryPanel(
                      robot!!,
                      drone!!,
                      plant!!,
                      deskLamp!!,
                      easyChair!!,
                      sculpture!!,
                  )
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = PANEL_WIDTH, height = PANEL_HEIGHT),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        )
    )
  }

  private fun loadGLXF(onLoaded: ((GLXFInfo) -> Unit) = {}): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          "apk:///scenes/Composition.glxf".toUri(),
          rootEntity = gltfxEntity!!,
          onLoaded = onLoaded,
      )
    }
  }
}
