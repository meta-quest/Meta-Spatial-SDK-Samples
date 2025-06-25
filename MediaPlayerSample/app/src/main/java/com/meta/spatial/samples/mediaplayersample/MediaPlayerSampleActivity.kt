/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mediaplayersample

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.webkit.WebView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector4
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.runtime.BlendMode
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.PanelConfigOptions
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.runtime.PanelShapeType
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneAudioAsset
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMaterialAttribute
import com.meta.spatial.runtime.SceneMaterialDataType
import com.meta.spatial.runtime.SceneMesh
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.runtime.panel.material
import com.meta.spatial.runtime.panel.style
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

enum class MovieSceneState {
  VR,
  SURROUND
}

// default activity
class MediaPlayerSampleActivity : AppSystemActivity() {

  var gltfxEntity: Entity? = null
  var videoPanel: Entity? = null
  var environment: Entity? = null
  var skyVideoPanel: Entity? = null

  val activityScope = CoroutineScope(Dispatchers.Main)
  var exoPlayer: ExoPlayer? = null
  var webView: WebView? = null
  val LIGHTS_UP_AMBIENT: Float = 5.0f
  lateinit var locomotionSystem: LocomotionSystem
  var sky: Entity? = null
  var videoTexture: SceneTexture? = null
  var sceneMaterials: Array<SceneMaterial>? = null
  var skyBoxMaterial: SceneMaterial? = null
  var skyPanelVisible: Boolean = false
  var animationScope: CoroutineScope? = null
  var animationJob: Job? = null

  private var mrState_: Boolean = false
  var mrState: Boolean = false
    set(value) {
      mrState_ = value

      // if we are in MR mode then we need to switch out of Surround mode.
      if (!mrState_ && movieState == MovieSceneState.SURROUND) {
        movieState = MovieSceneState.VR
      } else {
        // otherwise just update the state
        movieState = movieState
      }
    }

  var movieState: MovieSceneState = MovieSceneState.VR
    set(value) {
      field = value

      when (value) {
        MovieSceneState.VR -> {

          sky?.setComponent(Visible(!mrState_)) // hide sky
          environment?.setComponent(Visible(!mrState_)) // hide environment
          locomotionSystem.enableLocomotion(!mrState_)
          videoPanel?.setComponents(
              listOf(Grabbable(mrState_), Transform(Pose(Vector3(0f, 1.32f, 4.85f)))))

          // only animate if the sky panel is not visible.
          if (skyPanelVisible) {

            // stop 360 video
            exoPlayer?.stop()

            animationJob =
                animationScope!!.launch {
                  // animate sky box in
                  animateTransitionMask(
                      arrayOf(skyBoxMaterial!!), 1f, 0f, "customParams", 1f, true) {
                        skyVideoPanel?.setComponent(Visible(false))
                      }
                  playRandomSfx()
                  animateTransitionMask(sceneMaterials!!, 1f, 0f, "roughness", 1f, false) // show VR
                  skyPanelVisible = false
                  webView?.setVisibility(View.VISIBLE) // show flat screen
                  videoPanel?.setComponent(Visible(true))
                }
          }
        }
        MovieSceneState.SURROUND -> {
          skyVideoPanel?.setComponent(Visible(true))
          videoPanel?.setComponents(listOf(Grabbable(false), Visible(false))) // hide video panel

          // scene.setViewOrigin(0f, 0f, 0f, 0f) // reset locomotion
          locomotionSystem.enableLocomotion(false)

          // play 360 video.
          exoPlayer?.play()

          // unload webview video and hide.
          webView?.loadUrl("")

          if (!skyPanelVisible) {
            playRandomSfx()
            animationJob =
                animationScope!!.launch {
                  // hide VR
                  animateTransitionMask(sceneMaterials!!, 0f, 1f, "roughness", 1f, true) {
                    environment?.setComponent(Visible(false))
                  }
                  // animate sky box out
                  animateTransitionMask(arrayOf(skyBoxMaterial!!), 0f, 1f, "customParams", 1f)
                  skyPanelVisible = true
                }
          }
        }
      }
    }

  private fun playRandomSfx() {
    val sfxList =
        listOf(
            "sounds/system_transition_mrvr_01.wav",
            "sounds/system_transition_mrvr_02.wav",
            "sounds/system_transition_mrvr_03.wav")

    val asset = SceneAudioAsset.loadLocalFile(sfxList.random())
    scene.playSound(asset, 2.0f)
  }

  private fun requestScenePermissionIfNeeded() {
    val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1
    Log.d(TAG, "requestScenePermissionIfNeeded")

    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission has not been granted, request " + PERMISSION_USE_SCENE)
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_PERMISSION_USE_SCENE)
    }
  }

  override fun registerFeatures(): List<SpatialFeature> {
    val features =
        mutableListOf<SpatialFeature>(
            VRFeature(this), ComposeFeature(), IsdkFeature(this, spatial, systemManager))
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

    locomotionSystem = systemManager.findSystem<LocomotionSystem>()

    // Create a CoroutineScope for squencing the transition animations.
    animationScope = CoroutineScope(Dispatchers.Main)

    requestScenePermissionIfNeeded()

    NetworkedAssetLoader.init(
        File(applicationContext.getCacheDir().canonicalPath), OkHttpAssetFetcher())

    loadGLXF { composition ->
      environment = composition.getNodeByName("Environment").entity
      videoPanel = composition.getNodeByName("VideoPanel").entity

      val mrPanel: Entity = composition.getNodeByName("MRPanel").entity
      val listPanel: Entity = composition.getNodeByName("ListPanel").entity

      // parent mrPanel to listPanel so they move together.
      mrPanel.setComponent(TransformParent(listPanel))

      // set the environment mesh to use a custom shader.
      val environmentMesh = environment?.getComponent<Mesh>()
      environmentMesh?.defaultShaderOverride = "transition"
      environment?.setComponent(environmentMesh!!)

      updateTextures(videoTexture)
    }
  }

  override fun onPause() {
    super.onPause()
    exoPlayer?.pause()
  }

  override fun onResume() {
    super.onResume()
    exoPlayer?.play()
  }

  override fun onSceneReady() {
    super.onSceneReady()
    // set the reference space to enable recentering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)
    scene.setLightingEnvironment(
        ambientColor = Vector3(LIGHTS_UP_AMBIENT),
        sunColor = Vector3(0f, 0f, 0f),
        sunDirection = -Vector3(1.0f, 3.0f, 2.0f))
    scene.updateIBLEnvironment("chromatic.env")

    skyVideoPanel = createSkyViewer()

    sky = createSkyBox()
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        videoPanelRegistration(),
        PanelRegistration(R.integer.list_panel) {
          activityClass = ListPanel::class.java
          config {
            width = 1.5f
            height = 2f
            layoutWidthInPx = 750
            layoutHeightInPx = 1000
            layoutDpi = 260
            includeGlass = false
            layerConfig = LayerConfig()
            enableTransparent = true
          }
        },
        PanelRegistration(R.integer.mr_panel) {
          config {
            fractionOfScreen = 0.15f
            height = .2f
            width = .6f
            enableTransparent = true
            includeGlass = false
            themeResourceId = R.style.ThemeTransparent
          }
          composePanel { setContent { MRApp() } }
        })
  }

  fun playVideo(videoUrl: String) {
    Log.i(TAG, "movie ${videoUrl}")

    // if video from youtube not the 360 video stored locally then we need to use the webview
    // to play the video.
    if (videoUrl.indexOf("youtube") != -1) {
      movieState = MovieSceneState.VR

      // load the youtube video into the webview.
      webView!!.loadUrl(videoUrl)
    } else {

      movieState = MovieSceneState.SURROUND

      // load the 360 video into exoplayer
      exoPlayer?.let {
        it.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
        it.prepare()
      }
    }
  }

  private fun videoPanelRegistration(): PanelRegistration {
    return PanelRegistration(R.integer.video_id) {
      layoutResourceId = R.layout.video_layout
      config {
        includeGlass = false
        layoutWidthInPx = 1920
        layoutHeightInPx = 1080
        height = 2.25f
        width = 4f
        layerConfig = LayerConfig()
        // we want access to the video panel to perform lighting effects
        forceSceneTexture = true
        panel {
          webView = rootView?.findViewById<WebView>(R.id.web_view)
          val webSettings = webView!!.getSettings()
          webSettings.setJavaScriptEnabled(true)
          webSettings.setMediaPlaybackRequiresUserGesture(false)
          // have the panel influence the theatre
          updateTextures(getTexture()!!)
        }
      }
    }
  }

  private fun updateTextures(texture: SceneTexture?) {

    videoTexture = texture
    systemManager.findSystem<SceneObjectSystem>().getSceneObject(environment!!)?.thenAccept { so ->
      val roomMaterial = so.mesh!!.getMaterial("mediaroom_mat") as SceneMaterial
      val foliageMaterial = so.mesh!!.getMaterial("plants_mat") as SceneMaterial
      val groundMaterial = so.mesh!!.getMaterial("evironment_mat") as SceneMaterial

      // set the panel texture as the albedo texture for the room and foliage materials
      if (videoTexture != null) {

        // set the panel texture as the emissive texture for the room and foliage materials to
        // make it look like a movie screen is being projected on the room.
        roomMaterial.setTexture("emissive", videoTexture!!)
        roomMaterial.setTexture(
            "occlusion", SceneTexture(getDrawable(R.drawable._media_room_screen_mask)))

        foliageMaterial.setTexture("emissive", videoTexture!!)
        foliageMaterial.setTexture(
            "occlusion", SceneTexture(getDrawable(R.drawable._media_room_screen_mask)))

        sceneMaterials = arrayOf(roomMaterial, foliageMaterial, groundMaterial)
        sceneMaterials!!.forEach { material -> material.setMetalRoughness(0.0f) }
      }
    }
  }

  private fun createSkyBox(): Entity {
    val entity = Entity.create(Transform())
    val texture = SceneTexture(getDrawable(R.drawable.skydome))
    skyBoxMaterial =
        SceneMaterial.custom(
                "360",
                arrayOf<SceneMaterialAttribute>(

                    // define the some standard pbr material attributes.
                    SceneMaterialAttribute("albedoSampler", SceneMaterialDataType.Texture2D),
                    SceneMaterialAttribute("stereoParams", SceneMaterialDataType.Vector4),

                    // define the custom material attributes.
                    SceneMaterialAttribute("customParams", SceneMaterialDataType.Vector4),
                ))
            .apply {
              setBlendMode(BlendMode.TRANSLUCENT)

              // set the initial state of the custom material.
              setAttribute("customParams", Vector4(0.0f, 0f, 0f, 0f))
              setTexture("albedoSampler", texture)
            }
            .also { material ->
              val sceneObject =
                  SceneObject(scene, SceneMesh.skybox(280f, material), "skybox", entity)
              systemManager
                  .findSystem<SceneObjectSystem>()
                  .addSceneObject(
                      entity, CompletableFuture<SceneObject>().apply { complete(sceneObject) })
            }

    return entity
  }

  private fun createSkyViewer(): Entity {
    val entity = Entity.create(Transform(), Visible(false))
    val SPHERE_RADIUS: Float = 300.0f
    val panelConfig =
        PanelConfigOptions().apply {
          // height doesn't matter since surface will get resized
          layoutWidthInPx = 100
          layoutHeightInPx = 100
          stereoMode = StereoMode.UpDown
          panelShapeType = PanelShapeType.EQUIRECT
          radiusForCylinderOrSphere = SPHERE_RADIUS
          includeGlass = false
          // enable direct-to-compositor rendering
          mips = 1
          // always render the layer first (behind all the other layers)
          layerConfig = LayerConfig(zIndex = -1)
        }

    val panelSceneObject =
        PanelSceneObject(
            scene,
            entity,
            panelConfig,
        )

    systemManager
        .findSystem<SceneObjectSystem>()
        .addSceneObject(
            entity, CompletableFuture<SceneObject>().apply { complete(panelSceneObject) })

    exoPlayer =
        ExoPlayer.Builder(this).build().apply {
          repeatMode = Player.REPEAT_MODE_ONE
          setVideoSurface(panelSceneObject.getSurface())
        }
    return entity
  }

  suspend fun animateTransitionMask(
      materials: Array<SceneMaterial>,
      start: Float,
      end: Float,
      propertyName: String = "roughness",
      time: Float = 1.0f,
      accelerate: Boolean = true,
      onEnd: () -> Unit = {}
  ) =
      suspendCancellableCoroutine<Unit> { continuation ->
        // Create an ObjectAnimator to animate a float value from start to end
        val animator = ObjectAnimator.ofFloat(start, end)
        animator.duration = time.toLong() * 1000 // time in seconds to milliseconds.
        animator.interpolator =
            if (accelerate) AccelerateInterpolator() else DecelerateInterpolator()

        // Add an update listener to the animator
        animator.addUpdateListener { animation ->
          val animatedValue = animation.animatedValue as Float
          materials.forEach { material ->
            if (propertyName == "roughness") {
              material.setMetalRoughness(animatedValue)
            } else {
              material.setAttribute(propertyName, Vector4(animatedValue, 0f, 0f, 0f))
            }
          }
        }

        animator.addListener(
            object : AnimatorListenerAdapter() {
              override fun onAnimationEnd(animation: Animator) {
                continuation.resume(Unit)
                onEnd()
              }
            })

        animator.start()
        continuation.invokeOnCancellation {
          animator.cancel()
          onEnd()
          materials.forEach { material ->
            if (propertyName == "roughness") {
              material.setMetalRoughness(end)
            } else {
              material.setAttribute(propertyName, Vector4(end, 0f, 0f, 0f))
            }
          }
        }
      }

  private fun loadGLXF(onLoaded: ((GLXFInfo) -> Unit) = {}): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("apk:///scenes/Composition.glxf"),
          rootEntity = gltfxEntity!!,
          onLoaded = onLoaded)
    }
  }

  override fun onSpatialShutdown() {
    exoPlayer?.release()
    exoPlayer = null
    super.onSpatialShutdown()
  }

  companion object {
    private const val TAG = "MediaPlayerSampleActivity"
  }
}
