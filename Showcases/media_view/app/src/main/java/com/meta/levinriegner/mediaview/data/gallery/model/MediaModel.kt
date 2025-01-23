// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.gallery.model

import android.net.Uri
import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaModel(
    val id: Long,
    val uri: Uri,
    val name: String?,
    val size: Long?, // in bytes
    val mimeType: String?,
    val dateAdded: Date?,
    val durationMs: Int?, // in milliseconds
    val width: Int?,
    val height: Int?,
    val mediaType: MediaType?,
    val mediaFilter: MediaFilter?,
    val relativePath: String?,
    // Mutable fields
    var entityId: Long? = null,
    var minimizedMenuEntityId: Long? = null,
    var immersiveMenuEntityId: Long? = null,
) : Parcelable {

  val editOptions: List<MediaEditOption>
    get() =
        when (mediaType) {
          MediaType.IMAGE_2D -> emptyList()
          MediaType.VIDEO_2D -> emptyList()
          MediaType.IMAGE_PANORAMA -> emptyList()
          MediaType.IMAGE_360 -> listOf(MediaEditOption.Crop)
          MediaType.VIDEO_360 -> emptyList()
          MediaType.VIDEO_SPATIAL -> emptyList()
          null -> emptyList()
        }

  fun durationHMS(): Triple<Int, Int, Int>? {
    val duration = durationMs ?: return null
    val hours = duration / 3600000
    val minutes = (duration % 3600000) / 60000
    val seconds = (duration % 60000) / 1000
    return Triple(hours, minutes, seconds)
  }

  fun nameLabel(): String? {
    return name?.substringBeforeLast(".")
  }

  fun mimeTypeLabel(): String? {
    return mimeType?.split("/")?.last()?.uppercase()
  }

  fun sizeLabel(): String? {
    size?.let {
      val sizeInMb = it.div((1024.0 * 1024.0))
      val sizeInGb = it.div((1024.0 * 1024.0 * 1024.0))

      if (sizeInGb >= 1) {
        return String.format("%.2f GB", sizeInGb)
      } else {
        if (sizeInMb >= 1) {
          return String.format("%.2f MB", sizeInMb)
        } else {
          return "$it bytes"
        }
      }
    }

    return null
  }

  fun debugPrint(): String {
    return "MediaModel(id=$id, size=$size, mimeType=$mimeType, durationMs=$durationMs, width=$width, height=$height, mediaType=$mediaType, mediaFilter=$mediaFilter)"
  }

  companion object {}
}
