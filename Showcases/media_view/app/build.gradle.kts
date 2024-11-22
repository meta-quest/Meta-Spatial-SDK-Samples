// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

import java.io.FileInputStream
import java.util.Properties

// Retrieve Meta Spatial SDK Version from "gradle.properties"
val metaSpatialSdkVersion: String by project

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
  id("com.google.dagger.hilt.android")
  id("kotlin-parcelize")
  id("com.meta.spatial.plugin")
  id("com.datadoghq.dd-sdk-android-gradle-plugin")
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
  namespace = "com.meta.levinriegner.mediaview"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.meta.levinriegner.mediaview"
    minSdk = 29
    //noinspection ExpiredTargetSdkVersion
    targetSdk = 32
    versionCode = 14
    versionName = "0.0.13"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }

    // API Keys
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    // Google Drive Keys
    buildConfigField(
        "String",
        "DRIVE_CLIENT_ID",
        System.getenv("DRIVE_CLIENT_ID")
            ?: properties.getProperty("DRIVE_CLIENT_ID")
            ?: "\"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\"")
    buildConfigField(
        "String",
        "DRIVE_API_KEY",
        System.getenv("DRIVE_API_KEY")
            ?: properties.getProperty("DRIVE_API_KEY")
            ?: "\"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\"")
    buildConfigField(
        "String",
        "DRIVE_APP_ID",
        System.getenv("DRIVE_APP_ID")
            ?: properties.getProperty("DRIVE_APP_ID")
            ?: "\"XXXXXXXXXXXX\"")
    // Datadog Keys
    buildConfigField(
      "String",
      "DATADOG_CLIENT_TOKEN",
      System.getenv("DATADOG_CLIENT_TOKEN")
        ?: properties.getProperty("DATADOG_CLIENT_TOKEN")
        ?: "\"XXXXXXXXXXXX\"")
    buildConfigField(
      "String",
      "DATADOG_APPLICATION_ID",
      System.getenv("DATADOG_APPLICATION_ID")
        ?: properties.getProperty("DATADOG_APPLICATION_ID")
        ?: "\"XXXXXXXXXXXX\"")
  }

  buildTypes {
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
  kotlinOptions { jvmTarget = "1.8" }
  buildFeatures {
    viewBinding = true
    compose = true
    buildConfig = true
  }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  // Allow references to generated code (Hilt)
  kapt { correctErrorTypes = true }
}

dependencies {
    // Android presentation
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-video:2.7.0")

  implementation("com.github.bumptech.glide:glide:4.16.0")
  kapt("com.github.bumptech.glide:compiler:4.16.0")

    // ExoPlayer
  implementation("androidx.media3:media3-exoplayer:1.4.0")
  implementation("androidx.media3:media3-ui:1.4.0")

    // Dependency injection
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-android-compiler:2.51")

    // Utilities
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Meta Spatial SDK libs
    implementation("com.meta.spatial:meta-spatial-sdk:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-ovrmetrics:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-physics:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-toolkit:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-vr:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-mruk:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-castinputforward:$metaSpatialSdkVersion")

    // Meta Spatial SDK dependencies
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.facebook.soloader:soloader:0.11.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.11.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Monitoring
    implementation("com.datadoghq:dd-sdk-android-ndk:2.14.0")
    implementation("com.datadoghq:dd-sdk-android-logs:2.14.0")
}

val projectDir = layout.projectDirectory
val sceneDirectory = projectDir.dir("src/main/assets/scenes")

spatial {
  allowUsageDataCollection.set(true)
  scenes {
    // if you have installed Meta Spatial Editor somewhere else, update the file path.
    // cliPath.set("/Applications/Meta Spatial Editor.app/Contents/MacOS/CLI")
    exportItems {
      item {
        projectPath.set(sceneDirectory.file("Main.metaspatial"))
        outputPath.set(sceneDirectory)
      }
    }
  }
}
