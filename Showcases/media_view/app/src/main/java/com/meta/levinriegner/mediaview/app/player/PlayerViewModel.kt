// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.levinriegner.mediaview.app.events.AppEvent
import com.meta.levinriegner.mediaview.app.events.AppEventListener
import com.meta.levinriegner.mediaview.app.events.EditEvent
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.MediaPlayerEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.app.player.view.edit.CropState
import com.meta.levinriegner.mediaview.app.shared.util.FeatureFlags
import com.meta.levinriegner.mediaview.data.gallery.model.MediaEditOption
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.levinriegner.mediaview.data.gallery.model.MediaType
import com.meta.levinriegner.mediaview.data.gallery.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class PlayerViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val panelDelegate: PanelDelegate,
    private val eventBus: EventBus,
    private val galleryRepository: GalleryRepository,
) : ViewModel(), AppEventListener {

  private val mediaModel = savedStateHandle.get<MediaModel>("mediaModel")!!

  private val _state = MutableStateFlow<PlayerState>(PlayerState.Empty)
  val state = _state.asStateFlow()

  private val _event = MutableSharedFlow<PlayerEvent>()
  val event = _event.asSharedFlow()

  init {
    _state.value = buildState(mediaModel)
    Timber.i("Initializing player with state: ${state.value.javaClass.simpleName}")
    eventBus.register(this)
  }

  private fun buildState(mediaModel: MediaModel): PlayerState {
    return when (mediaModel.mediaType) {
      MediaType.IMAGE_2D,
      MediaType.IMAGE_360 ->
          PlayerState.Image2D(
              mediaModel.uri,
              cropState =
                  if (mediaModel.editOptions.contains(MediaEditOption.Crop) &&
                      FeatureFlags.MEDIA_EDIT_ENABLED)
                      CropState.Disabled
                  else CropState.NotSupported,
          )

      MediaType.VIDEO_2D -> PlayerState.Video2D(mediaModel.uri)
      MediaType.IMAGE_PANORAMA -> PlayerState.ImagePanorama(mediaModel.uri)
      MediaType.VIDEO_360 -> PlayerState.Video360(mediaModel.uri)
      MediaType.VIDEO_SPATIAL -> PlayerState.Video2D(mediaModel.uri)
      null -> PlayerState.Error("Unknown media type for mime ${mediaModel.mimeType}")
    }
  }

  fun onImageCropped(bitmap: Bitmap) {
    viewModelScope.launch {
      try {
        Timber.i("Saving cropped image")
        galleryRepository.saveCroppedMediaFile(mediaModel) { fos ->
          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        delay(500L) // Force a delay to ensure user sees the loading animation
        Timber.i("Success saving cropped image")
        if (state.value !is PlayerState.Image2D) {
          Timber.w("Invalid state for saving image: ${state.value.javaClass.simpleName}")
        } else {
          _state.value = (state.value as PlayerState.Image2D).copy(cropState = CropState.Disabled)
        }
        viewModelScope.launch { _event.emit(PlayerEvent.OnImageSaved(true, null)) }
        eventBus.post(EditEvent.SaveImageCompleted(mediaModel.id, true))
      } catch (t: Throwable) {
        Timber.w(t, "Failed to save cropped image")
        viewModelScope.launch { _event.emit(PlayerEvent.OnImageSaved(false, t.message)) }
        eventBus.post(EditEvent.SaveImageCompleted(mediaModel.id, false))
      } finally {
        bitmap.recycle()
      }
    }
  }

  fun onClose() {
    viewModelScope.launch { _event.emit(PlayerEvent.Close) }
    panelDelegate.closeMediaPanel(mediaModel)
  }

  override fun onEvent(event: AppEvent) {
    when (event) {
      is MediaPlayerEvent.Close -> {
        if (event.mediaId == mediaModel.id) {
          viewModelScope.launch { _event.emit(PlayerEvent.Close) }
        }
      }

      is MediaPlayerEvent.CloseAll -> viewModelScope.launch { _event.emit(PlayerEvent.Close) }
      is EditEvent.EnterCrop -> {
        if (event.mediaId == mediaModel.id) {
          _state.value =
              when (val state = state.value) {
                is PlayerState.Image2D -> state.copy(cropState = CropState.Enabled)
                else -> state
              }
        }
      }

      is EditEvent.ExitCrop -> {
        if (event.mediaId == mediaModel.id) {
          _state.value =
              when (val state = state.value) {
                is PlayerState.Image2D -> state.copy(cropState = CropState.Disabled)
                else -> state
              }
        }
      }

      is EditEvent.SaveImageRequest -> {
        if (event.mediaId == mediaModel.id) {
          viewModelScope.launch { _event.emit(PlayerEvent.OnCropImageRequested) }
        }
      }
    }
  }

  override fun onCleared() {
    eventBus.unregister(this)
    super.onCleared()
  }
}
