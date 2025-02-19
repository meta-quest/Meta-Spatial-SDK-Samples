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

- All samples, except **MRUKSample**, require you to install [Meta Spatial Editor](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-editor-overview).
- MediaPlayerSample contains an example of custom shaders, which requires the NDK to be installed and set up in *app/build.gradle.kts* (ex. `ndkVersion = "27.0.12077973"`)
- CustomComponentsSample supports our custom OVRMetrics integration
  - [Download & Enable OVRMetricsTool Steps](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-ovrmetrics)

## Samples

We have 10 sample apps, demonstrating various features of Meta Spatial SDK:

- [AnimationsSample](/AnimationsSample) shows how to play animation clips, create reusable animation drivers, and demonstrates frame-based procedural animation.
- [HybridSample](/HybridSample) shows how to begin with a standard Android-based 2D panel experience and switch between an immersive experience that hosts the same panel.
- [CustomComponentsSample](/CustomComponentsSample) shows how to create a custom component that embodies the data shared across various instances of an application.
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

The documentation for Meta Spatial SDK can be found [here](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-overview).

## 0.5.5 Updates

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

## Spatial SDK Gradle Plugin

The samples all include the Spatial SDK Gradle Plugin in their build files. This plugin is used for the [Spatial Editor integration](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-editor#use-the-spatial-sdk-gradle-plugin) and for build-related features like [custom shaders](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-custom-shaders).

Meta collects telemetry data from the Spatial SDK Gradle Plugin to help improve MPT Products. You can read the [Supplemental Meta Platforms Technologies Privacy Policy](https://www.meta.com/legal/privacy-policy/) to learn more.

## License

The Meta Spatial SDK Samples package is multi-licensed.

The majority of the project is licensed under the [MIT License](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/blob/main/LICENSE), as found in the LICENSE file.

The [Meta Platform Technologies SDK license](https://developer.oculus.com/licenses/oculussdk/) applies to the Meta Spatial SDK and supporting material, and to the assets used in the Meta Spatial SDK Samples package. The [MPT SDK license](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/tree/main/MrukSample/app/src/main/assets/LICENSE.md) can be found in the asset folder of each sample.

Specifically, all the supporting materials in each sample's `app/src/main/res/raw` and `app/src/main/assets` folders including 3D models, videos, sounds, and others, are licensed under the [MPT SDK license](https://developer.oculus.com/licenses/oculussdk/).
