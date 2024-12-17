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
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.AnchorProceduralMesh
import com.meta.spatial.mruk.AnchorProceduralMeshConfig
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.runtime.AlphaMode
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Color4
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
  private lateinit var procMeshSpawner: AnchorProceduralMesh
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

    // Basic material to show outlines of objects from the scene
    val material =
        Material().apply {
          alphaMode = AlphaMode.TRANSLUCENT.ordinal
          baseColor = Color4(0.6f, 0.6f, 0.8f, 0.7f)
          unlit = false
        }
    val floorMaterial =
        Material().apply { baseTextureAndroidResourceId = R.drawable.carpet_texture }

    procMeshSpawner =
        AnchorProceduralMesh(
            mrukFeature,
            mapOf(
                MRUKLabel.TABLE to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.SCREEN to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.LAMP to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.OTHER to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.COUCH to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.PLANT to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.STORAGE to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.BED to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.FLOOR to AnchorProceduralMeshConfig(floorMaterial, true),
                MRUKLabel.WALL_FACE to AnchorProceduralMeshConfig(material, true),
                MRUKLabel.CEILING to AnchorProceduralMeshConfig(material, true)))

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

            val showAllHitsButton = rootView?.findViewById<Button>(R.id.toggle_all_hits)
            showAllHitsButton?.setOnClickListener {
              updateRaycastSystem.showAllHits = !updateRaycastSystem.showAllHits
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
    procMeshSpawner.destroy()

    super.onDestroy()
  }

  companion object {
    const val TAG: String = "MrukRaycastSample"
    const val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    const val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1
  }
}
