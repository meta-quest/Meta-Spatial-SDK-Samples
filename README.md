# Meta Spatial SDK Samples

The Meta Spatial SDK Samples is a collection of code samples and projects that demonstrate the capabilities of the Meta Spatial SDK. The Meta Spatial SDK provides developers with tools to create immersive, spatially-aware applications for Meta's AR and VR platforms.

The samples in this repository showcase various features of the SDK, such as spatial anchors, scene understanding, and object recognition. Each sample project includes source code, build scripts, and documentation to help developers understand how to use the SDK to build their own spatially-aware applications.

Whether you're a seasoned developer or just starting out with Meta's AR and VR platforms, the Meta Spatial SDK Samples are a valuable resource for learning how to leverage the power of spatial computing in your applications.

## Requirements

To try out these sample apps, you will need:

- Meta Quest devices, like Quest 2/3/Pro.
  - [Meta Quest build v69.0 or newer](https://www.meta.com/help/quest/articles/whats-new/release-notes/).
- Mac or Windows Laptap.
  - Android Studio Hedgehog or newer
  - [Meta Spatial Editor](https://developers.meta.com/horizon/downloads/spatial-sdk/)

## Getting Started

First, ensure that all of the [requirements](#requirements) are met.

Then, bring this package into the project, to build and run these samples:

1. Download the sample applications in the local folder
2. Open the specific sample app with Android Studio
3. Plugin the Quest device to the Laptap
4. Click the "Run 'app'" button in the Android Studio toolbar, The sample should be built, installed and run in the connected Quest devices automatically

Notes:

- All samples, except **MRUKSample**, require you to install [Meta Spatial Editor](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-editor-overview).
- MediaPlayerSample contains an example of custom shaders, which requires the NDK to be installed and set up in *app/build.gradle.kts* (ex. `ndkVersion = "27.0.12077973"`)
- CustomComponentsSample supports our custom OVRMetrics integration
  - [Download & Enable OVRMetricsTool Steps](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-ovrmetrics)

## Samples

As showcases, we have 10 sample apps, demonstrating various features of Meta Spatial SDK:

- AnimationsSample
- HybridSample
- CustomComponentsSample
- MediaPlayerSample
- MixedRealitySample
- MrukSample
- Object3DSample
- PhysicsSample
- SpatialVideoSample
- StarterSample

## Documentation

The documentation for Meta Spatial SDK and samples can be found [here](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-overview).

## License

The Meta Spatial SDK Samples package is multi-licensed.

The majority of the project is licensed under the [MIT License](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/blob/main/LICENSE), as found in the LICENSE file.

The [Meta Platform Technologies SDK license](https://developer.oculus.com/licenses/oculussdk/) applies to the Meta Spatial SDK and supporting material, and to the assets used in the Meta Spatial SDK Samples package. The [MPT SDK license](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/tree/main/MrukSample/app/src/main/assets/LICENSE.md) can be found in the root folder of the each sample's assets.

Specifically, all the supporting materials in each sample's `app/src/main/res/raw` and `app/src/main/assets` folders including 3D models, videos, sounds, and others, are licensed under [MPT SDK license](https://developer.oculus.com/licenses/oculussdk/).
