// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.menu

import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.MediaPlayerEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryMenuViewModel
@Inject
constructor(
    private val panelDelegate: PanelDelegate,
    private val eventBus: EventBus,
) : ViewModel() {

  fun closeAll() {
    eventBus.post(MediaPlayerEvent.CloseAll)
    panelDelegate.closeAllMedia()
  }
}
