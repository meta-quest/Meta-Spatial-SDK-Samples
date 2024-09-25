// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.events

import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter

open class AppEvent

sealed class UploadAppEvent : AppEvent() {
  data object UploadSuccess : UploadAppEvent()

  data class UploadFailed(val error: String) : UploadAppEvent()
}

sealed class FilterAppEvent : AppEvent() {
  data class FilterChanged(val filter: MediaFilter) : FilterAppEvent()
}

sealed class MediaPlayerEvent : AppEvent() {
  data class Close(val mediaId: Long) : MediaPlayerEvent()

  data object CloseAll : MediaPlayerEvent()
}
