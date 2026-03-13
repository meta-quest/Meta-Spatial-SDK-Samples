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
  namespace = "com.meta.spatial.samples.kotlinfeature"
  compileSdk = 34

  defaultConfig {
    minSdk = 34

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
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

spatial { components { registrationsClassName = "PulsingFeatureComponentRegistrations" } }

publishing {
  publications {
    register<MavenPublication>("release") {
      artifactId = "pulsing-feature"

      afterEvaluate { from(components["release"]) }

      pom {
        name.set("Pulsing Feature")
        description.set(
            "A Meta Spatial SDK feature that provides pulsing/scaling animations for entities"
        )
      }
    }
  }

  repositories {
    // Default: ~/.m2/repository
    //   ./gradlew :kotlinfeature:publishToMavenLocal
    mavenLocal()

    // Custom local directory (easier to inspect published artifacts)
    //   ./gradlew :kotlinfeature:publishReleasePublicationToLocalDirRepository
    maven {
      name = "LocalDir"
      url = uri(layout.buildDirectory.dir("maven-repo"))
    }
  }
}
