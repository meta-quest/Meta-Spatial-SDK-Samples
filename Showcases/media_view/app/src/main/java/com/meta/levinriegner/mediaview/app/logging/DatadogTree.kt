// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.logging

import android.util.Log
import com.datadog.android.log.Logger
import timber.log.Timber

class DatadogTree(private val logger: Logger) : Timber.Tree() {

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    val attributes =
        if (tag != null) {
          mapOf("timber.tag" to tag)
        } else {
          emptyMap()
        }
    when (priority) {
      Log.INFO -> logger.i(message, t, attributes)
      Log.WARN -> logger.w(message, t, attributes)
      Log.ERROR -> logger.e(message, t, attributes)
      Log.ASSERT -> logger.e(message, t, attributes)
      Log.VERBOSE,
      Log.DEBUG -> {
        // Do nothing
      }
    }
  }
}
