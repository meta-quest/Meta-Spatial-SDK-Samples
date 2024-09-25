// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

pluginManagement {
  // Retrieve Meta Spatial SDK Version from "gradle.properties"
  val metaSpatialSdkVersion: String by settings

  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
  plugins { id("com.meta.spatial.plugin") version metaSpatialSdkVersion }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
  }
}

rootProject.name = "Geo Voyage"

include(":app")
