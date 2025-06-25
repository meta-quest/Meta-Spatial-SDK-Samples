/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.anchor

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKAnchor
import com.meta.spatial.mruk.MRUKLabel
import com.meta.spatial.mruk.MRUKPlane
import com.meta.spatial.mruk.getSize
import com.meta.spatial.mruk.hasLabel
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.samples.premiummediasample.AnchorOnLoad
import com.meta.spatial.samples.premiummediasample.Anchorable
import com.meta.spatial.samples.premiummediasample.fromAbsoluteToLocal
import com.meta.spatial.samples.premiummediasample.getHeadPose
import com.meta.spatial.samples.premiummediasample.hitTestBox
import com.meta.spatial.samples.premiummediasample.lookAt
import com.meta.spatial.samples.premiummediasample.projectPointOntoPlane
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.getAbsoluteTransform
import kotlin.math.absoluteValue

class AnchorSnappingSystem() : SystemBase() {
  private val rotationMap = mutableMapOf<Entity, Quaternion>()

  private var planeSnapSize = 0.1f
  private var anchorsLoaded = false

  override fun execute() {
    // Handle one-time placement on app start
    if (!anchorsLoaded) {
      if (!areMrukAndHeadLoaded()) return
      anchorsLoaded = true
      initSnapToAnchors(getHeadPose())
      return
    }

    // Grab all the anchorable objects, and also the walls/ceiling/floors
    val anchorables = Query.where { has(Anchorable.id, Transform.id, Grabbable.id) }.eval()
    val planes = Query.where { has(MRUKPlane.id, Transform.id, MRUKAnchor.id) }.eval()

    for (anchorable in anchorables) {
      if (!anchorable.getComponent<Grabbable>().isGrabbed) continue
      processGrabbedAnchorable(anchorable, planes)
    }
  }

  // Check if the MRUK Planes and head position are loaded
  private fun areMrukAndHeadLoaded(): Boolean {
    val planesQuery = Query.where { has(MRUKPlane.id, Transform.id, MRUKAnchor.id) }
    if (planesQuery.eval().count() == 0) return false

    val headPose = getHeadPose()
    return !(headPose.t.x == 0f && headPose.t.y == 0f && headPose.t.z == 0f)
  }

  // Every frame, find the best anchor plane for grabbed anchorables
  private fun processGrabbedAnchorable(anchorable: Entity, planes: Sequence<Entity>) {
    val anchorablePose = getAbsoluteTransform(anchorable)
    var hitPlane = false

    for (plane in planes) {
      if (trySnapToPlaneAnchor(anchorable, anchorablePose, plane)) {
        hitPlane = true
        break
      }
    }

    if (!hitPlane) {
      // Not hit box, but we're grabbing and there are anchors, so store the previous rotation
      rotationMap[anchorable] = anchorablePose.q
    }
  }

  // Check if there are valid anchor planes to anchor to
  private fun trySnapToPlaneAnchor(
      anchorable: Entity,
      anchorablePose: Pose,
      plane: Entity
  ): Boolean {
    // Get values
    val planeTransform = getAbsoluteTransform(plane)
    val planeAnchor = plane.getComponent<MRUKAnchor>()

    // Ensure plane is either WALL, CEILING OR FLOOR
    if (!isValidPlaneAnchor(planeAnchor)) return false

    val planePosition = planeTransform.t
    val planeNormal = planeTransform.forward().normalize()
    val planeSize: Vector2 = plane.getComponent<MRUKPlane>().getSize()

    val headPosition = getHeadPose().t
    val movementOffset = (anchorablePose.t - headPosition)

    // Check if anchorable object centerpoint is hitting the walls/floor/ceiling
    if (hitTestBox(
        anchorablePose.t,
        planeTransform.t,
        Vector3(planeSize.x, planeSize.y, planeSnapSize),
        planeTransform.q)) {
      // Snap to plane
      snapToAnchorViaGrab(
          anchorable,
          anchorablePose,
          planeNormal,
          projectPointOntoPlane(anchorablePose.t, planePosition, planeNormal) +
              planeNormal.times(anchorable.getComponent<Anchorable>().offset),
          planeAnchor)
      // Break out of the loop; only check 1 plane at a time to prevent corner fightin
      return true
    } else {
      // Use backup raycast check if box test fails
      val hitInfo =
          doesRayIntersectPlane(
              headPosition, movementOffset, plane, maxRayLength = movementOffset.length())
      if (hitInfo != null) {
        // Snap to plane
        snapToAnchorViaGrab(anchorable, anchorablePose, planeNormal, hitInfo.point, planeAnchor)
        // Break out of the loop; only check 1 plane at a time to prevent corner fighting
        return true
      }
    }
    return false
  }

  // Continually snap/animate grabbed anchorable to discovered plane anchor every frame
  private fun snapToAnchorViaGrab(
      anchorable: Entity,
      anchorablePose: Pose,
      planeNormal: Vector3,
      snappedPosition: Vector3,
      planeAnchor: MRUKAnchor
  ) {
    // If wall, snap position and also adjust rotation
    if (planeAnchor.hasLabel(MRUKLabel.WALL_FACE)) {
      // Find the wall normal
      var adjustedRotation = lookAt(Vector3(0f, 0f, 1f), Vector3(0f, 1f, 0f), planeNormal)
      // It's back to front, Flip on X axis 180
      adjustedRotation =
          adjustedRotation.times(Quaternion(0f, 1f, 0f, 0f)) // Flip it 180 to be correct

      // Slerp with previous rotation (not grabbed adjusted rotation, as that can be too strong)
      val previousRotation = rotationMap.getOrDefault(anchorable, anchorablePose.q)
      val slerpedRotation = previousRotation.slerp(adjustedRotation, 0.1f)

      // Lock rotation on walls
      anchorable.setComponent(
          Transform(fromAbsoluteToLocal(Pose(snappedPosition, slerpedRotation), anchorable)))
      rotationMap[anchorable] = slerpedRotation
    } else {
      // Snap just position (not rotation) to ceiling and floor
      anchorable.setComponent(
          Transform(fromAbsoluteToLocal(Pose(snappedPosition, anchorablePose.q), anchorable)))
    }
  }

  // One-time anchor snapping for all AnchorOnLoad entities when app starts
  private fun initSnapToAnchors(headPose: Pose) {
    val anchorOnLoadEntities = Query.where { has(AnchorOnLoad.id, Transform.id) }.eval()
    val planes = Query.where { has(MRUKPlane.id, Transform.id, MRUKAnchor.id) }.eval()

    for (anchorable in anchorOnLoadEntities) {
      snapToAnchorViaGaze(anchorable, headPose, planes)
    }
  }

  // Find and snap to plane anchors one time, when the app starts
  fun snapToAnchorViaGaze(
      anchorable: Entity,
      headPose: Pose,
      planesToCheck: Sequence<Entity>? = null,
      rescale: Boolean = false
  ) {
    val anchorablePose = getAbsoluteTransform(anchorable)
    val anchorOnLoad = anchorable.getComponent<AnchorOnLoad>()
    var hitPlane = false

    val planes: Sequence<Entity> =
        if (planesToCheck != null) planesToCheck
        else {
          val planesQuery = Query.where { has(MRUKPlane.id, Transform.id, MRUKAnchor.id) }
          planesQuery.eval()
        }

    for (plane in planes) {
      // Get values
      val planeAnchor = plane.getComponent<MRUKAnchor>()

      // Check pane is either WALL, CEILING OR FLOOR
      if (!isValidPlaneAnchor(planeAnchor)) {
        continue
      }
      val planeTransform = getAbsoluteTransform(plane)
      val planeNormal = planeTransform.forward().normalize()
      val headToAnchorableDirection: Vector3 = (anchorablePose.t - headPose.t).normalize()
      val hitInfo = doesRayIntersectPlane(headPose.t, headToAnchorableDirection, plane)
      if (hitInfo != null) {
        val distanceToWall = (hitInfo.point - headPose.t).length()

        // Don't anchor on a wall that's farther than our max distance, distanceCheck
        if (distanceToWall > anchorable.getComponent<AnchorOnLoad>().distanceCheck) {
          continue
        }

        hitPlane = true

        // Calculate and set new pose
        val newPoseAbsolute =
            calculatePoseFromAnchorPlane(
                anchorable, anchorablePose, planeAnchor, planeNormal, hitInfo.point)
        anchorable.setComponent(Transform(fromAbsoluteToLocal(newPoseAbsolute, anchorable)))

        // Set new scale if needed (keeps the old FOV)
        if (rescale || anchorOnLoad.scaleProportional) {
          val scalePercent =
              (headPose.t - newPoseAbsolute.t).length() / (headPose.t - anchorablePose.t).length()
          val scale = anchorable.tryGetComponent<Scale>()
          if (scale != null) {
            scale.scale *= scalePercent
            anchorable.setComponent(scale)
          }
        }
      }

      // Only snap/hit 1 plane per entity
      if (hitPlane) break
    }
  }

  private fun isValidPlaneAnchor(anchor: MRUKAnchor): Boolean {
    return anchor.hasLabel(MRUKLabel.WALL_FACE) ||
        anchor.hasLabel(MRUKLabel.CEILING) ||
        anchor.hasLabel(MRUKLabel.FLOOR)
  }

  // Find the correct anchorable position and rotation based on the plane anchor pose
  private fun calculatePoseFromAnchorPlane(
      anchorable: Entity,
      anchorablePose: Pose,
      planeAnchor: MRUKAnchor,
      planeNormal: Vector3,
      hitPoint: Vector3
  ): Pose {
    val newPoseAbsolute = Pose()
    // If wall, snap position and also adjust rotation
    if (planeAnchor.hasLabel(MRUKLabel.WALL_FACE)) {
      // Find the wall normal
      var adjustedRotation = lookAt(Vector3(0f, 0f, 1f), Vector3(0f, 1f, 0f), planeNormal)
      // It's back to front, Flip on X axis 180
      adjustedRotation =
          adjustedRotation.times(Quaternion(0f, 1f, 0f, 0f)) // Flip it 180 to be correct

      val normalOffset = planeNormal * anchorable.getComponent<Anchorable>().offset
      // Lock rotation on walls
      newPoseAbsolute.t = hitPoint + normalOffset
      newPoseAbsolute.q = adjustedRotation
    } else {
      // Snap just position (not rotation) to ceiling and floor
      newPoseAbsolute.t = hitPoint
      newPoseAbsolute.q = anchorablePose.q
    }

    return newPoseAbsolute
  }

  private fun doesRayIntersectPlane(
      rayOrigin: Vector3,
      rayDirection: Vector3,
      plane: Entity,
      epsilon: Float = 1e-6f,
      maxRayLength: Float = Float.MAX_VALUE,
  ): HitInfo? {
    val planePose = getAbsoluteTransform(plane)
    val mrukPlane = plane.getComponent<MRUKPlane>()

    // Step 1: Extract the transform's position and rotation (Pose) from the Transform object
    val planePosition = planePose.t // The position of the plane in world space (translation)
    val planeRotation = planePose.q // The rotation of the plane (as a quaternion or matrix)

    // Step 2: Transform the plane's normal and up vectors using the plane's rotation
    val localPlaneNormal =
        Vector3(0f, 0f, 1f) // Assume the plane's normal is originally pointing up (in local space)
    val localPlaneUp = Vector3(0f, 1f, 0f) // The up vector in local space
    val planeNormal = planeRotation.times(localPlaneNormal) // Apply rotation to the normal
    val planeUp = planeRotation.times(localPlaneUp) // Apply rotation to the up vector

    // The plane's right vector (perpendicular to planeUp and planeNormal)
    val planeRight = planeNormal.cross(planeUp).normalize()

    // Step 3: Ray-Plane intersection test (same as before)
    val normalizedRayDirection = rayDirection.normalize()
    val denominator = normalizedRayDirection.dot(planeNormal)

    if (denominator.absoluteValue < epsilon) {
      return null // No intersection if the ray is parallel to the plane
    }

    val t = (planePosition - rayOrigin).dot(planeNormal) / denominator
    if (t < 0f || t > maxRayLength) {
      return null // No valid intersection in the ray's forward direction
    }

    val intersectionPoint = rayOrigin + normalizedRayDirection * t

    // Step 4: Check if the intersection point lies within the plane's bounds
    val relativeIntersection = intersectionPoint - planePosition

    val distanceOnRight = relativeIntersection.dot(planeRight)
    val distanceOnUp = relativeIntersection.dot(planeUp)

    val minBounds = mrukPlane.min
    val maxBounds = mrukPlane.max

    if (!((distanceOnRight >= minBounds.x && distanceOnRight <= maxBounds.x) &&
        (distanceOnUp >= minBounds.y && distanceOnUp <= maxBounds.y))) {
      return null
    }

    return HitInfo(Entity.nullEntity(), 0, 0, 0, 0f, intersectionPoint, planeNormal, Vector2())
  }
}
