// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.meta.pixelandtexel.geovoyage.GrabbableNoRotation
import com.meta.pixelandtexel.geovoyage.Pinnable
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.Spin
import com.meta.pixelandtexel.geovoyage.Spinnable
import com.meta.pixelandtexel.geovoyage.Tether
import com.meta.pixelandtexel.geovoyage.ecs.grabbablenorotation.GrabbableNoRotationSystem
import com.meta.pixelandtexel.geovoyage.ecs.landmarkspawn.LandmarkSpawnSystem
import com.meta.pixelandtexel.geovoyage.ecs.pinnable.PinnableSystem
import com.meta.pixelandtexel.geovoyage.ecs.spin.SpinSystem
import com.meta.pixelandtexel.geovoyage.ecs.spinnable.SpinnableSystem
import com.meta.pixelandtexel.geovoyage.ecs.tether.TetherSystem
import com.meta.pixelandtexel.geovoyage.enums.PlayMode
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.models.GeoCoordinates
import com.meta.pixelandtexel.geovoyage.models.Landmark
import com.meta.pixelandtexel.geovoyage.models.PanoMetadata
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.services.googlemaps.GoogleTilesService
import com.meta.pixelandtexel.geovoyage.services.googlemaps.IPanoramaServiceHandler
import com.meta.pixelandtexel.geovoyage.services.llama.QueryLlamaService
import com.meta.pixelandtexel.geovoyage.utils.copyTo
import com.meta.spatial.core.Entity
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.runtime.AlphaMode
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.PanelConfigOptions
import com.meta.spatial.runtime.SceneAudioAsset
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.lang.ref.WeakReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : ActivityCompat.OnRequestPermissionsResultCallback, AppSystemActivity() {
  companion object {
    private const val TAG: String = "MainActivity"
    private const val REQUEST_PERMISSIONS_CODE = 1000

    lateinit var instance: WeakReference<MainActivity>
  }

  private var CurrentMode: PlayMode = PlayMode.INTRO

  private var startedSpeakingSoundAsset: SceneAudioAsset? = null
  private var finishedSpeakingSoundAsset: SceneAudioAsset? = null

  private var panelEntity: Entity? = null
  private var glxfEntity: Entity? = null

  private var skyboxSceneObject: SceneObject? = null

  private val activityScope = CoroutineScope(Dispatchers.Main)
  //    private val composition: GLXFInfo? = null

  private lateinit var permissionsResultCallback: (granted: Boolean) -> Unit

  override fun registerFeatures(): List<SpatialFeature> {
    return listOf(VRFeature(this))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    instance = WeakReference(this)

    SettingsService.initialize(this)
    QueryLlamaService.initialize(this)

    // unregister systems

    systemManager.unregisterSystem<LocomotionSystem>()

    // register our new systems

    componentManager.registerComponent<GrabbableNoRotation>(GrabbableNoRotation.Companion)
    systemManager.registerSystem(GrabbableNoRotationSystem())

    componentManager.registerComponent<Spinnable>(Spinnable.Companion)
    systemManager.registerSystem(SpinnableSystem())

    systemManager.registerSystem(LandmarkSpawnSystem(R.xml.landmarks, this.spatialContext))

    componentManager.registerComponent<Pinnable>(Pinnable.Companion)

    componentManager.registerComponent<Spin>(Spin.Companion)
    systemManager.registerSystem(SpinSystem())

    componentManager.registerComponent<Tether>(Tether.Companion)
    systemManager.registerLateSystem(TetherSystem())

    // get our sound assets

    startedSpeakingSoundAsset = SceneAudioAsset.loadLocalFile("audio/start_recording.wav")
    finishedSpeakingSoundAsset = SceneAudioAsset.loadLocalFile("audio/finish_recording.wav")
  }

  override fun registerPanels(): List<PanelRegistration> {
    // all of our panels share a common panel config, except for width/height values
    return listOf(
        PanelRegistration(R.integer.panel_id) {
          activityClass = PanelActivity::class.java
          config { getPanelConfig(1.21f, 0.94f, 1210).copyTo(this) }
        })
  }

  private fun getPanelConfig(width: Float, height: Float, dp: Int): PanelConfigOptions {
    return PanelConfigOptions(
        width = width,
        height = height,
        layoutWidthInDp = dp.toFloat(),
        layoutHeightInDp = dp * (height / width),
        includeGlass = false,
        themeResourceId = R.style.PanelAppThemeTransparent,
        enableTransparent = true,
        forceSceneTexture = true,
        // Enable better looking panels
        layerConfig = LayerConfig(),
        panelShader = SceneMaterial.HOLE_PUNCH_PANEL_SHADER,
        alphaMode = AlphaMode.HOLE_PUNCH)
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // Enable image-based lighting
    scene.updateIBLEnvironment("environment.env")

    // Enable better panel rendering
    scene.enableHolePunching(true)
    // Enable passthrough by default
    scene.enablePassthrough(true)

    scene.setViewOrigin(0.0f, 0.0f, 0.0f)

    // load our scene
    loadGLXF().invokeOnCompletion {
      val composition = glXFManager.getGLXFInfo("scene")

      // update collision on some entities since hittable isn't supported by MeshLoader

      disableCollisionFor(composition.getNodeByName("clouds").entity)
      disableCollisionFor(composition.getNodeByName("trees").entity)
      disableCollisionFor(composition.getNodeByName("pin").entity)
      disableCollisionFor(Entity(R.integer.skybox_id))

      // Wait until the scene is inflated, then register/notify relevant systems
      systemManager.findSystem<LandmarkSpawnSystem>().onCompositionLoaded(composition)
      systemManager.registerSystem(PinnableSystem(glXFManager))

      Entity(R.integer.skybox_id)
          .setComponents(
              Mesh(Uri.parse("mesh://skybox")),
              Material().apply { unlit = true },
              Transform(),
              Visible(false))

      // this is a workaround for preventing the black panels
      val globeEntity = composition.getNodeByName("earth").entity

      panelEntity =
          Entity.createPanelEntity(
              R.integer.panel_id,
              R.integer.panel_id,
              Transform(),
              Tether(globeEntity, -63f, 15f, 1.2f),
              Visible(false))
    }
  }

  private fun loadGLXF(): Job {
    glxfEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("scenes/Composition.glxf"), rootEntity = glxfEntity!!, keyName = "scene")
    }
  }

  private fun disableCollisionFor(entity: Entity) {
    if (!entity.hasComponent<Mesh>()) {
      return
    }

    val mesh = entity.getComponent<Mesh>()
    mesh.hittable = MeshCollision.NoCollision
    entity.setComponent(mesh)
    mesh.recycle()
  }

  // this is part of a workaround for preventing the black panels
  fun showMainPanel() {
    panelEntity?.setComponent(Visible(true))
  }

  fun tryStartMode(mode: PlayMode) {
    if (mode == CurrentMode) {
      return
    }

    exitCurrentMode()

    when (mode) {
      PlayMode.EXPLORE -> {
        val landmarkSystem = systemManager.findSystem<LandmarkSpawnSystem>()
        val pinnableSystem = systemManager.findSystem<PinnableSystem>()
        landmarkSystem.toggleLandmarks(SettingsService.get(SettingsKey.LANDMARKS_ENABLED, true))
        pinnableSystem.togglePinning(true)
      }

      else -> {}
    }

    CurrentMode = mode
  }

  private fun exitCurrentMode() {
    when (CurrentMode) {
      PlayMode.EXPLORE -> {
        toggleSkybox(false)

        // disable explore mode systems
        val landmarkSystem = systemManager.findSystem<LandmarkSpawnSystem>()
        val pinnableSystem = systemManager.findSystem<PinnableSystem>()
        landmarkSystem.toggleLandmarks(false)
        pinnableSystem.togglePinning(false)
      }

      else -> {}
    }
  }

  // user interactions during explore mode

  fun userDroppedPin(coords: GeoCoordinates) {
    if (CurrentMode !== PlayMode.EXPLORE) {
      return
    }

    toggleSkybox(false)

    PanelActivity.instance.get()?.startQueryAtCoordinates(coords)
  }

  fun userSelectedLandmark(info: Landmark, coords: GeoCoordinates) {
    if (CurrentMode !== PlayMode.EXPLORE) {
      return
    }

    toggleSkybox(false)

    val pinnableSystem = systemManager.findSystem<PinnableSystem>()
    pinnableSystem.hidePin()
    pinnableSystem.resetPin()

    PanelActivity.instance.get()?.displayLandmarkInfo(info, coords)
  }

  fun userToggledLandmarks(enabled: Boolean) {
    if (CurrentMode !== PlayMode.EXPLORE) {
      return
    }

    val landmarkSystem = systemManager.findSystem<LandmarkSpawnSystem>()
    landmarkSystem.toggleLandmarks(enabled)
  }

  fun tryShowSkyboxAt(panoData: PanoMetadata, onFinished: (success: Boolean) -> Unit) {
    toggleSkybox(false)

    val skyboxEntity = Entity(R.integer.skybox_id)

    getSkyboxSceneObject {
      GoogleTilesService.getPanoramaBitmapFor(
          panoData,
          object : IPanoramaServiceHandler {
            override fun onFinished(bitmap: Bitmap) {
              if (!skyboxEntity.hasComponent<Mesh>() || !skyboxEntity.hasComponent<Material>()) {
                Log.w(TAG, "Skybox entity missing mesh or material")
                onFinished(false)
                return
              }

              val sceneMaterial = skyboxSceneObject!!.mesh?.materials?.get(0)
              if (sceneMaterial == null) {
                Log.w(TAG, "Skybox scene object material not found")
                onFinished(false)
                return
              }

              sceneMaterial.setRepeat(1f, 1f, 0.5f, 0f)

              // destroy our old skybox texture
              sceneMaterial.texture?.destroy()

              // set our new texture
              val sceneTexture = SceneTexture(bitmap)
              sceneMaterial.setAlbedoTexture(sceneTexture)

              if (CurrentMode == PlayMode.EXPLORE) {
                toggleSkybox(true)
              }

              onFinished(true)
            }

            override fun onError(reason: String) {
              Log.w(TAG, "Failed to get panorama bitmap: $reason")
              onFinished(false)
            }
          })
    }
  }

  private fun getSkyboxSceneObject(then: () -> Unit) {
    if (skyboxSceneObject != null) {
      then()
      return
    }

    val entity = Entity(R.integer.skybox_id)
    val completable =
        systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)
            ?: throw Exception("Failed to fetch skybox scene object")

    completable.thenAccept { sceneObject ->
      skyboxSceneObject = sceneObject
      then()
    }
  }

  fun toggleSkybox(visible: Boolean? = null) {
    val entity = Entity(R.integer.skybox_id)

    // visibility not specified; just swap it
    if (visible == null) {
      val isVisible = entity.getComponent<Visible>().isVisible
      entity.setComponent(Visible(!isVisible))
      return
    }

    entity.setComponent(Visible(visible))
  }

  // sound playback

  fun userStartedSpeaking() {
    if (startedSpeakingSoundAsset != null) {
      scene.playSound(startedSpeakingSoundAsset!!, 1f)
    }
  }

  fun userFinishedSpeaking() {
    if (finishedSpeakingSoundAsset != null) {
      scene.playSound(finishedSpeakingSoundAsset!!, 1f)
    }
  }

  // permissions requesting

  fun requestPermissions(callback: (granted: Boolean) -> Unit) {
    permissionsResultCallback = callback

    ActivityCompat.requestPermissions(
        this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSIONS_CODE)
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    when (requestCode) {
      REQUEST_PERMISSIONS_CODE -> {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          Log.d(TAG, "Permissions granted")
          permissionsResultCallback(true)
        } else {
          Log.w(TAG, "Permissions denied")
          permissionsResultCallback(false)
        }
      }
    }
  }
}
