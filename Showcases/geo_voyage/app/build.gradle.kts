// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.meta.spatial.plugin)
  alias(libs.plugins.jetbrains.kotlin.plugin.compose)
}

android {
  namespace = "com.meta.pixelandtexel.geovoyage"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.meta.pixelandtexel.geovoyage"
    minSdk = 29
    //noinspection ExpiredTargetSdkVersion
    targetSdk = 32
    versionCode = 26
    versionName = "1.1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // Pass the ollama server url to the BuildConfig
    val ollamaServerURL = getLocalProperty("OLLAMA_SERVER_URL", project)
    buildConfigField("String", "OLLAMA_SERVER_URL", "\"$ollamaServerURL\"")

    // Pass our aws bedrock credentials to the BuildConfig
    val awsBedrockAccessKey = getLocalProperty("AWS_BEDROCK_ACCESS_KEY", project)
    val awsBedrockSecretKey = getLocalProperty("AWS_BEDROCK_SECRET_KEY", project)
    buildConfigField("String", "AWS_BEDROCK_ACCESS_KEY", "\"$awsBedrockAccessKey\"")
    buildConfigField("String", "AWS_BEDROCK_SECRET_KEY", "\"$awsBedrockSecretKey\"")

    // Pass the wit.ai client access token to the BuildConfig
    val witAiClientAccessToken = getLocalProperty("WIT_AI_CLIENT_ACCESS_TOKEN", project)
    buildConfigField("String", "WIT_AI_CLIENT_ACCESS_TOKEN", "\"$witAiClientAccessToken\"")

    // Pass the Google Maps API key to the BuildConfig
    val googleMapsApiKey = getLocalProperty("GOOGLE_MAPS_API_KEY", project)
    buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"$googleMapsApiKey\"")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    buildConfig = true
    compose = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.activity.ktx)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.navigation.runtime.ktx)
  implementation(libs.androidx.navigation.compose)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  // Meta Spatial SDK libs
  implementation(libs.meta.spatial.sdk.base)
  implementation(libs.meta.spatial.sdk.isdk)
  implementation(libs.meta.spatial.sdk.toolkit)
  implementation(libs.meta.spatial.sdk.uiset)
  implementation(libs.meta.spatial.sdk.vr)

  // For parsing json
  implementation(libs.google.gson)

  // For formatting llama response
  implementation(libs.compose.markdown)

  // AWS Bedrock integration
  implementation(libs.aws.bedrockruntime)

  // -- Jetpack compose --

  implementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(platform(libs.androidx.compose.bom))
  // Material Design 3
  implementation(libs.androidx.material3)
  // Android Studio Preview support
  implementation(libs.androidx.ui.tooling.preview)
  debugImplementation(libs.androidx.ui.tooling)
  // UI Tests
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.test.manifest)
  // Integration with activities
  implementation(libs.androidx.activity.compose)
}

// Function to load properties from the local.properties file
fun getLocalProperty(key: String, project: Project): String {
  val localProperties = Properties()
  val localPropertiesFile = project.rootProject.file("secrets.properties")
  if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { inputStream -> localProperties.load(inputStream) }
  }
  return localProperties.getProperty(key, "")
}

val projectDir = layout.projectDirectory
val sceneDirectory = projectDir.dir("scenes")

spatial {
  scenes {
    // if you have installed Meta Spatial Editor somewhere else, update the file path.
    // cliPath.set("/Applications/Meta Spatial Editor.app/Contents/MacOS/CLI")
    exportItems {
      item {
        projectPath.set(sceneDirectory.file("Main.metaspatial"))
        outputPath.set(projectDir.dir("src/main/assets/scenes"))
      }
    }
  }
  hotReload {
    appPackage.set("com.meta.pixelandtexel.geovoyage")
    appMainActivity.set(".MainActivity")
    assetsDir.set(File("src/main/assets"))
  }
}
