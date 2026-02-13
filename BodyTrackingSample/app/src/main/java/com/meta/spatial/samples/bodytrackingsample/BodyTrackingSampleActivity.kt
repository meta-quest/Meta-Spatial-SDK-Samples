/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.bodytrackingsample

import android.net.Uri
import android.os.Bundle
import com.meta.spatial.core.Color4
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SpatialSDKExperimentalAPI
import com.meta.spatial.core.SpatialSDKInternalTestingAPI
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.BodyTrackingFidelity
import com.meta.spatial.runtime.ControllerPose
import com.meta.spatial.runtime.JointPose
import com.meta.spatial.runtime.JointSet
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SkeletonJoint
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Box
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.vr.VRFeature

@OptIn(SpatialSDKExperimentalAPI::class)
class BodyTrackingSampleActivity : AppSystemActivity() {
  // Cubes to represent the skeleton bones
  private var boneCubes: MutableList<Entity> = mutableListOf()

  // Variables for body tracking
  private var skeletonChangedCount = -1
  private val jointPoses: MutableList<ControllerPose> = mutableListOf()
  private val skeletonPoses: MutableList<SkeletonJoint> = mutableListOf()

  override fun registerRequiredOpenXRExtensions(): List<String> {
    return listOf("XR_META_body_tracking_full_body", "XR_META_body_tracking_fidelity")
  }

  override fun registerFeatures(): List<SpatialFeature> {
    return listOf(VRFeature(this))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  @OptIn(SpatialSDKInternalTestingAPI::class, SpatialSDKExperimentalAPI::class)
  override fun onSceneReady() {
    super.onSceneReady()
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)
    registerTestingIntentReceivers()

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f,
    )
    scene.updateIBLEnvironment("environment.env")

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

    scene.setBodyTrackingJointSet(JointSet.FULL_BODY)
    scene.setBodyTrackingFidelity(BodyTrackingFidelity.HIGH)
  }

  @OptIn(SpatialSDKExperimentalAPI::class)
  override fun onSceneTick() {
    super.onSceneTick()
    if (scene.updateBodyTrackingBuffersAtTime(jointPoses, skeletonPoses)) {

      val jointsCount = skeletonPoses.size

      // Check if we need to to create or update the skeleton
      if (scene.getSkeletonChangedCount() != skeletonChangedCount) {
        skeletonChangedCount = scene.getSkeletonChangedCount()

        // Destroy old cubes
        for (cube in boneCubes) {
          cube.destroy()
        }
        boneCubes.clear()

        // Create a cube for each bone
        for (i in 2..<jointsCount) {
          val fromJoint = skeletonPoses[skeletonPoses[i].parentJointIndex]
          val toJoint = skeletonPoses[skeletonPoses[i].jointIndex]

          val p0 = fromJoint.pose.t
          val p1 = toJoint.pose.t
          val d = p1 - p0
          var h = d.length()

          val look = Quaternion.lookRotation(d.normalize())

          val entity =
              Entity.create(
                  listOf(
                      Box(
                          Vector3(
                              -0.005f,
                              -0.005f,
                              0f,
                          ),
                          Vector3(
                              0.005f,
                              0.005f,
                              h,
                          ),
                      ),
                      Mesh(Uri.parse("mesh://box")),
                      Material().apply { baseColor = Color4(0.0f, 0.0f, 1.0f, 1.0f) },
                      Transform(Pose(p0, look)),
                  )
              )
          boneCubes.add(entity)
        }
      }

      // If we have a skeleton, update the bone cubes poses
      if (skeletonChangedCount != -1) {
        for (i in 2..<jointsCount) {
          val fromJoint = jointPoses[skeletonPoses[i].parentJointIndex]
          val toJoint = jointPoses[skeletonPoses[i].jointIndex]
          if (
              fromJoint.flags and JointPose.ValidBits != 0 &&
                  toJoint.flags and JointPose.ValidBits != 0
          ) {

            val p0 = fromJoint.pose.t
            val p1 = toJoint.pose.t
            val d = p1 - p0
            val h = d.length()

            val look = Quaternion.lookRotation(d.normalize())

            boneCubes[i - 2].setComponent(Transform(Pose(p0, look)))
          }
        }
      }
    }
  }

  @OptIn(SpatialSDKExperimentalAPI::class)
  override fun onDestroy() {
    scene.releaseBodyTrackingBuffers()
    super.onDestroy()
  }
}
