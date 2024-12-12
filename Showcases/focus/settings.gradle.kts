// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

pluginManagement {
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
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/commeta-1086")
    }
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/commeta-1087")
    }
  }
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

rootProject.name = "Focus"

include(":app")
