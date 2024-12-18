// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.menu.minimized

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.MediaPlayerEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.levinriegner.mediaview.data.gallery.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MinimizedMenuViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val panelDelegate: PanelDelegate,
    private val eventBus: EventBus,
    private val galleryRepository: GalleryRepository,
) : ViewModel() {

    private val mediaModel = savedStateHandle.get<MediaModel>("mediaModel")!!

    fun maximize() {
        panelDelegate.maximizeMedia(mediaModel)
    }

    fun close() {
        panelDelegate.closeMediaPanel(mediaModel)
        eventBus.post(MediaPlayerEvent.Close(mediaModel.id))
    }

    fun delete() {
        // Delete
        Timber.d("Deleting media: ${mediaModel.id}")
        galleryRepository.setMediaFileDeleted(mediaModel.uri)
        eventBus.post(MediaPlayerEvent.Deleted(mediaModel.id))
        // Close
        close()
    }
}
