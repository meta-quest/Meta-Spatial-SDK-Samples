// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.events

import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel

open class AppEvent

sealed class UploadAppEvent : AppEvent() {
  data object UploadSuccess : UploadAppEvent()

  data class UploadFailed(val error: String) : UploadAppEvent()
}

sealed class FilterAppEvent : AppEvent() {
  data class FilterChanged(val filter: MediaFilter) : FilterAppEvent()
}

sealed class MediaSelectionEvent : AppEvent() {
  data class Deleted(val deletedMediaIds: List<Long>) : MediaSelectionEvent()
  data object DeleteConfirmation : MediaSelectionEvent()
}

sealed class MediaPlayerEvent : AppEvent() {
  data class Close(val mediaId: Long) : MediaPlayerEvent()

  data class Deleted(val mediaId: Long) : MediaPlayerEvent()

  data object CloseAll : MediaPlayerEvent()
}

sealed class NavigationEvent : AppEvent() {
  data object PrivacyPolicyAccepted : NavigationEvent()
}

sealed class EditEvent : AppEvent() {
  data class EnterCrop(val mediaId: Long) : EditEvent()

  data class ExitCrop(val mediaId: Long) : EditEvent()

  data class SaveImageRequest(val mediaId: Long) : EditEvent()

  data class SaveImageCompleted(val mediaId: Long, val success: Boolean) : EditEvent()
}
