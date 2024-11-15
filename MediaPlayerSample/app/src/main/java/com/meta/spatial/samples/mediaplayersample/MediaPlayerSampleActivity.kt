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
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.webkit.WebView
import android.widget.VideoView
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector4
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.runtime.AlphaMode
import com.meta.spatial.runtime.BlendMode
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.PanelConfigOptions
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneAudioAsset
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMaterialAttribute
import com.meta.spatial.runtime.SceneMaterialDataType
import com.meta.spatial.runtime.SceneMesh
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.runtime.TriangleMesh
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.io.File
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
  var videoView: VideoView? = null
  var webView: WebView? = null
  var triMesh: TriangleMesh? = null
  var panelMesh: SceneMesh? = null
  val LIGHTS_UP_AMBIENT: Float = 5.0f
  lateinit var locomotionSystem: LocomotionSystem
  var sky: Entity? = null
  var videoTexture: SceneTexture? = null
  var sceneMaterials: Array<SceneMaterial>? = null
  var skyPanelMaterial: SceneMaterial? = null
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
          skyVideoPanel?.setComponent(Visible(!mrState_)) // hide 360 panel
          environment?.setComponent(Visible(!mrState_)) // hide environment
          locomotionSystem.enableLocomotion(!mrState_)
          videoPanel?.setComponents(
              listOf(Grabbable(mrState_), Transform(Pose(Vector3(0f, 1.32f, 4.85f)))))

          // only animate if the sky panel is not visible.
          if (skyPanelVisible) {

            // stop 360 video
            videoView?.suspend()

            animationJob =
                animationScope!!.launch {
                  animateTransitionMask(
                      arrayOf(skyPanelMaterial!!), 0f, 1f, "customParams", 1f) // hide 360
                  playRandomSfx()
                  animateTransitionMask(sceneMaterials!!, 1f, 0f, "roughness", 1f, false) // show VR
                  skyPanelVisible = false
                  webView?.setVisibility(View.VISIBLE) // show flat screen
                  videoPanel?.setComponent(Visible(true))
                }
          }
        }
        MovieSceneState.SURROUND -> {

          sky?.setComponent(Visible(true)) // show room sky
          videoPanel?.setComponents(listOf(Grabbable(false), Visible(false))) // hide video panel

          // scene.setViewOrigin(0f, 0f, 0f, 0f) // reset locomotion
          locomotionSystem.enableLocomotion(false)

          // play 360 video.
          videoView?.start()
          videoView?.setVisibility(View.VISIBLE)

          // unload webview video and hide.
          webView?.loadUrl("")

          if (!skyPanelVisible) {
            playRandomSfx()
            animationJob =
                animationScope!!.launch {
                  animateTransitionMask(sceneMaterials!!, 0f, 1f, "roughness", 1f) // hide VR
                  animateTransitionMask(
                      arrayOf(skyPanelMaterial!!), 1f, 0f, "customParams", 1f) // show 360
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
    val features = mutableListOf<SpatialFeature>(VRFeature(this))
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
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

    loadGLXF().invokeOnCompletion {
      val composition = glXFManager.getGLXFInfo("example_key_name")
      environment = composition.getNodeByName("Environment").entity
      videoPanel = composition.getNodeByName("VideoPanel").entity
      skyVideoPanel = composition.getNodeByName("SkyVideoPanel").entity

      val mrPanel: Entity = composition.getNodeByName("MRPanel").entity
      val listPanel: Entity = composition.getNodeByName("ListPanel").entity

      // parent mrPanel to listPanel so they move together.
      mrPanel.setComponent(TransformParent(listPanel))

      // set the environment mesh to use a custom shader.
      val environmentMesh = environment?.getComponent<Mesh>()
      environmentMesh?.defaultShaderOverride = "data/shaders/custom/transition"
      environment?.setComponent(environmentMesh!!)

      updateTextures(videoTexture)
    }
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

    sky =
        Entity.create(
            listOf(
                Mesh(Uri.parse("mesh://skybox")),
                Material().apply {
                  baseTextureAndroidResourceId = R.drawable.skydome
                  unlit = true // Prevent scene lighting from affecting the skybox
                },
                Visible(true),
                Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f)))))
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        videoPanelRegistration(),
        skyVideoPanelRegistration(),
        PanelRegistration(R.integer.list_panel) {
          activityClass = ListPanel::class.java
          config {
            fractionOfScreen = 0.4f
            mips = PanelConfigOptions.MIPS_MAX
            height = 2f
            width = 1.5f
            layoutHeightInPx = 1365
            layoutWidthInPx = 1024
            includeGlass = false
          }
        },
        PanelRegistration(R.integer.mr_panel) {
          activityClass = MRPanel::class.java
          config {
            fractionOfScreen = 0.15f
            height = .2f
            width = .6f
            layerConfig = LayerConfig()
            enableTransparent = true
            includeGlass = false
          }
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

      // load the 360 video into the video view.
      videoView!!.setVideoURI(Uri.parse(videoUrl))
    }
  }

  private fun videoPanelRegistration(): PanelRegistration {
    return PanelRegistration(R.integer.video_id) {
      layoutResourceId = R.layout.video_layout
      config {
        pivotOffsetWidth = 0.0f
        pivotOffsetHeight = 0.0f
        height = 2.4f
        width = 4f
        fractionOfScreen = 1f
        sceneMeshCreator = { texture: SceneTexture ->
          val halfHeight = height / 2f
          val halfWidth = width / 2f
          triMesh =
              TriangleMesh(
                  4,
                  6,
                  intArrayOf(0, 6),
                  arrayOf(
                      SceneMaterial(texture, AlphaMode.MASKED, SceneMaterial.UNLIT_SHADER).apply {
                        setStereoMode(stereoMode)
                        setUnlit(true)
                      }))
          triMesh!!.updateGeometry(
              0,
              floatArrayOf(
                  -halfWidth,
                  -halfHeight,
                  0f,
                  halfWidth,
                  -halfHeight,
                  0f,
                  halfWidth,
                  halfHeight,
                  0f,
                  -halfWidth,
                  halfHeight,
                  0f),
              floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f),
              floatArrayOf(0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f),
              intArrayOf(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE))
          triMesh!!.updatePrimitives(0, intArrayOf(0, 1, 2, 0, 2, 3))
          panelMesh = SceneMesh.fromTriangleMesh(triMesh!!, false)
          panelMesh!!
        }
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

  private fun skyVideoPanelRegistration(): PanelRegistration {

    skyPanelMaterial =
        SceneMaterial.custom(
                "data/shaders/custom/360",
                arrayOf<SceneMaterialAttribute>(

                    // define the some standard pbr material attributes.
                    SceneMaterialAttribute("albedoSampler", SceneMaterialDataType.Texture2D),
                    SceneMaterialAttribute("stereoParams", SceneMaterialDataType.Vector4),

                    // define the custom material attributes.
                    SceneMaterialAttribute("customParams", SceneMaterialDataType.Vector4),
                ))
            .apply {
              setBlendMode(BlendMode.TRANSLUCENT)

              // set the stereo mode to up down so the 360 video is rendered in stereo
              // mode.
              setStereoMode(StereoMode.UpDown)

              // set the initial state of the custom material.
              setAttribute("customParams", Vector4(1.0f, 0f, 0f, 0f))
            }

    return PanelRegistration(R.integer.sky_video_id) {
      layoutResourceId = R.layout.sky_video_layout
      config {
        val SPHERE_RADIUS: Float = 300.0f
        // render at a lower resolution for perf
        layoutWidthInPx = (5760 / 1.75).toInt()
        layoutHeightInPx = (5760 / 1.75).toInt()
        pivotOffsetWidth = 0.0f
        pivotOffsetHeight = 0.0f
        stereoMode = StereoMode.UpDown
        sceneMeshCreator = { texture: SceneTexture ->
          skyPanelMaterial!!.apply { setTexture("albedoSampler", texture) }
          SceneMesh.skybox(SPHERE_RADIUS, skyPanelMaterial!!)
        }
      }
      panel {
        setIsVisible(false)
        videoView = rootView?.findViewById<VideoView>(R.id.video_view)
        entity?.setComponent(Transform.build { rotateY(180f) })
      }
    }
  }

  suspend fun animateTransitionMask(
      materials: Array<SceneMaterial>,
      start: Float,
      end: Float,
      propertyName: String = "roughness",
      time: Float = 1.0f,
      accelerate: Boolean = true
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
              }
            })

        animator.start()
        continuation.invokeOnCancellation {
          animator.cancel()
          materials.forEach { material ->
            if (propertyName == "roughness") {
              material.setMetalRoughness(end)
            } else {
              material.setAttribute(propertyName, Vector4(end, 0f, 0f, 0f))
            }
          }
        }
      }

  private fun loadGLXF(): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("apk:///scenes/Composition.glxf"),
          rootEntity = gltfxEntity!!,
          keyName = "example_key_name")
    }
  }

  companion object {
    private const val TAG = "MediaPlayerSampleActivity"
  }
}
