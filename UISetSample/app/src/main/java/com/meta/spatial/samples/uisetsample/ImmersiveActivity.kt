/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample

import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.samples.uisetsample.ecs.system.BatchedPanelCreationSystem
import com.meta.spatial.samples.uisetsample.ecs.system.LookAtHeadSystem
import com.meta.spatial.samples.uisetsample.navigation.NavigationUiItem
import com.meta.spatial.samples.uisetsample.panel.GLXFConstants
import com.meta.spatial.samples.uisetsample.panel.PanelNavigator
import com.meta.spatial.samples.uisetsample.panel.PanelRegistrationIds
import com.meta.spatial.samples.uisetsample.panel.PanelRegistry
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.AvatarSystem
import com.meta.spatial.toolkit.Hittable
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ImmersiveActivity : AppSystemActivity() {

  private val panelRegistry = PanelRegistry()
  private val batchedPanelCreationSystem = BatchedPanelCreationSystem(maxPanelsPerTick = 1)

  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var loadingEntity: Entity? = null

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
  }

  override fun onSceneReady() {
    super.onSceneReady()
    // Set Mixed Reality passthrough mode
    scene.enablePassthrough(true)
    // Support re-centering the panels in front of the user
    scene.setReferenceSpace(ReferenceSpace.LOCAL)

    val avatarSystem = systemManager.findSystem<AvatarSystem>()
    avatarSystem.setShowHands(false)
    avatarSystem.setShowControllers(false)

    val viewerPose = scene.getViewerPose()
    val forward = viewerPose.q * Vector3.Forward
    val flatForward = Vector3(forward.x, 0f, forward.z).normalize()
    val loadingPosition = viewerPose.t + flatForward * LOADING_PANEL_DISTANCE_METERS
    val yaw = Math.toDegrees(Math.atan2(flatForward.x.toDouble(), flatForward.z.toDouble()))
    val loadingRotation = Quaternion(0f, yaw.toFloat(), 0f)
    loadingEntity =
        Entity.create(
            listOf(
                Panel(PanelRegistrationIds.PANEL_LOADING),
                Transform(Pose(loadingPosition, loadingRotation)),
                Hittable(MeshCollision.NoCollision),
            ),
        )

    loadGLXF()
  }

  private fun onPanelLoadingComplete() {
    Log.i(TAG, "All panels loaded; hiding loading screen and showing UI")

    val utilityIds = listOf(
        PanelRegistrationIds.PANEL_NAVIGATOR,
        PanelRegistrationIds.PANEL_DEMO_VIDEO,
        PanelRegistrationIds.PANEL_THEMES,
    )
    val panelQuery = Query.where { has(Panel.id) }
    for (entity in panelQuery.eval()) {
      val id = entity.getComponent<Panel>().panelRegistrationId
      if (id in utilityIds) {
        entity.setComponent(Visible(true))
      }
    }

    val defaultItem = NavigationUiItem.Button
    val allViewIds = NavigationUiItem.entries.flatMap { it.panelRegistrationIds }
    PanelNavigator()
        .setPanelsVisible(
            defaultItem.panelRegistrationIds,
            allViewIds.filter { it !in defaultItem.panelRegistrationIds },
        )

    loadingEntity?.setComponent(Visible(false))
    val avatarSystem = systemManager.findSystem<AvatarSystem>()
    avatarSystem.setShowHands(true)
    avatarSystem.setShowControllers(true)
  }

  private fun loadGLXF() {
    activityScope.launch {
      glXFManager.inflateGLXF(
          GLXFConstants.URI_STRING.toUri(),
          rootEntity = Entity.create(),
          keyName = GLXFConstants.COMPOSITION_NAME,
          onLoaded = { info ->
            val glxfPanelEntities =
                info.nodes
                    .filter { node ->
                      if (node.entity.hasComponent<Panel>()) {
                        node.entity.setComponent(Visible(false))
                        true
                      } else {
                        false
                      }
                    }
                    .map { it.entity }
            batchedPanelCreationSystem.enqueuePanels(
                glxfPanelEntities,
                onComplete = ::onPanelLoadingComplete,
            )
          },
      )
    }
  }

  private fun registerComponents() {
    componentManager.registerComponent<LookAtHead>(LookAtHead.Companion)
  }

  private fun registerSystems() {
    systemManager.registerSystem(LookAtHeadSystem())
    systemManager.registerEarlySystem(batchedPanelCreationSystem)
  }

  override fun onDestroy() {
    activityScope.cancel()
    super.onDestroy()
  }

  companion object {
    private const val TAG = "UISetLoading"
    private const val LOADING_PANEL_DISTANCE_METERS = 1.5f
  }
}
