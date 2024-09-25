// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.gallery.model

enum class MediaType {
  IMAGE_2D,
  VIDEO_2D,
  IMAGE_PANORAMA,
  IMAGE_360,
  VIDEO_360,
  VIDEO_SPATIAL,
  ;

  companion object {
    // Spatial
    const val spatialVideoNumTracks = 5

    // Rayban
    const val raybanImageWidth = 3024
    const val raybanImageHeight = 4032
    const val raybanVideoWidth = 1552
    const val raybanVideoHeight = 2064

    // Panorama
    const val panoramaAspectRatioMin = 3.0f

    // 360
    const val media360ExactAspectRatio = 2f
    const val video360MinBitrate = 30 * 1024 * 1024
  }
}
