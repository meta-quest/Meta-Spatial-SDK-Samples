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
import com.meta.spatial.core.SpatialSDKExperimentalAPI
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

  fun isValidUrl(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches()
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.ui_qrcode_scanner) { entity ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layerConfig = LayerConfig()
            width = panelWidth
            height = panelHeight
          }
          panel {
            val webView = rootView?.findViewById<WebView>(R.id.web_view)!!

            webView.webViewClient =
                object : WebViewClient() {
                  override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    entity.setComponent(TargetScale(smallScale))
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
                  entity.setComponent(TargetScale(smallScale))
                }
              }
              true
            }
          }
        },
        PanelRegistration(R.layout.ui_qrcode_scanner_menu) { entity ->
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

  @OptIn(SpatialSDKExperimentalAPI::class)
  fun startTracker() {
    mrukFeature.configureTrackers(setOf(Tracker.QrCode)).whenComplete {
        result: MRUKStartTrackerResult,
        _ ->
      if (result == MRUKStartTrackerResult.SUCCESS) {
        updateStartStopTrackerButton(true)
      } else {
        updateStartStopTrackerButton(false)
      }
    }
  }

  private fun stopTrackers() {
    mrukFeature.stopTrackers()
    updateStartStopTrackerButton(false)
  }

  private fun updateStartStopTrackerButton(running: Boolean) {
    if (running) {
      startStopTrackerButton?.setText("Stop Tracker")
    } else {
      startStopTrackerButton?.setText("Start Tracker")
    }

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
    const val TAG = "QrCodeScannerSampleActivity"
    const val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    const val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1

    const val panelWidth = 0.3f
    const val panelHeight = 0.5f
    const val smallScale = 0.2f
  }
}
