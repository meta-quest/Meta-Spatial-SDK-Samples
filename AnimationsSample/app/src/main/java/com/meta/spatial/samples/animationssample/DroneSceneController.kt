/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import android.animation.ValueAnimator
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.AlphaMode
import com.meta.spatial.runtime.ButtonDownEventArgs
import com.meta.spatial.runtime.ControllerButton
import com.meta.spatial.runtime.SceneAudioAsset
import com.meta.spatial.runtime.SceneAudioPlayer
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.toolkit.Animated
import com.meta.spatial.toolkit.Color4
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.PlaybackState
import com.meta.spatial.toolkit.PlaybackType
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Sphere
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import java.util.Timer
import java.util.TimerTask

class DroneSceneController() {

  private val activity = SpatialActivityManager.getVrActivity<AnimationsSampleActivity>()
  private val glxf = activity.glXFManager.getGLXFInfo(activity.GLXF_DRONE_SCENE)
  private val sos = activity.systemManager.findSystem<SceneObjectSystem>()
  private val animClipMap = mutableMapOf<String, Int>()
  private val droneEnt = glxf.getNodeByName("drone").entity
  private val droneTargetEnt = glxf.getNodeByName("droneTarget").entity
  private val droneComponent = droneEnt.getComponent<DroneComponent>()
  private val scaleAnim = ValueAnimator.ofFloat(0f, 1f)
  private val pulseAnim = ValueAnimator.ofFloat(.95f, 1.05f)
  private val droneSound = SceneAudioAsset.loadLocalFile("drone.ogg")
  private val audioPlayer = SceneAudioPlayer(activity.scene, droneSound)
  private val defaultTargetMaterial =
      Material().apply {
        baseColor = Color4(red = .05f, green = .4f, blue = 1.0f, alpha = .8f)
        metallic = 0.6f
        roughness = 0.7f
        alphaMode = AlphaMode.TRANSLUCENT.ordinal
      }
  private val selectedTargetMaterial =
      Material().apply {
        baseColor = Color4(red = 1.0f, green = .6f, blue = .2f, alpha = .8f)
        metallic = 0.6f
        roughness = 0.7f
        alphaMode = AlphaMode.TRANSLUCENT.ordinal
      }

  init {
    val button = ButtonController(activity, "startButton")
    button.subscribe(
        object : ButtonEvent {
          override fun onClick() {
            unfold()
            glxf.getNodeByName("infoPanel").entity.setComponent(Visible(false))
          }
        })

    // create a map of anim clip names to indices so we can play them by name
    sos.getSceneObject(droneEnt)?.thenAccept { drone: SceneObject ->
      val animationTracks = drone.mesh?.getAnimationTracks()
      animationTracks?.forEach { track ->
        Log.d("drone_debug", track.name + ": " + track.id.toString())
        animClipMap.put(track.name, track.id)
      }

      // set initial anim state
      val anim =
          Animated(
              System.currentTimeMillis(),
              playbackState = PlaybackState.PAUSED,
              track = animClipMap.get("openarm")!!)
      droneEnt.setComponent(anim)
    }

    scaleAnim.apply {
      duration = 400
      interpolator = OvershootInterpolator(2f)
      addUpdateListener { animation ->
        val v = animation.animatedValue as Float
        droneTargetEnt.setComponent(Scale(Vector3(v, v, v)))
      }
    }

    pulseAnim.apply {
      duration = 800
      interpolator = AccelerateDecelerateInterpolator()
      repeatCount = ValueAnimator.INFINITE
      repeatMode = ValueAnimator.REVERSE
      addUpdateListener { animation ->
        val v = animation.animatedValue as Float
        droneTargetEnt.setComponent(Scale(Vector3(v, v, v)))
      }
    }
  }

  private fun playAnimation(track: Int, playbackType: PlaybackType = PlaybackType.CLAMP) {
    val anim =
        Animated(
            System.currentTimeMillis(),
            playbackState = PlaybackState.PLAYING,
            playbackType = playbackType,
            track = track)
    droneEnt.setComponent(anim)
  }

  private fun unfold() {
    playAnimation(animClipMap.get("openarm")!!)
    delayAction(::ascend, 3467)
  }

  private fun ascend() {
    // play drone sound
    audioPlayer.play(droneEnt.getComponent<Transform>().transform.t, 1f, true)

    playAnimation(animClipMap.get("takeoff")!!)
    delayAction(::flyAround, 3100)
  }

  private fun flyAround() {
    playAnimation(animClipMap.get("fancyflyaround")!!)
    delayAction(::idle, 4667)
  }

  private fun idle() {

    // apply offset since idle animation is at the origin
    val t = droneEnt.getComponent<Transform>()
    t.transform.t = t.transform.t + Vector3(0f, .905f, .265f)
    droneEnt.setComponent(t)

    playAnimation(animClipMap.get("idleground")!!, PlaybackType.LOOP)
    enableManualControls()
  }

  private fun enableManualControls() {
    // enable the drone component
    val droneComponent = droneEnt.getComponent<DroneComponent>()
    droneComponent.enabled = true
    droneEnt.setComponent(droneComponent)
    droneTargetEnt.setComponent(Visible(true))

    val followerComponent = droneEnt.getComponent<FollowerComponent>()
    followerComponent.enabled = true
    droneEnt.setComponent(followerComponent)

    // animations need to happen on a looper thread
    activity.runOnUiThread(
        Runnable({
          // scale up the target from zero
          scaleAnim.start()
          // apply pulsing animation to the target
          Handler(Looper.getMainLooper()).postDelayed({ pulseAnim.start() }, 800)
        }))

    // show the grab instruction panel
    val grabPanel = glxf.getNodeByName("grabPanel").entity
    grabPanel.setComponent(Visible(true))

    // hide the instructions panel on first sphere grab
    var firstGrab = true
    glxf
        .getNodeByName("droneTarget")
        .entity
        .registerEventListener(ButtonDownEventArgs.EVENT_NAME) {
            entity: Entity,
            eventArgs: ButtonDownEventArgs ->
          if (firstGrab) {
            if (eventArgs.button == ControllerButton.RightSqueeze ||
                eventArgs.button == ControllerButton.LeftSqueeze) {
              firstGrab = false
              grabPanel.setComponent(Visible(false))
            }
          }
          if (eventArgs.button == ControllerButton.RightTrigger ||
              eventArgs.button == ControllerButton.LeftTrigger) {
            val followerTarget = entity.getComponent<FollowerTarget>()
            followerTarget.isBuiltInFollower = !followerTarget.isBuiltInFollower
            val newMaterial =
                if (followerTarget.isBuiltInFollower) selectedTargetMaterial
                else defaultTargetMaterial
            entity.setComponents(newMaterial, followerTarget)
          }
        }
        .setComponents(Mesh(Uri.parse("mesh://sphere")), Sphere(0.2f), defaultTargetMaterial)
  }

  private fun delayAction(action: () -> Unit, duration: Long): TimerTask {
    val timerTask =
        object : TimerTask() {
          override fun run() {
            action()
          }
        }
    Timer().schedule(timerTask, duration)
    return timerTask
  }

  public fun tick() {
    // this works, but doesn't track the position during animation clip playback
    audioPlayer.setPosition(position = droneEnt.getComponent<Transform>().transform.t)
  }
}
