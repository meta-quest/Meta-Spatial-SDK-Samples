// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.media_select.delete_confirm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.MediaSelectionEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaDeleteConfirmViewModel
@Inject
constructor(
  savedStateHandle: SavedStateHandle,
  private val eventBus: EventBus,
  private val panelDelegate: PanelDelegate
) : ViewModel() {

  private val mediaToDelete = savedStateHandle.get<List<MediaModel>>("mediaToDelete")

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
