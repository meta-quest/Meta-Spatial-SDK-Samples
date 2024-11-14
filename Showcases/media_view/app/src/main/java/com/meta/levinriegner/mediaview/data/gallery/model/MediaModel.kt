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
    // Mutable fields
    var entityId: Long? = null,
    var minimizedMenuEntityId: Long? = null,
    var immersiveMenuEntityId: Long? = null,
) : Parcelable {

  fun durationHMS(): Triple<Int, Int, Int>? {
    val duration = durationMs ?: return null
    val hours = duration / 3600000
    val minutes = (duration % 3600000) / 60000
    val seconds = (duration % 60000) / 1000
    return Triple(hours, minutes, seconds)
  }

  fun debugPrint(): String {
    return "MediaModel(id=$id, size=$size, mimeType=$mimeType, durationMs=$durationMs, width=$width, height=$height, mediaType=$mediaType, mediaFilter=$mediaFilter)"
  }

  companion object {}
}
