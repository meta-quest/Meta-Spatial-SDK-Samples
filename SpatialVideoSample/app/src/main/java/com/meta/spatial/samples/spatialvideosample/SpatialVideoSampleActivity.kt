/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.spatialvideosample

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.audio.ChannelMixingMatrix
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.metadata.MetadataOutput
import androidx.media3.exoplayer.text.TextOutput
import androidx.media3.exoplayer.video.VideoRendererEventListener
import androidx.media3.ui.PlayerView
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.AlphaMode
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneAudioAsset
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMesh
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.runtime.TriangleMesh
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.AvatarSystem
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableSystem
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun lerp(start: Float, end: Float, fraction: Float): Float = start + (end - start) * fraction

// default activity
class SpatialVideoSampleActivity : AppSystemActivity() {

  lateinit var playerView: PlayerView
  lateinit var player: ExoPlayer
  lateinit var controllerView: View
  lateinit var controlsFadeOutTimer: CountDownTimer
  lateinit var audio: SceneAudioAsset
  var seekBar: CompletableFuture<SeekBar> = CompletableFuture<SeekBar>()
  var playPauseButton: CompletableFuture<Button> = CompletableFuture<Button>()
  val panner: ChannelMixingAudioProcessor = ChannelMixingAudioProcessor()
  var isPlaying: Boolean = false
  var isSeeking: Boolean = false
  var isFirstReadyDone: Boolean = false
  lateinit var locomotionSystem: LocomotionSystem
  lateinit var avatarSystem: AvatarSystem
  var setUri: Uri? = null
  var targetLights: Float = 1.0f
  val PERMISSIONS_REQUEST_CODE = 100
  var alphaAnimator: ObjectAnimator? = null
  var mrPanelPose: Pose = Pose()
  var skydome: Entity? = null
  var skydomeMat: SceneMaterial? = null
  var environmentGLXF: Entity? = null

  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)

  override fun registerFeatures(): List<SpatialFeature> {
    val features = mutableListOf<SpatialFeature>(VRFeature(this))
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
    }
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestPermissions()

    appPackageName = getPackageName()
    appContext = spatialContext

    panner.putChannelMixingMatrix(ChannelMixingMatrix.create(2, 2))

    val audioProcessors: Array<BaseAudioProcessor> = arrayOf(panner)

    val audioSink = DefaultAudioSink.Builder().setAudioProcessors(audioProcessors).build()

    player = ExoPlayer.Builder(this, CustomRenderersFactory(this, audioSink)).build()

    audio = SceneAudioAsset.loadLocalFile("data/common/audio/ui_press_direct.ogg")

    // since this is MR mode, we want to disable the controllers rendering
    // We will enable the controllers when not in MR, but will not enable avatar
    avatarSystem = systemManager.findSystem<AvatarSystem>()
    avatarSystem.setShowControllers(false)
    avatarSystem.setShowHands(false)

    // locomotion system interferes sometimes with scrolling on panels, disabling for now
    // not needed in MR mode anyways
    locomotionSystem = systemManager.findSystem<LocomotionSystem>()
    locomotionSystem.enableLocomotion(false)

    // Make X (left hand pinch) also trigger grabbing so we move panels with hand pinch
    systemManager.findSystem<GrabbableSystem>().grabButtons =
        (ButtonBits.ButtonSqueezeR or ButtonBits.ButtonSqueezeL or ButtonBits.ButtonX)

    controlsFadeOutTimer =
        object : CountDownTimer(100, 100) {
          override fun onTick(millisUntilFinished: Long) {}

          override fun onFinish() {
            animateControllerVisibility(false)
          }
        }

    loadGLXF().invokeOnCompletion {
      val composition = glXFManager.getGLXFInfo("example_key_name")
      environmentGLXF = composition.getNodeByName("MediaRoom").entity
      environmentGLXF?.let {
        val environmentMesh = it.getComponent<Mesh>()
        it.setComponent(
            environmentMesh.apply { defaultShaderOverride = SceneMaterial.UNLIT_SHADER })
      }
      setMrMode(scene.isSystemPassthroughEnabled())
    }
  }

  override fun registerPanels(): List<PanelRegistration> {
    return mutableListOf(
            videoPanelRegistration(),
            controlsPanelRegistration(),
            selectorPanelRegistration(),
            mrPanelRegistration())
        .apply {
          if (DEBUG) {
            add(debugPanelRegistration())
          }
        }
  }

  private fun requestPermissions() {
    val permissionsNeeded =
        arrayOf("com.oculus.permission.USE_SCENE", Manifest.permission.READ_EXTERNAL_STORAGE)

    ActivityCompat.requestPermissions(this, permissionsNeeded, PERMISSIONS_REQUEST_CODE)
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray,
  ) {
    when (requestCode) {
      PERMISSIONS_REQUEST_CODE -> {
        val granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        if (granted) {
          // All permissions have been granted
          Log.i(TAG, "All permissions have been granted")
        } else {
          // One or more permissions have been denied
          Log.i(TAG, "One or more permissions were DENIED!")
        }
      }
    }
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // set the reference space to enable recentering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    systemManager.registerSystem(SpatialAudioSystem(panner))
    componentManager.registerComponent<SpatializedAudioPanel>(SpatializedAudioPanel.Companion)

    scene.isSystemPassthroughEnabled().let { isMrMode ->
      scene.enablePassthrough(isMrMode)
      locomotionSystem.enableLocomotion(!isMrMode)
      avatarSystem.setShowControllers(!isMrMode)
      avatarSystem.setShowHands(!isMrMode)
    }

    scene.setViewOrigin(0f, 0.0f, 0.0f, 0.0f)

    skydome =
        Entity.create(
            listOf(
                Mesh(Uri.parse("mesh://skybox")),
                Material().apply {
                  baseTextureAndroidResourceId = R.drawable.skydome
                  unlit = true
                },
                Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
                Visible(false)))

    scene.updateIBLEnvironment("chromatic.env")
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

  override fun onVRReady() {
    super.onVRReady()
    if (!isFirstReadyDone) {
      val initialPose = Pose()
      Entity(R.integer.spatialized_video_panel)
          .setComponents(
              listOf(
                  Grabbable(type = GrabbableType.PIVOT_Y, minHeight = 0.75f, maxHeight = 2.5f),
                  SpatializedAudioPanel(),
                  Panel(R.layout.spatialized_video),
                  Transform(initialPose * Pose(Vector3(0f, 1.25f, 2f), Quaternion(0f, 0f, 0f))),
              ))
      Entity(R.integer.video_selector_panel)
          .setComponents(
              listOf(
                  Grabbable(),
                  Panel(R.integer.video_selector_panel),
                  Transform(
                      initialPose * Pose(Vector3(-1f, 1.25f, 1.2f), Quaternion(0f, -45f, 0f))),
              ))
      Entity(R.integer.controls_id)
          .setComponents(
              listOf(
                  Panel(R.layout.controls),
                  TransformParent(Entity(R.integer.spatialized_video_panel)),
              ))
      Entity(R.integer.mr_panel)
          .setComponents(
              listOf(
                  Panel(R.integer.mr_panel),
                  Transform(Pose(Vector3(0.0f, -0.6f, -0.1f))),
                  TransformParent(Entity(R.integer.video_selector_panel)),
              ))
      environmentGLXF?.setComponents(listOf(Visible(false), Transform(initialPose)))
      if (DEBUG) {
        Entity(R.integer.debug_panel)
            .setComponents(
                listOf(
                    Grabbable(),
                    Panel(R.layout.debug),
                    Transform(initialPose * Pose(Vector3(1f, 1.25f, 1f), Quaternion(0f, 45f, 0f))),
                ))
      }
      mrPanelPose = Entity(R.integer.spatialized_video_panel).getComponent<Transform>().transform
      setMrMode(scene.isSystemPassthroughEnabled())
      isFirstReadyDone = true
    }
  }

  // Video Panel
  private fun videoPanelRegistration(): PanelRegistration {
    return PanelRegistration(R.layout.spatialized_video) {
      config {
        // based on video sizes
        layoutWidthInPx = 3840
        layoutHeightInPx = 1080
        stereoMode = StereoMode.LeftRight
        includeGlass = false
        width = MR_SCREEN_WIDTH
        height = MR_SCREEN_HEIGHT
        layerConfig = LayerConfig()
        // want to disable left hand pinch so we can drag the panel around with hands
        clickButtons =
            (ButtonBits.ButtonA or ButtonBits.ButtonTriggerL or ButtonBits.ButtonTriggerR)
        // force efficient copy of video texture
        mips = 1
        sceneMeshCreator = { texture: SceneTexture ->
          val halfHeight = height / 2f
          val halfWidth = width / 2f
          val halfDepth = 0.1f
          val rounding = 0.075f
          val triMesh =
              TriangleMesh(
                  8,
                  18,
                  intArrayOf(6, 6, 12, 6, 0, 6),
                  arrayOf(
                      SceneMaterial(texture, AlphaMode.TRANSLUCENT, "data/shaders/spatial/reflect")
                          .apply {
                            setStereoMode(stereoMode)
                            setUnlit(true)
                          },
                      SceneMaterial(texture, AlphaMode.TRANSLUCENT, "data/shaders/spatial/shadow")
                          .apply { setUnlit(true) },
                      SceneMaterial(texture, AlphaMode.OPAQUE, SceneMaterial.HOLE_PUNCH_SHADER)
                          .apply {
                            setStereoMode(stereoMode)
                            setUnlit(true)
                          },
                  ))
          triMesh.updateGeometry(
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
                  0f,
                  // shadow
                  -halfWidth,
                  -halfHeight,
                  halfDepth,
                  halfWidth,
                  -halfHeight,
                  halfDepth,
                  halfWidth,
                  -halfHeight,
                  -halfDepth,
                  -halfWidth,
                  -halfHeight,
                  -halfDepth),
              floatArrayOf(
                  0f,
                  0f,
                  1f,
                  0f,
                  0f,
                  1f,
                  0f,
                  0f,
                  1f,
                  0f,
                  0f,
                  1f,
                  0f,
                  0f,
                  1f,
                  0f,
                  0f,
                  1f,
                  0f,
                  0f,
                  1f,
                  0f,
                  0f,
                  1f),
              floatArrayOf(
                  // front
                  0f,
                  1f,
                  1f,
                  1f,
                  1f,
                  0f,
                  0f,
                  0f,
                  // shadow
                  halfWidth - rounding,
                  halfDepth - rounding,
                  halfWidth - rounding,
                  halfDepth - rounding,
                  halfWidth - rounding,
                  halfDepth - rounding,
                  halfWidth - rounding,
                  halfDepth - rounding),
              intArrayOf(
                  Color.WHITE,
                  Color.WHITE,
                  Color.WHITE,
                  Color.WHITE,
                  Color.WHITE,
                  Color.WHITE,
                  Color.WHITE,
                  Color.WHITE))
          triMesh.updatePrimitives(
              0, intArrayOf(0, 1, 2, 0, 2, 3, 0, 2, 1, 0, 3, 2, 4, 6, 5, 4, 7, 6))
          SceneMesh.fromTriangleMesh(triMesh, false)
        }
      }
      panel {
        playerView = rootView?.findViewById<PlayerView>(R.id.video_view)!!
        playerView.setUseController(false)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.setSeekParameters(SeekParameters.CLOSEST_SYNC)
        playerView.player = player
        seekBar.thenAccept { it ->
          player.addListener(
              object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                  if (playbackState == Player.STATE_READY) {
                    seekBar.thenAccept { it.max = player.duration.toInt() }
                  }
                }

                override fun onPositionDiscontinuity(reason: Int) {
                  it.progress = player.currentPosition.toInt()
                }

                override fun onPlayerError(error: PlaybackException) {
                  // The ExoPlayer can throw a decoder error under heavy load such as app startup,
                  // in the case of a decoding error reloading the video into exoplayer fixes the
                  // issue.
                  // The theory here is that the file itself is not an issue, but the hardware
                  // decoder
                  // becomes backed up during app startup which causes a decoding error to be
                  // thrown.
                  setUri?.let { uri -> setVideo(uri) }
                  Log.e("ExoPlayer", "Player encountered an error: $error")
                }
              })

          it.setOnSeekBarChangeListener(
              object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                  if (fromUser) {
                    player.seekTo(progress.toLong())
                    resetControllerFadeOutTimer()
                  }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                  if (isPlaying) {
                    // Pause the player while the user is dragging the SeekBar
                    player.playWhenReady = false
                  }
                  resetControllerFadeOutTimer()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                  if (isPlaying) {
                    // Resume the player when the user stops dragging the SeekBar
                    player.playWhenReady = true
                  }
                }
              })
        }

        // Default media
        Movie.fromRawVideo("doggie", "Doggie")?.let { movie -> setVideo(movie.uri) }

        playerView.setOnClickListener { togglePlay() }
        setupHoverAndTouchListeners(playerView)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(
            object : Runnable {
              override fun run() {
                if (isPlaying && !isSeeking) {
                  seekBar.thenAccept { it.progress = player.currentPosition.toInt() }
                }
                handler.postDelayed(this, 500)
              }
            },
            500)
      }
    }
  }

  private fun debugPanelRegistration(): PanelRegistration {
    return PanelRegistration(R.layout.debug) {
      config {
        fractionOfScreen = 0.5f
        height = 1f
        width = 0.6f
        includeGlass = false
        layerConfig = LayerConfig()
        enableTransparent = true
        themeResourceId = R.style.PanelAppThemeTransparent
      }
      panel {
        val scaleText = rootView?.findViewById<TextView>(R.id.scale_text)
        val scaleBar = rootView?.findViewById<SeekBar>(R.id.scale_bar)
        val scaleMax = scaleBar?.max ?: 1
        scaleBar?.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
              override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                  // in range [0, 1]
                  val normalized = progress.toDouble() / scaleMax
                  // in range [-1.5, 1.5]
                  val expRange = normalized * 3 - 1.5
                  val newScale = Math.pow(10.0, expRange).toFloat()
                  scaleText?.text = "Scale: %.2f".format(newScale)
                  Entity(R.integer.spatialized_video_panel).setComponent(Scale(newScale))
                }
              }

              override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

              override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            })
      }
    }
  }

  // Movies Controller panel
  private fun controlsPanelRegistration(): PanelRegistration {
    return PanelRegistration(R.layout.controls) {
      config {
        width = 0.8f
        height = 0.25f
        pivotOffsetWidth = 0.5f
        pivotOffsetHeight = 1.05f
        includeGlass = false
        layerConfig = LayerConfig()
        enableTransparent = true
        layoutDpi = 600
        themeResourceId = R.style.PanelAppThemeTransparent
      }
      panel {
        var localSeekBar = rootView?.findViewById<SeekBar>(R.id.seek_bar)!!
        seekBar.complete(localSeekBar)

        val playPauseButtonLocal = rootView?.findViewById<Button>(R.id.play_pause_button)!!
        playPauseButton.complete(playPauseButtonLocal)
        playPauseButtonLocal.setOnClickListener { togglePlay() }
        setupHoverAndTouchListeners(playPauseButtonLocal)
        val backButton = rootView?.findViewById<Button>(R.id.back_button)!!
        backButton.setOnClickListener { setUri?.let { MoviePanel.viewModel.previousVideo(it) } }
        setupHoverAndTouchListeners(backButton)
        val forwardButton = rootView?.findViewById<Button>(R.id.forward_button)!!
        forwardButton.setOnClickListener { setUri?.let { MoviePanel.viewModel.nextVideo(it) } }
        setupHoverAndTouchListeners(forwardButton)
        controllerView = rootView!!
        setupHoverAndTouchListeners(controllerView)
        controllerView.alpha = 1.0f
      }
    }
  }

  private fun setupHoverAndTouchListeners(view: View) {
    view.setOnTouchListener { v, event ->
      val action = event.action
      when (action) {
        MotionEvent.ACTION_DOWN -> {}
        MotionEvent.ACTION_MOVE -> {
          resetControllerFadeOutTimer()
        }
        MotionEvent.ACTION_UP -> {}
        MotionEvent.ACTION_CANCEL -> {}
      }
      false
    }
    view.setOnHoverListener { v, event ->
      val action = event.action
      when (action) {
        MotionEvent.ACTION_HOVER_ENTER -> {
          animateControllerVisibility(true)
          resetControllerFadeOutTimer()
        }
        MotionEvent.ACTION_HOVER_MOVE -> {
          resetControllerFadeOutTimer()
        }
        MotionEvent.ACTION_HOVER_EXIT -> {}
      }
      true
    }
  }

  fun animateControllerVisibility(visible: Boolean) {
    if (visible) {
      alphaAnimator =
          ObjectAnimator.ofFloat(controllerView, "alpha", controllerView.alpha, 1.0f).apply {
            duration = 200
            start()
          }
    } else {
      alphaAnimator =
          ObjectAnimator.ofFloat(controllerView, "alpha", controllerView.alpha, 0.0f).apply {
            duration = 200
            start()
          }
    }
  }

  fun resetControllerFadeOutTimer() {
    if (isPlaying) {
      controlsFadeOutTimer.cancel()
      controlsFadeOutTimer.start()
      if (controllerView.alpha == 0.0f && alphaAnimator?.isRunning != true) {
        animateControllerVisibility(true)
      }
    }
  }

  fun togglePlay() {
    scene.playSound(audio, 1f)
    if (isPlaying) {
      pauseVideo()
    } else {
      playVideo()
    }
  }

  public fun setVideo(video: Uri) {
    setUri = video
    val mediaItem = MediaItem.fromUri(video)
    // Set the media item to be played.
    player.setMediaItem(mediaItem)
    // Prepare the player.
    player.prepare()
  }

  public fun playVideo() {
    player.play()
    isPlaying = true
    playPauseButton.thenAccept {
      it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pause, 0, 0, 0)
    }
    dimLights()
    resetControllerFadeOutTimer()
  }

  public fun pauseVideo() {
    player.pause()
    isPlaying = false
    playPauseButton.thenAccept {
      it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play, 0, 0, 0)
    }
    brightenLights()
    animateControllerVisibility(true)
    controlsFadeOutTimer.cancel()
  }

  public fun dimLights() {
    targetLights = 0.0f
  }

  public fun brightenLights() {
    targetLights = 1.0f
  }

  // Movies List Panel
  private fun selectorPanelRegistration(): PanelRegistration {
    return PanelRegistration(R.integer.video_selector_panel) {
      activityClass = MoviePanel::class.java
      config {
        fractionOfScreen = 0.75f
        height = 1.1f
        width = 0.6f
        layoutDpi = 800
        includeGlass = false
        layerConfig = LayerConfig()
        enableTransparent = true
        // want to disable left hand pinch so we can drag the panel around with hands
        clickButtons =
            (ButtonBits.ButtonA or ButtonBits.ButtonTriggerL or ButtonBits.ButtonTriggerR)
      }
    }
  }

  // Passthrough (MR) panel
  private fun mrPanelRegistration(): PanelRegistration {
    return PanelRegistration(R.integer.mr_panel) {
      panelIntent =
          Intent(spatialContext, MRPanel::class.java).apply {
            putExtra("isMrMode", scene.isSystemPassthroughEnabled().toString())
          }
      config {
        fractionOfScreen = 0.2f
        height = .2f
        width = .6f
        layoutDpi = 400
        layerConfig = LayerConfig()
        enableTransparent = true
        includeGlass = false
      }
    }
  }

  public fun setMrMode(isMrMode: Boolean) {
    if (!isMrMode) {
      mrPanelPose =
          Entity(R.integer.spatialized_video_panel).tryGetComponent<Transform>()?.transform
              ?: Pose()
      environmentGLXF?.setComponent(Visible(true))
      skydome?.setComponent(Visible(true))
    }
    val grabbable =
        Entity(R.integer.spatialized_video_panel).tryGetComponent<Grabbable>() ?: Grabbable()
    grabbable.enabled = isMrMode
    Entity(R.integer.spatialized_video_panel).setComponent(grabbable)

    if (isMrMode) {
      scene.setViewOrigin(0f, 0f, 0f, 0f) // reset locomotion
      environmentGLXF?.setComponent(Visible(false))
      skydome?.setComponent(Visible(false))
      Entity(R.integer.spatialized_video_panel)
          .setComponents(
              listOf(Scale(1.0f), Transform(mrPanelPose), TransformParent(Entity.nullEntity())))
      Entity(R.integer.controls_id)
          .setComponent(Transform(Pose(Vector3(0.0f, -0.43f, -0.15f), Quaternion(20f, 0f, 0f))))
    } else {
      Entity(R.integer.spatialized_video_panel)
          .setComponents(
              listOf(
                  Scale(SpatialVideoSampleActivity.VR_SCREEN_RATIO),
                  Transform(Pose(Vector3(0.2f, 1.7f, 4.5f), Quaternion(0f, 0f, 0f))),
              ))
      Entity(R.integer.controls_id)
          .setComponents(
              listOf(Transform(Pose(Vector3(0.0f, -1.1f, -2.0f), Quaternion(20f, 0f, 0f)))))
    }
    scene.enablePassthrough(isMrMode)
    locomotionSystem.enableLocomotion(!isMrMode)
    avatarSystem.setShowControllers(!isMrMode)
    avatarSystem.setShowHands(!isMrMode)
  }

  companion object {
    const val TAG = "SpatialVideoSampleActivity"
    lateinit var appContext: Context
    lateinit var appPackageName: String

    const val LIGHTS_UP_SCALE: Float = 1.0f
    const val LIGHTS_DOWN_SCALE: Float = 0.25f

    const val MR_SCREEN_WIDTH: Float = 16.0f / 10.0f
    const val MR_SCREEN_HEIGHT: Float = 9.0f / 10.0f
    const val VR_SCREEN_RATIO: Float = 2.5f

    // spawns debug menu if true
    const val DEBUG: Boolean = false
  }
}

class CustomRenderersFactory : DefaultRenderersFactory {
  private val context_: Context
  private val audioSink_: AudioSink

  constructor(context: Context, audioSink: AudioSink) : super(context) {
    context_ = context
    audioSink_ = audioSink
  }

  override fun createRenderers(
      eventHandler: Handler,
      videoRendererEventListener: VideoRendererEventListener,
      audioRendererEventListener: AudioRendererEventListener,
      textRendererOutput: TextOutput,
      metadataRendererOutput: MetadataOutput,
  ): Array<Renderer> {
    val renderers =
        super.createRenderers(
            eventHandler,
            videoRendererEventListener,
            audioRendererEventListener,
            textRendererOutput,
            metadataRendererOutput,
        )
    var rendererList = renderers.toMutableList()
    val audioRenderer =
        MediaCodecAudioRenderer(
            context_,
            getCodecAdapterFactory(),
            MediaCodecSelector.DEFAULT,
            false,
            eventHandler,
            audioRendererEventListener,
            audioSink_,
        )
    rendererList.add(0, audioRenderer)
    return rendererList.toTypedArray()
  }
}
