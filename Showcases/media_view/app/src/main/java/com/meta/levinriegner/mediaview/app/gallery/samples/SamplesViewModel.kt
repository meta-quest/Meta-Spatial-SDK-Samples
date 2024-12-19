// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.samples

import android.content.ContentValues
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.levinriegner.mediaview.app.shared.InternetAvailability
import com.meta.levinriegner.mediaview.data.gallery.model.StorageType
import com.meta.levinriegner.mediaview.data.gallery.repository.GalleryRepository
import com.meta.levinriegner.mediaview.data.samples.model.SamplesList
import com.meta.levinriegner.mediaview.data.samples.repository.SamplesRepository
import com.meta.levinriegner.mediaview.data.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SamplesViewModel @Inject constructor(
    private val samplesRepository: SamplesRepository,
    private val userRepository: UserRepository,
    private val galleryRepository: GalleryRepository,
    private val internetAvailability: InternetAvailability,
) : ViewModel() {

    private val _state = MutableStateFlow<UiSamplesState>(UiSamplesState.Idle)
    val state = _state.asStateFlow()

    fun checkNewSamples() {
        Timber.i("Checking for new media samples")
        _state.value = UiSamplesState.Loading
        viewModelScope.launch {
            if (!internetAvailability.isInternetAvailable()) {
                Timber.w("No internet connection")
                delay(1000L) // Delay to feel like we're trying to get online
                _state.value = UiSamplesState.NoInternet
                return@launch
            }
            try {
                val samples = samplesRepository.getSamplesList()
                val currentVersion = userRepository.getSampleMediaVersion()
                Timber.i("Samples versions: remote=v${samples.version}, local=v$currentVersion")
                if (currentVersion == null || currentVersion == 0) {
                    downloadSamples(samples)
                } else if ((samples.version ?: 0) > currentVersion) {
                    _state.value = UiSamplesState.NewSamplesAvailable(samples)
                } else {
                    _state.value = UiSamplesState.Idle
                }
            } catch (e: Exception) {
                Timber.w(e, "Failed to get samples list: ${e.message}")
                _state.value = UiSamplesState.DownloadError(e.message ?: "Unknown error")
            }
        }
    }

    fun downloadSamples(samples: SamplesList) {
        Timber.i("Downloading sample media (${samples.items?.size})")
        if (samples.items.isNullOrEmpty()) {
            Timber.w("No samples to download")
            _state.value = UiSamplesState.Idle
            return
        }
        if (!internetAvailability.isInternetAvailable()) {
            Timber.w("No internet connection")
            _state.value = UiSamplesState.NoInternet
            return
        }
        val relativePath = samplesDirectory(samples.version ?: 0)
        viewModelScope.launch {
            // Delete previous folder if it exists
            galleryRepository.deleteSampleMediaSubFolder(relativePath)
            // Download files
            for (i in 0..<samples.items.size) {
                val item = samples.items[i]
                _state.value = UiSamplesState.DownloadingSamples(i + 1, samples.items.size)
                var mediaFile: Pair<ContentValues, Uri?>? = null
                try {
                    Timber.i("Downloading sample media: ${item.name} (${i + 1}/${samples.items.size})")
                    if (item.url == null) {
                        throw IllegalStateException("Sample media has no drive ID: ${item.name}")
                    }
                    // Create file
                    mediaFile = galleryRepository.createMediaFile(
                        displayFileName = item.name,
                        mimeType = null,
                        relativeSubPath = relativePath,
                        storageType = StorageType.Sample,
                    )
                    mediaFile.second?.let { uri ->
                        samplesRepository.downloadFile(item.url)
                            .collectIndexed { index, inputStream ->
                                Timber.d("Saving chunk $index")
                                galleryRepository.writeMediaFile(uri) { outputStream ->
                                    inputStream.use { inputStream -> inputStream.copyTo(outputStream) }
                                }
                            }
                        galleryRepository.setMediaFileReady(mediaFile.first, uri)
                    } ?: throw IllegalStateException("Failed to create media file (null URI)")
                    Timber.i("Success saving sample media: ${item.name}")
                } catch (e: Exception) {
                    Timber.w(e, "Failed to download sample media: ${item.name}.")
                    // Clean up
                    mediaFile?.second?.let {
                        galleryRepository.setMediaFileDeleted(it)
                    }
                    galleryRepository.deleteSampleMediaSubFolder(relativePath)
                    // Error and stop
                    _state.value =
                        UiSamplesState.DownloadError(e.message ?: "Failed to download samples")
                    return@launch
                }
                delay(Random.nextLong(500)) // Be nice to the server
            }
            // Delete previous media
            Timber.i("Deleting previous sample media")
            galleryRepository.deleteSampleMedia(exceptRelativePath = relativePath)

            // Save new version
            Timber.i("Saving new sample media version")
            userRepository.setSampleMediaVersion(samples.version ?: 0)

            // Done
            Timber.i("Success downloading samples")
            _state.value = UiSamplesState.DownloadSuccess
        }
    }

    fun dismissSamples() {
        Timber.i("Dismiss samples. State was: ${_state.value}")
        _state.value = UiSamplesState.Idle
    }

    companion object {
        private fun samplesDirectory(version: Int): String {
            return "v$version"
        }
    }

}
