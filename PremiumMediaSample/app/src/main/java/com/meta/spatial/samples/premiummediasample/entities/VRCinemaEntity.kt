/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.entities

import android.net.Uri
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.samples.premiummediasample.getHeadPose
import com.meta.spatial.samples.premiummediasample.getPoseInFrontOfVector
import com.meta.spatial.toolkit.Hittable
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible

class VRCinemaEntity(val config: VRCinemaConfig) {
  val entity: Entity
  val planes: MutableList<Entity>

  var _isVisible = true
  val isVisible
    get() = _isVisible

  init {
    entity = Entity.create(Transform())

    val cinemaSize = config.cinemaSize
    val negHalfCinemaSize = cinemaSize * -0.5f
    val meshName = "mesh://WallLightingSystem_" + MRUKLabel.WALL_FACE

    val faces =
        listOf(
            Vector3.Up,
            -Vector3.Up,
            Vector3.Right,
            -Vector3.Right,
            Vector3.Forward,
            -Vector3.Forward,
        )
    val sizes =
        listOf(
            Vector3(cinemaSize.x, cinemaSize.z, 1f),
            Vector3(cinemaSize.x, cinemaSize.z, 1f),
            Vector3(cinemaSize.z, cinemaSize.y, 1f),
            Vector3(cinemaSize.z, cinemaSize.y, 1f),
            Vector3(cinemaSize.x, cinemaSize.y, 1f),
            Vector3(cinemaSize.x, cinemaSize.y, 1f),
        )
    val rotations =
        listOf(
            Quaternion(-90f, 0f, 0f),
            Quaternion(90f, 180f, 0f),
            Quaternion(0f, 90f, 0f),
            Quaternion(0f, -90f, 0f),
            Quaternion(0f, 0f, 0f),
            Quaternion(-180f, 0f, 180f),
        )

    planes = mutableListOf()
    if (config.floorOnly) {
      val bigSize = (config.screenSize.x + config.screenSize.y) * 5f
      val index = 0
      planes.add(
          Entity.create(
              Mesh(Uri.parse(meshName)),
              Transform(
                  Pose(
                      negHalfCinemaSize * faces[index] +
                          Vector3(0f, 0f, cinemaSize.z * 0.5f - config.distanceBehindScreen),
                      rotations[index],
                  )),
              Hittable(MeshCollision.NoCollision),
              Scale(bigSize),
              TransformParent(entity),
              Visible(_isVisible),
          ))
    } else {
      faces.forEachIndexed { index, faceDirection ->
        planes.add(
            Entity.create(
                Mesh(Uri.parse(meshName)),
                Transform(Pose(negHalfCinemaSize * faceDirection, rotations[index])),
                Hittable(MeshCollision.NoCollision),
                Scale(sizes[index]),
                TransformParent(entity),
                Visible(_isVisible),
            ))
      }
    }

    setCinemaPoseRelativeToUser()
  }

  fun setVisible(visible: Boolean) {
    if (isVisible == visible) return

    _isVisible = visible

    for (entity in planes) entity.setComponent(Visible(visible))
  }

  fun getScreenPose(): Pose {
    val pose = entity.getComponent<Transform>()
    return Pose(
        pose.transform.t +
            pose.transform.forward() *
                (config.cinemaSize.z * 0.5f -
                    config.distanceBehindScreen), // push back to end of wall
        pose.transform.q * Quaternion(0f, 0f, 0f),
    )
  }

  fun setCinemaPoseRelativeToUser(headPose: Pose = getHeadPose()) {
    val cinemaPose =
        getPoseInFrontOfVector(
            headPose,
            (config.cinemaSize.z) * 0.5f - config.distanceToWallBehindYou,
        )
    cinemaPose.t +=
        Vector3(
            0f,
            config.screenSize.y / 6f,
            0f,
        ) // Adjust height to be 1/6th of screen up, so head is 1/3rd up from bottom of screen
    entity.setComponent(Transform(cinemaPose))
  }

  fun setCinemaPoseRelativeToTV(tvPose: Pose = getHeadPose(), userPose: Pose = getHeadPose()) {
    val cinemaPose =
        getPoseInFrontOfVector(
            tvPose,
            (config.cinemaSize.z) * 0.5f - config.distanceToWallBehindYou,
        )
    cinemaPose.t +=
        Vector3(
            0f,
            config.screenSize.y / 6f,
            0f,
        ) // Adjust height to be 1/6th of screen up, so head is 1/3rd up from bottom of screen
    cinemaPose.t += (userPose.t - tvPose.t)
    entity.setComponent(Transform(cinemaPose))
  }

  companion object {
    val TAG = "VRCinemaEntity"
  }

  data class VRCinemaConfig(
      val screenSize: Vector2 = Vector2(22f, 12.375f),
      val distanceToScreen: Float = 15f,
      val distanceToWallBehindYou: Float = 2f,
      val distanceBehindScreen: Float = 0.5f,
      val screenPadding: Float = 0.65f,
      val floorOnly: Boolean = false,
  ) {
    val cinemaSize: Vector3
      get() =
          Vector3(
              screenSize.x + screenPadding * 2,
              screenSize.y + screenPadding * 2f,
              distanceToScreen + distanceToWallBehindYou + distanceBehindScreen,
          )
  }
}
