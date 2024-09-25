// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps.models

data class GeocodeResult(
    val address_components: List<GeocodeAddressComponent>,
    val formatted_address: String,
    val place_id: String,
    val types: List<String>
)
