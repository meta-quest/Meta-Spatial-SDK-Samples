// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.10.1" apply false
  id("org.jetbrains.kotlin.android") version "2.1.21" apply false
  id("com.google.dagger.hilt.android") version "2.56.2" apply false
  id("com.datadoghq.dd-sdk-android-gradle-plugin") version "1.14.0" apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21" apply false
  id("org.jetbrains.kotlin.plugin.compose") version "2.1.21" apply false
  id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}
