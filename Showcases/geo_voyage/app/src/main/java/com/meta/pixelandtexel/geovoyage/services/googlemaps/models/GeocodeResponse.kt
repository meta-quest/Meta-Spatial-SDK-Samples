// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps.models

import com.meta.pixelandtexel.geovoyage.services.googlemaps.enums.GeocodeStatus

data class GeocodeResponse(
    val status: GeocodeStatus,
    val plus_code: GeocodePlusCode,
    val results: List<GeocodeResult>
)

sealed class GeocodeResponseWrapper {
  data class Success(val data: GeocodeResponse) : GeocodeResponseWrapper()

  data class Error(val message: String) : GeocodeResponseWrapper()
}
