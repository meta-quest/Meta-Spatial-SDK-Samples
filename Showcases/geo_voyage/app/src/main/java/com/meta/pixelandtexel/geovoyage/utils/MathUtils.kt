// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.utils

import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object MathUtils {
  const val PIf: Float = 3.1415927f

  fun smoothOver(dt: Float, convergenceFraction: Float): Float {
    // standardize frame rate for interpolation
    val smoothTime = 1f / 60f
    return 1f - (1f - convergenceFraction.toDouble()).pow((dt / smoothTime).toDouble()).toFloat()
  }

  fun lerp(from: Float, to: Float, fraction: Float): Float {
    return from + (to - from) * fraction
  }

  /** Extension functions */
  fun Float.clamp(lower: Float, upper: Float): Float {
    return max(lower, min(upper, this))
  }

  fun Float.clamp01(): Float {
    return this.clamp(0f, 1f)
  }

  fun Vector3.lengthSq(): Float {
    return this.x * this.x + this.y * this.y + this.z * this.z
  }

  fun Vector3.angle(other: Vector3): Float {
    val dot = this.dot(other)
    val thisLength = this.length()
    val otherLength = other.length()

    return acos(dot / (thisLength * otherLength))
  }

  fun Vector3.yawAngle(other: Vector3): Float {
    val thisVxz = Vector2(this.x, this.z)
    val otherVxz = Vector2(other.x, other.z)

    val dot = thisVxz.dot(otherVxz)
    val thisLength = thisVxz.length()
    val otherLength = otherVxz.length()

    val angle = acos(dot / (thisLength * otherLength))

    val cross = thisVxz.cross(otherVxz)
    val sign = if (cross >= 0) 1 else -1

    return sign * angle
  }

  fun Vector3.pitchAngle(other: Vector3): Float {
    // calculate the pitch of each vector on the XZ plane
    val p1 = atan2(-this.y, sqrt(this.x * this.x + this.z * this.z))
    val p2 = atan2(-other.y, sqrt(other.x * other.x + other.z * other.z))

    return p1 - p2
  }

  /**
   * A static extension method to construct a quaternion representation of a rotation around an axis
   *
   * @param axis The axis around which to rotate
   * @param angleDegrees The amount of degrees to rotate
   * @return A quaternion representing the degrees of rotation around the axis
   */
  fun Quaternion.Companion.fromAxisAngle(axis: Vector3, angleDegrees: Float): Quaternion {
    val angleRadians = angleDegrees * PIf / 180f
    val halfAngle = angleRadians / 2
    val sinHalfAngle = sin(halfAngle)

    return Quaternion(
            cos(halfAngle), axis.x * sinHalfAngle, axis.y * sinHalfAngle, axis.z * sinHalfAngle)
        .normalize()
  }
}
