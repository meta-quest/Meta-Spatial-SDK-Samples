/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.qr_code_scanner

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import com.meta.spatial.core.Entity
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKStartTrackerResult
import com.meta.spatial.mruk.Tracker
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.samples.mruksample.Menu
import com.meta.spatial.samples.mruksample.MrukSampleStartMenuActivity
import com.meta.spatial.samples.mruksample.R
import com.meta.spatial.samples.mruksample.TargetScale
import com.meta.spatial.samples.mruksample.TransformParentFollow
import com.meta.spatial.samples.mruksample.common.MrukInputSystem
import com.meta.spatial.samples.mruksample.common.UIPositionSystem
import com.meta.spatial.samples.mruksample.common.getHmd
import com.meta.spatial.samples.mruksample.common.recenterElementInView
import com.meta.spatial.samples.mruksample.common.returnTo2DActivity
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.VRFeature
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Activity that demonstrates QR code scanning functionality using MRUK (Mixed Reality Understanding
 * Kit). This sample shows how to start/stop QR code trackers and display scanned content in web
 * panels.
 */
class QrCodeScannerSampleActivity : AppSystemActivity() {
  private lateinit var mrukFeature: MRUKFeature
  private var showUiPanel = false
  private val panelId = 2
  private var startStopTrackerButton: Button? = null
  private var trackerRunning = false

  override fun registerFeatures(): List<SpatialFeature> {
    mrukFeature = MRUKFeature(this, systemManager)
    return listOf(VRFeature(this), mrukFeature)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    componentManager.registerComponent<Menu>(Menu)
    componentManager.registerComponent<TargetScale>(TargetScale)
    componentManager.registerComponent<TransformParentFollow>(TransformParentFollow)
    systemManager.registerSystem(MenuSpawner())
    systemManager.registerSystem(MenuPlacementSystem())
    systemManager.registerSystem(UIPositionSystem(::setUIPanelVisibility))
    systemManager.registerSystem(MrukInputSystem(::toggleUiPanelVisibility))

    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {
      // Request the scene permission if it hasn't already been granted. Make sure to hook into
      // onRequestPermissionsResult() to try to load the scene again.
      Log.i(TAG, "Scene permission has not been granted, requesting $PERMISSION_USE_SCENE")
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_PERMISSION_USE_SCENE)
    } else {
      startTracker()
    }
  }

  override fun onSceneReady() {
    super.onSceneReady()

    scene.enablePassthrough(true)

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(0.1f, 0.1f, 0.1f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.2f,
    )
    scene.updateIBLEnvironment("environment.env")

    Entity.createPanelEntity(
        panelId,
        R.layout.ui_qrcode_scanner_menu,
        Transform(),
        Visible(showUiPanel),
        Grabbable(),
    )
  }

  override fun onRecenter(isUserInitiated: Boolean) {
    super.onRecenter(isUserInitiated)
    recenterElementInView(getHmd(systemManager), Entity(panelId))
  }

  /**
   * Toggles the visibility of the UI panel. This function is called by the input system when the
   * user triggers the panel toggle.
   */
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

  private fun isValidUrl(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches()
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.ui_qrcode_scanner) { entity ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layerConfig = LayerConfig()
            width = PANEL_WIDTH
            height = PANEL_HEIGHT
          }
          panel {
            val webView = rootView?.findViewById<WebView>(R.id.web_view) ?: return@panel

            webView.webViewClient =
                object : WebViewClient() {
                  override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    entity.setComponent(TargetScale(SMALL_SCALE))
                  }
                }

            val url = entity.getComponent<Menu>().url
            if (isValidUrl(url)) {
              webView.loadUrl(url)
            } else {
              val escaped = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
              webView.loadUrl("https://www.google.com/search?q=$escaped")
            }

            webView.setOnHoverListener { _, event ->
              when (event.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                  entity.setComponent(TargetScale(1.0f))
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                  entity.setComponent(TargetScale(SMALL_SCALE))
                }
              }
              true
            }
          }
        },
        PanelRegistration(R.layout.ui_qrcode_scanner_menu) { _ ->
          config {
            includeGlass = false
            layerConfig = LayerConfig()
            width = 0.5f
            height = 0.5f
          }
          panel {
            val closeSampleButton = rootView?.findViewById<Button>(R.id.close_sample)
            closeSampleButton?.setOnClickListener {
              returnTo2DActivity(
                  this@QrCodeScannerSampleActivity,
                  applicationContext,
                  MrukSampleStartMenuActivity::class.java,
              )
            }

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
        },
    )
  }

  /**
   * Starts the QR code tracker. This function configures and starts the MRUK QR code tracking
   * functionality. Updates the UI button state based on the tracker start result.
   */
  private fun startTracker() {
    mrukFeature.configureTrackers(setOf(Tracker.QrCode)).whenComplete { result, _ ->
      val success = result == MRUKStartTrackerResult.SUCCESS
      updateStartStopTrackerButton(success)
    }
  }

  /** Stops all active trackers and updates the UI button state. */
  private fun stopTrackers() {
    mrukFeature.stopTrackers()
    updateStartStopTrackerButton(false)
  }

  /**
   * Updates the start/stop tracker button text and internal state based on tracker running status.
   *
   * @param running true if the tracker is running, false otherwise
   */
  private fun updateStartStopTrackerButton(running: Boolean) {
    startStopTrackerButton?.text = if (running) STOP_TRACKER_TEXT else START_TRACKER_TEXT
    trackerRunning = running
  }

  override fun onSpatialShutdown() {
    stopTrackers()
    super.onSpatialShutdown()
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
        startTracker()
      } else {
        Log.i(TAG, "Use scene permission was DENIED!")
      }
    }
  }

  companion object {
    private const val TAG = "QrCodeScannerSampleActivity"

    // Permission constants
    private const val PERMISSION_USE_SCENE = "com.oculus.permission.USE_SCENE"
    private const val REQUEST_CODE_PERMISSION_USE_SCENE = 1

    // Panel constants
    private const val PANEL_WIDTH = 0.3f
    internal const val PANEL_HEIGHT = 0.5f
    private const val SMALL_SCALE = 0.2f

    // Button text constants
    private const val STOP_TRACKER_TEXT = "Stop Tracker"
    private const val START_TRACKER_TEXT = "Start Tracker"
  }
}
