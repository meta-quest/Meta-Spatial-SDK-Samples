// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

import java.io.FileInputStream
import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.meta.spatial)
  alias(libs.plugins.compose.compiler)
}

// Signing
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("private/keystore.properties")

if (keystorePropertiesFile.exists()) {
  keystoreProperties.load(FileInputStream(keystorePropertiesFile))
  project.logger.info("Signing loaded from local file")
} else if (System.getenv("KEYSTORE_FILE") != null) {
  keystoreProperties.setProperty("keystoreFile", System.getenv("KEYSTORE_FILE"))
  keystoreProperties.setProperty("keystorePassword", System.getenv("KEYSTORE_PASSWORD"))
  keystoreProperties.setProperty("keyAlias", System.getenv("KEY_ALIAS"))
  keystoreProperties.setProperty("keyPassword", System.getenv("KEY_PASSWORD"))
} else {
  project.logger.info("No release signing available")
}

android {
  namespace = "com.meta.levinriegner.uiset"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.meta.levinriegner.uiset"
    minSdk = 29
    //noinspection ExpiredTargetSdkVersion
    targetSdk = 32
    versionCode = 9
    versionName = "0.0.16"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
  }

  signingConfigs {
    getByName("debug") {
      keyAlias = "androiddebugkey"
      keyPassword = "android"
      storeFile = file("../.debug/debug.jks")
      storePassword = "android"
    }
    if (keystoreProperties["keystoreFile"] != null) {
      create("release") {
        keyAlias = keystoreProperties.getProperty("keyAlias")
        keyPassword = keystoreProperties.getProperty("keyPassword")
        storeFile = file(keystoreProperties.getProperty("keystoreFile"))
        storePassword = keystoreProperties.getProperty("keystorePassword")
      }
    }
  }

  buildTypes {
    getByName("release") { signingConfig = signingConfigs.getByName("debug") }
    debug {
      isMinifyEnabled = false
      isShrinkResources = false
      signingConfig = signingConfigs.getByName("debug")
    }
    if (keystoreProperties["keystoreFile"] != null) {
      release {
        isMinifyEnabled = false
        isShrinkResources = false
        isDebuggable = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
        )
        signingConfig = signingConfigs.getByName("release")
      }
    }
  }

  productFlavors {
    create("qa") {
      dimension = "app"
      applicationIdSuffix = ".qa"
      manifestPlaceholders["applicationLabel"] = "UISetSample QA"
    }
    create("production") {
      dimension = "app"
      manifestPlaceholders["applicationLabel"] = "UISetSample"
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = "1.8" }
  buildFeatures {
    compose = true
    buildConfig = true
    flavorDimensions += "app"
  }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)
  implementation(libs.androidx.media3.exoplayer)
  implementation(libs.androidx.media3.ui)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  // Meta Spatial SDK
  implementation(libs.meta.spatial.sdk)
  implementation(libs.meta.spatial.sdk.castinputforward)
  implementation(libs.meta.spatial.sdk.compose)
  implementation(libs.meta.spatial.sdk.isdk)
  implementation(libs.meta.spatial.sdk.mruk)
  implementation(libs.meta.spatial.sdk.ovrmetrics)
  implementation(libs.meta.spatial.sdk.physics)
  implementation(libs.meta.spatial.sdk.toolkit)
  implementation(libs.meta.spatial.sdk.uiset)
  implementation(libs.meta.spatial.sdk.vr)

  // Utilities
  implementation(libs.jakewharton.timber)
}

val sceneProjectPath = "src/main/assets/scenes"

spatial {
  allowUsageDataCollection.set(true)
  scenes {
    // if you have installed Meta Spatial Editor somewhere else, update the file path.
    // cliPath.set("/Applications/Meta Spatial Editor.app/Contents/MacOS/CLI")
    exportItems {
      item {
        projectPath.set(File("$sceneProjectPath/Main.metaspatial"))
        outputPath.set(File("src/main/assets/scenes"))
      }
    }
  }
}
