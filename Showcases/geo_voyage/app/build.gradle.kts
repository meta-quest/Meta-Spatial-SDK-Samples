// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

import java.util.Properties

val metaSpatialSdkVersion: String by project

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.serialization")
  id("org.jetbrains.kotlin.plugin.compose")
  id("com.meta.spatial.plugin")
  id("com.google.devtools.ksp") version "2.0.0-1.0.24" apply true
}

android {
  namespace = "com.meta.pixelandtexel.geovoyage"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.meta.pixelandtexel.geovoyage"
    minSdk = 29
    //noinspection ExpiredTargetSdkVersion
    targetSdk = 32
    versionCode = 20
    versionName = "1.0.0"

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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = "1.8" }
}

composeCompiler { enableStrongSkippingMode = true }

dependencies {
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.activity:activity-ktx:1.9.2")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.navigation:navigation-runtime-ktx:2.8.1")
  implementation("androidx.navigation:navigation-compose:2.8.1")
  implementation("androidx.compose.ui:ui-text-google-fonts:1.7.2")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

  // Meta Spatial SDK libs
  implementation("com.meta.spatial:meta-spatial-sdk:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-ovrmetrics:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-toolkit:$metaSpatialSdkVersion")
  implementation("com.meta.spatial:meta-spatial-sdk-vr:$metaSpatialSdkVersion")

  // For parsing json
  implementation("com.google.code.gson:gson:2.10.1")

  // For formatting llama response
  implementation("com.github.jeziellago:compose-markdown:0.5.2")

  // AWS Bedrock integration
  implementation("aws.sdk.kotlin:bedrockruntime:1.3.3")

  // -- Jetpack compose --

  val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
  implementation(composeBom)
  androidTestImplementation(composeBom)
  // Material Design 3
  implementation("androidx.compose.material3:material3")
  // Android Studio Preview support
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  // UI Tests
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
  // Integration with activities
  implementation("androidx.activity:activity-compose:1.9.2")
  // Compose ConstraintLayout
  implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

  ksp("com.meta.spatial.plugin:com.meta.spatial.plugin.gradle.plugin:$metaSpatialSdkVersion")
  ksp("com.google.code.gson:gson:2.10.1")
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
val sceneDirectory = projectDir.dir("src/main/assets/scenes")

spatial {
  scenes {
    // if you have installed Meta Spatial Editor somewhere else, update the file path.
    // cliPath.set("/Applications/Meta Spatial Editor.app/Contents/MacOS/CLI")
    exportItems {
      item {
        projectPath.set(sceneDirectory.file("Main.metaspatial"))
        outputPath.set(sceneDirectory)
      }
    }

    componentGeneration { outputPath.set(sceneDirectory) }
  }
}
