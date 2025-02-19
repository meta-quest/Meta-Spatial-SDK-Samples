# Changelog

This format is roughly based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## 0.5.5 - 2025-02-18

### Added

- Component XML
  - Components can now be defined in XML instead of Kotlin, this is now the preferred way to write Components
  - This makes it easier to define Components, and greatly improves the Spatial Editor integration with Components
  - The build process will generate Kotlin code and resource files for the XML components
- Panel Animation
  - This new feature includes animation timing and callback APIs, enabling you to manipulate panel transitions seamlessly
  - A panel zoom in and out animation is now available when creating and destroying panels
  - These capabilities enhance the interactivity and aesthetic appeal of your panels, providing you with greater control and flexibility
- Panel Native Transition between Quad and Cylinder Shapes
  - We have implemented animations for transitions between Quad and Cylinder panels
  - Check out the AnimationSample for an example
- Refined Cylinder Panels
  - We have conducted a comprehensive refinement of our Cylinder Panels to deliver enhanced performance and versatility. Key improvements include:
    - Grabbable Bug Fix: We have resolved a bug with Grabbable and cylinder panels, grabbing cylinder panels will now be more reliable
    - Backside Transparency: We have added transparency to the backside of the cylinder panels, staying consistent with quad panels

### Changed

- com.meta.spatial.toolkit.Controller.kt
  - In the Controller class, property “type” is now an EnumAttribute that accepts Enum values of `com.meta.spatial.toolkit.ControllerType`. The ControllerType enum is defined in the Java package ControllerType and has three values: `ControllerType.CONTROLLER`, `ControllerType.HAND`, and `ControllerType.EYES`.
  - The constants `com.meta.spatial.toolkit.Controller.CONTROLLER_TYPE`, `com.meta.spatial.toolkit.Controller.HAND_TYPE`, and `com.meta.spatial.toolkit.Controller.EYES_TYPE` are removed.
- TimeAttribute has changed from Int->Long

### Deprecated

- Writing Components using Kotlin is now deprecated, please shift to using Component XML to define your Components

### Fixed

- Fixed bug with rotation with Locomotion + behavior with updateViewOrigin
- Optimized panel update performance for large panels

## 0.5.4 - 2025-01-23

### Added

- Added `onHeadsetMounted` and `onHeadsetUnmounted` APIs for detecting when a user puts on or takes off their headset.

### Changed

- None

### Deprecated

- None

### Fixed

- Fixed Windows specific hot reload bug with "Read-only filesystem" error in Gradle task.
- Fixed crash where a `SceneLayer` was being destroyed twice due to garbage collection.

## 0.5.3 - 2024-12-16

### Added

- The `meta-spatial-sdk-compose` package is now available, enabling View-based panels to render Jetpack Compose UI
- Javadocs are now available for Maven Central released packages (starting with 0.5.3)
- MRUK
  - Added Scene Raycasting functionality (and a raycast demo in the MrukSample project)
  - Optimized scene loading

### Changed

- None

### Deprecated

- None

### Fixed

- Hot reload is now more reliable (previously has issues with parallelization)

## 0.5.2 - 2024-11-14

### Added

- Added `Followable` Component and `FollowableSystem` which allows devs to easily tether objects together. See Animations Sample for an example use.
- **Hot Reload**: Adds the ability to reload your `glb`/`gltf`/`glxf` and Meta Spatial Editor scenes while running your app via the Gradle plugin.
  - Auto Export from Meta Spatial Editor: Saving in Spatial Editor automatically exports to the app and pushes to the headset for hot reload
  - Two Reload Types:
    - Delete all entities and recreate them: more stable but does not work for all apps
    - Keep entities and reload meshes only: works for all apps, but less stable and does not reload components

### Changed

- `SamplerConfig`s now also apply to layers instead of just non-layer panels
- Cylinder panels now have a transparent back applied to them (instead of just being invisible)
- **Gradle Plugin**: References to string paths in plugin configuration are replaced with file references.
  - **NOTE:** This requires changes to your `build.gradle.kts`. Example new usage can be found in the sample `build.gradle.kts` files.
- **Gradle Plugin**: Telemetry now reports out simple usage statistics.

### Deprecated

- Deprecated `QuadLayerConfig`/`CylinderLayerConfig`/`EquirectLayerConfig`. Use `LayerConfig` instead for panel's layer configuration.

### Fixed

- Fixed a crash when garbage collecting a panel.
- Fixed a crash when updating the `Panel` component on an entity that already had a `Panel` component.
- Crash fixed in Focus showcase

## 0.5.1 - 2024-10-23

### Added

- Added support for Secure layers Layers can now be marked as secure making them appear black while recording. This is possible at a global level or an individual layer level.

```kotlin
// globally enable
scene.setSecureLayers(true)

// individually enable
myLayerConfig.secure = true
```

In addition, this value can be set on a global level using your AndroidManifest

```kotlin
<meta-data
      android:name="com.meta.spatial.SECURE_LAYERS"
      android:value="true"
/>
```

### Removed

- Removed the old Anchor Systems in toolkit, please use MRUK (Mixed Reality Utility Kit) in Meta Spatial SDK

### Fixed

- Init order issue for FeatureManager An overridden registerFeatures() was not able to reference the top level class variables. This was because the initialization was happening very early in the creation of a Feature. This was resolved by moving initialization later in activity startup.
- Memory leak on panel destruction Destroy the activity panel by calling lifecycle events onPause, onStop, onDestroy sequentially. Release the panel scene texture and mesh as panel destruction, which reclaims the resource early.
- Fixed the issue that the panel faced the user backwards when grabbed from behind. The panel will now always face the user correctly, regardless of the angle of grabbing.
- Fix the grabbable bug for the cylinder panel when the user is close to the panel. When the user is very close to the panel, i.e., the center of the cylinder is behind the user, the grabbable system does not work well for rotation.
- Fix compatibility issues with Android Studio Ladybug.
