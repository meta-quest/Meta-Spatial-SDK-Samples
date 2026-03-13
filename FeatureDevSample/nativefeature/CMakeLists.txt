# Copyright (c) Meta Platforms, Inc. and affiliates.
#
# This source code is licensed under the MIT license found in the
# LICENSE file in the root directory of this source tree.

# CMake build configuration for the native bobbing library.
# This demonstrates how to set up CMake for Android NDK in the Spatial SDK.

cmake_minimum_required(VERSION 3.22.1)
project("native_bobbing")

# Use C++17 for modern features
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Build the native library
# The library name "native_bobbing" will produce "libnative_bobbing.so"
add_library(native_bobbing SHARED
    src/main/cpp/native_bobbing.cpp
)

# Find the Android log library for debugging output
find_library(log-lib log)

# Link the native library with required system libraries
target_link_libraries(native_bobbing
    ${log-lib}
)
