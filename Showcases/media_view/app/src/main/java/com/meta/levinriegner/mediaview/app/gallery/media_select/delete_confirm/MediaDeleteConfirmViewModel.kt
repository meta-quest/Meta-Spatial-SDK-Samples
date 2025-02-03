// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.media_select.delete_confirm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.events.AppEvent
import com.meta.levinriegner.mediaview.app.events.AppEventListener
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.MediaSelectionEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.internal.toImmutableList
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaDeleteConfirmViewModel
@Inject
constructor(
  private val eventBus: EventBus,
  private val panelDelegate: PanelDelegate
) : ViewModel(), AppEventListener {
  private val _mediaToDelete = MutableStateFlow<List<MediaModel>>(emptyList())
  val mediaToDelete = _mediaToDelete.asStateFlow()

  init {
    eventBus.register(this)
  }

  override fun onEvent(event: AppEvent) {
    when (event) {
      is MediaSelectionEvent.DeleteConfirmationOpened -> {
        _mediaToDelete.value = event.mediaToDelete.toImmutableList()
      }
    }
  }

  fun confirm() {
    Timber.i("Confirm media deletion")
    eventBus.post(MediaSelectionEvent.DeleteConfirmation)
    panelDelegate.closeDeleteConfirmationPanel()
  }

  fun cancel() {
    Timber.i("Closing delete confirmation panel")
    panelDelegate.closeDeleteConfirmationPanel()
  }
}
