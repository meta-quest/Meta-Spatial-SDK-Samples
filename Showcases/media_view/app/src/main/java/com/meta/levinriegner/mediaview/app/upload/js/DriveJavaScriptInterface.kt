// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.upload.js

import android.util.Base64
import android.webkit.JavascriptInterface

class DriveJavaScriptInterface(
    private val onAuthCompleted: (token: String?) -> Unit,
    private val onMediaDownloaded: (media: DriveMedia) -> Unit,
    private val onDownloadFailed: (reason: String) -> Unit,
    private val onDownloadCanceled: () -> Unit = {},
) {

  @JavascriptInterface
  fun onAccessTokenReceived(token: String?) {
    onAuthCompleted(token)
  }

  @JavascriptInterface
  fun downloadFile(
      fileId: String,
      base64: String,
      mimeType: String,
      fileName: String,
      currentChunk: Int,
      totalChunks: Int,
  ) {
    val cleanedBase64 = base64.substringAfter(";base64,") // Remove the base64 prefix
    val mediaBytes = Base64.decode(cleanedBase64, Base64.DEFAULT)
    val driveMedia =
        DriveMedia(fileId, mediaBytes, mimeType, fileName, Pair(currentChunk, totalChunks))
    onMediaDownloaded(driveMedia)
  }

  @JavascriptInterface
  fun onGetFileFailed(reason: String) {
    onDownloadFailed(reason)
  }

  @JavascriptInterface
  fun onGetFiledCanceled() {
    onDownloadCanceled()
  }
}
