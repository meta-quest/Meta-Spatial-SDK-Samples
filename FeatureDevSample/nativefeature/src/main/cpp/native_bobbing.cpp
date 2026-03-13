/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

/**
 * Native bobbing calculation implementation.
 *
 * This file demonstrates how to create JNI bindings for native C++ code
 * in a Meta Spatial SDK library module. The bobbing calculation uses a
 * sine wave to create smooth up-and-down motion.
 *
 * Key Concepts:
 * 1. JNI function naming convention: Java_<package>_<class>_<method>
 *    - Underscores in package names become "_1"
 * 2. JNI parameter types map from Java/Kotlin types:
 *    - jfloat -> float, jlong -> int64_t, etc.
 * 3. Use Android logging for debugging native code
 */

#include <android/log.h>
#include <jni.h>
#include <cmath>

// Logging macros for debugging
#define LOG_TAG "NativeBobbing"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Mathematical constant
static constexpr float TWO_PI = 6.28318530718f;

extern "C" {

/**
 * Called when the native library is loaded via System.loadLibrary().
 * Use this for one-time initialization if needed.
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  LOGI("NativeBobbing library loaded successfully");
  return JNI_VERSION_1_6;
}

/**
 * Calculates the Y-offset for bobbing motion using a sine wave.
 *
 * This function is called from Kotlin via JNI. The naming convention is:
 * Java_<package_with_underscores>_<ClassName>_<methodName>
 *
 *
 * Mathematical Formula:
 *   offset = amplitude * sin(2π * frequency * time)
 *
 * @param env The JNI environment pointer
 * @param thiz The calling NativeBobbingSystem instance
 * @param elapsedTimeMs Time since bobbing started, in milliseconds
 * @param amplitude Maximum vertical displacement in meters
 * @param frequency Oscillation frequency in Hz (cycles per second)
 * @return The Y-axis offset to apply to the entity's position
 */
JNIEXPORT jfloat JNICALL
Java_com_meta_spatial_samples_nativefeature_NativeBobbingSystem_nativeCalculateBobbingOffset(
    JNIEnv* env,
    jobject thiz,
    jlong elapsedTimeMs,
    jfloat amplitude,
    jfloat frequency) {
  // Convert milliseconds to seconds for frequency calculation
  float elapsedSeconds = static_cast<float>(elapsedTimeMs) / 1000.0f;

  // Calculate phase: 2π * frequency * time
  float phase = TWO_PI * frequency * elapsedSeconds;

  // Calculate offset using sine wave
  float offset = amplitude * std::sin(phase);

  // Uncomment for debugging:
  // LOGD("Bobbing: time=%.2fs, amp=%.2f, freq=%.1fHz -> offset=%.3f",
  //      elapsedSeconds, amplitude, frequency, offset);

  return offset;
}

} // extern "C"
