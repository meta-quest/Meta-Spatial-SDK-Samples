// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player

sealed class PlayerEvent {
  data object Close : PlayerEvent()

  data object OnCropImageRequested : PlayerEvent()

  data class OnImageSaved(val success: Boolean, val error: String?) : PlayerEvent()
}
