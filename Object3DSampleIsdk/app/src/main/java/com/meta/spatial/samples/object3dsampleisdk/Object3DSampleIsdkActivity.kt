/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.object3dsampleisdk

import android.animation.ValueAnimator
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.isdk.IsdkGrabbable
import com.meta.spatial.isdk.IsdkInputListenerSystem
import com.meta.spatial.physics.Physics
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.physics.PhysicsMaterial
import com.meta.spatial.physics.PhysicsState
import com.meta.spatial.physics.PhysicsWorldBounds
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.PointerEventType
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.runtime.SemanticType
import com.meta.spatial.toolkit.Animated
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Box
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PlaybackState
import com.meta.spatial.toolkit.PlaybackType
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.vr.VRFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Object3DSampleIsdkActivity : AppSystemActivity() {

  private val activityScope = CoroutineScope(Dispatchers.Main)
  private val glxfKey = "deskScene"
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
                worldBounds = PhysicsWorldBounds(minY = -100.0f)),
            VRFeature(this),
            IsdkFeature(this, spatial, systemManager, BuildConfig.DEBUG))
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
      features.add(DataModelInspectorFeature(spatial, this.componentManager))
    }
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    loadGLXF().invokeOnCompletion {
      val composition = glXFManager.getGLXFInfo(glxfKey)
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

      // TEMP FIX
      environmentEntity?.setComponent(
          Physics(
              shape = "environmentDeskCollisions.gltf",
              state = PhysicsState.STATIC,
              density = 1.0f))
    }

    // Set up ISDK input listener
    //
    // This performs two major functions:
    // 1. Hover affordance - indicate objects are hovered by slightly scaling them up in size
    // 2. Handle object release - notify physics when objects are released
    systemManager
        .findSystem<IsdkInputListenerSystem>()
        .setInputListener(
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
                  semanticType: Int
              ) {
                super.onPointerEvent(
                    receiver, hitInfo, type, sourceOfInput, scrollInfo, semanticType)

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
            })
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // set the reference space to enable recentering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f)
    scene.updateIBLEnvironment("environment.env")

    scene.setViewOrigin(0f, 0.0f, 0.0f, 0.0f)

    skybox =
        Entity.create(
            listOf(
                Mesh(Uri.parse("mesh://skybox")),
                Material().apply {
                  baseTextureAndroidResourceId = R.drawable.skydome
                  unlit = true
                },
                Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f)))))

    Entity.create(
        listOf(
            Panel(R.layout.scrolling),
            Transform(Pose(Vector3(x = -0.3f, y = 1f, z = 0.2f))),
            Grabbable()))

    // uncomment to see the physics debug lines
    // spatial.enablePhysicsDebugLines(true)
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.about) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
          }
        },
        PanelRegistration(R.layout.library_panel) { ent ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
          }
          panel {
            val button1 = rootView?.findViewById<ImageView>(R.id.button1)
            val button2 = rootView?.findViewById<ImageView>(R.id.button2)
            val button3 = rootView?.findViewById<ImageView>(R.id.button3)
            val button4 = rootView?.findViewById<ImageView>(R.id.button4)
            val button5 = rootView?.findViewById<ImageView>(R.id.button5)
            val button6 = rootView?.findViewById<ImageView>(R.id.button6)

            // dimensions are from scaled 1m cube
            // keep in mind that bounding boxes are centered on the object origin
            setUpButton(button1, robot, dimensions = Vector3(0.11f, 0.21f, 0.08f))
            setUpButton(
                button2, drone, isAnimated = true, dimensions = Vector3(0.106f, 0.07f, 0.22f))
            setUpButton(button3, plant, dimensions = Vector3(0.09f, 0.09f, 0.09f))
            setUpButton(button4, deskLamp, dimensions = Vector3(0.2f, 0.34f, 0.06f))
            setUpButton(button5, easyChair, dimensions = Vector3(0.3f, 0.34f, 0.3f))
            setUpButton(button6, sculpture, dimensions = Vector3(0.23f, 0.17f, 0.17f))
          }
        },
        PanelRegistration(R.layout.scrolling) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            height = 0.6f
            width = 0.3375f
          }
        })
  }

  private fun setUpButton(
      button: ImageView?,
      entity: Entity? = null,
      collisionMesh: String = "box",
      isAnimated: Boolean = false,
      dimensions: Vector3 = Vector3(0.1f, 0.1f, 0.1f)
  ) {

    val scale = entity?.getComponent<Scale>() ?: Scale(Vector3(1f, 1f, 1f))
    val invScale = Vector3(1.0f / scale.scale.x, 1.0f / scale.scale.y, 1.0f / scale.scale.z)
    val glb = entity?.getComponent<Mesh>()?.mesh?.toString()
    val createObject =
        View.OnClickListener {
          val objModel =
              Entity.create(
                  listOf(
                      Mesh(
                          mesh = Uri.parse(glb),
                          defaultShaderOverride = SceneMaterial.PHYSICALLY_BASED_SHADER),
                      Grabbable(type = GrabbableType.PIVOT_Y),
                      Box(min = dimensions * invScale * -1.0f, max = dimensions * invScale * 1.0f),
                      scale,
                      Physics(
                              shape = collisionMesh,
                              density = 0.1f,
                              state = PhysicsState.DYNAMIC,
                              dimensions = dimensions)
                          .applyMaterial(PhysicsMaterial.WOOD),
                      Transform(Pose(Vector3(0f, 1.2f, 2.1f), Quaternion(0f, 180f, 0f)))))

          scaleUp(objModel, scale.scale)

          // add animation
          if (isAnimated) {
            objModel.setComponent(
                Animated(
                    startTime = System.currentTimeMillis(),
                    playbackState = PlaybackState.PLAYING,
                    playbackType = PlaybackType.LOOP,
                ))
          }
        }
    button?.setOnClickListener(createObject)
  }

  private fun scaleUp(entity: Entity, scale: Vector3) {
    ValueAnimator.ofFloat(0f, 1f)
        .apply {
          duration = 1000
          interpolator = OvershootInterpolator(1f)
          addUpdateListener { animation ->
            val v = animation.animatedValue as Float
            entity.setComponent(Scale(scale.multiply(v)))
          }
        }
        .start()
  }

  private fun loadGLXF(): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("apk:///scenes/Composition.glxf"),
          rootEntity = gltfxEntity!!,
          keyName = glxfKey)
    }
  }
}
