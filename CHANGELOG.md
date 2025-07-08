# Changelog

Check out our official [release notes](https://developers.meta.com/horizon/documentation/spatial-sdk/release-notes).

This format is roughly based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## 0.7.0 - 2025-06-25

This release is a major version bump, which means some breaking changes were introduced.

### Added

- Spatial SDK activity lifecycle
  - Add an activity lifecycle callback `onSpatialShutdown`. This lifecycle callback is guaranteed to be called during a Spatial activity’s shutdown process. Spatial resources such as entities and scene objects should be cleaned up here.
- Experimental Feature: `childrenOf` query
  - Add a `childrenOf` query to query the child entities for an entity.
- Experimental Feature: `changedSince` query
  - Add a `changedSince` query to get the changed entities since a certain datamodel version.
- CPU/GPU performance level now defaults to `SUSTAINED_HIGH`, added new performance level controls
- Support for spatialization of Dolby Atmos and other soundfield audio using the AudioSessionId Component
- Support for compositor layer sharpening and super sampling (https://developers.meta.com/horizon/documentation/native/android/mobile-openxr-composition-layer-filtering) via `LayerFilters`

### Changed

- The Spatial SDK AARs are now built with Kotlin 2.1.0. This is a breaking change, so you will need to update the Kotlin version in your application.
- Components are cached by default now, which reduces access to native C++ data and improves performance.
- The component GLXF’s attribute `uri` is now a UriAttribute. The component Mesh’s `mesh` attribute is now also a UriAttribute.
- Ray intersections with non-BVH meshes now ignore triangle backfaces (consistent with BVH intersections).
  - This may break some apps - especially those with panels/buttons. Make sure they are oriented correctly.
- Custom shaders should now import base Spatial SDK shader files prefixed with “data/shaders”, which matches the path to these files from the APK assets directory.
  - `#include <metaSpatialSdkFragmentBase.glsl>` should become `#include <data/shaders/metaSpatialSdkFragmentBase.glsl>`
- Experimental feature: panel animations
  - The PanelAnimation component is removed. Use PanelQuadCylinderAnimation, PanelScaleInAnimation or PanelScaleOutAnimation instead.
- Experimental feature: ISDK
  - Rename IsdkToolkitBridgeSystem to IsdkComponentCreationSystem
  - Near-field Grabbable entities can be picked up with whole-palm grab
  - Interactor type hints added to PointerEvent.
  - Panel offsets added to IsdkPanelDimensions.
  - Updated sample applications to use IsdkFeature.

### Fixed

- Fixed issue with glTF Animation Pointer node transformations getting applied to the wrong nodes
- Experimental feature: ISDK
  - Input now works via Cast Input Forwarding
  - Entities can now be selected in the Data Model Inspector via in-game controllers or Cast Input Forwarding
  - IsdkGrabbable billboarding now supports mesh direction offsets.
  - Fix locomotion / IsdkSystem dependency ordering
  - Allow non-uniform scaling on grabbables
  - Improved cursor & ray visualizations
  - Disabling collision via `Hittable` component now disables Isdk Surfaces.
  - Visible(false) components now excluded from ISDK
  - Animation of Curved/Quad panels now correctly updates IsdkPanelDimensions

## 0.6.1 - 2025-05-15

### Added

- None

### Changed

- None

### Fixed

- Fixed a bug where the keyboard would not appear on v76+
- Fixed a bug where panel animations would cause a crash
- Fixed an issue where setting the passthrough LUT would not work after sleeping and waking up the device

## 0.6.0 - 2025-04-22

This release is a major version bump because it has a number of large improvements, new features, and a small number of breaking changes.

### Added

- Experimental Feature: [Interaction SDK](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-isdk-overview/)
  - Using `IsdkFeature` automatically replaces built in toolkit components/systems like Grabbable with `Isdk` equivalents
  - Provides interactions that are consistent with the Meta Horizon OS and brings parity between controller and hand interactions
    - Interact with panels directly using hands or controllers
    - Grab 3D objects with hands or controllers, directly or using a raycast
    - Advanced grab customization (responsiveness, two-handed, constraints)
  - The `Object3DSampleIsdk` sample app in the [samples repo](https://github.com/meta-quest/Meta-Spatial-SDK-Samples) demonstrates how to use the new IsdkFeature and other Isdk APIs
- Datamodel Inspector
  - Using `DataModelInspectorFeature` launches a webserver at a specified port that provides a live table view of ECS.
  - Connect to a running app via Data Model Inspector Tool Window in the new Meta Horizon Android Studio Plugin.
- [Query filters and sorting](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-queries/)
  - [Add filtering API for queries](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-filters/) so that developers can refine the entity query results by applying filters on attributes.
  - [Add sorting API for queries](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-sorting/) so that developers can sort the entity by criteria on attributes.
- GLTF Animation Pointer Support
  - Added the ability to modify material factors and UV transforms via `KHR_animation_pointer` support.
  - This can allow you to do things like animate opacity or make moving textures and play them with the `Animated()` component.
- [DRM support](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-2dpanel-drm) for Activity based panels on v76
  - Using an Activity based panel (not inflated view) along with a `LayerConfig` set to `secure=true` will allow you to display DRM content on v76+. Previously, you had to render directly to a secure swapchain.

### Changed

- We now support Color4 as an Attribute Type for use directly in components. Because of this, Color4 has been moved packages from `com.meta.spatial.toolkit` -> `com.meta.spatial.core`
- Uris are now supported as an Attribute Type for use in components
- Component XML is now the preferred method for making Spatial SDK Components
  - Using Components XML increases performance of components and queries.
  - Components XML can be used in Spatial Editor.
  - Components written in Kotlin (instead of XML) will no longer be able to be added to objects in Spatial Editor
- `PanelAnimation` and `PanelConfigOptions2` are now marked as experimental
  - These APIs may be unstable and/or subject to change in the future. If you want to use them now, you will need to use the `@SpatialSDKExperimentalAPI` annotation
- Default cursor has been changed to more closely match the Quest Home environment cursor
- Samples now use `libs.versions.toml` for version management (removing the need to set the version in the `gradle.properties` file)
- Changed the behavior of the `Layer.setClip()` API
  - If the area of the clip for the left or right eye is 0, there will be no layer submitted for that eye.
  - This can allow you to have separate transforms for layers sharing a swapchain with the left and right eyes
- Bundled shaders assets have been cleaned up and compressed, decreasing APK size.
- Various performance and stability improvements.

### Fixed

- Fixed bug where deleting an entity while grabbed causes a crash.

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
