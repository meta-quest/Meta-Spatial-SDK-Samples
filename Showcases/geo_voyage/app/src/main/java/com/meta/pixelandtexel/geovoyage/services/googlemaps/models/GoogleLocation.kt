// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps.models

import com.meta.pixelandtexel.geovoyage.models.GeoCoordinates

data class GoogleLocation(val lat: Float, val lng: Float) {
  companion object {
    fun fromGeoCoords(coords: GeoCoordinates): GoogleLocation {
      return GoogleLocation(coords.latitude, coords.longitude)
    }
  }
}
