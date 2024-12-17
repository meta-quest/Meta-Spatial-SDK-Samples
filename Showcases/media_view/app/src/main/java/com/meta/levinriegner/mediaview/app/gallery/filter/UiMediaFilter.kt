// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.filter

import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter

data class UiMediaFilter(
    val type: MediaFilter,
    val isSelected: Boolean,
)

/// [MediaType] extension function to get the title string resource
@StringRes
fun MediaFilter.titleResId(): Int {
  return when (this) {
    MediaFilter.ALL -> R.string.media_filter_all
    MediaFilter.PHOTOS_VIDEOS_2D -> R.string.media_filter_2d
    MediaFilter.PANORAMAS -> R.string.media_filter_panoramas
    MediaFilter.MEDIA_360 -> R.string.media_filter_360
    MediaFilter.MEDIA_SPATIAL -> R.string.media_filter_spatial
    MediaFilter.MEDIA_POV -> R.string.media_filter_rayban
    MediaFilter.SAMPLE_MEDIA -> R.string.media_filter_samples
  }
}

@IntegerRes
fun MediaFilter.iconResId(): Int {
  return when (this) {
    MediaFilter.ALL -> R.drawable.icon_viewall
    MediaFilter.PHOTOS_VIDEOS_2D -> R.drawable.icon_flatmedia
    MediaFilter.PANORAMAS -> R.drawable.icon_panorama
    MediaFilter.MEDIA_360 -> R.drawable.icon_360media
    MediaFilter.MEDIA_SPATIAL -> R.drawable.icon_spatialmedia
    MediaFilter.MEDIA_POV -> R.drawable.icon_pov
    MediaFilter.SAMPLE_MEDIA -> R.drawable.icon_sample_media
  }
}
