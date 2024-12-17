// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

// Retrieve Meta Spatial SDK Version from "gradle.properties"
val metaSpatialSdkVersion: String by project

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.serialization")
  id("com.meta.spatial.plugin")
  // plugin.compose version must match your Kotlin version
  id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
  id("com.google.devtools.ksp") version "2.0.20-1.0.24" apply true
}

android {
  namespace = "com.meta.spatial.samples.customcomponentssample"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.meta.spatial.samples.customcomponentssample"
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
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
}

dependencies {
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.media3:media3-exoplayer:1.4.1")
  implementation("androidx.media3:media3-ui:1.4.1")
  implementation("androidx.navigation:navigation-compose:2.8.2")
  implementation("androidx.navigation:navigation-fragment-ktx:2.8.2")
  implementation("androidx.navigation:navigation-ui-ktx:2.8.2")

  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation(platform("androidx.compose:compose-bom:2024.09.03"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material:material")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
  implementation("androidx.compose.material:material-icons-core:1.7.4")

  // Meta Spatial SDK libs
  implementation("com.meta.spatial:meta-spatial-sdk:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-compose:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-ovrmetrics:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-physics:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-toolkit:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-vr:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-mruk:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-castinputforward:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-hotreload:$metaSpatialSdkVersion")

  androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.03"))
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

val projectDir = layout.projectDirectory
val sceneDirectory = projectDir.dir("scenes")

spatial {
  allowUsageDataCollection.set(true)
  scenes {
    // if you have installed Meta Spatial Editor somewhere else, update the file path.
    // cliPath.set("/Applications/Meta Spatial Editor.app/Contents/MacOS/CLI")
    exportItems {
      item {
        projectPath.set(sceneDirectory.file("Main.metaspatial"))
        outputPath.set(projectDir.dir("src/main/assets/scenes"))
      }
    }
    componentGeneration {
      outputPath.set(sceneDirectory)
      // We attempt to auto-detect where your "custom_components.json" is placed but if this does
      // not work then you can uncomment the following line and force it to a specific location.
      // customComponentsPath.set(projectDir.dir("build/generated/ksp/debug/resources"))
    }
    hotReload {
      appPackage.set("com.meta.spatial.samples.customcomponentssample")
      appMainActivity.set(".CustomComponentsSampleActivity")
      assetsDir.set(File("src/main/assets"))
    }
  }
}
