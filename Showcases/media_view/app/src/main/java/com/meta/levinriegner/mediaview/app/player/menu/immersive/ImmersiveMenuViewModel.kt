// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.menu.immersive

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.MediaPlayerEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImmersiveMenuViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val panelDelegate: PanelDelegate,
    private val eventBus: EventBus,
) : ViewModel() {

  private val mediaModel = savedStateHandle.get<MediaModel>("mediaModel")!!

  fun exitImmersiveMedia() {
    panelDelegate.minimizeMedia(mediaModel, false)
  }

  fun closeImmersiveMedia() {
    panelDelegate.minimizeMedia(mediaModel, true)
    eventBus.post(MediaPlayerEvent.Close(mediaModel.id))
  }
}
