// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.upload.js

import android.util.Base64
import timber.log.Timber

data class DriveMedia(
    val fileId: String,
    val blob: ByteArray,
    val mimeType: String,
    val fileName: String, // Includes the extension
    val progress: Pair<Int, Int>, // Current(starts at 1), Total
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as DriveMedia

    if (!blob.contentEquals(other.blob)) return false
    if (mimeType != other.mimeType) return false
    if (fileName != other.fileName) return false

    return true
  }

  override fun hashCode(): Int {
    var result = blob.contentHashCode()
    result = 31 * result + mimeType.hashCode()
    result = 31 * result + fileName.hashCode()
    return result
  }

  fun debugPrintBase64() {
    val base64 = Base64.encodeToString(blob, Base64.DEFAULT)
    base64.chunked(3999).forEach { Timber.d(it) }
  }
}
