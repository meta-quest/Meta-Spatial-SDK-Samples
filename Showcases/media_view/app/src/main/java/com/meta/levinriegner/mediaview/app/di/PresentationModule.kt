// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.di

import android.content.Context
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.app.shared.util.InternetAvailability
import com.meta.spatial.toolkit.SpatialActivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Use this module to share data between different activities
@Module
@InstallIn(SingletonComponent::class)
object PresentationModule {

  @Provides
  fun providePanelDelegate(): PanelDelegate {
    return SpatialActivityManager.getAppSystemActivity() as PanelDelegate
  }

  @Singleton
  @Provides
  fun provideEventBus(): EventBus {
    return EventBus()
  }

  @Singleton
  @Provides
  fun internetChecker(@ApplicationContext appContext: Context): InternetAvailability {
    return InternetAvailability(appContext)
  }
}
