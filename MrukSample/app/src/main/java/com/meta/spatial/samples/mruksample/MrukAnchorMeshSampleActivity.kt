/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.AnchorMeshSpawner
import com.meta.spatial.mruk.AnchorProceduralMesh
import com.meta.spatial.mruk.AnchorProceduralMeshConfig
import com.meta.spatial.mruk.MRUKAnchor
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.mruk.MRUKLoadDeviceResult
import com.meta.spatial.mruk.MRUKRoom
import com.meta.spatial.mruk.MRUKSceneEventListener
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.io.File

// default activity
class MrukAnchorMeshSampleActivity : AppSystemActivity(), MRUKSceneEventListener {
  private lateinit var mrukFeature: MRUKFeature
  private lateinit var meshSpawner: AnchorMeshSpawner
  private lateinit var procMeshSpawner: AnchorProceduralMesh
  private var globalMeshSpawner: AnchorProceduralMesh? = null
  private var currentRoomTextView: TextView? = null

  private var sceneDataLoaded = false
  private var showUiPanel = false
  private var showColliders = false

  private val panelId = 2

  override fun registerFeatures(): List<SpatialFeature> {
    // Register the features that are needed for that sample. Note that in addition to the
    // MRUKFeature the PhysicsFeature gets enabled as well. This is needed for having the physics
    // colliders on the AnchorProceduralMesh working.
    mrukFeature = MRUKFeature(this, systemManager)
    val features = mutableListOf(VRFeature(this), PhysicsFeature(spatial), mrukFeature)
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
    }
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    systemManager.findSystem<LocomotionSystem>().enableLocomotion(false)
    systemManager.registerSystem(UIPositionSystem(::setUIPanelVisibility))
    systemManager.registerSystem(MrukInputSystem(::toggleUiPanelVisibility))
    systemManager.registerSystem(UpdateRoomSystem(mrukFeature, ::currentRoomTextView))

    scene.enablePassthrough(true)

    // Setup the AnchorMeshSpawner. The AnchorMeshSpawner is able to spawn glb/gltf meshes in place
    // of scene anchors.
    // See the documentation AnchorMeshSpawner for more information.

    meshSpawner =
        AnchorMeshSpawner(
            mrukFeature,
            mutableMapOf(
                MRUKLabel.TABLE to AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/Table.glb")),
                MRUKLabel.COUCH to AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/Couch.glb")),
                MRUKLabel.WINDOW_FRAME to
                    AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/Window.glb")),
                MRUKLabel.DOOR_FRAME to
                    AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/Door.glb")),
                MRUKLabel.OTHER to
                    AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/BoxCardBoard.glb")),
                MRUKLabel.STORAGE to
                    AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/Storage.glb")),
                MRUKLabel.BED to AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/TwinBed.glb")),
                MRUKLabel.SCREEN to
                    AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/ComputerScreen.glb")),
                MRUKLabel.LAMP to AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/Lamp.glb")),
                MRUKLabel.PLANT to
                    AnchorMeshSpawner.AnchorMeshGroup(
                        listOf(
                            "Furniture/Plant1.glb",
                            "Furniture/Plant2.glb",
                            "Furniture/Plant3.glb",
                            "Furniture/Plant4.glb")),
                MRUKLabel.WALL_ART to
                    AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/WallArt.glb")),
            ))

    // Setup the AnchorProceduralMesh. The AnchorProceduralMesh will generate meshes in place of
    // scene anchors. For example floor, ceiling, and walls.
    // For more information see the documentation on AnchorProceduralMesh.

    val floorMaterial =
        Material().apply { baseTextureAndroidResourceId = R.drawable.carpet_texture }
    val wallMaterial = Material().apply { baseTextureAndroidResourceId = R.drawable.wall1 }
    procMeshSpawner =
        AnchorProceduralMesh(
            mrukFeature,
            mapOf(
                MRUKLabel.TABLE to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.SCREEN to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.LAMP to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.OTHER to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.COUCH to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.PLANT to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.STORAGE to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.BED to AnchorProceduralMeshConfig(null, true),
                MRUKLabel.FLOOR to AnchorProceduralMeshConfig(floorMaterial, true),
                MRUKLabel.WALL_FACE to AnchorProceduralMeshConfig(wallMaterial, true),
                MRUKLabel.CEILING to AnchorProceduralMeshConfig(wallMaterial, true)))

    NetworkedAssetLoader.init(File(applicationContext.cacheDir.canonicalPath), OkHttpAssetFetcher())

    mrukFeature.addSceneEventListener(this)

    // Request the user to give the app scene permission if not already granted.
    // Otherwise, the app will not be able to access the device scene data.

    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {

      // Request the scene permission if it hasn't already been granted. Make sure to hook into
      // onRequestPermissionsResult()
      // to try to load the scene again.

      Log.i(TAG, "Scene permission has not been granted, requesting $PERMISSION_USE_SCENE")
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_PERMISSION_USE_SCENE)
    } else {

      // Load the scene data from the device. If scene data was found and loaded successfully the
      // AnchorMeshSpawner and AnchorProceduralMesh will populate the room with virtual objects.

      loadScene(true)
    }
  }

  // Implement listeners that get called whenever rooms and anchors get updated, added or removed.
  // Hooking into these events can be useful to keep your app in sync with the loaded anchors.
  override fun onRoomAdded(room: MRUKRoom) {
    Log.d("MRUK", "Activity: Room added: ${room.anchor}")
  }

  override fun onRoomRemoved(room: MRUKRoom) {
    Log.d("MRUK", "Activity: Room removed: ${room.anchor}")
  }

  override fun onRoomUpdated(room: MRUKRoom) {
    Log.d("MRUK", "Activity: Room updated: ${room.anchor}")
  }

  override fun onAnchorAdded(room: MRUKRoom, anchor: Entity) {
    val anchorUuid = anchor.getComponent<MRUKAnchor>().uuid
    Log.d("MRUK", "Activity: Anchor $anchorUuid added to room ${room.anchor}")
  }

  override fun onAnchorRemoved(room: MRUKRoom, anchor: Entity) {
    val anchorUuid = anchor.getComponent<MRUKAnchor>().uuid
    Log.d("MRUK", "Activity: Anchor $anchorUuid removed from room ${room.anchor}")
  }

  override fun onAnchorUpdated(room: MRUKRoom, anchor: Entity) {
    val anchorUuid = anchor.getComponent<MRUKAnchor>().uuid
    Log.d("MRUK", "Activity: Anchor $anchorUuid updated to room ${room.anchor}")
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    if (requestCode == REQUEST_CODE_PERMISSION_USE_SCENE &&
        permissions.size == 1 &&
        permissions[0] == PERMISSION_USE_SCENE) {
      val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
      if (granted) {
        Log.i(TAG, "Use scene permission has been granted")
      } else {
        Log.i(TAG, "Use scene permission was DENIED!")
      }
      loadScene(granted)
    }
  }

  override fun onSceneReady() {
    super.onSceneReady()

    scene.setLightingEnvironment(
        ambientColor = Vector3(0.2f),
        sunColor = Vector3(1.0f, 1.0f, 1.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f)

    Entity.createPanelEntity(
        panelId, R.layout.ui_anchor_mesh_menu, Transform(), Visible(showUiPanel), Grabbable())
  }

  override fun onDestroy() {
    mrukFeature.removeSceneEventListener(this)
    meshSpawner.destroy()
    procMeshSpawner.destroy()
    globalMeshSpawner?.destroy()
    super.onDestroy()
  }

  private fun loadScene(scenePermissionsGranted: Boolean) {
    // MRUK has support for loading scene data from JSON files in addition to loading the scene data
    // from device.
    // We fallback here to JSON rooms in case the user hasn't given any scene permission just to
    // show something.
    // JSON rooms can be useful to accelerate development or testing different room layouts.

    if (!sceneDataLoaded) {
      sceneDataLoaded = true

      if (scenePermissionsGranted) {
        loadSceneFromDevice()
      } else {
        loadFallbackScene()
      }
    }
  }

  private fun loadFallbackScene() {
    Log.i(TAG, "Loading fallback scene from JSON")
    val file = applicationContext.assets.open("MeshBedroom3.json")
    val text = file.bufferedReader().use { it.readText() }
    mrukFeature.loadSceneFromJsonString(text)
  }

  private fun loadSceneFromDevice() {
    Log.i(TAG, "Loading scene from device")
    val future = mrukFeature.loadSceneFromDevice()

    future.whenComplete { result: MRUKLoadDeviceResult, _ ->
      if (result != MRUKLoadDeviceResult.SUCCESS) {
        Log.e(TAG, "Error loading scene from device: $result")
        loadFallbackScene()
      }
    }
  }

  override fun onRecenter() {
    super.onRecenter()
    recenterElementInView(getHmd(systemManager), Entity(panelId))
  }

  fun toggleUiPanelVisibility() {
    setUIPanelVisibility(!showUiPanel)
  }

  private fun setUIPanelVisibility(visible: Boolean) {
    showUiPanel = visible
    val panel = Entity(panelId)
    panel.setComponent(Visible(showUiPanel))
    if (showUiPanel) {
      recenterElementInView(getHmd(systemManager), panel)
    }
  }

  private fun toggleGlobalMesh() {
    if (globalMeshSpawner == null) {
      val wallMaterial = Material().apply { baseTextureAndroidResourceId = R.drawable.wall1 }
      globalMeshSpawner =
          AnchorProceduralMesh(
              mrukFeature,
              mapOf(MRUKLabel.GLOBAL_MESH to AnchorProceduralMeshConfig(wallMaterial, false)))
    } else {
      globalMeshSpawner?.destroy()
      globalMeshSpawner = null
    }
  }

  private fun toggleShowColliders() {
    showColliders = !showColliders
    spatial.enablePhysicsDebugLines(showColliders)
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.ui_anchor_mesh_menu) {
          config {
            height = 0.5f
            width = 0.5f
            fractionOfScreen = 1f
            layoutDpi = 500
          }
          panel {
            requireNotNull(rootView)

            currentRoomTextView = rootView?.findViewById<TextView>(R.id.mruk_current_room)

            val jsonFileSpinner =
                requireNotNull(rootView?.findViewById<Spinner>(R.id.json_file_spinner))
            ArrayAdapter.createFromResource(
                    rootView?.context!!,
                    R.array.json_rooms_array,
                    android.R.layout.simple_spinner_item)
                .also { adapter ->
                  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                  jsonFileSpinner.adapter = adapter
                }

            val loadSceneFromJSONButton = rootView?.findViewById<Button>(R.id.load_scene_from_json)
            loadSceneFromJSONButton?.setOnClickListener {
              // Get selection from spinner
              jsonFileSpinner.selectedItem?.let { item ->
                val file = applicationContext.assets.open("${item}.json")
                val text = file.bufferedReader().use { reader -> reader.readText() }
                mrukFeature.loadSceneFromJsonString(text)
              }
            }

            val clearSceneButton = rootView?.findViewById<Button>(R.id.clear_scene)
            clearSceneButton?.setOnClickListener { mrukFeature.clearRooms() }

            val loadSceneFromDeviceButton =
                rootView?.findViewById<Button>(R.id.load_scene_from_device)
            loadSceneFromDeviceButton?.setOnClickListener { mrukFeature.loadSceneFromDevice() }

            val showGlobalMeshButton = rootView?.findViewById<Button>(R.id.show_global_mesh)
            showGlobalMeshButton?.setOnClickListener { toggleGlobalMesh() }

            val showCollidersButton = rootView?.findViewById<Button>(R.id.show_colliders)
            showCollidersButton?.setOnClickListener { toggleShowColliders() }

            val launchSceneCaptureButton = rootView?.findViewById<Button>(R.id.launch_scene_capture)
            launchSceneCaptureButton?.setOnClickListener { mrukFeature.requestSceneCapture() }

            val closeSampleButton = rootView?.findViewById<Button>(R.id.close_sample)
            closeSampleButton?.setOnClickListener {
              returnTo2DActivity(
                  this@MrukAnchorMeshSampleActivity,
                  applicationContext,
                  MrukSampleStartMenuActivity::class.java)
            }
          }
        })
  }

  companion object {
    const val TAG: String = "MrukAnchorMeshSample"
    const val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    const val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1
  }
}
