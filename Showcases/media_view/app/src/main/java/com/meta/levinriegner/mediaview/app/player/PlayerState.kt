// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player

import android.net.Uri
import com.meta.levinriegner.mediaview.app.player.view.edit.CropState

sealed class PlayerState {

  data object Empty : PlayerState()

  data class Image2D(val uri: Uri, val cropState: CropState) : PlayerState()

  data class ImagePanorama(val uri: Uri) : PlayerState()

  data class Video2D(val uri: Uri) : PlayerState()

  data class Video360(val uri: Uri) : PlayerState()

  data class Error(val reason: String) : PlayerState()
}
