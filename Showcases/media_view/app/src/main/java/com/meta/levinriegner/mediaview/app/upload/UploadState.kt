// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.upload

import android.content.ContentValues
import android.net.Uri

sealed class UploadState {
  data object Idle : UploadState()

  data class Uploading(
      val fileId: String,
      val fileName: String,
      val progress: Int,
      val total: Int,
      val contentValues: ContentValues,
      val uri: Uri,
  ) : UploadState()

  data class Error(val message: String) : UploadState()

  data object Completed : UploadState()
}
