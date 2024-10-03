## Media View Tooling

The Media View project leverages a variety of tools and third-party libraries to streamline development, enhance functionality, and ensure a high-quality user experience. This document provides an overview of the essential tooling used in the project.

### Third-Party Libraries

1. **Meta Spatial SDK:**
   - **Purpose:** The foundation for Media View's immersive experience.  Provides the framework for creating and managing the 3D environment, interacting with user input, rendering panels, and more.
   - **Documentation:** [https://developer.meta.com/docs/spatial-sdk](https://developer.meta.com/docs/spatial-sdk)
   - **Integration:**  Integrated directly into the project and used extensively in the `ImmersiveActivity`, `PanelManager`, and related components.

2. **Jetpack Compose:**
   - **Purpose:**  A modern, declarative UI toolkit for Android. Used to build all of Media View's UI components, including the media gallery, player panels, menus, and settings screens.
   - **Documentation:** [https://developer.android.com/jetpack/compose](https://developer.android.com/jetpack/compose)
   - **Integration:** The `presentation` module heavily relies on Compose for defining UI elements and their behavior.

3. **Dagger Hilt:**
   - **Purpose:** A dependency injection framework for Android, simplifying the management of dependencies and improving code testability.
   - **Documentation:**  [https://dagger.dev/hilt/](https://dagger.dev/hilt/)
   - **Integration:** Dagger Hilt is used throughout the project to inject dependencies, as illustrated in modules like `ContextModule` and `DispatcherModule`.

4. **ExoPlayer:**
   - **Purpose:** A robust and highly customizable media player library for Android. Used for playing video content within the `VideoView` composable.
   - **Documentation:** [https://exoplayer.dev/](https://exoplayer.dev/)
   - **Integration:** ExoPlayer is initialized and managed within the `VideoView` composable, providing a seamless video playback experience.

5. **Coil:**
   - **Purpose:** An image loading library for Android, simplifying the process of loading and displaying images from various sources. 
   - **Documentation:** [https://coil-kt.github.io/coil/](https://coil-kt.github.io/coil/)
   - **Integration:** Coil is used in `ImageView`, `MediaItemView`, and other composables to efficiently load and display images, including those from the MediaStore.

6. **Timber:**
   - **Purpose:** A logging library that provides a more fluent and flexible way to log messages compared to the standard Android logging system.
   - **Documentation:** [https://github.com/JakeWharton/timber](https://github.com/JakeWharton/timber)
   - **Integration:** Timber is used throughout the project for logging debug messages, warnings, and errors. The `MediaViewDebugTree` provides a custom Timber tree for improved log output.

7. **Glide:**
   - **Purpose:** An image loading and caching library used for loading high-resolution images, such as panoramas, efficiently.
   - **Documentation:** [https://bumptech.github.io/glide/](https://bumptech.github.io/glide/)
   - **Integration:** Integrated specifically in `PanoramaImageView` to handle large panorama images smoothly.

### Build Scripts (Gradle)

- **Purpose:**  Gradle is the build system used for Android projects. The project's `build.gradle(.kts)` files define dependencies, build configurations, and other build-related settings. 
- **Key Configurations:**
   - **Dependencies:**  The `dependencies` block within the `build.gradle(.kts)` files specifies all external libraries used in the project.
   - **Build Types:** Different build types (e.g., `debug`, `release`) can be defined to configure build settings for different environments. 
   - **Signing Configs:**  Signing configurations are used to sign the APK for release builds. 

### Other Tools

1. **Android Debug Bridge (ADB):**
   - **Purpose:**  A command-line tool used to communicate with Android devices. 
   - **Use Cases:**
     - Installing APKs: `adb install path/to/app.apk`
     - Debugging: `adb logcat`
     - File Transfer: `adb push/pull`

2. **SideQuest:**
   - **Purpose:**  A desktop application that provides a more convenient way to manage and install apps on Meta Quest devices.
   - **Use Cases:**
     - APK Installation
     - File Management
     - Device Settings

3. **Logcat:**
   - **Purpose:** The Android logging system, providing a real-time stream of system messages, application logs, and more.
   - **Use Cases:**
     - Debugging
     - Monitoring App Behavior

By leveraging these powerful tools and libraries, the Media View project achieves a streamlined development process, robust media handling capabilities, a modern and responsive UI, and a compelling immersive experience.
