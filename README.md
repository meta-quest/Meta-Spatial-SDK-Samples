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

- [AnimationsSample](/AnimationsSample)
- [HybridSample](/HybridSample)
- [CustomComponentsSample](/CustomComponentsSample)
- [MediaPlayerSample](/MediaPlayerSample)
- [MixedRealitySample](/MixedRealitySample)
- [MrukSample](/MrukSample)
- [Object3DSample](/Object3DSample)
- [PhysicsSample](/PhysicsSample)
- [SpatialVideoSample](/SpatialVideoSample)
- [StarterSample](/StarterSample)

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

## License

The Meta Spatial SDK Samples package is multi-licensed.

The majority of the project is licensed under the [MIT License](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/blob/main/LICENSE), as found in the LICENSE file.

The [Meta Platform Technologies SDK license](https://developer.oculus.com/licenses/oculussdk/) applies to the Meta Spatial SDK and supporting material, and to the assets used in the Meta Spatial SDK Samples package. The [MPT SDK license](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/tree/main/MrukSample/app/src/main/assets/LICENSE.md) can be found in the asset folder of each sample.

Specifically, all the supporting materials in each sample's `app/src/main/res/raw` and `app/src/main/assets` folders including 3D models, videos, sounds, and others, are licensed under the [MPT SDK license](https://developer.oculus.com/licenses/oculussdk/).
