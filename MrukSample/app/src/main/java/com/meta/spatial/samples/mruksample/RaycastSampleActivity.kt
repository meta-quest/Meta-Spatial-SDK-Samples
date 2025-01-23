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
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector4
import com.meta.spatial.mruk.AnchorProceduralMesh
import com.meta.spatial.mruk.AnchorProceduralMeshConfig
import com.meta.spatial.mruk.MRUKAnchorTexCoordMode
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.mruk.MRUKSpawnMode
import com.meta.spatial.mruk.MRUKWallTexCoordModeU
import com.meta.spatial.mruk.MRUKWallTexCoordModeV
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.runtime.BlendMode
import com.meta.spatial.runtime.DepthWrite
import com.meta.spatial.runtime.MaterialSidedness
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMaterialAttribute
import com.meta.spatial.runtime.SceneMaterialDataType
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature

class RaycastSampleActivity : AppSystemActivity() {

  private lateinit var mrukFeature: MRUKFeature
  private var currentRoomTextView: TextView? = null
  private var procMeshSpawner: AnchorProceduralMesh? = null
  private var globalMeshSpawner: AnchorProceduralMesh? = null
  private lateinit var roomOutlineMaterial: SceneMaterial
  private var showUiPanel = false
  private val panelId = 2
  private val arrowEntities: MutableList<Entity> = mutableListOf()
  private lateinit var updateRaycastSystem: UpdateRaycastSystem

  private val sampleDescription =
      "This sample showcases raycasting from your right controller to the scene." +
          " Use your captured scene or load an example from a json file." +
          " You can toggle between showing the first or all hits."

  override fun registerFeatures(): List<SpatialFeature> {
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

    updateRaycastSystem = UpdateRaycastSystem(mrukFeature, arrowEntities)
    systemManager.registerSystem(updateRaycastSystem)

    scene.enablePassthrough(true)

    roomOutlineMaterial =
        SceneMaterial.custom(
                "data/shaders/custom/outline",
                arrayOf<SceneMaterialAttribute>(
                    SceneMaterialAttribute("roughnessMetallicUnlit", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("minAlphaAlphaCutoff", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("stereoParams", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("emissiveFactor", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("albedoFactor", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("albedoUVTransformM00", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("albedoUVTransformM10", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "roughnessMetallicUVTransformM00", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "roughnessMetallicUVTransformM10", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "emissiveMetallicUVTransformM00", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "emissiveMetallicUVTransformM10", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "occlusionMetallicUVTransformM00", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "occlusionMetallicUVTransformM10", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "normalMetallicUVTransformM00", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute(
                        "normalMetallicUVTransformM10", SceneMaterialDataType.Vector4),
                ),
            )
            .apply {
              setAttribute("albedoUVTransformM00", Vector4(1f, 0f, 0f, 0f))
              setAttribute("albedoUVTransformM10", Vector4(0f, 1f, 0f, 0f))
              setAttribute("stereoParams", Vector4(0f, 0f, 1f, 1f))
            }
    roomOutlineMaterial.setSidedness(MaterialSidedness.DOUBLE_SIDED)
    roomOutlineMaterial.setBlendMode(BlendMode.TRANSLUCENT)
    roomOutlineMaterial.setDepthWrite(DepthWrite.DISABLE)

    setProcMeshVisibility(true)

    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_PERMISSION_USE_SCENE)
    } else {
      mrukFeature.loadSceneFromDevice()
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
        panelId, R.layout.ui_raycast_menu, Transform(), Visible(showUiPanel), Grabbable())
  }

  private fun setProcMeshVisibility(visible: Boolean) {
    procMeshSpawner?.destroy()
    procMeshSpawner = null
    if (visible) {
      procMeshSpawner =
          AnchorProceduralMesh(
              mrukFeature,
              mapOf(
                  MRUKLabel.TABLE to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.SCREEN to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.LAMP to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.OTHER to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.COUCH to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.PLANT to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.STORAGE to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.BED to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.FLOOR to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.WALL_FACE to AnchorProceduralMeshConfig(roomOutlineMaterial, true),
                  MRUKLabel.CEILING to AnchorProceduralMeshConfig(roomOutlineMaterial, true)),
              MRUKSpawnMode.CURRENT_ROOM_ONLY,
              MRUKWallTexCoordModeU.STRETCH_SECTION,
              MRUKWallTexCoordModeV.STRETCH,
              MRUKAnchorTexCoordMode.STRETCH,
          )
    }
  }

  private fun setGlobalMeshVisibility(visible: Boolean) {
    globalMeshSpawner?.destroy()
    globalMeshSpawner = null

    if (visible) {
      val wallMaterial = Material().apply { baseTextureAndroidResourceId = R.drawable.wall1 }
      globalMeshSpawner =
          AnchorProceduralMesh(
              mrukFeature,
              mapOf(MRUKLabel.GLOBAL_MESH to AnchorProceduralMeshConfig(wallMaterial, false)))
    }
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.ui_raycast_menu) {
          config {
            height = 0.5f
            width = 0.5f
            fractionOfScreen = 1f
            layoutDpi = 500
          }
          panel {
            requireNotNull(rootView)

            currentRoomTextView = rootView?.findViewById(R.id.mruk_current_room)

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

            rootView?.findViewById<TextView>(R.id.mruk_sample_description)?.text = sampleDescription

            val loadSceneFromDeviceButton =
                rootView?.findViewById<Button>(R.id.load_scene_from_device)
            loadSceneFromDeviceButton?.setOnClickListener { mrukFeature.loadSceneFromDevice() }

            val launchSceneCaptureButton = rootView?.findViewById<Button>(R.id.launch_scene_capture)
            launchSceneCaptureButton?.setOnClickListener { mrukFeature.requestSceneCapture() }

            val allHitsRadioButton = rootView?.findViewById<RadioButton>(R.id.raycast_hits_all)
            allHitsRadioButton?.setOnCheckedChangeListener { _, isChecked ->
              updateRaycastSystem.showAllHits = isChecked
            }

            val singleHitRadioButton = rootView?.findViewById<RadioButton>(R.id.raycast_hits_single)
            singleHitRadioButton?.setOnCheckedChangeListener { _, isChecked ->
              updateRaycastSystem.showAllHits = !isChecked
            }

            val checkBoxSceneObjects = rootView?.findViewById<CheckBox>(R.id.checkbox_scene_objects)
            checkBoxSceneObjects?.setOnCheckedChangeListener { _, isChecked ->
              setProcMeshVisibility(isChecked)
            }

            val checkBoxGlobalMesh = rootView?.findViewById<CheckBox>(R.id.checkbox_global_mesh)
            checkBoxGlobalMesh?.setOnCheckedChangeListener { _, isChecked ->
              setGlobalMeshVisibility(isChecked)
            }

            val clearSceneButton = rootView?.findViewById<Button>(R.id.clear_scene)
            clearSceneButton?.setOnClickListener { mrukFeature.clearRooms() }

            val closeSampleButton = rootView?.findViewById<Button>(R.id.close_sample)
            closeSampleButton?.setOnClickListener {
              returnTo2DActivity(
                  this@RaycastSampleActivity,
                  applicationContext,
                  MrukSampleStartMenuActivity::class.java)
            }
          }
        })
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
        mrukFeature.loadSceneFromDevice()
      } else {
        Log.i(TAG, "Use scene permission was DENIED! Loading fallback scene")
        val item = resources.getStringArray(R.array.json_rooms_array).getOrNull(0)
        val file = applicationContext.assets.open("${item}.json")
        val text = file.bufferedReader().use { reader -> reader.readText() }
        mrukFeature.loadSceneFromJsonString(text)
      }
    }
  }

  private fun recenterUiPanelPosition(): Boolean {
    val head = getHmd(systemManager) ?: return false
    val headPose = head.tryGetComponent<Transform>()?.transform
    if (headPose == null || headPose == Pose()) {
      return false
    }
    val forward = headPose.q * Vector3(0f, 0f, 1f)
    forward.y = 0f
    headPose.q = Quaternion.lookRotation(forward)
    val uiEntity = Entity(panelId)
    // Rotate it to face away from the hmd
    headPose.q *= Quaternion(20f, 0f, 0f)
    // Bring it away from the hmd
    headPose.t += headPose.q * Vector3(0f, 0f, 0.8f)
    uiEntity.setComponent(Transform(headPose))
    return true
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

  override fun onRecenter() {
    super.onRecenter()
    recenterUiPanelPosition()
  }

  override fun onDestroy() {
    procMeshSpawner?.destroy()

    super.onDestroy()
  }

  companion object {
    const val TAG: String = "MrukRaycastSample"
    const val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    const val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1
  }
}
