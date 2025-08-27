// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.gallery.service.util

import android.net.Uri
import android.provider.MediaStore
import com.meta.levinriegner.mediaview.data.gallery.model.MediaSortBy

object MediaStoreQueryBuilder {

  // Uri
  val collectionUri: Uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

  // Projection
  // Reference:
  // https://developer.android.com/reference/android/provider/MediaStore.MediaColumns#constants_1
  val allColumns =
      arrayOf(
          MediaStore.Files.FileColumns._ID,
          MediaStore.Files.FileColumns.ALBUM,
          MediaStore.Files.FileColumns.ALBUM_ARTIST,
          MediaStore.Files.FileColumns.ARTIST,
          MediaStore.Files.FileColumns.AUTHOR,
          MediaStore.Files.FileColumns.BITRATE,
          MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
          MediaStore.Files.FileColumns.BUCKET_ID,
          MediaStore.Files.FileColumns.CAPTURE_FRAMERATE,
          MediaStore.Files.FileColumns.CD_TRACK_NUMBER,
          MediaStore.Files.FileColumns.COMPILATION,
          MediaStore.Files.FileColumns.COMPOSER,
          MediaStore.Files.FileColumns.DATA,
          MediaStore.Files.FileColumns.DATE_ADDED,
          MediaStore.Files.FileColumns.DATE_EXPIRES,
          MediaStore.Files.FileColumns.DATE_MODIFIED,
          MediaStore.Files.FileColumns.DATE_TAKEN,
          MediaStore.Files.FileColumns.DISC_NUMBER,
          MediaStore.Files.FileColumns.DISPLAY_NAME,
          MediaStore.Files.FileColumns.DOCUMENT_ID,
          MediaStore.Files.FileColumns.DURATION,
          MediaStore.Files.FileColumns.GENERATION_ADDED,
          MediaStore.Files.FileColumns.GENERATION_MODIFIED,
          MediaStore.Files.FileColumns.GENRE,
          MediaStore.Files.FileColumns.HEIGHT,
          MediaStore.Files.FileColumns.INSTANCE_ID,
          MediaStore.Files.FileColumns.IS_DOWNLOAD,
          MediaStore.Files.FileColumns.IS_DRM,
          MediaStore.Files.FileColumns.IS_FAVORITE,
          MediaStore.Files.FileColumns.IS_PENDING,
          MediaStore.Files.FileColumns.IS_TRASHED,
          MediaStore.Files.FileColumns.MIME_TYPE,
          MediaStore.Files.FileColumns.NUM_TRACKS,
          MediaStore.Files.FileColumns.ORIENTATION,
          MediaStore.Files.FileColumns.ORIGINAL_DOCUMENT_ID,
          MediaStore.Files.FileColumns.OWNER_PACKAGE_NAME,
          MediaStore.Files.FileColumns.RELATIVE_PATH,
          MediaStore.Files.FileColumns.RESOLUTION,
          MediaStore.Files.FileColumns.SIZE,
          MediaStore.Files.FileColumns.TITLE,
          MediaStore.Files.FileColumns.VOLUME_NAME,
          MediaStore.Files.FileColumns.WIDTH,
          MediaStore.Files.FileColumns.WRITER,
          MediaStore.Files.FileColumns.XMP,
          MediaStore.Files.FileColumns.YEAR,
      )

  // Selection
  fun buildSelection(): Pair<String?, Array<String>?> {
    // TODO: Add support for 3d objects
    return Pair(
        "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?",
        arrayOf(
            "${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}",
            "${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}",
        ),
    )
  }

  // Sort
  fun buildSortOrder(sortBy: MediaSortBy): String {
    return when (sortBy) {
      MediaSortBy.DateAsc -> "${MediaStore.Files.FileColumns.DATE_ADDED} ASC"
      MediaSortBy.DateDesc -> "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
      MediaSortBy.SizeAsc -> "${MediaStore.Files.FileColumns.SIZE} ASC"
      MediaSortBy.SizeDesc -> "${MediaStore.Files.FileColumns.SIZE} DESC"
      MediaSortBy.NameAsc -> "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"
      MediaSortBy.NameDesc -> "${MediaStore.Files.FileColumns.DISPLAY_NAME} DESC"
    }
  }
}
