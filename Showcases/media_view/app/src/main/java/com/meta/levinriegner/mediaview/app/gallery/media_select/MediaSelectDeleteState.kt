// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.media_select

sealed class MediaSelectDeleteState {

  data object Idle : MediaSelectDeleteState()

  data class Deleting(val mediaToDeleteCount: Int) : MediaSelectDeleteState()

  data class Success(val deletedCount: Int) : MediaSelectDeleteState()

  data class Error(val message: String) : MediaSelectDeleteState()
}
