// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app

import android.app.Application
import android.util.Log
import coil.Coil
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.datadog.android.Datadog
import com.datadog.android.DatadogSite
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.log.Logger
import com.datadog.android.log.Logs
import com.datadog.android.log.LogsConfiguration
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.Rum
import com.datadog.android.rum.RumConfiguration
import com.datadog.android.rum.tracking.ActivityViewTrackingStrategy
import com.meta.levinriegner.mediaview.BuildConfig
import com.meta.levinriegner.mediaview.app.logging.DatadogTree
import com.meta.levinriegner.mediaview.app.logging.MediaViewDebugTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MediaViewApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    // Logging
    if (BuildConfig.DEBUG) {
      Timber.plant(MediaViewDebugTree())
    } else {
        initDatadog()
    }
    // Coil
    Coil.setImageLoader(
        ImageLoader.Builder(this)
            .components { add(VideoFrameDecoder.Factory()) }
            .crossfade(true)
            .build())
  }

    private fun initDatadog() {
        // SDK Configuration
        val environmentName = "production"
        val appVariantName = ""
        val configuration = Configuration.Builder(
            clientToken = BuildConfig.DATADOG_CLIENT_TOKEN,
            env = environmentName,
            variant = appVariantName
        )
            .useSite(DatadogSite.US1)
            .build()
        Datadog.initialize(this, configuration, TrackingConsent.GRANTED)

        // RUM
        val rumConfiguration = RumConfiguration.Builder(BuildConfig.DATADOG_APPLICATION_ID)
            .trackUserInteractions()
            .trackLongTasks()
            .useViewTrackingStrategy(ActivityViewTrackingStrategy(true))
            .build()
        Rum.enable(rumConfiguration)

        // Logs
        val logsConfig = LogsConfiguration.Builder().build()
        Logs.enable(logsConfig)
        val logger = Logger.Builder()
            .setNetworkInfoEnabled(true)
            .setLogcatLogsEnabled(true)
            .setBundleWithTraceEnabled(true)
            .setName("MediaView")
            .setRemoteLogThreshold(Log.INFO)
            .setLogcatLogsEnabled(false)
            .build()
        Timber.plant(DatadogTree(logger))
    }
}
