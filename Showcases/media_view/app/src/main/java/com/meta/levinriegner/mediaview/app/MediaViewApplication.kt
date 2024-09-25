// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.meta.levinriegner.mediaview.app.logging.MediaViewDebugTree
import com.meta.spatial.core.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MediaViewApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    // Timber
    if (BuildConfig.DEBUG) {
      Timber.plant(MediaViewDebugTree())
    }
    // Coil
    Coil.setImageLoader(
        ImageLoader.Builder(this)
            .components { add(VideoFrameDecoder.Factory()) }
            .crossfade(true)
            .build())
  }
}
