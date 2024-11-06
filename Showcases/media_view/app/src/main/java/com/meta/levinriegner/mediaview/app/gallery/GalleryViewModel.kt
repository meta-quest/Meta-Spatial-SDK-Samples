// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.levinriegner.mediaview.app.events.AppEvent
import com.meta.levinriegner.mediaview.app.events.AppEventListener
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.FilterAppEvent
import com.meta.levinriegner.mediaview.app.events.MediaPlayerEvent
import com.meta.levinriegner.mediaview.app.events.UploadAppEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.app.shared.model.UiState
import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.levinriegner.mediaview.data.gallery.model.MediaSortBy
import com.meta.levinriegner.mediaview.data.gallery.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class GalleryViewModel
@Inject
constructor(
    private val galleryRepository: GalleryRepository,
    private val panelDelegate: PanelDelegate,
    private val eventBus: EventBus,
) : ViewModel(), AppEventListener {

  private val _state = MutableStateFlow<UiState<List<MediaModel>>>(UiState.Idle)
  val state = _state.asStateFlow()

  private val _events = MutableSharedFlow<GalleryEvent>()
  val events = _events.asSharedFlow()

  private val _filter = MutableStateFlow(MediaFilter.initial)
  val filter = _filter.asStateFlow()

  private val _sortBy = MutableStateFlow(MediaSortBy.DateDesc)
  val sortBy = _sortBy.asStateFlow()

  private val _showMetadata = MutableStateFlow(false)
  val showMetadata = _showMetadata.asStateFlow()

  init {
    // Will trigger the initial load
    subscribeToFilterChanges()
    eventBus.register(this)
  }

  fun onOnboardingButtonPressed() {
    panelDelegate.openOnboardingPanel()
  }

  fun onMediaSelected(mediaModel: MediaModel) {
    Timber.i("Opening media: ${mediaModel.debugPrint()}")
    Timber.d("With name: ${mediaModel.name}")
    panelDelegate.openMediaPanel(mediaModel)
  }

  fun onSortBy(sortBy: MediaSortBy) {
    Timber.i("Sorting by: $sortBy")
    _sortBy.value = sortBy
    loadMedia()
  }

  fun onToggleMetadata(show: Boolean) {
    Timber.i("Toggling metadata to $show")
    _showMetadata.value = show
  }

  fun loadMedia(
      filter: MediaFilter = this.filter.value,
      sortBy: MediaSortBy = _sortBy.value,
  ) =
      viewModelScope.launch {
        _state.value = UiState.Loading
        try {
          Timber.i("Getting media for filter: $filter and sort by: $sortBy")
          val media = galleryRepository.getMedia(filter, sortBy)
          Timber.i("Got media: ${media.size}")
          _state.value = UiState.Success(media)
        } catch (t: Throwable) {
          Timber.w("Failed to get media: ${t.message}")
          _state.value = UiState.Error("Failed to get media: ${t.message}")
        }
      }

  private fun subscribeToFilterChanges() =
      viewModelScope.launch { filter.collect { loadMedia(it) } }

  override fun onEvent(event: AppEvent) {
    when (event) {
      is UploadAppEvent.UploadSuccess -> loadMedia()
      is UploadAppEvent.UploadFailed -> {
        viewModelScope.launch { _events.emit(GalleryEvent.UploadFailed(event.error)) }
      }

      is FilterAppEvent.FilterChanged -> _filter.value = event.filter

      is MediaPlayerEvent.Deleted -> {
        if (state.value is UiState.Success) {
          val media = (state.value as UiState.Success<List<MediaModel>>).data
          val updatedMedia = media.filter { it.id != event.mediaId }
          _state.value = UiState.Success(updatedMedia)
        }
      }
    }
  }

  override fun onCleared() {
    eventBus.unregister(this)
    super.onCleared()
  }
}
