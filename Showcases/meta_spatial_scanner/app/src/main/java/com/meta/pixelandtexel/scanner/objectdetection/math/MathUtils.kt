// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.math

import android.graphics.PointF
import android.graphics.Rect
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

object MathUtils {
  const val EPSILON = 1e-9
  const val DEG_TO_RAD = PI / 180f

  /**
   * Calculates the distance at which a panel must be spaced from the camera (at the specified
   * camera horizontal field of view and panel width) in order for the panel to exactly fill the
   * field of view, width-wise.
   *
   * @param fov The camera's horizontal field of view, in degrees.
   * @param size The width of the panel.
   * @return The distance at which to place the panel.
   */
  fun panelDistanceForSize(fov: Float, size: Float): Float {
    val rad = fov * DEG_TO_RAD
    val distance = (size / 2f) / tan(rad / 2f)
    return distance.toFloat()
  }

  /**
   * Tests whether or not a ray intersects with a plane, and returns the intersection point if so.
   *
   * @param ray The ray to test the intersection.
   * @param plane The plane against which to test the intersection
   * @return The 3D point representing the intersection, or null if there is no intersection.
   */
  fun rayPlaneIntersection(ray: Ray, plane: Plane): Vector3? {
    // direction vectors are too close to 0
    if (plane.normal.lengthSquared() < EPSILON * EPSILON ||
        ray.direction.lengthSquared() < EPSILON * EPSILON) {
      return null
    }

    val denom = plane.normal.dot(ray.direction)

    // check if ray is parallel to plane
    if (abs(denom) < EPSILON) {
      return null
    }

    // ray intersects; calculate distance parameter t
    val originToPlanePoint = plane.point - ray.origin
    val t = plane.normal.dot(originToPlanePoint) / denom

    // check if intersection is behind ray's origin
    if (t < -EPSILON) {
      return null
    }

    val intersectionPoint = ray.origin + ray.direction * t
    return intersectionPoint
  }

  fun Vector3.lengthSquared(): Float {
    return this.dot(this)
  }

  fun Vector3.copy(other: Vector3): Vector3 {
    this.x = other.x
    this.y = other.y
    this.z = other.z
    return this // for chaining
  }

  fun PointF.toVector2(): Vector2 {
    return Vector2(this.x, this.y)
  }

  /**
   * Computes the overlapping intersection between this Rect and another, setting the result to the
   * intersection, and returning a boolean representing whether or not there was any intersection.
   *
   * @param other The other [Rect] against with to test the intersection.
   * @param result The resulting [Rect] intersection overlap between the two rectangles.
   * @return Whether or not there was any intersection.
   */
  fun Rect.intersection(other: Rect, result: Rect): Boolean {
    val intersectLeft = max(this.left, other.left)
    val intersectTop = max(this.top, other.top)
    val intersectRight = min(this.right, other.right)
    val intersectBottom = min(this.bottom, other.bottom)

    val intersection = Rect(intersectLeft, intersectTop, intersectRight, intersectBottom)
    result.copy(intersection)

    return !result.isEmpty
  }

  fun Rect.copy(other: Rect) {
    this.left = other.left
    this.top = other.top
    this.right = other.right
    this.bottom = other.bottom
  }

  fun Rect.area(): Int {
    return this.width() * this.height()
  }
}
