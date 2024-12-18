// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.gallery.service

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter
import com.meta.levinriegner.mediaview.data.gallery.model.MediaSortBy
import com.meta.levinriegner.mediaview.data.gallery.model.StorageType
import com.meta.levinriegner.mediaview.data.gallery.service.model.MediaStoreFileDto
import com.meta.levinriegner.mediaview.data.gallery.service.util.MediaStoreDebugUtils
import com.meta.levinriegner.mediaview.data.gallery.service.util.MediaStoreQueryBuilder
import com.meta.spatial.core.BuildConfig
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class DeviceGalleryService
@Inject
constructor(
    private val contentResolver: ContentResolver,
) {

    // Get media from the device gallery
    fun getMedia(
        filter: MediaFilter,
        sortBy: MediaSortBy,
    ): List<MediaStoreFileDto> {
        // Build query
        val selection = MediaStoreQueryBuilder.buildSelection()
        val cursor =
            contentResolver.query(
                MediaStoreQueryBuilder.collectionUri,
                MediaStoreQueryBuilder.allColumns,
                selection.first,
                selection.second,
                MediaStoreQueryBuilder.buildSortOrder(sortBy),
            )
        // Iterate through the results
        val media = mutableListOf<MediaStoreFileDto>()
        cursor?.use {
            Timber.i("Cursor count: ${cursor.count}")
            while (cursor.moveToNext()) {
                val dto = MediaStoreFileDto.fromCursor(cursor)
                if (dto.mimeType == null) continue
                // Check if the media is a sample
                if (dto.relativePath?.startsWith("$SAVED_MEDIA_FOLDER_NAME/$SAMPLES_DEVICE_SUBFOLDER_NAME") == true) {
                    if (filter == MediaFilter.SAMPLE_MEDIA) {
                        media.add(dto)
                    } else {
                        continue
                    }
                }
                if (filter != MediaFilter.ALL) {
                    // Check if the media is of the correct type
                    if (dto.mediaFilter == filter) {
                        media.add(dto)
                    }
                } else {
                    media.add(dto)
                }
            }
            if (BuildConfig.DEBUG && DUMP_CURSOR_TO_CSV) {
                MediaStoreDebugUtils.dumpCursorToCsv(cursor)
            }
        } ?: Timber.w("Cursor is null.")

        return media
    }

    fun createMediaFile(
        displayFileName: String?,
        mimeType: String?,
        relativeSubPath: String?,
        storageType: StorageType,
    ): Pair<ContentValues, Uri?> {
        val contentValues =
            ContentValues().apply {
                displayFileName?.let { put(MediaStore.MediaColumns.DISPLAY_NAME, it) }
                mimeType?.let { put(MediaStore.MediaColumns.MIME_TYPE, it) }
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    SAVED_MEDIA_FOLDER_NAME +
                            when (storageType) {
                                StorageType.Sample -> "/$SAMPLES_DEVICE_SUBFOLDER_NAME"
                                StorageType.GoogleDrive -> "/$DRIVE_ASSET_SUBFOLDER_NAME"
                            } +
                            (relativeSubPath?.takeIf { it.isNotEmpty() }?.let { "/$it" } ?: ""))
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        val mediaUri = contentResolver.insert(MediaStoreQueryBuilder.collectionUri, contentValues)
        return Pair(contentValues, mediaUri)
    }

    fun writeMediaFile(
        uri: Uri,
        onWrite: (FileOutputStream) -> Unit,
    ) {
        contentResolver.openFileDescriptor(uri, "wa", null).use { file ->
            file?.let {
                val fos = FileOutputStream(it.fileDescriptor)
                fos.use { onWrite(fos) }
            }
        }
    }

    fun setMediaFileReady(contentValues: ContentValues, uri: Uri) {
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        contentResolver.update(uri, contentValues, null, null)
    }

    fun setMediaFileDeleted(uri: Uri) {
        Timber.d("Deleting media: $uri")
        contentResolver.delete(uri, null, null)
    }

    private fun sampleMediaFolderPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath +
                "/$SAVED_MEDIA_FOLDER_NAME" +
                "/$SAMPLES_DEVICE_SUBFOLDER_NAME"
    }

    fun deleteSampleMedia(exceptRelativePath: String?) {
        val file = File(sampleMediaFolderPath())
        if (file.exists()) {
            if (exceptRelativePath != null) {
                file.listFiles()?.forEach {
                    if (it.isDirectory && it.name == exceptRelativePath) {
                        // Skip
                    } else {
                        it.deleteRecursively()
                    }
                }
            } else {
                file.deleteRecursively()
            }
        } else {
            Timber.i("No sample folder to delete")
        }
    }

    fun deleteSampleMediaSubFolder(relativePath: String) {
        val file = File(sampleMediaFolderPath())
        if (file.exists()) {
            file.listFiles()?.forEach {
                if (it.isDirectory && it.name == relativePath) {
                    it.deleteRecursively()
                }
            }
        } else {
            Timber.i("No sample folder to delete")
        }
    }

    companion object {
        private const val DUMP_CURSOR_TO_CSV = false
        private const val SAVED_MEDIA_FOLDER_NAME = "Media Viewer"
        private const val SAMPLES_DEVICE_SUBFOLDER_NAME = "Samples"
        private const val DRIVE_ASSET_SUBFOLDER_NAME = "Drive"
    }
}
