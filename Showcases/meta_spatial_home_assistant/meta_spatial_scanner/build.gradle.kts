// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false

  id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
  id("com.google.devtools.ksp") version "2.1.20-1.0.31" apply false

}
