// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.logging

import timber.log.Timber

class MediaViewDebugTree : Timber.DebugTree() {

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    super.log(priority, "Timber.$tag", message, t)
  }
}
