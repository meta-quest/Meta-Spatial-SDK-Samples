/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.anchor_mesh

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
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
import com.meta.spatial.mruk.SceneModel
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMesh
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.samples.mruksample.MrukSampleStartMenuActivity
import com.meta.spatial.samples.mruksample.R
import com.meta.spatial.samples.mruksample.RoomMeshFace
import com.meta.spatial.samples.mruksample.common.MrukInputSystem
import com.meta.spatial.samples.mruksample.common.UIPositionSystem
import com.meta.spatial.samples.mruksample.common.UpdateRoomSystem
import com.meta.spatial.samples.mruksample.common.getHmd
import com.meta.spatial.samples.mruksample.common.recenterElementInView
import com.meta.spatial.samples.mruksample.common.returnTo2DActivity
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCreationSystem
import com.meta.spatial.toolkit.MeshManager
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class MrukAnchorMeshSampleActivity : AppSystemActivity(), MRUKSceneEventListener {
  private lateinit var mrukFeature: MRUKFeature
  private lateinit var meshSpawner: AnchorMeshSpawner
  private lateinit var procMeshSpawner: AnchorProceduralMesh
  private var globalMeshSpawner: AnchorProceduralMesh? = null
  private var currentRoomTextView: TextView? = null
  private var checkboxRoomMesh: CheckBox? = null

  private var sceneDataLoaded = false
  private var showUiPanel = false
  private var showColliders = false

  private val panelId = 2

  private val roomMeshEntities = mutableListOf<Entity>()

  override fun registerFeatures(): List<SpatialFeature> {
    // Register the features that are needed for that sample. Note that in addition to the
    // MRUKFeature the PhysicsFeature gets enabled as well. This is needed for having the
    // physics colliders on the AnchorProceduralMesh working.
    mrukFeature = MRUKFeature(this, systemManager)
    return listOf(VRFeature(this), PhysicsFeature(spatial), mrukFeature)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    componentManager.registerComponent<RoomMeshFace>(RoomMeshFace)

    systemManager.findSystem<LocomotionSystem>().enableLocomotion(false)
    systemManager.registerSystem(UIPositionSystem(::setUIPanelVisibility))
    systemManager.registerSystem(MrukInputSystem(::toggleUiPanelVisibility))
    systemManager.registerSystem(UpdateRoomSystem(mrukFeature, ::currentRoomTextView))

    scene.enablePassthrough(true)

    // Setup the AnchorMeshSpawner. The AnchorMeshSpawner is able to spawn glb/gltf meshes in
    // place of scene anchors.
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
                            "Furniture/Plant4.glb",
                        )
                    ),
                MRUKLabel.WALL_ART to
                    AnchorMeshSpawner.AnchorMeshGroup(listOf("Furniture/WallArt.glb")),
            ),
        )

    // Setup the AnchorProceduralMesh. The AnchorProceduralMesh will generate meshes in place
    // of scene anchors. For example floor, ceiling, and walls.
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
                MRUKLabel.INVISIBLE_WALL_FACE to AnchorProceduralMeshConfig(wallMaterial, true),
                MRUKLabel.INNER_WALL_FACE to AnchorProceduralMeshConfig(wallMaterial, true),
                MRUKLabel.CEILING to AnchorProceduralMeshConfig(wallMaterial, true),
            ),
        )

    mrukFeature.addSceneEventListener(this)

    val meshManager = systemManager.findSystem<MeshCreationSystem>().meshManager
    registerRoomMeshCreator(meshManager)

    // Request the user to give the app scene permission if not already granted.
    // Otherwise, the app will not be able to access the device scene data.

    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {

      // Request the scene permission if it hasn't already been granted. Make sure to hook into
      // onRequestPermissionsResult()
      // to try to load the scene again.

      Log.i(TAG, "Scene permission has not been granted, requesting $PERMISSION_USE_SCENE")
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_PERMISSION_USE_SCENE)
    } else {

      // Load the scene data from the device. If scene data was found and loaded successfully
      // the AnchorMeshSpawner and AnchorProceduralMesh will populate the room with virtual
      // objects.

      loadScene(true)
    }
  }

  // Implement listeners that get called whenever rooms and anchors get updated, added or
  // removed. Hooking into these events can be useful to keep your app in sync with the loaded
  // anchors.
  override fun onRoomAdded(room: MRUKRoom) {
    Log.d(TAG, "Activity: Room added: ${room.anchor}")
    checkboxRoomMesh?.let {
      if (room.roomMesh != null) {
        it.visibility = View.VISIBLE
      } else {
        it.visibility = View.GONE
      }
    }
  }

  override fun onRoomRemoved(room: MRUKRoom) {
    Log.d(TAG, "Activity: Room removed: ${room.anchor}")
  }

  override fun onRoomUpdated(room: MRUKRoom) {
    Log.d(TAG, "Activity: Room updated: ${room.anchor}")
  }

  override fun onAnchorAdded(room: MRUKRoom, anchor: Entity) {
    val anchorUuid = anchor.getComponent<MRUKAnchor>().uuid
    Log.d(TAG, "Activity: Anchor $anchorUuid added to room ${room.anchor}")
  }

  override fun onAnchorRemoved(room: MRUKRoom, anchor: Entity) {
    val anchorUuid = anchor.getComponent<MRUKAnchor>().uuid
    Log.d(TAG, "Activity: Anchor $anchorUuid removed from room ${room.anchor}")
  }

  override fun onAnchorUpdated(room: MRUKRoom, anchor: Entity) {
    val anchorUuid = anchor.getComponent<MRUKAnchor>().uuid
    Log.d(TAG, "Activity: Anchor $anchorUuid updated to room ${room.anchor}")
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
        environmentIntensity = 0.3f,
    )

    Entity.createPanelEntity(
        panelId,
        R.layout.ui_anchor_mesh_menu,
        Transform(),
        Visible(showUiPanel),
        Grabbable(),
    )
  }

  override fun onSpatialShutdown() {
    mrukFeature.removeSceneEventListener(this)
    meshSpawner.destroy()
    procMeshSpawner.destroy()
    globalMeshSpawner?.destroy()
    super.onSpatialShutdown()
  }

  private fun loadScene(scenePermissionsGranted: Boolean) {
    // MRUK has support for loading scene data from JSON files in addition to loading the scene
    // data from device.
    // We fallback here to JSON rooms in case the user hasn't given any scene permission just
    // to show something.
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

  override fun onRecenter(isUserInitiated: Boolean) {
    super.onRecenter(isUserInitiated)
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
              mapOf(MRUKLabel.GLOBAL_MESH to AnchorProceduralMeshConfig(wallMaterial, false)),
          )
    } else {
      globalMeshSpawner?.destroy()
      globalMeshSpawner = null
    }
  }

  private fun toggleShowColliders() {
    showColliders = !showColliders
    spatial.enablePhysicsDebugLines(showColliders)
  }

  private fun writeStringToFile(context: Context, fileName: String, content: String) {
    // Check if external storage is writable
    if (isExternalStorageWritable()) {
      // Get the external storage directory
      val externalStorageDir = context.getExternalFilesDir(null)
      if (externalStorageDir != null) {
        // Create a new file in the external storage directory
        val file = File(externalStorageDir, fileName)
        try {
          FileOutputStream(file).use { fos ->
            fos.write(content.toByteArray())
            Log.d(TAG, "File saved at: ${file.canonicalPath}")
          }
        } catch (e: IOException) {
          Log.e(TAG, "Error writing to file", e)
        }
      } else {
        Log.e(TAG, "External storage directory is null")
      }
    } else {
      Log.e(TAG, "External storage is not writable")
    }
  }

  // Helper method to check if external storage is writable
  private fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
  }

  private fun showRoomMesh() {
    hideRoomMesh()

    for (room in mrukFeature.rooms) {
      room.entity?.let { roomEntity ->
        if (room.roomMesh != null) {
          val roomMesh = requireNotNull(room.roomMesh)
          for (roomMeshFace in roomMesh.faces) {
            val roomMeshEntity =
                Entity.create(
                    listOf(
                        Mesh(Uri.parse(ROOM_MESH_ID)),
                        RoomMeshFace(room.anchor.uuid, roomMeshFace.uuid, roomMeshFace.label),
                        Transform(),
                        TransformParent(roomEntity),
                    )
                )
            roomMeshEntities.add(roomMeshEntity)
          }
        }
      }
    }
  }

  private fun hideRoomMesh() {
    for (roomMeshEntity in roomMeshEntities) {
      roomMeshEntity.destroy()
    }
    roomMeshEntities.clear()
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.ui_anchor_mesh_menu) {
          config {
            height = 0.5f
            width = 0.5f
            fractionOfScreen = 1f
            layoutDpi = 500
            layerConfig = LayerConfig()
            enableTransparent = true
          }
          panel {
            requireNotNull(rootView)

            currentRoomTextView = rootView?.findViewById<TextView>(R.id.mruk_current_room)

            checkboxRoomMesh = rootView?.findViewById<CheckBox>(R.id.checkbox_room_mesh)
            checkboxRoomMesh?.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                showRoomMesh()
              } else {
                hideRoomMesh()
              }
            }

            val sceneModelSpinner =
                requireNotNull(rootView?.findViewById<Spinner>(R.id.scene_model_spinner))
            ArrayAdapter.createFromResource(
                    rootView?.context!!,
                    R.array.scene_models_array,
                    android.R.layout.simple_spinner_item,
                )
                .also { adapter ->
                  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                  sceneModelSpinner.adapter = adapter
                }

            val jsonFileSpinner =
                requireNotNull(rootView?.findViewById<Spinner>(R.id.json_file_spinner))
            ArrayAdapter.createFromResource(
                    rootView?.context!!,
                    R.array.json_rooms_array,
                    android.R.layout.simple_spinner_item,
                )
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
                mrukFeature.loadSceneFromJsonString(
                    text,
                    true,
                    getSelectedSceneModel(sceneModelSpinner),
                )
              }
            }

            val clearSceneButton = rootView?.findViewById<Button>(R.id.clear_scene)
            clearSceneButton?.setOnClickListener { mrukFeature.clearRooms() }

            val loadSceneFromDeviceButton =
                rootView?.findViewById<Button>(R.id.load_scene_from_device)
            loadSceneFromDeviceButton?.setOnClickListener {
              mrukFeature.loadSceneFromDevice(true, true, getSelectedSceneModel(sceneModelSpinner))
            }

            val showGlobalMeshButton = rootView?.findViewById<Button>(R.id.show_global_mesh)
            showGlobalMeshButton?.setOnClickListener { toggleGlobalMesh() }

            val showCollidersButton = rootView?.findViewById<Button>(R.id.show_colliders)
            showCollidersButton?.setOnClickListener { toggleShowColliders() }

            val launchSceneCaptureButton = rootView?.findViewById<Button>(R.id.launch_scene_capture)
            launchSceneCaptureButton?.setOnClickListener {
              mrukFeature.requestSceneCapture().whenComplete { _, _ ->
                mrukFeature.loadSceneFromDevice()
              }
            }

            val saveSceneToJsonButton = rootView?.findViewById<Button>(R.id.save_to_json)
            saveSceneToJsonButton?.setOnClickListener {
              if (
                  checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                      PackageManager.PERMISSION_GRANTED
              ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE,
                )
              } else {
                val fileName = "scene_${UUID.randomUUID()}.json"
                writeStringToFile(applicationContext, fileName, mrukFeature.saveSceneToJsonString())
              }
            }

            val closeSampleButton = rootView?.findViewById<Button>(R.id.close_sample)
            closeSampleButton?.setOnClickListener {
              returnTo2DActivity(
                  this@MrukAnchorMeshSampleActivity,
                  applicationContext,
                  MrukSampleStartMenuActivity::class.java,
              )
            }
          }
        }
    )
  }

  private fun registerRoomMeshCreator(meshManager: MeshManager) {
    meshManager.meshCreators.put(ROOM_MESH_ID) { entity ->
      // Access the room face mesh component
      val roomFaceComponent = entity.getComponent<RoomMeshFace>()
      // Find the room mesh
      val room = requireNotNull(mrukFeature.findRoom(roomFaceComponent.roomUuid))
      val roomMesh = requireNotNull(room.roomMesh)
      // Find the room face mesh
      val roomFace = requireNotNull(roomMesh.faces.find { it.uuid == roomFaceComponent.uuid })
      // Create the positions and index buffer for the mesh
      val positions = FloatArray(roomFace.indices.size * 3)
      val indices = IntArray(roomFace.indices.size)
      roomFace.indices.forEachIndexed { i, index ->
        positions[i * 3 + 0] = roomMesh.positions[index * 3 + 0]
        positions[i * 3 + 1] = roomMesh.positions[index * 3 + 1]
        positions[i * 3 + 2] = roomMesh.positions[index * 3 + 2]
        indices[i] = i
      }
      // Create the normals buffer for the mesh
      val normals = FloatArray(positions.size) { 0.0f }
      // Create the colors buffer for the mesh
      val color =
          when (roomFace.label) {
            MRUKLabel.FLOOR -> Color.valueOf(0.2f, 0.6f, 0.2f, 1.0f) // Green
            MRUKLabel.CEILING -> Color.valueOf(0.8f, 0.8f, 0.8f, 1.0f) // White
            MRUKLabel.WALL_FACE -> Color.valueOf(0.6f, 0.6f, 0.8f, 1.0f) // Blue
            MRUKLabel.INVISIBLE_WALL_FACE -> Color.valueOf(0.8f, 0.3f, 0.8f, 1.0f) // Purple
            MRUKLabel.INNER_WALL_FACE -> Color.valueOf(0.4f, 0.4f, 0.6f, 1.0f) // Dark Blue
            MRUKLabel.WINDOW_FRAME -> Color.valueOf(0.7f, 0.9f, 1.0f, 1.0f) // Light Blue
            MRUKLabel.DOOR_FRAME -> Color.valueOf(0.6f, 0.4f, 0.2f, 1.0f) // Brown
            else -> Color.valueOf(1.0f, 1.0f, 1.0f)
          }
      val colors: IntArray = IntArray(positions.size / 3) { color.toArgb() }
      // Create the UV buffer for the mesh
      val uvs: FloatArray = FloatArray(positions.size / 3 * 2)

      // Create the mesh
      val sceneMesh =
          SceneMesh.meshWithMaterials(
              positions,
              normals,
              uvs,
              colors,
              indices,
              intArrayOf(0, indices.size),
              arrayOf(SceneMaterial(SceneTexture(Color.valueOf(1.0f, 1.0f, 1.0f, 1.0f)))),
              false,
          )
      sceneMesh
    }
  }

  private fun getSelectedSceneModel(sceneModelSpinner: Spinner): SceneModel {
    return sceneModelSpinner.selectedItem?.let { item ->
      when (item as? String) {
        "Scene V1 (Basic Scene)" -> SceneModel.V1
        "Scene V2 (High-Fidelity Scene)" -> SceneModel.V2
        "Scene V2 with Fallback to Scene V1" -> SceneModel.V2_FALLBACK_V1
        else -> SceneModel.V1
      }
    } ?: SceneModel.V1 // Provide a default value if null
  }

  companion object {
    const val TAG: String = "MrukAnchorMeshSample"
    const val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    const val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1
    const val REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 2
    const val ROOM_MESH_ID: String = "mesh://roommesh"
  }
}
