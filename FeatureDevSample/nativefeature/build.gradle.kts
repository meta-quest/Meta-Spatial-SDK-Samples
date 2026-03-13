/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.meta.spatial.plugin)
  `maven-publish`
}

// Publishing metadata — update these for your library
group = "com.example.spatial"

version = "1.0.0"

android {
  namespace = "com.meta.spatial.samples.nativefeature"
  compileSdk = 34

  defaultConfig {
    minSdk = 34

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

    // NDK/CMake configuration for native code
    externalNativeBuild {
      cmake {
        cppFlags("-std=c++17")
        arguments("-DANDROID_STL=c++_shared")
      }
    }

    // Update the ndkVersion to the right version for your app
    // ndkVersion = "27.0.12077973"
  }

  // Configure CMake build
  externalNativeBuild {
    cmake {
      path("CMakeLists.txt")
      version = "3.22.1"
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }

  // Required for maven-publish to work with Android libraries
  publishing {
    singleVariant("release") {
      withSourcesJar()
      withJavadocJar()
    }
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)

  // Meta Spatial SDK libs
  implementation(libs.meta.spatial.sdk.base)
  implementation(libs.meta.spatial.sdk.toolkit)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}

spatial { components { registrationsClassName = "NativeFeatureComponentRegistrations" } }

publishing {
  publications {
    register<MavenPublication>("release") {
      artifactId = "native-bobbing-feature"

      afterEvaluate { from(components["release"]) }

      pom {
        name.set("Native Bobbing Feature")
        description.set(
            "A Meta Spatial SDK feature that provides native-calculated bobbing motion via JNI"
        )
      }
    }
  }

  repositories {
    // Default: ~/.m2/repository
    //   ./gradlew :nativefeature:publishToMavenLocal
    mavenLocal()

    // Custom local directory (easier to inspect published artifacts)
    //   ./gradlew :nativefeature:publishReleasePublicationToLocalDirRepository
    maven {
      name = "LocalDir"
      url = uri(layout.buildDirectory.dir("maven-repo"))
    }
  }
}
