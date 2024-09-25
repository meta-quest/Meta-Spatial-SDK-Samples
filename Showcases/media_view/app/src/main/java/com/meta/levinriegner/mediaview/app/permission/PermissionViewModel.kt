// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.permission

import android.content.res.AssetManager
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.levinriegner.mediaview.data.gallery.model.StorageType
import com.meta.levinriegner.mediaview.data.gallery.repository.GalleryRepository
import com.meta.levinriegner.mediaview.data.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class PermissionViewModel
@Inject
constructor(
    private val assetManager: AssetManager,
    private val galleryRepository: GalleryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

  private val _state = MutableStateFlow<PermissionState>(PermissionState.CheckPermissionState)
  val state = _state.asStateFlow()

  fun onCheckPermissionResult(granted: Boolean) {
    Timber.i("Storage permission status granted: $granted")
    if (granted) {
      loadSampleAssets()
    } else {
      _state.value = PermissionState.RequestPermission
    }
  }

  fun onStoragePermissionGranted() {
    Timber.i("Storage permission granted")
    loadSampleAssets()
  }

  fun onStoragePermissionDenied() {
    Timber.i("Storage permission denied")
    _state.value = PermissionState.PermissionDenied
  }

  private fun loadSampleAssets() {
    _state.value = PermissionState.LoadingSampleAssets
    // Check if sample assets are already saved
    if (userRepository.isSampleMediaSaved()) {
      Timber.i("Sample assets already saved")
      _state.value = PermissionState.SampleAssetsLoaded
      return
    }
    viewModelScope.launch {
      // Check if the folder exists from any previous installs
      if (galleryRepository.sampleMediaExists()) {
        Timber.i("Sample assets folder exists, deleting")
        galleryRepository.deleteSampleMedia()
      }
      // Save sample assets
      try {
        Timber.i("Saving sample assets")
        saveAssetDirectory(assetManager, SAMPLES_ASSETS_SUBFOLDER_NAME)
        Timber.i("Sample assets saved")
        userRepository.setSampleMediaSaved(true)
        _state.value = PermissionState.SampleAssetsLoaded
      } catch (t: Throwable) {
        Timber.w(t, "Failed to save sample assets")
        // Fail silently
        _state.value = PermissionState.SampleAssetsLoaded
      }
    }
  }

  private suspend fun saveAssetFile(assetManager: AssetManager, path: String) {
    val relativePath =
        if (path.contains("/"))
            path.substringAfter("${SAMPLES_ASSETS_SUBFOLDER_NAME}/").substringBeforeLast("/")
        else null
    val fileName = if (path.contains("/")) path.substringAfterLast("/") else path
    val mimeType =
        MimeTypeMap.getFileExtensionFromUrl(fileName)?.let {
          MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
        }
    try {
      Timber.d("Saving asset $fileName with rel path: $relativePath")
      val (contentValues, mediaUri) =
          galleryRepository.createMediaFile(fileName, mimeType, relativePath, StorageType.Sample)
      galleryRepository.writeMediaFile(mediaUri!!) { outputStream ->
        assetManager.open(path).use { input -> input.copyTo(outputStream) }
      }
      galleryRepository.setMediaFileReady(contentValues, mediaUri)
    } catch (t: Throwable) {
      Timber.w(t, "Failed to save asset file: $path")
    }
  }

  private suspend fun saveAssetDirectory(assetManager: AssetManager, path: String) {
    assetManager.list(path)?.forEach { child ->
      val childPath = "$path/$child"
      assetManager
          .list(childPath)
          ?.takeIf { it.isNotEmpty() }
          ?.let {
            // Is Directory
            saveAssetDirectory(assetManager, childPath)
          }
          ?: run {
            // Is File
            saveAssetFile(assetManager, childPath)
          }
    }
  }

  companion object {
    private const val SAMPLES_ASSETS_SUBFOLDER_NAME = "samples"
  }
}
