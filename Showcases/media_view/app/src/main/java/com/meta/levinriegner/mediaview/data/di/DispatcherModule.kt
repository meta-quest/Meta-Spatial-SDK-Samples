// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
  @DefaultDispatcher
  @Provides
  fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

  @IoDispatcher @Provides fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

  @MainDispatcher @Provides fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

@Retention(AnnotationRetention.BINARY) @Qualifier annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY) @Qualifier annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY) @Qualifier annotation class MainDispatcher
