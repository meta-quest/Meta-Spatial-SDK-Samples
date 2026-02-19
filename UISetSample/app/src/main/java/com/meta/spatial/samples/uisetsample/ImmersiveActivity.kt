/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample

import android.os.Bundle
import androidx.core.net.toUri
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.samples.uisetsample.ecs.system.LookAtHeadSystem
import com.meta.spatial.samples.uisetsample.panel.GLXFConstants
import com.meta.spatial.samples.uisetsample.panel.PanelRegistry
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

class ImmersiveActivity : AppSystemActivity() {

  private val panelRegistry = PanelRegistry()

  private val activityScope = CoroutineScope(Dispatchers.Main)

  override fun registerFeatures(): List<SpatialFeature> {
    val features = mutableListOf(VRFeature(this), ComposeFeature())
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
    }
    return features
  }

  override fun registerPanels(): List<PanelRegistration> {
    return panelRegistry.initialPanelRegistration()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Disable Locomotion
    systemManager.unregisterSystem<LocomotionSystem>()
    // Register elements
    registerComponents()
    registerSystems()
    // Inflate scene
    loadGLXF()
  }

  override fun onSceneReady() {
    super.onSceneReady()
    // Position the user when they launch into the app
    scene.setViewOrigin(0.0f, 0.0f, -1.0f, 0.0f)
    // Enable better panel rendering (default)
    scene.enableHolePunching(true)
    // Set Mixed Reality passthrough mode
    scene.enablePassthrough(true)
    // Support re-centering the panels in front of the user
    scene.setReferenceSpace(ReferenceSpace.LOCAL)
  }

  private fun loadGLXF() {
    activityScope.launch {
      val glxInfo =
          glXFManager.inflateGLXF(
              GLXFConstants.URI_STRING.toUri(),
              rootEntity = Entity.create(),
              keyName = GLXFConstants.COMPOSITION_NAME,
          )
      if (BuildConfig.DEBUG) {
        glxInfo?.nodes?.forEach { Timber.i("Node name: ${it.name}, Entity Id: ${it.entity.id}") }
      }
    }
  }

  private fun registerComponents() {
    componentManager.registerComponent<LookAtHead>(LookAtHead.Companion)
  }

  private fun registerSystems() {
    systemManager.registerSystem(LookAtHeadSystem())
  }

  override fun onDestroy() {
    super.onDestroy()
    activityScope.cancel()
  }
}
