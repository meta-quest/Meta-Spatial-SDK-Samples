// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.gallery.repository

import android.content.ContentValues
import android.net.Uri
import com.meta.levinriegner.mediaview.data.di.IoDispatcher
import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.levinriegner.mediaview.data.gallery.model.MediaSortBy
import com.meta.levinriegner.mediaview.data.gallery.model.StorageType
import com.meta.levinriegner.mediaview.data.gallery.service.DeviceGalleryService
import java.io.FileOutputStream
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GalleryRepository
@Inject
constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val galleryService: DeviceGalleryService,
) {

  suspend fun getMedia(
      filter: MediaFilter,
      sortBy: MediaSortBy,
  ): List<MediaModel> =
      withContext(dispatcher) { galleryService.getMedia(filter, sortBy).map { it.mapToDomain() } }

  suspend fun createMediaFile(
      displayFileName: String?,
      mimeType: String?,
      relativeSubPath: String?,
      storageType: StorageType,
  ): Pair<ContentValues, Uri?> =
      withContext(dispatcher) {
        galleryService.createMediaFile(displayFileName, mimeType, relativeSubPath, storageType)
      }

  suspend fun writeMediaFile(
      uri: Uri,
      onWrite: (FileOutputStream) -> Unit,
  ) = withContext(dispatcher) { galleryService.writeMediaFile(uri, onWrite) }

  suspend fun setMediaFileReady(contentValues: ContentValues, uri: Uri) =
      withContext(dispatcher) { galleryService.setMediaFileReady(contentValues, uri) }

  suspend fun sampleMediaExists(): Boolean =
      withContext(dispatcher) { galleryService.sampleMediaExists() }

  suspend fun deleteSampleMedia() = withContext(dispatcher) { galleryService.deleteSampleMedia() }
}
