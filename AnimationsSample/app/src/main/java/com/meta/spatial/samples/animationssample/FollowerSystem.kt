/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import com.meta.spatial.core.Pose
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Transform

/** This system moves the entity towards the target. */
class FollowerSystem() : SystemBase() {

  private var prevTime = System.currentTimeMillis()
  private val rotationSpeed = .05f
  private val smoothTime = 1f / 60f

  override fun execute() {

    val targetQuery = Query.where { has(FollowerTarget.id, Transform.id) }
    val targets = targetQuery.eval()

    // delta time
    val currentTime = System.currentTimeMillis()
    val deltaTime = (currentTime - prevTime) / 1000f
    prevTime = currentTime

    val followerEntities =
        Query.where { has(FollowerComponent.id, Transform.id) }
            .filter { by(FollowerComponent.enabledData).isEqualTo(true) }
            .eval()
    followerEntities.forEach { followerEntity ->
      val follower = followerEntity.getComponent<FollowerComponent>()

      // match target by name (there should only be one)
      val target =
          targets
              .filter { it.getComponent<FollowerTarget>().targetName == follower.targetName }
              .firstOrNull()

      if (target == null) return

      // get references
      val followerTransform = followerEntity.getComponent<Transform>()
      var followerPose: Pose = followerTransform.transform
      var targetPose: Pose = target.getComponent<Transform>().transform

      // apply follow offset
      var offsetTargetPose = targetPose.t.add(follower.followOffset)

      // set a minimum follow distance
      val followDistance =
          (followerPose.t - offsetTargetPose).normalize().multiply(follower.followDistance)
      followerPose.t =
          expDecay(followerPose.t, offsetTargetPose + followDistance, follower.expDecay, deltaTime)

      // set the transform to apply the changes
      followerEntity.setComponent(followerTransform)
    }
  }

  /**
   * Exponential decay function from Freya Holmer's presentation on lerp smoothing.
   *
   * @param a The current position.
   * @param b The target position.
   * @param decay A value between 1 and 25, representing the speed of the decay (slow to fast).
   * @param deltaTime The time since the last update in seconds.
   * @return The new position after exponential decay.
   */
  fun expDecay(a: Vector3, b: Vector3, decay: Float, deltaTime: Float): Vector3 {
    return b + (a - b) * Math.exp(-decay * deltaTime.toDouble()).toFloat()
  }
}
