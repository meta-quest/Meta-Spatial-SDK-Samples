/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.ComposeViewPanelRegistration
import com.meta.spatial.core.Entity
import com.meta.spatial.core.EventArgs
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.physics.Physics
import com.meta.spatial.physics.PhysicsFeature
import com.meta.spatial.physics.PhysicsState
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.GLXFNode
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.vr.VRFeature
import java.io.File
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// default activity
class BallRunActivity : AppSystemActivity() {

  enum class BallRunState {
    READY,
    PLAYING,
    FINISHED,
  }

  private var state_: BallRunState = BallRunState.READY
  public var state: BallRunState
    get() {
      return state_
    }
    set(value) {
      state_ = value
      if (value == BallRunState.READY) {
        reset()
        infoPanel?.entity?.setComponent(Visible(true))
      } else if (value == BallRunState.PLAYING) {
        fireBalls()
        infoPanel?.entity?.setComponent(Visible(false))
      } else if (value == BallRunState.FINISHED) {
        infoPanel?.entity?.setComponent(Visible(true))
      }
    }

  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var balls: List<GLXFNode> = listOf()
  private var startPositions: List<Vector3> = listOf()
  private var infoPanel: GLXFNode? = null
  private var finishCount: Int = 0

  override fun registerFeatures(): List<SpatialFeature> {
    val features =
        mutableListOf<SpatialFeature>(
            PhysicsFeature(spatial, useGrabbablePhysics = false),
            ComposeFeature(),
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
    NetworkedAssetLoader.init(
        File(applicationContext.getCacheDir().canonicalPath),
        OkHttpAssetFetcher(),
    )

    // After definitions, we need to register the systems and components
    componentManager.registerComponent<Trigger>(Trigger.Companion)
    componentManager.registerComponent<TriggerArea>(TriggerArea.Companion)
    componentManager.registerComponent<Button>(Button.Companion)
    componentManager.registerComponent<Spinner>(Spinner.Companion)
    componentManager.registerComponent<UpAndDown>(UpAndDown.Companion)

    systemManager.registerSystem(TriggerSystem())
    systemManager.registerSystem(ButtonSystem())
    systemManager.registerSystem(SpinnerSystem())
    systemManager.registerSystem(UpAndDownSystem())

    loadGLXF { composition -> onCompositionReady(composition) }
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        createSimpleComposePanel(R.id.ui_example, 2.048f, 1.254f) { WelcomePanel() },
        createSimpleComposePanel(R.id.ui_info, 0.64f, 0.158f) { InfoPanel() },
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
    scene.setViewOrigin(0.0f, 0.5f, 0.0f, 0.0f)

    Entity.create(
        listOf(
            Mesh("mesh://skybox".toUri(), hittable = MeshCollision.NoCollision),
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.skydome
              unlit = true // Prevent scene lighting from affecting the skybox
            },
            Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
        )
    )

    // spatial.enablePhysicsDebugLines(true)
  }

  fun onCompositionReady(composition: GLXFInfo) {

    val upDownSystem = systemManager.tryFindSystem<UpAndDownSystem>()
    upDownSystem?.resetEntities()
    // set the environment shader to unlit
    val environmentEntity: Entity? = composition.getNodeByName("Environment").entity
    val environmentMesh = environmentEntity?.getComponent<Mesh>()
    environmentMesh?.defaultShaderOverride = SceneMaterial.UNLIT_SHADER
    environmentEntity?.setComponent(environmentMesh!!)

    val ball0 = composition.getNodeByName("ball0")
    val ball1 = composition.getNodeByName("ball1")
    val ball2 = composition.getNodeByName("ball2")
    val button = composition.getNodeByName("button")
    infoPanel = composition.getNodeByName("InfoPanel")
    balls = listOf(ball0, ball1, ball2)

    // setup button event listener
    button.entity.registerEventListener<EventArgs>("button") { _, _ ->
      if (state == BallRunState.READY) {
        state = BallRunState.PLAYING
      } else if (state == BallRunState.PLAYING) {
        state = BallRunState.READY
      } else if (state == BallRunState.FINISHED) {
        state = BallRunState.READY
      }
    }

    // setup ball event listeners
    balls.forEach { ball ->
      ball.entity.registerEventListener<TriggerEventArgs>("speed_up") { entity, args ->
        shootBall(entity, args.direction, args.value)
      }
      ball.entity.registerEventListener<TriggerEventArgs>("finish") { _, _ ->
        Log.d("ball_run", "Finished ${ball.name}")

        onBallFinish(ball)

        finishCount += 1
        if (finishCount == balls.size) {
          state = BallRunState.READY
        }
      }

      // store the start position of the balls for resetting
      val transform = ball.entity.getComponent<Transform>()
      startPositions += transform.transform.t
    }

    activityScope.launch {
      delay(10L)
      state = BallRunState.READY
    }
  }

  private fun createSimpleComposePanel(
      panelId: Int,
      width: Float,
      height: Float,
      content: @Composable () -> Unit,
  ): ComposeViewPanelRegistration {
    return ComposeViewPanelRegistration(
        panelId,
        composeViewCreator = { _, ctx -> ComposeView(ctx).apply { setContent { content() } } },
        settingsCreator = { _ ->
          UIPanelSettings(
              shape = QuadShapeOptions(width = width, height = height),
              style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
              display = DpPerMeterDisplayOptions(),
          )
        },
    )
  }

  private fun onBallFinish(ball: GLXFNode) {
    val physics = ball.entity.getComponent<Physics>()
    physics.state = PhysicsState.KINEMATIC
    ball.entity.setComponent(physics)

    // reset the ball transform to the start position
    activityScope.launch {
      delay(10L)
      val index = balls.indexOf(ball)
      resetBall(ball, startPositions[index])
    }
  }

  private fun reset() {
    finishCount = 0
    balls.forEachIndexed { index, ball ->

      // stop physics on the balls
      val physics = ball.entity.getComponent<Physics>()
      physics.state = PhysicsState.KINEMATIC
      ball.entity.setComponent(physics)

      // reset the ball transform to the start position
      activityScope.launch {
        delay(10L)
        resetBall(ball, startPositions[index])
      }
    }
  }

  private fun resetBall(ball: GLXFNode, position: Vector3) {

    val transform = ball.entity.getComponent<Transform>()
    transform.transform.t = position
    ball.entity.setComponent(transform)
  }

  private fun fireBalls() {
    val speed = 100f + (Random.nextFloat() * 20f) // random speed between 100 and 120
    balls.forEach { ball -> shootBall(ball.entity, Vector3(1f, 0f, 0f), speed) }
  }

  private fun shootBall(entity: Entity, direction: Vector3, speed: Float) {
    val physics = entity.getComponent<Physics>()
    physics.linearVelocity = Vector3(0f) // reset velocity
    physics.state = PhysicsState.DYNAMIC
    physics.applyForce = direction * Vector3(speed) // apply force at the given direction and speed
    entity.setComponent(physics)
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
