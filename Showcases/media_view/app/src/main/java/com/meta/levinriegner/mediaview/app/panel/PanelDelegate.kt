// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.panel

import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel

interface PanelDelegate {
  fun openMediaPanel(mediaModel: MediaModel)

  fun closeMediaPanel(mediaModel: MediaModel)

  fun closeAllMedia()

  fun maximizeMedia(mediaModel: MediaModel)

  fun minimizeMedia(mediaModel: MediaModel, close: Boolean)

  fun openUploadPanel()

  fun closeUploadPanel()
}
