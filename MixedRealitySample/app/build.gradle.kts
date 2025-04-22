// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.meta.spatial.plugin)
}

android {
  namespace = "com.meta.spatial.samples.mixedrealitysample"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.meta.spatial.samples.mixedrealitysample"
    minSdk = 29
    //noinspection ExpiredTargetSdkVersion
    targetSdk = 32
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
  buildFeatures { buildConfig = true }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  // Meta Spatial SDK libs
  implementation(libs.meta.spatial.sdk.base)
  implementation(libs.meta.spatial.sdk.ovrmetrics)
  implementation(libs.meta.spatial.sdk.toolkit)
  implementation(libs.meta.spatial.sdk.physics)
  implementation(libs.meta.spatial.sdk.vr)
  implementation(libs.meta.spatial.sdk.mruk)
  implementation(libs.meta.spatial.sdk.castinputforward)
  implementation(libs.meta.spatial.sdk.hotreload)
  implementation(libs.meta.spatial.sdk.datamodelinspector)
}

afterEvaluate { tasks.named("assembleDebug") { dependsOn("export") } }

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
    hotReload {
      appPackage.set("com.meta.spatial.samples.mixedrealitysample")
      appMainActivity.set(".MixedRealitySampleActivity")
      assetsDir.set(File("src/main/assets"))
    }
  }
}
