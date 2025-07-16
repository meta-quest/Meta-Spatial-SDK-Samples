// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

val metaSpatialSdkVersion: String by project

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.meta.spatial.plugin")
}

android {
    namespace = "com.meta.theelectricfactory.focus"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.meta.theelectricfactory.focus"
        minSdk = 28
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 32
        versionCode = 21
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.sqlite.bundled.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    // AI Integration dependencies
    implementation(libs.google.gson)
    implementation(libs.squareup.okhttp3)
    implementation(platform(libs.squareup.okhttp3.boom))
    implementation(libs.squareup.okhttp3.mockwebserver)
    implementation(libs.squareup.okhttp3.logging.interceptor)
    implementation(libs.facebook.soloader)

    // Meta Spatial SDK dependencies
    implementation("com.meta.spatial:meta-spatial-sdk:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-ovrmetrics:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-physics:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-toolkit:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-vr:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-mruk:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-isdk:$metaSpatialSdkVersion")
    implementation("com.meta.spatial:meta-spatial-sdk-compose:$metaSpatialSdkVersion")

    // Meta Spatial UI Set
    implementation(files("libs/meta-spatial-uiset-1.0.1.aar"))
}
