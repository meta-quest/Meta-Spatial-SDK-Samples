// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.gallery.service.model

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getBlobOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.levinriegner.mediaview.data.gallery.model.MediaType
import java.nio.charset.StandardCharsets
import java.util.Date

// Reference:
// https://developer.android.com/reference/android/provider/MediaStore.MediaColumns#constants_1
data class MediaStoreFileDto(
    val id: Long,
    val uri: Uri,
    val albumArtist: String?,
    val artist: String?,
    val author: String?,
    val bitrate: Int?,
    val bucketDisplayName: String?,
    val bucketId: Int?,
    val captureFramerate: Float?,
    val cdTrackNumber: String?,
    val compilation: String?,
    val composer: String?,
    val data: String?,
    val dateAdded: Int?, // seconds since epoch
    val dateExpires: Int?, // seconds since epoch
    val dateModified: Int?, // seconds since epoch
    val dateTaken: Int?, // seconds since epoch
    val discNumber: String?,
    val displayName: String?, // file name + extension
    val documentId: String?, // GUID extracted from the XMP metadata
    val duration: Int?, // milliseconds
    val generationAdded: Int?,
    val generationModified: Int?,
    val genre: String?,
    val height: Int?,
    val instanceId: String?,
    val isDownload: Int?,
    val isDrm: Int?,
    val isFavorite: Int?,
    val isPending: Int?,
    val isTrashed: Int?,
    val mimeType: String?, // Dublin Core Media Initiative standard
    val numTracks: Int?,
    val orientation: Int?, // degrees
    val originalDocumentId: String?,
    val ownerPackageName: String?,
    val relativePath: String?,
    val resolution: String?, // user-presentable string
    val size: Int?, // bytes
    val title: String?,
    val volumeName: String?,
    val width: Int?,
    val writer: String?,
    val xmp: String?, // XMP Media Management standard, published as ISO 16684-1:2012
    val year: Int?,
) {

  fun mapToDomain(): MediaModel {
    return MediaModel(
        id = id,
        uri = uri,
        name = displayName,
        size = size?.toLong(),
        mimeType = mimeType,
        dateAdded = dateAdded?.let { Date(it.toLong() * 1000) },
        durationMs = duration,
        width = width,
        height = height,
        mediaType = mediaType,
        mediaFilter = mediaFilter,
        relativePath = relativePath,
    )
  }

  val aspectRatio: Float?
    get() {
      val width = width ?: return null
      val height = height ?: return null
      if (height == 0) return null
      return width.toFloat() / height.toFloat()
    }

  // # region Media Type
  private fun isImage(): Boolean {
    return mimeType?.contains("image/") == true
  }

  private fun isVideo(): Boolean {
    return mimeType?.contains("video/") == true
  }

  private fun isPanorama(): Boolean {
    return aspectRatio?.let { it > MediaType.panoramaAspectRatioMin } ?: false
  }

  private fun isRayban(): Boolean {
    return if (isImage()) {
      width == MediaType.raybanImageWidth && height == MediaType.raybanImageHeight
    } else if (isVideo()) {
      width == MediaType.raybanVideoWidth && height == MediaType.raybanVideoHeight
    } else {
      false
    }
  }

  private fun is360(): Boolean {
    return aspectRatio?.let { it == MediaType.media360ExactAspectRatio } ?: false
  }

  private fun isSpatial(): Boolean {
    return if (isImage()) {
      false
    } else if (isVideo()) {
      val numTracks = this.numTracks ?: return false
      numTracks >= MediaType.spatialVideoNumTracks
    } else {
      false
    }
  }

  private val mediaType: MediaType?
    get() {
      if (mimeType.isNullOrEmpty()) {
        return null
      }
      return when {
        isPanorama() -> MediaType.IMAGE_PANORAMA
        is360() -> if (isImage()) MediaType.IMAGE_360 else MediaType.VIDEO_360
        isSpatial() -> MediaType.VIDEO_SPATIAL
        isRayban() -> if (isImage()) MediaType.IMAGE_2D else MediaType.VIDEO_2D
        else -> if (isImage()) MediaType.IMAGE_2D else if (isVideo()) MediaType.VIDEO_2D else null
      }
    }

  val mediaFilter: MediaFilter?
    get() {
      if (mimeType.isNullOrEmpty()) {
        return null
      }
      return when {
        isPanorama() -> MediaFilter.PANORAMAS
        is360() -> MediaFilter.MEDIA_360
        isSpatial() -> MediaFilter.MEDIA_SPATIAL
        isRayban() -> MediaFilter.MEDIA_POV
        else -> if (isImage() || isVideo()) MediaFilter.PHOTOS_VIDEOS_2D else null
      }
    }

  // # endregion

  companion object {
    fun fromCursor(cursor: Cursor): MediaStoreFileDto {
      return MediaStoreFileDto(
          id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
          uri =
              ContentUris.withAppendedId(
                  MediaStore.Files.getContentUri(
                      cursor.getString(
                          cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.VOLUME_NAME))),
                  cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
              ),
          albumArtist =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ARTIST)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          artist =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          author =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.AUTHOR)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          bitrate =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.BITRATE)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          bucketDisplayName =
              cursor
                  .getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          bucketId =
              cursor
                  .getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          captureFramerate =
              cursor
                  .getColumnIndex(MediaStore.Video.VideoColumns.CAPTURE_FRAMERATE)
                  .takeIf { it != -1 }
                  ?.let { cursor.getFloatOrNull(it) },
          cdTrackNumber =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          compilation =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.COMPILATION)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          composer =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.COMPOSER)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          data =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.DATA)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          dateAdded =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          dateExpires =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.DATE_EXPIRES)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          dateModified =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          dateTaken =
              cursor
                  .getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          discNumber =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.DISC_NUMBER)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          displayName =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          documentId =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.DOCUMENT_ID)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          duration =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          generationAdded =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.GENERATION_ADDED)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          generationModified =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.GENERATION_MODIFIED)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          genre =
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                  cursor
                      .getColumnIndex(MediaStore.Audio.AudioColumns.GENRE)
                      .takeIf { it != -1 }
                      ?.let { cursor.getStringOrNull(it) }
              else null,
          height =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.HEIGHT)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          instanceId =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.INSTANCE_ID)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          isDownload =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.IS_DOWNLOAD)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          isDrm =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.IS_DRM)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          isFavorite =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.IS_FAVORITE)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          isPending =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.IS_PENDING)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          isTrashed =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.IS_TRASHED)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          mimeType =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          numTracks =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.NUM_TRACKS)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          orientation =
              cursor
                  .getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          originalDocumentId =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.ORIGINAL_DOCUMENT_ID)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          ownerPackageName =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.OWNER_PACKAGE_NAME)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          relativePath =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          resolution =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.RESOLUTION)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          size =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.SIZE)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          title =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          volumeName =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.VOLUME_NAME)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          width =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.WIDTH)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
          writer =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.WRITER)
                  .takeIf { it != -1 }
                  ?.let { cursor.getStringOrNull(it) },
          xmp =
              cursor
                  .getColumnIndex(MediaStore.MediaColumns.XMP)
                  .takeIf { it != -1 }
                  ?.let { cursor.getBlobOrNull(it) }
                  ?.let { String(it, StandardCharsets.UTF_8) },
          year =
              cursor
                  .getColumnIndex(MediaStore.Audio.AudioColumns.YEAR)
                  .takeIf { it != -1 }
                  ?.let { cursor.getIntOrNull(it) },
      )
    }
  }
}
