// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.menu.immersive

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.events.AppEvent
import com.meta.levinriegner.mediaview.app.events.AppEventListener
import com.meta.levinriegner.mediaview.app.events.EditEvent
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.app.shared.util.FeatureFlags
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImmersiveMenuViewModel
@Inject
constructor(
  savedStateHandle: SavedStateHandle,
  private val panelDelegate: PanelDelegate,
  private val eventBus: EventBus,
) : ViewModel(), AppEventListener {

  init {
    eventBus.register(this)
  }

  private val mediaModel = savedStateHandle.get<MediaModel>("mediaModel")!!

  private val _state =
      MutableStateFlow<ImmersiveMenuState>(
          ImmersiveMenuState.Initial(
              canEdit = mediaModel.editOptions.isNotEmpty() && FeatureFlags.MEDIA_EDIT_ENABLED,
          ),
      )
  val state = _state.asStateFlow()

  fun exitImmersiveMedia() {
    panelDelegate.minimizeMedia(mediaModel, false)
  }

  fun onEditPressed() {
    Timber.i("Edit pressed")
    _state.value = ImmersiveMenuState.Editing(saveLoading = false)
    eventBus.post(EditEvent.EnterCrop(mediaModel.id))
  }

  fun onExitEditPressed() {
    Timber.i("Exit edit pressed")
    _state.value = ImmersiveMenuState.Initial(canEdit = true)
    eventBus.post(EditEvent.ExitCrop(mediaModel.id))
  }

  fun onSaveImagePressed() {
    if (_state.value is ImmersiveMenuState.Editing && (_state.value as ImmersiveMenuState.Editing).saveLoading) {
      // Skip if already saving
      return
    }
    Timber.i("Save image pressed")
    _state.value = ImmersiveMenuState.Editing(saveLoading = true)
    eventBus.post(EditEvent.SaveImageRequest(mediaModel.id))
  }

  override fun onEvent(event: AppEvent) {
    when (event) {
      is EditEvent.SaveImageCompleted -> {
        if (event.mediaId == mediaModel.id) {
          _state.value = ImmersiveMenuState.Initial(canEdit = true)
        }
      }
    }
  }
}
