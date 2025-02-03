// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.media_select

import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel

data class MediaSelectUiState(
    val isEnabled: Boolean = false,
    val selectedMedia: List<MediaModel> = emptyList(),
    val deleteState: MediaSelectDeleteState = MediaSelectDeleteState.Idle,
)
