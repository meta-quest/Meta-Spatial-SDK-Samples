// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps.models

data class GeocodeAddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)
