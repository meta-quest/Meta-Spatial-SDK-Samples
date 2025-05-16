// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.util.logging

import timber.log.Timber

class UISetSampleDebugTree : Timber.DebugTree() {

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    super.log(priority, "Timber.$tag", message, t)
  }
}
