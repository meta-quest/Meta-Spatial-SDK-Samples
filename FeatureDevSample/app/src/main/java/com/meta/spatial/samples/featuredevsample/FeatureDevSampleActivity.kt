/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.featuredevsample

import android.net.Uri
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.ComposeViewPanelRegistration
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SpatialSDKExperimentalAPI
import com.meta.spatial.core.SpatialSDKInternalTestingAPI
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.samples.kotlinfeature.Pulsing
import com.meta.spatial.samples.kotlinfeature.PulsingFeature
import com.meta.spatial.samples.nativefeature.NativeBobbing
import com.meta.spatial.samples.nativefeature.NativeBobbingFeature
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
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.VRFeature
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FeatureDevSampleActivity : AppSystemActivity() {
  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)

  override fun registerFeatures(): List<SpatialFeature> {
    val features =
        mutableListOf<SpatialFeature>(
            VRFeature(this),
            ComposeFeature(),
            NativeBobbingFeature(), // Native C++ bobbing feature via JNI
            PulsingFeature(), // Pure Kotlin pulsing feature (no native code)
        )
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
      features.add(DataModelInspectorFeature(spatial, this.componentManager))
      features.add(HotReloadFeature(this))
      features.add(
          OVRMetricsFeature(
              this,
              OVRMetricsDataModel() {
                numberOfMeshes()
                numberOfGrabbables()
              },
          )
      )
    }
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    NetworkedAssetLoader.init(
        File(applicationContext.getCacheDir().canonicalPath),
        OkHttpAssetFetcher(),
    )

    loadGLXF { composition ->
      // get the environment mesh from Meta Spatial Editor and set it to use an unlit shader.
      val environmentEntity: Entity? = composition.getNodeByName("collab_room").entity
      val environmentMesh = environmentEntity?.getComponent<Mesh>()
      environmentMesh?.defaultShaderOverride = SceneMaterial.UNLIT_SHADER
      environmentEntity?.setComponent(environmentMesh!!)

      // get the basketBall and robot entities from the composition
      val basketBall = composition.getNodeByName("basketBall").entity
      val robot = composition.getNodeByName("robot").entity

      // apply bobbing to the basketball using native C++ calculations
      basketBall.setComponent(NativeBobbing(amplitude = 0.3f, frequency = 1.0f))

      // apply pulsing to the robot using pure Kotlin calculations
      robot.setComponent(Pulsing(minScale = 0.5f, maxScale = 1.1f, frequency = 0.5f))
    }
  }

  @OptIn(SpatialSDKInternalTestingAPI::class)
  override fun onSceneReady() {
    super.onSceneReady()
    registerTestingIntentReceivers()

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f,
    )
    scene.updateIBLEnvironment("environment.env")

    scene.setViewOrigin(0.0f, 0.0f, 2.0f, 180.0f)

    Entity.create(
        listOf(
            Mesh(Uri.parse("mesh://skybox"), hittable = MeshCollision.NoCollision),
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.skydome
              unlit = true // Prevent scene lighting from affecting the skybox
            },
            Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
        )
    )

    Entity.createPanelEntity(
        R.id.ui_panel,
        Transform(Pose(Vector3(0f, 1.1f, -1.7f), Quaternion(0f, 180f, 0f))),
    )
  }

  @OptIn(SpatialSDKExperimentalAPI::class)
  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        ComposeViewPanelRegistration(
            R.id.ui_panel,
            composeViewCreator = { _, context ->
              ComposeView(context).apply { setContent { UIPanel() } }
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
          Uri.parse("apk:///scenes/Composition.glxf"),
          rootEntity = gltfxEntity!!,
          keyName = "example_key_name",
          onLoaded = onLoaded,
      )
    }
  }
}
