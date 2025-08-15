/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.keyboard_tracker

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SpatialSDKExperimentalAPI
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKSceneEventListener
import com.meta.spatial.mruk.MRUKStartTrackerResult
import com.meta.spatial.mruk.Tracker
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.samples.mruksample.MrukSampleStartMenuActivity
import com.meta.spatial.samples.mruksample.R
import com.meta.spatial.samples.mruksample.common.MrukInputSystem
import com.meta.spatial.samples.mruksample.common.UIPositionSystem
import com.meta.spatial.samples.mruksample.common.getHmd
import com.meta.spatial.samples.mruksample.common.recenterElementInView
import com.meta.spatial.samples.mruksample.common.returnTo2DActivity
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature

// default activity
class KeyboardTrackerSampleActivity : AppSystemActivity(), MRUKSceneEventListener {
  private lateinit var mrukFeature: MRUKFeature
  private var scenePermissionTextView: TextView? = null
  private var keyboardTrackingTextView: TextView? = null
  private var startStopTrackerButton: Button? = null

  private var isScenePermissionGiven = false
  private var isKeyboardTrackingEnabled = false
  private var trackerRunning = false

  private var showUiPanel = false

  private val panelId = 2

  override fun registerFeatures(): List<SpatialFeature> {
    mrukFeature = MRUKFeature(this, systemManager)
    return listOf(VRFeature(this), mrukFeature)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    systemManager.findSystem<LocomotionSystem>().enableLocomotion(false)
    systemManager.registerSystem(UIPositionSystem(::setUIPanelVisibility))
    systemManager.registerSystem(MrukInputSystem(::toggleUiPanelVisibility))
    systemManager.registerSystem(SpawnKeyboardSystem(mrukFeature))

    scene.enablePassthrough(true)

    // Request the user to give the app scene permission if not already granted.
    // Otherwise, the app will not be able to access the device scene data.

    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {
      setScenePermissionText(false)
      Log.i(TAG, "Scene permission has not been granted, requesting $PERMISSION_USE_SCENE")
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_PERMISSION_USE_SCENE)
    } else {
      setScenePermissionText(true)
      startTracker()
    }
  }

  private fun setScenePermissionText(enabled: Boolean) {
    isScenePermissionGiven = enabled
    scenePermissionTextView?.text = "Scene Permission Given: ${if (enabled) "YES" else "NO"}"
  }

  private fun setKeyboardTrackerSettingText(enabled: Boolean) {
    isKeyboardTrackingEnabled = enabled
    keyboardTrackingTextView?.text = "Keyboard Tracker Enabled: ${if (enabled) "YES" else "NO"}"
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
        setScenePermissionText(true)
      } else {
        Log.i(TAG, "Use scene permission was DENIED!")
        setScenePermissionText(false)
      }
      startTracker()
    }
  }

  override fun onSceneReady() {
    super.onSceneReady()

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f)
    scene.updateIBLEnvironment("environment.env")

    Entity.create(
        listOf(
            Mesh(Uri.parse("mesh://skybox")),
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.skydome
              unlit = true // Prevent scene lighting from affecting the skybox
            },
            Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f)))))

    Entity.createPanelEntity(
        panelId, R.layout.ui_keyboard_tracker_menu, Transform(), Visible(showUiPanel), Grabbable())
  }

  override fun onSpatialShutdown() {
    stopTracker()
    super.onSpatialShutdown()
  }

  private fun stopTracker() {
    mrukFeature.stopTrackers()
    updateStartStopTrackerButton(false)
  }

  @OptIn(SpatialSDKExperimentalAPI::class)
  private fun startTracker() {
    mrukFeature.configureTrackers(setOf(Tracker.Keyboard)).whenComplete {
        result: MRUKStartTrackerResult,
        _ ->
      if (result == MRUKStartTrackerResult.SUCCESS) {
        setKeyboardTrackerSettingText(true)
        updateStartStopTrackerButton(true)
      } else {
        setKeyboardTrackerSettingText(false)
        updateStartStopTrackerButton(false)
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

  private fun updateStartStopTrackerButton(running: Boolean) {
    if (running) {
      startStopTrackerButton?.setText("Stop Tracker")
    } else {
      startStopTrackerButton?.setText("Start Tracker")
    }

    trackerRunning = running
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.ui_keyboard_tracker_menu) {
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
            val closeSampleButton = rootView?.findViewById<Button>(R.id.close_sample)
            closeSampleButton?.setOnClickListener {
              returnTo2DActivity(
                  this@KeyboardTrackerSampleActivity,
                  applicationContext,
                  MrukSampleStartMenuActivity::class.java)
            }

            scenePermissionTextView = rootView?.findViewById<TextView>(R.id.scene_permission_text)
            setScenePermissionText(isScenePermissionGiven)
            keyboardTrackingTextView = rootView?.findViewById<TextView>(R.id.keyboard_tracking_text)
            setKeyboardTrackerSettingText(isKeyboardTrackingEnabled)

            startStopTrackerButton = rootView?.findViewById<Button>(R.id.start_stop_tracker)
            startStopTrackerButton?.setOnClickListener {
              if (trackerRunning) {
                mrukFeature.stopTrackers()
              } else {
                startTracker()
              }
              updateStartStopTrackerButton(!trackerRunning)
            }
            updateStartStopTrackerButton(trackerRunning)
          }
        })
  }

  companion object {
    const val TAG: String = "KeyboardTrackerSampleActivity"
    const val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    const val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1
  }
}
