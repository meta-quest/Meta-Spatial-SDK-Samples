// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ContextModule {
  @Provides
  fun provideContentResolver(@ApplicationContext appContext: Context): ContentResolver {
    return appContext.contentResolver
  }

  @Provides
  fun provideAssetManager(@ApplicationContext appContext: Context): AssetManager {
    return appContext.assets
  }

  @Provides
  fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
    return appContext.getSharedPreferences("mediaview", Context.MODE_PRIVATE)
  }
}
