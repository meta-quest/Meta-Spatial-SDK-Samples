// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.models

import com.meta.pixelandtexel.geovoyage.utils.MathUtils.PIf
import com.meta.spatial.core.Vector3
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class GeoCoordinates(val latitude: Float, val longitude: Float) {
  companion object {
    fun fromCartesianCoords(x: Float, y: Float, z: Float): GeoCoordinates {
      // calculate the latitude and convert to degrees
      val hyp = sqrt(x * x + z * z)
      val latitude = atan2(y, hyp) * 180f / PIf

      // calculate the longitude and convert to degrees
      val longitude = atan2(z, x) * 180f / PIf

      return GeoCoordinates(latitude, longitude)
    }
  }

  fun toCartesianCoords(radius: Float = 1.0f): Vector3 {
    // convert latitude and longitude from degrees to radians
    val latRad = latitude * PIf / 180f
    val lonRad = longitude * PIf / 180f

    // calculate cartesian coordinates (x, y, z) give the radius value
    val x = radius * cos(latRad) * cos(lonRad)
    val y = radius * sin(latRad)
    val z = radius * cos(latRad) * sin(lonRad)

    return Vector3(x, y, z)
  }

  fun toCommonNotation(): String {
    val latDirection = if (latitude >= 0) "N" else "S"
    val lonDirection = if (longitude >= 0) "E" else "W"
    val adjustedLatitude = abs(latitude)
    val adjustedLongitude = abs(longitude) % 180

    return String.format(
        Locale.US,
        "%.2f°%s, %.2f°%s",
        adjustedLatitude,
        latDirection,
        adjustedLongitude,
        lonDirection)
  }
}
