// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.camera.models

import android.util.Size
import com.meta.pixelandtexel.scanner.objectdetection.camera.enums.CameraEye
import com.meta.pixelandtexel.scanner.objectdetection.math.MathUtils
import com.meta.pixelandtexel.scanner.objectdetection.math.Plane
import com.meta.pixelandtexel.scanner.objectdetection.math.Ray
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3

/**
 * Represents the intrinsic and extrinsic properties of a camera. This class stores information
 * about the camera's position, orientation, lens characteristics, and image resolution. It also
 * provides utility methods for calculations related to screen points and camera rays.
 *
 * Adapted from:
 * https://github.com/oculus-samples/Unity-PassthroughCameraApiSamples/blob/main/Assets/PassthroughCameraApiSamples/PassthroughCamera/Scripts/PassthroughCameraUtils.cs
 *
 * @property eye Specifies which [CameraEye] eye the camera properties correspond to (e.g. LEFT or
 *   RIGHT).
 * @property translation The [Vector3] position of the camera in 3D space.
 * @property rotation The [Quaternion] orientation of the camera.
 * @property focalLength The focal length of the camera lens, typically in pixels, for both x and y
 *   axes in a [Vector2].
 * @property principalPoint The optical center of the image, typically in pixels, in a [Vector2].
 * @property resolution The [Size] width and height of the camera's image sensor or output image, in
 *   pixels.
 */
class CameraProperties(
    val eye: CameraEye,
    val translation: Vector3,
    val rotation: Quaternion,
    val focalLength: Vector2,
    val principalPoint: Vector2,
    val resolution: Size
) {
  val fov: Float

  init {
    // calculate the horizontal field of view in degrees

    val left = Vector2(0f, resolution.height.toFloat() / 2f)
    val right = Vector2(resolution.width.toFloat(), resolution.height.toFloat() / 2f)
    val leftSidePointInCamera = screenPointToRayInCamera(left)
    val rightSidePointInCamera = screenPointToRayInCamera(right)
    fov = leftSidePointInCamera.angleBetweenDegrees(rightSidePointInCamera)
  }

  /**
   * Retrieves the pose (position and rotation) of the camera relative to the head.
   *
   * @return A [Pose] object representing the camera's translation and rotation.
   */
  fun getHeadToCameraPose(): Pose {
    return Pose(translation, rotation)
  }

  /**
   * Converts a 2D point on the screen (in pixel coordinates) to a normalized 3D ray originating
   * from the camera's optical center and passing through that screen point, represented in the
   * camera's coordinate system. The y-coordinate of the screen point is assumed to be from the
   * top-left corner.
   *
   * @param screenPoint The [Vector2] 2D coordinates (x, y) of the point on the screen.
   * @return A normalized [Vector3] representing the direction of the ray in camera space. The ray's
   *   z-component is 1f, indicating it points forward from the camera.
   */
  fun screenPointToRayInCamera(screenPoint: Vector2): Vector3 {
    val direction =
        Vector3(
                x = (screenPoint.x - principalPoint.x) / focalLength.x,
                y = ((resolution.height - screenPoint.y) - principalPoint.y) / focalLength.y,
                z = 1f)
            .normalize()
    return direction
  }

  /**
   * Projects a 2D screen point (in pixel coordinates) onto a virtual 3D plane located at a
   * specified distance in front of the camera.
   *
   * @param screenPoint The 2D coordinates (x, y) of the point on the screen, in a [Vector2].
   * @param viewDistance The distance of the virtual view plane from the camera origin along the
   *   camera's forward axis.
   * @return A [Vector3] representing the 3D coordinates of the projected point on the view plane,
   *   in the camera's coordinate system.
   */
  fun screenPointToPointOnViewPlane(screenPoint: Vector2, viewDistance: Float): Vector3 {
    val viewPlane = Plane(Vector3.Forward * viewDistance, -Vector3.Forward)
    val direction = screenPointToRayInCamera(screenPoint)
    val intersection = MathUtils.rayPlaneIntersection(Ray(Vector3(0f), direction), viewPlane)
    return intersection!!
  }
}
