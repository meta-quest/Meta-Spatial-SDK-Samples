// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.utils

import com.meta.spatial.core.Bound3D
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object MathUtils {
  /**
   * Constructs a quaternion from an axis-angle representation of a rotation.
   *
   * @param axis The 3D [Vector3] axis around which to perform the rotation.
   * @param angleDegrees The angle in degrees of rotation to perform around the axis
   * @return The [Quaternion] representing the rotation.
   */
  fun Quaternion.Companion.fromAxisAngle(axis: Vector3, angleDegrees: Float): Quaternion {
    val angleRadians = angleDegrees * PI / 180f
    val halfAngle = angleRadians / 2
    val sinHalfAngle = sin(halfAngle).toFloat()

    return Quaternion(
            cos(halfAngle).toFloat(),
            axis.x * sinHalfAngle,
            axis.y * sinHalfAngle,
            axis.z * sinHalfAngle,
        )
        .normalize()
  }

  /**
   * Creates a [Quaternion] representing a rotation in 3D space that is the combination of the
   * supplied rotations in degrees around the pitch, yaw, and roll axes, in that order.
   *
   * @param pitchDeg The angle in degrees to apply to the rotation around the x axis.
   * @param yawDeg The angle in degrees to apply to the rotation around the y axis.
   * @param rollDeg The angle in degrees to apply to the rotation around the z axis.
   * @return The [Quaternion] representing the rotation around the axes in sequential order.
   */
  fun Quaternion.Companion.fromSequentialPYR(
      pitchDeg: Float,
      yawDeg: Float,
      rollDeg: Float,
  ): Quaternion {
    return Quaternion.fromAxisAngle(Vector3.Right, pitchDeg)
        .times(Quaternion.fromAxisAngle(Vector3.Up, yawDeg))
        .times(Quaternion.fromAxisAngle(Vector3.Forward, rollDeg))
        .normalize()
  }

  fun Bound3D.isValid(): Boolean {
    return this.min.x.isFinite() &&
        this.min.y.isFinite() &&
        this.min.z.isFinite() &&
        this.max.x.isFinite() &&
        this.max.y.isFinite() &&
        this.max.z.isFinite()
  }
}
