// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app

import android.app.Application
import com.meta.levinriegner.uiset.BuildConfig
import com.meta.levinriegner.uiset.app.util.logging.UISetSampleDebugTree
import timber.log.Timber

class UISetSampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // Logging
    if (BuildConfig.DEBUG) {
      Timber.plant(UISetSampleDebugTree())
    }
  }
}
