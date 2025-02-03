// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.media_select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.levinriegner.mediaview.app.events.AppEvent
import com.meta.levinriegner.mediaview.app.events.AppEventListener
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.MediaSelectionEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.levinriegner.mediaview.data.gallery.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.FileNotFoundException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaSelectViewModel
@Inject
constructor(
  private val panelDelegate: PanelDelegate,
  private val eventBus: EventBus,
  private val galleryRepository: GalleryRepository,
) : ViewModel(), AppEventListener {
  private val _uiState = MutableStateFlow(MediaSelectUiState())
  val uiState = _uiState.asStateFlow()

  init {
    eventBus.register(this)
  }

  fun enableSelectMode() {
    Timber.i("Enabling media select mode")
    _uiState.update {
      it.copy(
          isEnabled = true,
      )
    }
  }

  fun disableSelectMode() {
    Timber.i("Disabling media select mode")
    _uiState.update {
      it.copy(
          isEnabled = false,
          selectedMedia = emptyList(),
      )
    }
  }

  fun toggleMediaSelection(mediaItem: MediaModel) {
    _uiState.update {
      val listToUpdate = it.selectedMedia.toMutableList()

      val isMediaAlreadySelected = listToUpdate.singleOrNull { element ->
        element.id == mediaItem.id
      } != null

      if (isMediaAlreadySelected) {
        Timber.i("Unselecting media: ${mediaItem.id}")
        listToUpdate.remove(mediaItem)
      } else {
        Timber.i("Selecting media: ${mediaItem.id}")
        listToUpdate.add(mediaItem)
      }

      it.copy(
          selectedMedia = listToUpdate,
      )
    }
  }

  fun openConfirmationPanel() {
    panelDelegate.openDeleteConfirmationPanel()
  }

  override fun onEvent(event: AppEvent) {
    when(event) {
      is MediaSelectionEvent.DeleteConfirmation -> {
        val mediaToDelete = _uiState.value.selectedMedia

        deleteSelectedMedia(mediaToDelete)
      }
    }
  }

  private fun deleteSelectedMedia(mediaToDelete: List<MediaModel>) {
    panelDelegate.closeDeleteConfirmationPanel()

    viewModelScope.launch {
      _uiState.update { it.copy(deleteState = MediaSelectDeleteState.Deleting(mediaToDelete.size)) }

      try {
        for (mediaItem in mediaToDelete) {
          Timber.i("Deleting media: ${mediaItem.id}")
          galleryRepository.setMediaFileDeleted(mediaItem.uri)
        }

        _uiState.update {
          it.copy(
              deleteState = MediaSelectDeleteState.Success(mediaToDelete.size),
              isEnabled = false,
              selectedMedia = emptyList(),
          )
        }

        eventBus.post(
            MediaSelectionEvent.Deleted(
                mediaToDelete.map {
                  it.id
                }.toList(),
            ),
        )
      } catch (e: Exception) {
        _uiState.update {
          it.copy(
              deleteState = MediaSelectDeleteState.Error(
                  message = when (e) {
                    is SecurityException -> "Storage permission denied"
                    is FileNotFoundException -> "File not found"
                    else -> "Deletion failed: ${e.localizedMessage}"
                  },
              ),
          )
        }
      }
    }
  }
}
