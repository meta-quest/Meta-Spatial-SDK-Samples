/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.object3dsample

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
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.physics.Physics
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.physics.PhysicsMaterial
import com.meta.spatial.physics.PhysicsState
import com.meta.spatial.physics.PhysicsWorldBounds
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.panel.style
import com.meta.spatial.toolkit.Animated
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
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

class Object3DSampleActivity : AppSystemActivity() {

  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var gltfxEntity: Entity? = null
  private var passthroughEnabled = false

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
            PhysicsFeature(spatial, worldBounds = PhysicsWorldBounds(minY = -100.0f)),
            VRFeature(this),
        )
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
      features.add(HotReloadFeature(this))
      features.add(OVRMetricsFeature(this, OVRMetricsDataModel() { numberOfMeshes() }))
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
                Mesh(Uri.parse("mesh://skybox"), hittable = MeshCollision.NoCollision),
                Material().apply {
                  baseTextureAndroidResourceId = R.drawable.skydome
                  unlit = true
                },
                Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
            )
        )

    // uncomment to see the physics debug lines
    // spatial.enablePhysicsDebugLines(true)
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.about) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            width = 2.0f
            height = 1.5f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
        },
        PanelRegistration(R.layout.library_panel) { ent ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            width = 1.3f
            height = 1.0f
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
                button2,
                drone,
                isAnimated = true,
                dimensions = Vector3(0.106f, 0.07f, 0.22f),
            )
            setUpButton(button3, plant, dimensions = Vector3(0.09f, 0.09f, 0.09f))
            setUpButton(button4, deskLamp, dimensions = Vector3(0.2f, 0.34f, 0.06f))
            setUpButton(button5, easyChair, dimensions = Vector3(0.3f, 0.34f, 0.3f))
            setUpButton(button6, sculpture, dimensions = Vector3(0.23f, 0.17f, 0.17f))
          }
        },
    )
  }

  private fun setUpButton(
      button: ImageView?,
      entity: Entity? = null,
      collisionMesh: String = "box",
      isAnimated: Boolean = false,
      dimensions: Vector3 = Vector3(0.1f, 0.1f, 0.1f),
  ) {

    val scale: Vector3 = entity?.getComponent<Scale>()?.scale?.copy() ?: Vector3(1f, 1f, 1f)
    val glb = entity?.getComponent<Mesh>()?.mesh?.toString()
    val createObject =
        View.OnClickListener {
          val objModel =
              Entity.create(
                  listOf(
                      Mesh(
                          mesh = Uri.parse(glb),
                          defaultShaderOverride = SceneMaterial.PHYSICALLY_BASED_SHADER,
                      ),
                      Grabbable(type = GrabbableType.PIVOT_Y),
                      Scale(scale),
                      Physics(
                              shape = collisionMesh,
                              density = 0.1f,
                              state = PhysicsState.DYNAMIC,
                              dimensions = dimensions,
                          )
                          .applyMaterial(PhysicsMaterial.WOOD),
                      Transform(Pose(Vector3(0f, 1.2f, 2.1f), Quaternion(0f, 180f, 0f))),
                  )
              )

          scaleUp(objModel, scale)

          // add animation
          if (isAnimated) {
            objModel.setComponent(
                Animated(
                    startTime = System.currentTimeMillis(),
                    playbackState = PlaybackState.PLAYING,
                    playbackType = PlaybackType.LOOP,
                )
            )
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

  private fun loadGLXF(onLoaded: ((GLXFInfo) -> Unit) = {}): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("apk:///scenes/Composition.glxf"),
          rootEntity = gltfxEntity!!,
          onLoaded = onLoaded,
      )
    }
  }
}
