// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

// Retrieve Meta Spatial SDK Version from "gradle.properties"
val metaSpatialSdkVersion: String by project

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.serialization")
  id("com.meta.spatial.plugin")
  id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply true
}

android {
  namespace = "com.meta.spatial.samples.hybridsample"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.meta.spatial.samples.hybridsample"
    minSdk = 28
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // Update the ndkVersion to the right version for your app
    // ndkVersion = "27.0.12077973"
  }

  packaging { resources.excludes.add("META-INF/LICENSE") }

  lint { abortOnError = false }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  composeOptions { kotlinCompilerExtensionVersion = "1.4.4" }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
}

dependencies {
  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.11.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.media3:media3-exoplayer:1.4.1")
  implementation("androidx.media3:media3-ui:1.4.1")
  implementation("androidx.navigation:navigation-compose:2.8.0")
  implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
  implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
  implementation("androidx.activity:activity-compose:1.9.0")
  implementation(platform("androidx.compose:compose-bom:2023.03.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material:material")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

  // Meta Spatial SDK libs
  implementation("com.meta.spatial:meta-spatial-sdk:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-ovrmetrics:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-physics:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-toolkit:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-vr:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-mruk:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-castinputforward:$metaSpatialSdkVersion")

  androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")

  ksp("com.google.code.gson:gson:2.11.0")
  ksp("com.meta.spatial.plugin:com.meta.spatial.plugin.gradle.plugin:$metaSpatialSdkVersion")
}

afterEvaluate {
  tasks.named("assembleDebug") {
    dependsOn("export")
    finalizedBy("generateComponents")
  }
}

val sceneProjectPath = "app/scenes"
spatial {
  allowUsageDataCollection.set(true)
  scenes {
    // if you have installed Meta Spatial Editor somewhere else, update the file path.
    // cliPath.set("/Applications/Meta Spatial Editor.app/Contents/MacOS/CLI")
    exportItems {
      item {
        projectPath.set("$sceneProjectPath/Main.metaspatial")
        outputPath.set("app/src/main/assets/scenes")
      }
    }
    componentGeneration {
      outputPath.set(sceneProjectPath)
      // We attempt to auto-detect where your "custom_components.json" is placed but if this does not
      // work then you can uncomment the following line and force it to a specific location.
      // customComponentsPath.set("app/build/generated/ksp/debug/resources/custom_components.json")
    }
  }

}
