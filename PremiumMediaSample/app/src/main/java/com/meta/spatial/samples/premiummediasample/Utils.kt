/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.TriangleMesh
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelCreationSystem
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.getAbsoluteTransform
import kotlin.math.atan

// Used for panel registration
var temporalID: Int = 1500000

// Creates temporal ids to identify entities during runtime
fun getDisposableID(): Int {
  temporalID += 1
  return temporalID
}

// Unregister panel removes functions from maps, to stop these functions from leaking objects
fun AppSystemActivity.unregisterPanel(panelId: Int) {
  panelRegistrations.remove(panelId)
  systemManager.findSystem<PanelCreationSystem>().panelCreator.remove(panelId)
}

// Function to project a point onto a plane
fun projectPointOntoPlane(point: Vector3, planePoint: Vector3, planeNormal: Vector3): Vector3 {
  // Vector from plane to point
  val pointToPlane = point - planePoint

  // Project point onto plane by subtracting the component of pointToPlane along the normal
  val distanceToPlane = pointToPlane.dot(planeNormal)
  val projection = point - planeNormal * distanceToPlane

  return projection
}

// Function to project a ray onto a plane
fun projectRayOntoPlane(
    rayOrigin: Vector3,
    rayDirection: Vector3,
    planePoint: Vector3,
    planeNormal: Vector3,
): Vector3? {
  // Normalize the plane normal and ray direction
  val normalizedPlaneNormal = planeNormal.normalize()
  val normalizedRayDirection = rayDirection.normalize()

  // Compute the dot product between the ray direction and the plane normal
  val denominator = normalizedRayDirection.dot(normalizedPlaneNormal)

  // If the denominator is 0, the ray is parallel to the plane (no intersection)
  if (denominator == 0f) {
    return null // No intersection
  }

  // Compute the parameter t for the intersection point
  val t = (planePoint - rayOrigin).dot(normalizedPlaneNormal) / denominator

  // If t < 0, the intersection is behind the ray origin, so ignore it
  if (t < 0f) {
    return null // No valid intersection in the ray's forward direction
  }

  // Calculate the intersection point
  val intersectionPoint = rayOrigin + normalizedRayDirection * t

  return intersectionPoint
}

fun hitTestBox(
    point: Vector3,
    boxCenter: Vector3,
    boxSize: Vector3,
    boxRotation: Quaternion,
): Boolean {
  // Transform the point into the box's local space
  val inverseRotation = boxRotation.inverse()
  val localPoint = inverseRotation * (point - boxCenter)

  // Calculate half the size of the box
  val halfSizeX = boxSize.x * 0.5f
  val halfSizeY = boxSize.y * 0.5f
  val halfSizeZ = boxSize.z * 0.5f

  // Check if the point is within the bounds of the box along each axis
  val insideX = localPoint.x >= -halfSizeX && localPoint.x <= halfSizeX
  val insideY = localPoint.y >= -halfSizeY && localPoint.y <= halfSizeY
  val insideZ = localPoint.z >= -halfSizeZ && localPoint.z <= halfSizeZ

  return insideX && insideY && insideZ
}

// Function to make the object "look at" a target position (in your case, the plane normal)
fun lookAt(forward: Vector3, up: Vector3, targetDirection: Vector3): Quaternion {
  val targetDirNormalized = targetDirection.normalize()
  val forwardNormalized = forward.normalize()

  // If the direction is already aligned, return identity rotation
  if (forwardNormalized == targetDirNormalized) {
    return Quaternion() // Identity
  }

  // Calculate the rotation axis (cross product of forward and target)
  val rotationAxis = forwardNormalized.cross(targetDirNormalized).normalize()

  // Calculate the angle between the forward vector and target direction (dot product and arccos)
  val dotProduct = forwardNormalized.dot(targetDirNormalized).coerceIn(-1f, 1f) // Clamp for safety
  val angle = Math.acos(dotProduct.toDouble()).toFloat() // Angle in radians

  // Create a quaternion that represents this rotation
  return fromAxisAngle(rotationAxis, angle)
}

fun fromAxisAngle(axis: Vector3, angle: Float): Quaternion {
  val halfAngle = angle * 0.5f
  val sinHalfAngle = kotlin.math.sin(halfAngle)
  return Quaternion(
      axis.x * sinHalfAngle,
      axis.y * sinHalfAngle,
      axis.z * sinHalfAngle,
      kotlin.math.cos(halfAngle),
  )
}

// Converts from Global to Local Pose
// Will convert to local co-ordinates that could be added to entity's parent to become global again
fun fromAbsoluteToLocal(globalPose: Pose = Pose(), localRelativeTo: Entity): Pose {
  if (localRelativeTo.hasComponent<TransformParent>()) {
    val parentGlobal = getAbsoluteTransform(localRelativeTo.getComponent<TransformParent>().entity)
    return parentGlobal.inverse().times(globalPose)
  }
  return globalPose
}

fun setAbsolutePosition(entity: Entity, abolutePosition: Pose) {
  val transformParent = entity.tryGetComponent<TransformParent>()
  if (transformParent == null) {
    entity.setComponent(Transform(abolutePosition))
    return
  }

  val parentsAbsolute = transformParent.entity.getComponent<Transform>()
  val localPose = parentsAbsolute.transform.inverse().times(abolutePosition)
  entity.setComponent(Transform(localPose))
}

fun getControllers(): List<Controller> {
  val controllersEval = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }
  val controllers = mutableListOf<Controller>()
  for (controllerEntity in controllersEval) {
    val controller = controllerEntity.getComponent<Controller>()
    controllers.add(controller)
  }
  return controllers
}

fun getAnyKeyDown(): Boolean {
  val controllers = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }
  for (entity in controllers) {
    val controller = entity.getComponent<Controller>()
    if (!controller.isActive) continue

    if (getAnyKeyDown(controller)) return true
  }
  return false
}

private val allButtonBits =
    ButtonBits.ButtonB.or(ButtonBits.ButtonA)
        .or(ButtonBits.ButtonX)
        .or(ButtonBits.ButtonY)
        .or(ButtonBits.ButtonTriggerL)
        .or(ButtonBits.ButtonTriggerR)

fun getAnyKeyDown(controller: Controller): Boolean {
  return ((controller.changedButtons.and(allButtonBits) > 0) &&
      (controller.buttonState.and(allButtonBits) > 0))
}

fun getKeyDown(buttonBitsMask: Int): Boolean {
  val controllers = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }
  for (entity in controllers) {
    val controller = entity.getComponent<Controller>()
    if (!controller.isActive) continue

    if (getKeyDown(controller, buttonBitsMask)) return true
  }
  return false
}

fun getKey(controller: Controller, buttonBitsMask: Int): Boolean {
  return controller.buttonState.and(buttonBitsMask) == buttonBitsMask
}

fun getKey(buttonBitsMask: Int): Boolean {
  val controllers = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }
  for (entity in controllers) {
    val controller = entity.getComponent<Controller>()
    if (!controller.isActive) continue

    if (getKey(controller, buttonBitsMask)) return true
  }
  return false
}

fun getKeyDown(controllers: Collection<Controller>, buttonBitsMask: Int): Boolean {
  for (controller in controllers) {
    if (getKeyDown(controller, buttonBitsMask)) return true
  }
  return false
}

fun getKeyDown(controller: Controller, buttonBitsMask: Int): Boolean {
  return ((controller.changedButtons.and(buttonBitsMask) == buttonBitsMask) &&
      (controller.buttonState.and(buttonBitsMask) == buttonBitsMask))
}

fun getKeyUp(buttonBitsMask: Int): Boolean {
  val controllers = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }
  for (entity in controllers) {
    val controller = entity.getComponent<Controller>()
    if (!controller.isActive) continue

    if (getKeyUp(controller, buttonBitsMask)) return true
  }
  return false
}

fun getKeyUp(controllers: Collection<Controller>, buttonBitsMask: Int): Boolean {
  for (controller in controllers) {
    if (getKeyUp(controller, buttonBitsMask)) return true
  }
  return false
}

fun getKeyUp(controller: Controller, buttonBitsMask: Int): Boolean {
  return ((controller.changedButtons.and(buttonBitsMask) == buttonBitsMask) &&
      (controller.buttonState.and(buttonBitsMask) == 0))
}

// Function to get the pose of the user's head
fun getHeadPose(): Pose {
  val head =
      Query.where { has(AvatarAttachment.id) }
          .eval()
          .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
          .first()
  return head.getComponent<Transform>().transform
}

fun placeInFrontOfHead(
    entity: Entity,
    distanceAway: Float = 1f,
    offset: Vector3 = Vector3(0f, 0f, 0f),
    pivotType: GrabbableType = GrabbableType.PIVOT_Y,
    angleYAxisFromHead: Float = 0f,
) {
  val pose = getPoseInFrontOfHead(distanceAway, offset, pivotType, angleYAxisFromHead)

  // Set
  val transformParent: TransformParent? = entity.tryGetComponent<TransformParent>()
  if (transformParent !== null && transformParent.entity.id !== Entity.nullEntity().id) {
    setAbsolutePosition(entity, pose)
  } else {
    entity.setComponent(Transform(pose))
  }
}

fun getPoseInFrontOfHead(
    distanceAway: Float = 1f,
    offset: Vector3 = Vector3(0f, 0f, 0f),
    pivotType: GrabbableType = GrabbableType.PIVOT_Y,
    angleYAxisFromHead: Float = 0f,
    useHeadY: Boolean = true,
): Pose {
  // Get head position
  val pose = getHeadPose()
  return getPoseInFrontOfVector(
      pose,
      distanceAway = distanceAway,
      offset = offset,
      pivotType = pivotType,
      angleYAxisFromHead = angleYAxisFromHead,
      useHeadY = useHeadY,
  )
}

fun getPoseInFrontOfVector(
    vector: Pose,
    distanceAway: Float = 1f,
    offset: Vector3 = Vector3(0f, 0f, 0f),
    pivotType: GrabbableType = GrabbableType.PIVOT_Y,
    angleYAxisFromHead: Float = 0f,
    useHeadY: Boolean = true,
): Pose {
  val vectorHead = Pose(vector.t, vector.q)
  // Get head position
  if (angleYAxisFromHead != 0f) {
    vectorHead.q = vectorHead.q.times(Quaternion(0f, angleYAxisFromHead, 0f))
  }
  // Offset by distance
  val poseFoward = vectorHead.forward()
  var offsetFromHead: Vector3
  if (useHeadY) {
    poseFoward.y = 0f
    offsetFromHead = poseFoward.normalize() * distanceAway
  } else {
    offsetFromHead = poseFoward * distanceAway
  }
  // Add the extra user offset
  offsetFromHead += offset
  // Add to head pose for final destination
  vectorHead.t += offsetFromHead
  // Look rotation
  if (pivotType == GrabbableType.PIVOT_Y) {
    // Force look rotation to have no X/Z rotation
    offsetFromHead.y = 0f
    vectorHead.t.y = vectorHead.t.y // Force Y to prevent neck cramp
  }
  vectorHead.q = Quaternion.lookRotation(offsetFromHead)
  return vectorHead
}

fun quadTriangleMesh(
    width: Float = 1f,
    height: Float = 1f,
    material: SceneMaterial,
    xSubDivisions: Int = 1,
    ySubdivisions: Int = 1,
): TriangleMesh {
  val halfWidth = width * 0.5f
  val halfHeight = height * 0.5f

  val vertexLength = (xSubDivisions + 1) * (ySubdivisions + 1)
  val trianglesLength = xSubDivisions * ySubdivisions * 2

  val triangleMesh =
      TriangleMesh(
          vertexLength,
          trianglesLength * 3,
          intArrayOf(
              0,
              trianglesLength * 3,
          ), // what range of materials (fromMat0, toMat0, fromMat1, toMat1, fromMat2,
          // toMat2) do the materials array apply to
          arrayOf(material), // Materials
      )

  // Array Creations
  val vertices = FloatArray(vertexLength * 3)
  val normals = FloatArray(vertexLength * 3)
  val uvs = FloatArray(vertexLength * 2)
  val colors = IntArray(vertexLength) { android.graphics.Color.WHITE }
  val triangles = IntArray(trianglesLength * 3)

  // Initial setup of values
  val uStep = 1f / xSubDivisions
  val vStep = 1f / ySubdivisions
  var index = 0
  val widthStep = uStep * width
  val heightStep = vStep * height

  for (y in 0 until ySubdivisions + 1) {
    for (x in 0 until xSubDivisions + 1) {
      index = y * (xSubDivisions + 1) + x

      uvs[index * 2] = x * uStep
      uvs[index * 2 + 1] = 1 - y * vStep

      vertices[index * 3] = x * widthStep - halfWidth
      vertices[index * 3 + 1] = y * heightStep - halfHeight

      normals[index * 3 + 2] = 1f // Forward facing normal
    }
  }

  // Assign triangles
  var triangleIndex = 0

  for (y in 0 until ySubdivisions) {
    for (x in 0 until xSubDivisions) {
      // Calculate vertex indices for the current square
      val bottomLeft = y * (xSubDivisions + 1) + x
      val topLeft = bottomLeft + (xSubDivisions + 1)
      val topRight = topLeft + 1
      val bottomRight = bottomLeft + 1

      triangles[triangleIndex++] = bottomLeft
      triangles[triangleIndex++] = topLeft
      triangles[triangleIndex++] = bottomRight

      triangles[triangleIndex++] = bottomRight
      triangles[triangleIndex++] = topLeft
      triangles[triangleIndex++] = topRight
    }
  }

  // Update the triangles
  triangleMesh.updateGeometry(0, vertices, normals, uvs, colors)
  triangleMesh.updatePrimitives(0, triangles)

  return triangleMesh
}

fun getSize(entity: Entity): Vector3 {
  val panelDimensions = entity.tryGetComponent<PanelDimensions>()
  val scale = entity.tryGetComponent<Scale>()
  if (panelDimensions != null && scale != null) {
    return Vector3(
        panelDimensions.dimensions.x * scale.scale.x,
        panelDimensions.dimensions.y * scale.scale.y,
        scale.scale.z,
    )
  } else if (panelDimensions != null)
      return Vector3(panelDimensions.dimensions.x, panelDimensions.dimensions.y, 1f)
  else if (scale != null) return scale.scale

  return Vector3(1f)
}

fun setSize(entity: Entity, size: Vector3) {
  val scale = if (entity.hasComponent<Scale>()) entity.getComponent<Scale>() else Scale()
  val panel = entity.tryGetComponent<PanelDimensions>()
  if (panel != null) {
    scale.scale.x = if (panel.dimensions.x != 0f) size.x / panel.dimensions.x else 0f
    scale.scale.y = if (panel.dimensions.y != 0f) size.y / panel.dimensions.y else 0f
  }
  entity.setComponent(scale)
}

fun getFovFromSize(size: Vector3, distance: Float, basedOnWidth: Boolean = true): Float {
  return if (basedOnWidth) {
    Math.toDegrees(2 * atan(size.x.toDouble() / (2 * distance))).toFloat()
  } else {
    Math.toDegrees(2 * atan(size.y.toDouble() / (2 * distance))).toFloat()
  }
}

fun setDistanceAndSize(
    entity: Entity,
    size: Vector2,
    distance: Float,
    eyeAngle: Float = 0f,
    axisAngle: Float = 0f,
    angleContent: Boolean = false,
    vector: Pose = getHeadPose(),
) {
  val pose = getPoseInFrontOfVector(vector, distance, angleYAxisFromHead = axisAngle)

  if (eyeAngle != 0f) {
    val yDistance = (atan(Math.toRadians(eyeAngle.toDouble())) * distance).toFloat()
    pose.t += Vector3(0f, yDistance, 0f)
    if (angleContent) {
      pose.q *= Quaternion(-eyeAngle, 0f, 0f)
    }
  }
  entity.setComponent(Transform(pose))
  setSize(entity, Vector3(size.x, size.y, 1f))
}

fun updateSizeFromFov(
    entity: Entity,
    fov: Float,
    basedOnWidth: Boolean = true,
    vector: Pose = getHeadPose(),
) {
  val t = getAbsoluteTransform(entity)
  val distance = (vector.t - t.t).length()
  val size = getSizeFromFov(entity, distance, fov, basedOnWidth)
  setSize(entity, Vector3(size.x, size.y, 1f))
}

fun getSizeFromFov(
    entity: Entity,
    distance: Float,
    fov: Float,
    basedOnWidth: Boolean = true,
): Vector2 {
  val fovRad = Math.toRadians(fov.toDouble()).toFloat()

  // Get the aspect ratio from the entity size
  val size = getSize(entity)
  val aspectRatio = size.x / size.y

  // Calculate the size using the tangent of half the FOV
  val newSize =
      if (basedOnWidth) {
        val width = 2 * distance * kotlin.math.tan(fovRad / 2)
        Vector2(width, width / aspectRatio)
      } else {
        val height = 2 * distance * kotlin.math.tan(fovRad / 2)
        Vector2(height * aspectRatio, height)
      }
  return newSize
}

fun setDistanceFov(
    entity: Entity,
    distance: Float,
    fov: Float,
    basedOnWidth: Boolean = true,
    eyeAngle: Float = 0f,
    axisAngle: Float = 0f,
    angleContent: Boolean = false,
    vector: Pose = getHeadPose(),
) {
  // Convert FOV from degrees to radians
  val newSize = getSizeFromFov(entity, distance, fov, basedOnWidth)
  setDistanceAndSize(entity, newSize, distance, eyeAngle, axisAngle, angleContent, vector)
}

fun Int.millisToFloat(): Float {
  return (this).toFloat() / 1000
}
