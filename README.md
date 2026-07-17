# Meta Spatial SDK Samples

This repository is a collection of code samples and projects that demonstrate
the capabilities of Meta Spatial SDK.
[Meta Spatial SDK](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-overview)
is a new way to build immersive apps for Meta Horizon OS. Meta Spatial SDK lets
you combine the rich ecosystem of Android development and the unique
capabilities of Meta Quest via accessible APIs.

The samples in this repository showcase various features of the SDK, such as
spatial anchors, scene understanding, and object recognition. Each sample
project includes source code, build scripts, and documentation to help
developers understand how to use the SDK to build their own spatially-aware
applications.

Whether you're a seasoned developer or just starting out with Meta Quest/Horizon
OS, the Meta Spatial SDK Samples are a valuable resource for learning how to
leverage the power of spatial computing in your applications.

## Requirements

To try out these sample apps, you will need:

- A Meta Quest device (Quest 2/3/3S/Pro)
  - [Meta Quest build v69.0 or newer](https://www.meta.com/help/quest/articles/whats-new/release-notes/)
- Mac or Windows
  - [Android Studio Narwhal (2025.1.1) or newer](https://developer.android.com/studio) — required for Android Gradle Plugin 8.11
  - JDK 17 (bundled with Android Studio; needed separately only if you build from the command line)
  - [Meta Spatial Editor](https://developers.meta.com/horizon/downloads/spatial-sdk/)

## Build tooling

These samples are configured for the build toolchain below. If you are upgrading
an existing project, match these versions:

| Tool                        | Version                        |
| --------------------------- | ------------------------------ |
| Gradle                      | 9.4.1 (via the Gradle wrapper) |
| Android Gradle Plugin (AGP) | 8.11.1                         |
| Kotlin                      | 2.1.0                          |
| JDK                         | 17                             |

> **Upgrading from an older sample?** These samples use the Gradle 9.x wrapper
> with AGP 8.11.1. A project on AGP 8.5 (or earlier) running the Gradle 9.x
> wrapper can fail during native/CMake model sync, because that AGP version calls
> a Gradle API removed in Gradle 9 — upgrading AGP to 8.11.1 resolves it. If you
> are coming from Gradle 7/8, AGP 8.5 or earlier, or Kotlin 1.9, also review the
> [Gradle upgrade guide](https://docs.gradle.org/current/userguide/upgrading_major_version.html)
> and run the
> [AGP Upgrade Assistant](https://developer.android.com/build/agp-upgrade-assistant)
> in Android Studio, which automates most of the migration.

## Getting Started

First, ensure that all of the [requirements](#requirements) are met.

Then, to build and run a sample:

1. Clone this repository to your computer
2. Open the specific sample app with Android Studio
3. Plug in your Quest device to your computer
4. Click the "Run" button in the Android Studio toolbar, the app will now be
   running on your headset

**Notes**:

- All samples, except **MRUKSample** and **PremiumMediaSample**, require you to
  install
  [Meta Spatial Editor](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-editor-overview).
- MediaPlayerSample and PremiumMediaSample contain examples of custom shaders,
  which requires the NDK to be installed and set up in _app/build.gradle.kts_
  (ex. `ndkVersion = "27.0.12077973"`)
- Our samples support our custom OVRMetrics integration
  - [Download & Enable OVRMetricsTool Steps](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-ovrmetrics)

## Samples

We have 13 sample apps, demonstrating various features of Meta Spatial SDK:

- [AnimationsSample](/AnimationsSample) shows how to play animation clips,
  create reusable animation drivers, and demonstrates frame-based procedural
  animation.
- [HybridSample](/HybridSample) shows how to begin with a standard Android-based
  2D panel experience and switch between an immersive experience that hosts the
  same panel.
- [CustomComponentsSample](/CustomComponentsSample) shows how to create a custom
  component that embodies the data shared across various instances of an
  application.
- [PremiumMediaSample](/PremiumMediaSample) shows how to stream DRM-protected
  content, play 180-degree videos, and cast reflections from panels into the
  user's spatial setup with MRUK.
- [MediaPlayerSample](/MediaPlayerSample) shows how to build an immersive video
  playback experience.
- [MixedRealitySample](/MixedRealitySample) shows an immersive experience that
  interacts with the user's physical surroundings.
- [MrukSample](/MrukSample) shows an immersive experience influenced by the
  user's physical surroundings.
- [Object3DSample](/Object3DSample) shows inserting 3D objects into a scene and
  adjusting their properties in Meta Spatial Editor.
- [PhysicsSample](/PhysicsSample) shows adding a physics component and adjusting
  its properties in Meta Spatial Editor.
- [PremiumMediaSample](/PremiumMediaSample) shows a media streaming experience
  integrated into the users spatial environment.
- [SpatialVideoSample](/SpatialVideoSample) shows how to play video with
  spatialized audio.
- [StarterSample](/StarterSample) is a starter project that is part of
  [Getting Started](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-helloworld)
  with Meta Spatial SDK.
- [BodyTrackingSample](/BodyTrackingSample) shows how to access body tracking and utilize skeleton joint data.
- [UISetSample](/UISetSample) shows how to leverage the Meta Horizon OS UI Set to create high-quality, consistent user interfaces.
- [FeatureDevSample](/FeatureDevSample) shows how to build reusable `SpatialFeature` library modules as separate Android Library projects (Gradle modules).

We also have a starter app
[CustomComponentsStarter](/CodelabStarters/CustomComponentsStarter), which only
contains the boilerplate code of
[CustomComponentsSample](/CustomComponentsSample). You can download this starter
app and follow
[this](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-editor-create-app-content)
tutorial to build a LookAt app with Meta Spatial Editor and SDK.

## Showcases

The [Showcases](/Showcases) folder contains five apps. These are fully-featured
applications built with Meta Spatial SDK, and are open-sourced here in this
repository.

- [Focus](/Showcases/focus)
  - [Download from the store](https://www.meta.com/experiences/focus/8625912667430203/)
- [Media View](/Showcases/media_view)
  - [Download from the store](https://www.meta.com/experiences/media-view/8510454682344317/)
- [Geo Voyage](/Showcases/geo_voyage)
  - [Download from the store](https://www.meta.com/experiences/geo-voyage/8230251250434003/)
- [Spatial Scanner](/Showcases/meta_spatial_scanner)

## Documentation

The documentation for Meta Spatial SDK can be found
[here](https://developers.meta.com/horizon/develop/spatial-sdk).

## Release Notes

Find our official release notes
[here](https://developers.meta.com/horizon/release-notes/?search_key=spatial-sdk).

## Spatial SDK Gradle Plugin

The samples all include the Spatial SDK Gradle Plugin in their build files. This
plugin is used for the
[Spatial Editor integration](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-editor#use-the-spatial-sdk-gradle-plugin)
and for build-related features like
[custom shaders](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-custom-shaders).

Meta collects telemetry data from the Spatial SDK Gradle Plugin to help improve
MPT Products. You can read the
[Supplemental Meta Platforms Technologies Privacy Policy](https://www.meta.com/legal/privacy-policy/)
to learn more.

## License

The Meta Spatial SDK Samples package is multi-licensed.

The majority of the project is licensed under the
[MIT License](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/blob/main/LICENSE),
as found in the LICENSE file.

The
[Meta Platform Technologies SDK license](https://developer.oculus.com/licenses/oculussdk/)
applies to the Meta Spatial SDK and supporting material, and to the assets used
in the Meta Spatial SDK Samples package. The
[MPT SDK license](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/tree/main/MrukSample/app/src/main/assets/LICENSE.md)
can be found in the asset folder of each sample.

Specifically, all the supporting materials in each sample's
`app/src/main/res/raw` and `app/src/main/assets` folders including 3D models,
videos, sounds, and others, are licensed under the
[MPT SDK license](https://developer.oculus.com/licenses/oculussdk/).

## AI coding agents

This repo is wired up for AI coding agents: `AGENTS.md`, `.vscode/extensions.json`, `.mcp.json`, `.cursor/rules/`, and a few client-specific dotfiles surface the **Meta Horizon** VS Code/Cursor extension, the `hzdb` MCP server, and the Meta Quest skill set automatically.

Full toolchain, including Meta Spatial SDK skills and per-client install instructions: [github.com/meta-quest/agentic-tools](https://github.com/meta-quest/agentic-tools).
