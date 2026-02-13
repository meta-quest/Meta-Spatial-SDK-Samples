/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.object3dsampleisdk

import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.ComposeViewPanelRegistration
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.isdk.IsdkGrabbable
import com.meta.spatial.isdk.IsdkInputListenerSystem
import com.meta.spatial.isdk.IsdkPanelResize
import com.meta.spatial.isdk.ResizeMode
import com.meta.spatial.physics.Physics
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.physics.PhysicsState
import com.meta.spatial.physics.PhysicsWorldBounds
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.PointerEventType
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.runtime.SemanticType
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.LayoutXMLPanelRegistration
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.vr.VRFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Object3DSampleIsdkActivity : AppSystemActivity() {

  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var gltfxEntity: Entity? = null
  private var passthroughEnabled = false

  private val physicsStates = HashMap<Entity, PhysicsState>()

  private var robot: Entity? = null
  private var drone: Entity? = null
  private var plant: Entity? = null
  private var deskLamp: Entity? = null
  private var easyChair: Entity? = null
  private var sculpture: Entity? = null
  private var skybox: Entity? = null

  override fun registerFeatures(): List<SpatialFeature> {
    val features =
        mutableListOf<SpatialFeature>(
            PhysicsFeature(
                spatial,
                useGrabbablePhysics = false,
                worldBounds = PhysicsWorldBounds(minY = -100.0f),
            ),
            VRFeature(this),
            ComposeFeature(),
        )
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
      features.add(DataModelInspectorFeature(spatial, this.componentManager))
    }
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    loadGLXF { composition ->
      robot = composition.getNodeByName("robot").entity
      drone = composition.getNodeByName("drone").entity
      plant = composition.getNodeByName("plant").entity
      deskLamp = composition.getNodeByName("deskLamp").entity
      easyChair = composition.getNodeByName("easyChair").entity
      sculpture = composition.getNodeByName("sculpture").entity

      // set the environment to unlit
      val environmentEntity: Entity? = composition.getNodeByName("Environment").entity
      val environmentMesh = environmentEntity?.getComponent<Mesh>()
      environmentMesh?.defaultShaderOverride = SceneMaterial.UNLIT_SHADER
      environmentEntity?.setComponent(environmentMesh!!)
    }

    // Set up ISDK input listener
    //
    // This performs two major functions:
    // 1. Hover affordance - indicate objects are hovered by slightly scaling them up in size
    // 2. Handle object release - notify physics when objects are released
    systemManager
        .tryFindSystem<IsdkInputListenerSystem>()
        ?.setInputListener(
            object : InputListener {
              val selectCounts: HashMap<Long, Int> = HashMap<Long, Int>()

              // we are only interested in grabbing physics objects
              fun isGrabbablePhysicsObject(ent: Entity): Boolean {
                return ent.hasComponent<IsdkGrabbable>() && ent.hasComponent<Physics>()
              }

              override fun onPointerEvent(
                  receiver: SceneObject,
                  hitInfo: HitInfo,
                  type: Int,
                  sourceOfInput: Entity,
                  scrollInfo: Vector2,
                  semanticType: Int,
              ) {
                super.onPointerEvent(
                    receiver,
                    hitInfo,
                    type,
                    sourceOfInput,
                    scrollInfo,
                    semanticType,
                )

                // We only want to process "grab" events
                if (semanticType != SemanticType.Grab.id) {
                  return
                }

                when (type) {
                  PointerEventType.Hover.id -> {
                    var ent = receiver.entity ?: return
                    if (isGrabbablePhysicsObject(ent)) {
                      // Slighly increase the scale to visually indicate object is hovered
                      val originalScale = ent.getComponent<Scale>().scale
                      val newScale = originalScale + Vector3(0.05f)
                      ent.setComponent(Scale(newScale))
                    }
                  }
                  PointerEventType.Unhover.id -> {
                    var ent = receiver.entity ?: return
                    if (isGrabbablePhysicsObject(ent)) {
                      // Return back to normal scale
                      val originalScale = ent.getComponent<Scale>().scale
                      val newScale = originalScale - Vector3(0.05f)
                      ent.setComponent(Scale(newScale))
                    }
                  }
                  PointerEventType.Select.id -> {
                    var ent = receiver.entity ?: return
                    if (isGrabbablePhysicsObject(ent)) {
                      // Keeping track of selects, to know when an object is actually released
                      selectCounts[ent.id] = (selectCounts[ent.id] ?: 0) + 1

                      if (selectCounts[ent.id] == 1) {
                        val physics = ent.getComponent<Physics>()
                        physicsStates[ent] = physics.state
                        physics.state = PhysicsState.KINEMATIC
                        ent.setComponent(physics)
                        physics.recycle()
                      }
                    }
                  }
                  PointerEventType.Unselect.id -> {
                    var ent = receiver.entity ?: return
                    if (isGrabbablePhysicsObject(ent)) {
                      if (selectCounts.containsKey(ent.id) && selectCounts[ent.id]!! == 1) {
                        // notify physics it should control this object again
                        // Note: if we do not set physics.state, the object will drop straight down
                        // instead of carrying forward with momentum
                        val physics = ent.getComponent<Physics>()
                        physics.state = physicsStates.remove(ent) ?: PhysicsState.DYNAMIC
                        ent.setComponent(physics)
                        physics.recycle()
                      }
                      selectCounts[ent.id] = (selectCounts[ent.id] ?: 0) - 1
                    }
                  }
                  else -> {} // No-op for other event types
                }
              }
            }
        )
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // set the reference space to enable recentering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f,
    )
    scene.updateIBLEnvironment("environment.env")

    scene.setViewOrigin(0f, 0.0f, 0.0f, 0.0f)

    skybox =
        Entity.create(
            listOf(
                Mesh("mesh://skybox".toUri(), hittable = MeshCollision.NoCollision),
                Material().apply {
                  baseTextureAndroidResourceId = R.drawable.skydome
                  unlit = true
                },
                Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
            )
        )

    Entity.create(
        listOf(
            Panel(R.id.scroll_panel),
            Transform(
                Pose(Vector3(x = -0.5f, y = 1f, z = 0.6f), Quaternion.fromEuler(0f, -45f, 0f))
            ),
            Grabbable(),
            IsdkPanelResize(resizeMode = ResizeMode.Relayout),
        )
    )
    Entity.create(
        listOf(
            Panel(R.id.scroll_panel),
            Transform(Pose(Vector3(x = 0.5f, y = 1f, z = 0.6f), Quaternion.fromEuler(0f, 45f, 0f))),
            Grabbable(),
            IsdkPanelResize(),
        )
    )

    // uncomment to see the physics debug lines
    // spatial.enablePhysicsDebugLines(true)
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        ComposeViewPanelRegistration(
            R.id.library_panel,
            composeViewCreator = { _, context ->
              ComposeView(context).apply {
                setContent {
                  ObjectLibraryPanel(
                      robot!!,
                      drone!!,
                      plant!!,
                      deskLamp!!,
                      easyChair!!,
                      sculpture!!,
                  )
                }
              }
            },
            settingsCreator = {
              UIPanelSettings(
                  shape = QuadShapeOptions(width = PANEL_WIDTH, height = PANEL_HEIGHT),
                  style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
                  display = DpPerMeterDisplayOptions(),
              )
            },
        ),
        LayoutXMLPanelRegistration(
            R.id.scroll_panel,
            layoutIdCreator = { R.layout.scrolling },
            settingsCreator = {
              UIPanelSettings(shape = QuadShapeOptions(width = 0.3375f, height = 0.6f))
            },
        ),
    )
  }

  private fun loadGLXF(onLoaded: ((GLXFInfo) -> Unit) = {}): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          "apk:///scenes/Composition.glxf".toUri(),
          rootEntity = gltfxEntity!!,
          onLoaded = onLoaded,
      )
    }
  }
}
