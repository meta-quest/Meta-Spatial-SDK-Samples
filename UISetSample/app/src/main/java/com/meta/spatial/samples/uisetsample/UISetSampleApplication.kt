/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample

import android.app.Application
import com.meta.spatial.samples.uisetsample.util.logging.UISetSampleDebugTree
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
