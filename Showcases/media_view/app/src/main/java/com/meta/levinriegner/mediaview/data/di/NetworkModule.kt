// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.di

import com.meta.levinriegner.mediaview.data.util.HttpRetryInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  @Provides
  fun providesHttpClient(): OkHttpClient =
      OkHttpClient.Builder().addInterceptor(HttpRetryInterceptor()).build()
}
