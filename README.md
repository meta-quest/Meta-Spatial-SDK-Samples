# Meta Spatial SDK Samples

This repository is a collection of code samples and projects that demonstrate the capabilities of Meta Spatial SDK. [Meta Spatial SDK](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-overview) is a new way to build immersive apps for Meta Horizon OS. Meta Spatial SDK lets you combine the rich ecosystem of Android development and the unique capabilities of Meta Quest via accessible APIs.

The samples in this repository showcase various features of the SDK, such as spatial anchors, scene understanding, and object recognition. Each sample project includes source code, build scripts, and documentation to help developers understand how to use the SDK to build their own spatially-aware applications.

Whether you're a seasoned developer or just starting out with Meta Quest/Horizon OS, the Meta Spatial SDK Samples are a valuable resource for learning how to leverage the power of spatial computing in your applications.

## Requirements

To try out these sample apps, you will need:

- A Meta Quest device (Quest 2/3/3S/Pro)
  - [Meta Quest build v69.0 or newer](https://www.meta.com/help/quest/articles/whats-new/release-notes/)
- Mac or Windows
  - Android Studio Hedgehog or newer
  - [Meta Spatial Editor](https://developers.meta.com/horizon/downloads/spatial-sdk/)

## Getting Started

First, ensure that all of the [requirements](#requirements) are met.

Then, to build and run a sample:

1. Clone this repository to your computer
2. Open the specific sample app with Android Studio
3. Plug in your Quest device to your computer
4. Click the "Run" button in the Android Studio toolbar, the app will now be running on your headset

**Notes**:

- All samples, except **MRUKSample** and **PremiumMediaSample**, require you to install [Meta Spatial Editor](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-editor-overview).
- MediaPlayerSample and PremiumMediaSample contain examples of custom shaders, which requires the NDK to be installed and set up in *app/build.gradle.kts* (ex. `ndkVersion = "27.0.12077973"`)
- Our samples support our custom OVRMetrics integration
  - [Download & Enable OVRMetricsTool Steps](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-ovrmetrics)

## Samples

We have 11 sample apps, demonstrating various features of Meta Spatial SDK:

- [AnimationsSample](/AnimationsSample) shows how to play animation clips, create reusable animation drivers, and demonstrates frame-based procedural animation.
- [HybridSample](/HybridSample) shows how to begin with a standard Android-based 2D panel experience and switch between an immersive experience that hosts the same panel.
- [CustomComponentsSample](/CustomComponentsSample) shows how to create a custom component that embodies the data shared across various instances of an application.
- [PremiumMediaSample](/PremiumMediaSample) shows how to stream DRM-protected content, play 180-degree videos, and cast reflections from panels into the user's spatial setup with MRUK.
- [MediaPlayerSample](/MediaPlayerSample) shows how to build an immersive video playback experience.
- [MixedRealitySample](/MixedRealitySample) shows an immersive experience that interacts with the user's physical surroundings.
- [MrukSample](/MrukSample) shows an immersive experience influenced by the user's physical surroundings.
- [Object3DSample](/Object3DSample) shows inserting 3D objects into a scene and adjusting their properties in Meta Spatial Editor.
- [PhysicsSample](/PhysicsSample) shows adding a physics component and adjusting its properties in Meta Spatial Editor.
- [SpatialVideoSample](/SpatialVideoSample) shows how to play video with spatialized audio.
- [StarterSample](/StarterSample) is a starter project that is part of [Getting Started](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-helloworld) with Meta Spatial SDK.

We also have a starter app [CustomComponentsStarter](/CodelabStarters/CustomComponentsStarter), which only contains the boilerplate code of [CustomComponentsSample](/CustomComponentsSample). You can download this starter app and follow [this](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-editor-create-app-content) tutorial to build a LookAt app with Meta Spatial Editor and SDK.

## Showcases

The [Showcases](/Showcases) folder contains three apps which are deployed to the Meta Horizon Store. These are fully-featured applications built with Meta Spatial SDK, and are open-sourced here in this repository.

- [Focus](/Showcases/focus)
  - [Download from the store](https://www.meta.com/experiences/focus/8625912667430203/)
- [Media View](/Showcases/media_view)
  - [Download from the store](https://www.meta.com/experiences/media-view/8510454682344317/)
- [Geo Voyage](/Showcases/geo_voyage)
  - [Download from the store](https://www.meta.com/experiences/geo-voyage/8230251250434003/)

## Documentation

The documentation for Meta Spatial SDK can be found [here](https://developers.meta.com/horizon/develop/spatial-sdk).

## Release Notes

Find our official release notes [here](https://developers.meta.com/horizon/documentation/spatial-sdk/release-notes).

## 0.6.0 Updates

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

- Fixed bug where deleting an entity while grabbed causes a crash

## Spatial SDK Gradle Plugin

The samples all include the Spatial SDK Gradle Plugin in their build files. This plugin is used for the [Spatial Editor integration](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-editor#use-the-spatial-sdk-gradle-plugin) and for build-related features like [custom shaders](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-custom-shaders).

Meta collects telemetry data from the Spatial SDK Gradle Plugin to help improve MPT Products. You can read the [Supplemental Meta Platforms Technologies Privacy Policy](https://www.meta.com/legal/privacy-policy/) to learn more.

## License

The Meta Spatial SDK Samples package is multi-licensed.

The majority of the project is licensed under the [MIT License](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/blob/main/LICENSE), as found in the LICENSE file.

The [Meta Platform Technologies SDK license](https://developer.oculus.com/licenses/oculussdk/) applies to the Meta Spatial SDK and supporting material, and to the assets used in the Meta Spatial SDK Samples package. The [MPT SDK license](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/tree/main/MrukSample/app/src/main/assets/LICENSE.md) can be found in the asset folder of each sample.

Specifically, all the supporting materials in each sample's `app/src/main/res/raw` and `app/src/main/assets` folders including 3D models, videos, sounds, and others, are licensed under the [MPT SDK license](https://developer.oculus.com/licenses/oculussdk/).
