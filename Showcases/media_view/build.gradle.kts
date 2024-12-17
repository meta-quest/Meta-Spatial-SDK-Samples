// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.1.4" apply false
  id("org.jetbrains.kotlin.android") version "1.9.25" apply false
  id("com.google.dagger.hilt.android") version "2.51" apply false
  id("com.datadoghq.dd-sdk-android-gradle-plugin") version "1.14.0" apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "1.9.25" apply false
}
