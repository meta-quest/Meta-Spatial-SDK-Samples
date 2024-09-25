// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.models

/** https://developers.google.com/maps/documentation/tile/streetview#street_view_metadata */
data class PanoMetadata(
    val panoId: String,

    // image and tile dimensions for fetching images to compose the full image

    val imageHeight: Int,
    val imageWidth: Int,
    val tileHeight: Int,
    val tileWidth: Int,

    // data about the image that Google requires you to display

    val copyright: String,
    val date: String?,
    val reportProblemLink: String,
)
