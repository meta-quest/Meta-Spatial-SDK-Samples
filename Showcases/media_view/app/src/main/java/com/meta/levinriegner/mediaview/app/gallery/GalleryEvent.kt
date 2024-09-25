// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery

sealed class GalleryEvent {
  data class UploadFailed(val error: String?) : GalleryEvent()
}
