// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps.models

/** https://developers.google.com/maps/documentation/tile/session_tokens#session_token_response */
data class SessionResponse(
    val session: String,
    val expiry: Long,
    val tileWidth: Int,
    val tileHeight: Int,
    val imageFormat: String
)
