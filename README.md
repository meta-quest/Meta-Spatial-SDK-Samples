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

The [Showcases](/Showcases) folder contains five apps. These are fully-featured applications built with Meta Spatial SDK, and are open-sourced here in this repository.

- [Focus](/Showcases/focus)
  - [Download from the store](https://www.meta.com/experiences/focus/8625912667430203/)
- [Media View](/Showcases/media_view)
  - [Download from the store](https://www.meta.com/experiences/media-view/8510454682344317/)
- [Geo Voyage](/Showcases/geo_voyage)
  - [Download from the store](https://www.meta.com/experiences/geo-voyage/8230251250434003/)
- [Meta Horizon UI Set](/Showcases/UISetSample)
- [Spatial Scanner](/Showcases/meta_spatial_scanner)


## Documentation

The documentation for Meta Spatial SDK can be found [here](https://developers.meta.com/horizon/develop/spatial-sdk).

## Release Notes

Find our official release notes [here](https://developers.meta.com/horizon/documentation/spatial-sdk/release-notes).

## 0.7.0 Updates

This release is a major version bump, which means some breaking changes were introduced.

### Added

- Spatial SDK activity lifecycle
  - Add an activity lifecycle callback `onSpatialShutdown`. This lifecycle callback is guaranteed to be called during a Spatial activity’s shutdown process. Spatial resources such as entities and scene objects should be cleaned up here.
- Experimental Feature: `childrenOf` query
  - Add a [childrenOf](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-childrenof-query) query to query the child entities for an entity.
- Experimental Feature: `changedSince` query
  - Add a [changedSince](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-changedsince-query) query to get the changed entities since a certain datamodel version.
- CPU/GPU performance level now defaults to `SUSTAINED_HIGH`, added new performance level controls
- Support for [compositor layer sharpening and super sampling](https://developers.meta.com/horizon/documentation/native/android/mobile-openxr-composition-layer-filtering) via `LayerFilters`
- Support for [Live Edits](https://developer.android.com/develop/ui/compose/tooling/iterative-development) on Jetpack Compose UIs.

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

## Spatial SDK Gradle Plugin

The samples all include the Spatial SDK Gradle Plugin in their build files. This plugin is used for the [Spatial Editor integration](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-editor#use-the-spatial-sdk-gradle-plugin) and for build-related features like [custom shaders](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-custom-shaders).

Meta collects telemetry data from the Spatial SDK Gradle Plugin to help improve MPT Products. You can read the [Supplemental Meta Platforms Technologies Privacy Policy](https://www.meta.com/legal/privacy-policy/) to learn more.

## License

The Meta Spatial SDK Samples package is multi-licensed.

The majority of the project is licensed under the [MIT License](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/blob/main/LICENSE), as found in the LICENSE file.

The [Meta Platform Technologies SDK license](https://developer.oculus.com/licenses/oculussdk/) applies to the Meta Spatial SDK and supporting material, and to the assets used in the Meta Spatial SDK Samples package. The [MPT SDK license](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/tree/main/MrukSample/app/src/main/assets/LICENSE.md) can be found in the asset folder of each sample.

Specifically, all the supporting materials in each sample's `app/src/main/res/raw` and `app/src/main/assets` folders including 3D models, videos, sounds, and others, are licensed under the [MPT SDK license](https://developer.oculus.com/licenses/oculussdk/).
