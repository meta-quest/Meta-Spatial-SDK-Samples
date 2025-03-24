// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.6.1" apply false
  id("org.jetbrains.kotlin.android") version "2.1.20" apply false
  id("com.google.dagger.hilt.android") version "2.55" apply false
  id("com.datadoghq.dd-sdk-android-gradle-plugin") version "1.14.0" apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "1.9.25" apply false
  id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}
