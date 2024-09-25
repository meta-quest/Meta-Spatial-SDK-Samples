// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.gallery.model

enum class MediaFilter {
  ALL,
  PHOTOS_VIDEOS_2D,
  PANORAMAS,
  MEDIA_360,
  MEDIA_SPATIAL,
  MEDIA_POV,
  ;

  companion object {
    val initial = ALL
  }
}
