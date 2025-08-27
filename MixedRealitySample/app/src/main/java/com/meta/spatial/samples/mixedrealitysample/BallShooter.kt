/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mixedrealitysample

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.physics.Physics
import com.meta.spatial.physics.PhysicsState
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import java.util.Timer
import java.util.TimerTask

class BallShooter(private val mesh: Mesh) : SystemBase() {

  public var enabled = true

  private fun shootBall(transform: Pose) {
    val speed = 6f
    val meshCopy = Mesh(mesh.mesh)
    val pose = Transform(transform)
    val physics =
        Physics().apply {
          shape = "sphere"
          dimensions = Vector3(.12f, .12f, .12f)
          state = PhysicsState.DYNAMIC
          linearVelocity = pose.transform.q * Vector3(0f, 0f, 1f) * speed
          angularVelocity = pose.transform.q * Vector3(10f, 0f, 0f)
          restitution = .99f
        }

    val entity = Entity.create(listOf(physics, meshCopy, pose))

    // destroy balls after some time
    delayAction(entity::destroy, 8000)
  }

  override fun execute() {

    // detect trigger presses
    val controllers = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }
    controllers.forEach { controller ->
      val c = controller.getComponent<Controller>()
      if (
          (c.buttonState and
              c.changedButtons and
              (ButtonBits.ButtonTriggerL or ButtonBits.ButtonTriggerR)) != 0
      ) {
        if (enabled) shootBall(controller.getComponent<Transform>().transform)
      }
    }
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
}
