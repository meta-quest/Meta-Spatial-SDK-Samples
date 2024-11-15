/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Followable
import com.meta.spatial.toolkit.Transform

/** This system handles drone rotations */
class DroneSystem(val droneSceneController: DroneSceneController?) : SystemBase() {

  private var prevTime = System.currentTimeMillis()
  private val smoothTime = 1f / 60f
  private val prevPositionMap = mutableMapOf<DroneComponent, Vector3>()

  override fun execute() {

    val targetQuery = Query.where { has(FollowerTarget.id, Transform.id) }
    val droneTargets = targetQuery.eval()

    // delta time
    val currentTime = System.currentTimeMillis()
    val deltaTime = (currentTime - prevTime) / 1000f
    prevTime = currentTime

    val droneEntities = Query.where { has(DroneComponent.id, Transform.id) }.eval()
    droneEntities.forEach { droneEnt ->
      val droneComponent = droneEnt.getComponent<DroneComponent>()
      if (!droneComponent.enabled) return

      // match target by name (there should only be one)
      val target =
          droneTargets
              .filter { it.getComponent<FollowerTarget>().targetName == droneComponent.targetName }
              .firstOrNull()

      if (target == null) return

      // There are two types of followable in this Sample. The built-in Followable component in
      // toolkit and the custom one here. We use the custom one to allow for faster rotation
      // mimicing a real drone, but this also shows how you can add a followable component to an
      // entity without extra work.
      val followable = droneEnt.getComponent<Followable>()
      if (target.getComponent<FollowerTarget>().isBuiltInFollower) {
        followable.target = target
        followable.offset = Pose(Vector3(0f, 0f, 1f), Quaternion(1f, 0f, 0f, 0f))
        if (followable.active) return
        followable.active = true
        droneEnt.setComponent(followable)
        return
      } else if (followable.active) {
        followable.active = false
        droneEnt.setComponent(followable)
      }

      // get references
      val droneTransform = droneEnt.getComponent<Transform>()
      var dronePose: Pose = droneTransform.transform
      var targetPose: Pose = target.getComponent<Transform>().transform

      // apply rotation with lookat
      var rotation: Quaternion = Quaternion.lookRotationAroundY(targetPose.t - dronePose.t)

      // apply tilt based on forward movement speed
      val velocity = dronePose.t.sub(prevPositionMap.get(droneComponent) ?: Vector3(0f, 0f, 0f))
      val xzMovement = velocity.multiply(Vector3(1f, 0f, 1f))
      val tilt = xzMovement.length() / deltaTime / smoothTime * droneComponent.tiltFactor
      rotation *= Quaternion(tilt, 0f, 0f)

      // smooth rotation with slerp
      dronePose.q = dronePose.q.slerp(rotation, smoothOver(deltaTime, droneComponent.rotationSpeed))

      // set the transform to apply the changes
      droneEnt.setComponent(droneTransform)

      // store the previous position for velocity calculation
      prevPositionMap.put(droneComponent, dronePose.t)
    }

    droneSceneController?.tick()
  }

  private fun smoothOver(dt: Float, convergenceFraction: Float): Float {
    return 1f -
        Math.pow(1f - convergenceFraction.toDouble(), (dt / smoothTime).toDouble()).toFloat()
  }
}
