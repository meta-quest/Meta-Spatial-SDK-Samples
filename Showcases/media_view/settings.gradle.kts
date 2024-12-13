// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

pluginManagement {
  // Retrieve Meta Spatial SDK Version from "gradle.properties"
  val metaSpatialSdkVersion: String by settings

  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/commeta-1086")
    }
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/commeta-1087")
    }
  }
  plugins { id("com.meta.spatial.plugin") version metaSpatialSdkVersion }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/commeta-1086")
    }
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/commeta-1087")
    }
  }
}

rootProject.name = "Media View"

include(":app")
