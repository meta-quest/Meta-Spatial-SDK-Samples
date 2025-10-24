/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mixedrealitysample

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.ComposeViewPanelRegistration
import com.meta.spatial.core.Entity
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.mruk.AnchorProceduralMesh
import com.meta.spatial.mruk.AnchorProceduralMeshConfig
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.mruk.MRUKLoadDeviceResult
import com.meta.spatial.mruk.MRUKRoom
import com.meta.spatial.mruk.MRUKSceneEventListener
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.physics.PhysicsWorldBounds
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MixedRealitySampleActivity : AppSystemActivity() {

  var glxfLoaded = false
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var gltfxEntity: Entity? = null
  private var ballShooter: BallShooter? = null
  private var gotAllAnchors = false
  private var debug = false
  private lateinit var mrukFeature: MRUKFeature
  private lateinit var sceneEventListener: MRUKSceneEventListener
  private lateinit var procMeshSpawner: AnchorProceduralMesh

  override fun registerFeatures(): List<SpatialFeature> {
    mrukFeature = MRUKFeature(this, systemManager)
    val features =
        mutableListOf(
            PhysicsFeature(spatial, worldBounds = PhysicsWorldBounds(minY = -100.0f)),
            VRFeature(this),
            ComposeFeature(),
            mrukFeature,
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

    systemManager.registerSystem(UiPanelUpdateSystem())

    // NOTE: Here a material could be set as well to visualize the walls, ceiling, etc
    //       It is also possible to spawn procedural meshes for volumes
    procMeshSpawner =
        AnchorProceduralMesh(
            mrukFeature,
            mapOf(
                MRUKLabel.FLOOR to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.WALL_FACE to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.CEILING to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.TABLE to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.OTHER to AnchorProceduralMeshConfig(null, true),
            ),
        )

    // Enable MR mode
    systemManager.findSystem<LocomotionSystem>().enableLocomotion(false)
    scene.enablePassthrough(true)

    loadGLXF { composition ->
      systemManager.unregisterSystem<BallShooter>()
      glxfLoaded = true
      val bball = composition.getNodeByName("BasketBall").entity
      val mesh = bball.getComponent<Mesh>()
      ballShooter = BallShooter(mesh)
      systemManager.registerSystem(ballShooter!!)

      sceneEventListener =
          object : MRUKSceneEventListener {
            override fun onRoomAdded(room: MRUKRoom) {
              // If a room exists, it has a floor. Remove the default floor.
              val floor = composition.tryGetNodeByName("defaultFloor")
              floor!!.entity.destroy()
            }
          }
      mrukFeature.addSceneEventListener(sceneEventListener)

      if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {
        log("Scene permission has not been granted, requesting $PERMISSION_USE_SCENE")
        requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_PERMISSION_USE_SCENE)
      } else {
        log("Scene permission has already been granted!")
        loadSceneFromDevice()
      }
    }
  }

  private fun loadSceneFromDevice() {
    log("Loading scene from device...")
    mrukFeature.loadSceneFromDevice().whenComplete { result: MRUKLoadDeviceResult, _ ->
      if (result != MRUKLoadDeviceResult.SUCCESS) {
        log("Error loading scene from device: $result")
      } else {
        log("Scene loaded from device")
      }
    }
  }

  override fun onSpatialShutdown() {
    procMeshSpawner.destroy()
    mrukFeature.removeSceneEventListener(sceneEventListener)
    super.onSpatialShutdown()
  }

  override fun onSceneReady() {
    super.onSceneReady()

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f,
    )
    scene.updateIBLEnvironment("environment.env")
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray,
  ) {
    if (
        requestCode == REQUEST_CODE_PERMISSION_USE_SCENE &&
            permissions.size == 1 &&
            permissions[0] == PERMISSION_USE_SCENE
    ) {
      val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
      if (granted) {
        log("Use scene permission has been granted")
        loadSceneFromDevice()
      } else {
        log("Use scene permission was DENIED!")
      }
    }
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        ComposeViewPanelRegistration(
            R.id.panel,
            composeViewCreator = { _, ctx ->
              ComposeView(ctx).apply {
                setContent {
                  AboutPanelLayout(
                      onConfigureRoomClick = {
                        scene.requestSceneCapture().whenComplete { _, _ -> loadSceneFromDevice() }
                      },
                      onToggleDebugClick = {
                        debug = !debug
                        spatial.enablePhysicsDebugLines(debug)
                      },
                  )
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = ABOUT_PANEL_WIDTH, height = ABOUT_PANEL_HEIGHT),
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
          keyName = GLXF_SCENE,
          onLoaded = onLoaded,
      )
    }
  }

  companion object {
    const val TAG = "MixedRealitySampleActivityDebug"
    const val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    const val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1
    const val GLXF_SCENE = "GLXF_SCENE"
  }
}

fun log(msg: String) {
  Log.d(MixedRealitySampleActivity.TAG, msg)
}
