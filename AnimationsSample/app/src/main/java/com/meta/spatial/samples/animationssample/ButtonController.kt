/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.animation.OvershootInterpolator
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Matrix44
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.SceneAudioAsset
import com.meta.spatial.runtime.SceneObject

interface ButtonEvent {
  fun onClick()
}

class ButtonController(
    private val activity: AnimationsSampleActivity,
    private val buttonName: String = "startButton"
) {
  private val unhoveredColor = 0xff5722
  private val hoveredColor = 0xb84fff
  private val clickedColor = 0x00ff00
  private val hoverAnim = ValueAnimator.ofFloat(0f, 1f)
  private val clickAnim = ValueAnimator.ofFloat(0f, 1f)
  private val eventListeners = mutableListOf<ButtonEvent>()

  public fun subscribe(eventListener: ButtonEvent) {
    eventListeners.add(eventListener)
  }

  public fun unsubscribe(eventListener: ButtonEvent) {
    eventListeners.remove(eventListener)
  }

  private fun clicked() {
    eventListeners.forEach { it.onClick() }
  }

  private var wasClicked = false

  init {
    activity.getSceneObjectByName(buttonName)?.thenAccept { button: SceneObject ->
      setupButton(button)
    }
  }

  private fun setupButton(button: SceneObject) {

    // get button material
    val mat = button.mesh?.materials?.get(1)
    mat?.setAlbedoColor(Color.valueOf(unhoveredColor))

    // different way of managing transforms for gltf nodes
    val activatorID = button.mesh!!.nodeNameToId.get("buttonTriggerGeo")!!
    val activatorMatrix = button.mesh!!.getNodeLocalMatrix(activatorID)
    val originalTransform = getPositionFromMatrix(activatorMatrix)
    val startY = originalTransform.y
    val pose = Pose(originalTransform, Quaternion(1f, 0f, 0f, 0f))

    // setup click animation
    clickAnim.apply {
      duration = 400
      interpolator = OvershootInterpolator(2f)
      addUpdateListener {
        val t = it.animatedValue as Float
        pose.t.y = startY - t * 0.01f
        button.setLocalNodePose(activatorID, pose)
      }
    }

    // setup hover animation
    hoverAnim.apply {
      duration = 400
      addUpdateListener {
        val value = it.animatedValue as Float
        val colorI = ArgbEvaluator().evaluate(value, unhoveredColor, hoveredColor) as Int
        mat?.setAlbedoColor(Color.valueOf(colorI))
      }
    }

    button.addInputListener(
        object : InputListener {
          override fun onHoverStart(receiver: SceneObject, sourceOfInput: Entity) {
            if (!wasClicked) hoverAnim.start()
          }

          override fun onHoverStop(receiver: SceneObject, sourceOfInput: Entity) {
            if (!wasClicked) hoverAnim.reverse()
          }

          override fun onClick(receiver: SceneObject, hitInfo: HitInfo, sourceOfInput: Entity) {
            if (wasClicked) return
            wasClicked = true
            clicked()

            // set button color to green
            hoverAnim.cancel()
            mat?.setAlbedoColor(Color.valueOf(clickedColor))
            clickAnim.start()

            // play button click sound
            val clickSound = SceneAudioAsset.loadLocalFile("click.ogg")
            activity.scene.playSound(clickSound, hitInfo.point, 1f)
          }
        })
  }

  private fun getPositionFromMatrix(matrix: Matrix44): Vector3 {
    return Vector3(matrix.m30, matrix.m31, matrix.m32)
  }
}
