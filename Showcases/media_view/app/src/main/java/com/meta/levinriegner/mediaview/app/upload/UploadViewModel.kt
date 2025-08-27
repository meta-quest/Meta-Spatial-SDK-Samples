// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.UploadAppEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.app.upload.js.DriveMedia
import com.meta.levinriegner.mediaview.data.gallery.model.StorageType
import com.meta.levinriegner.mediaview.data.gallery.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class UploadViewModel
@Inject
constructor(
    private val galleryRepository: GalleryRepository,
    private val panelDelegate: PanelDelegate,
    private val eventBus: EventBus,
) : ViewModel() {

  private val _state = MutableStateFlow<UploadState>(UploadState.Idle)
  val state
    get() = _state.asStateFlow()

  fun onDownload(driveMedia: DriveMedia) =
      viewModelScope.launch {
        try {
          // Create or Get media file
          val (contentValues, uri) =
              if (
                  _state.value is UploadState.Idle ||
                      _state.value is UploadState.Completed ||
                      (_state.value is UploadState.Uploading &&
                          (_state.value as UploadState.Uploading).fileId != driveMedia.fileId)
              ) {
                Timber.i("Creating new media file for file ${driveMedia.fileId}")
                galleryRepository.createMediaFile(
                    driveMedia.fileName,
                    driveMedia.mimeType,
                    null,
                    StorageType.GoogleDrive,
                )
              } else {
                Pair(
                    (_state.value as UploadState.Uploading).contentValues,
                    (_state.value as UploadState.Uploading).uri,
                )
              }
          // Write data to file
          galleryRepository.writeMediaFile(uri!!) { fos -> fos.write(driveMedia.blob) }
          // Update file progress
          if (driveMedia.progress.first >= driveMedia.progress.second) {
            // Success!! File completed
            galleryRepository.setMediaFileReady(contentValues, uri)
            Timber.i("Download completed")
            eventBus.post(UploadAppEvent.UploadSuccess)
            panelDelegate.closeUploadPanel()
            _state.value = UploadState.Completed
          } else {
            // In progress
            Timber.i("Download progress ${driveMedia.progress.first}/${driveMedia.progress.second}")
            _state.value =
                UploadState.Uploading(
                    driveMedia.fileId,
                    driveMedia.fileName,
                    driveMedia.progress.first,
                    driveMedia.progress.second,
                    contentValues,
                    uri,
                )
          }
        } catch (t: Throwable) {
          Timber.w(t, "Download failed")
          _state.value = UploadState.Error(t.message ?: "Download failed")
          onMediaDownloadFailed(t.message ?: "Download failed")
        }
      }

  fun onMediaDownloadFailed(reason: String) {
    Timber.i("Download failed: $reason")
    eventBus.post(UploadAppEvent.UploadFailed(reason))
    panelDelegate.closeUploadPanel()
    _state.value = UploadState.Completed
  }

  fun onDismiss() {
    Timber.i("onDismiss")
    panelDelegate.closeUploadPanel()
    _state.value = UploadState.Completed
  }
}
