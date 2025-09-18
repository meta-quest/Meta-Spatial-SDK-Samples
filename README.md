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
- [PremiumMediaSample](/PremiumMediaSample) shows a media streaming experience integrated into the users spatial environment.
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

## 0.8.0 Updates

### Added

- New Panel Registration APIs
  - `ComposeViewPanelRegistration`: Panel registration for Jetpack Compose-based panels.
  - `ViewPanelRegistration`: Panel registration for dynamically created View-based panels.
  - `LayoutXMLPanelRegistration`: Panel registration for XML layout view-based panels.
  - `IntentPanelRegistration`: Panel registration for Intent-based panels that launch Activities.
  - `ActivityPanelRegistration`: Panel registration for Activity-based panels that launch specific Activity classes.
  - `VideoSurfacePanelRegistration`: Panel registration for direct-to-surface media rendering.
  - `ReadableVideoSurfacePanelRegistration`: Panel registration for when you want both post-processing and a simple surface to render on.
  - The new APIs are focused on simplifying panel configuration, for more information see our [documentation page on panel registration](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-2dpanel-registration)
- Shader hot reload
  - You can now hot reload your custom shader code. More information is available in [our documentation](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-hot-reload/#shader-hot-reload).
- `preloadMesh()` support
  - This method loads and caches a mesh from the specified URI so that when it's later assigned to an entity via the Mesh component, it will be available immediately. This is useful for performance optimization, especially during app startup when you can front-load your meshes.
  - Example code:

    ```kotlin
    systemManager.findSystem<MeshCreationSystem>()
    meshCreationSystem.preloadMesh(Uri.parse(meshFile))
    ```

- Panel graphics changes
  - New `layerBlendType`, replaces the `enableTransparent` control for panels. Now supports the ability to set the layer as:
    - `OPAQUE`: Alpha/transparency is ignored
    - `MASKED`: This is what `enableTransparent` previously enabled, panels can become transparent but will not blend with the background (the alpha is essentially force to be only 0 or 1)
    - `ALPHA_BLEND`: Alpha is blended accurately, resulting in higher quality edges. However, it is slower and can result in blending artifacts when overlapping with semitransparent meshes.
  - New `enableLayerFeatheredEdge` option in `PanelRenderSettings`, this will allow you to remove sharp black outlines on the edge of panels
- UISet
  - Bordered buttons
    - New composables: `BorderedButton`, `BorderedIconButton` (`com.meta.spatial.uiset.button`).
    - New defaults and colors: `BorderedButtonDefaults`, `BorderedButtonColors` (`com.meta.spatial.uiset.button.foundation`).
    - New button variants: `ButtonVariant.Regular.Bordered`, `ButtonVariant.Circled.Bordered`.
  - Card components
    - New composables: `PrimaryCard`, `SecondaryCard`, `OutlinedCard` (`com.meta.spatial.uiset.card.SpatialCard`).
    - Foundation: `CardImpl`, `CardDefaults`, `CardVariant`.
- Experimental: `Entity.markComponentChanged()` API
  - Marks a component as "changed" without setting new component data
  - This is useful in rare cases where you want the component to be considered "changed" for change detection purposes, even when the actual component data hasn't been modified. This will trigger component change listeners and queries that look for changed components.


### Changed

- ISDK
  - [ISDK](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-isdk-overview) is now the default input system.
  - Opt-out of ISDK via a new argument on VRFeature

    ```kotlin
    override fun registerFeatures(): List<SpatialFeature> {
      return listOf(
          // Set the inputSystemType to SIMPLE_CONTROLLER to turn off ISDK
          VRFeature(this, inputSystemType = VrInputSystemType.SIMPLE_CONTROLLER),
      )
    }
    ```

    - Adds near-field touch limiting & grabbing of objects
    - Some updates to your app may be required - interactions no longer support 2 sided panels. ensure panels and buttons are oriented with their normal vector pointing forwards.
  - `com.meta.spatial.vr.InputSystem` is no longer enabled by default. (`com.meta.spatial.isdk.IsdkSystem` now controls input)
  - `com.meta.spatial.toolkit.GrabbableSystem`  is no longer enabled by default (`com.meta.spatial.isdk.IsdkSystem` now controls grabbing & transforms)
  - `IsdkGrabbableFollowableSystem` fixes behavior when Entity has both `Grabbable` and `Followable` components.
  - `IsdkDefaultCursorSystem`: made some non-user-servicable fields private
  - Support for microgestures, check out `MicrogestureBits`
- Cast input forwarding
  - The debug lines (green lines that would appear during input forwarding) have now been removed.
  - Cursor placement for input forwarding is most accurate when the eye is set to "Right Eye" in MQDH Cast
- Spatial gradle plugin
  - The `export` step of the Gradle build process is now always executed and cannot be opted-out. If you don’t specify the Spatial Editor asset folder, the export step will not generate a GLXF file.
- UISet
  - Buttons foundation (`ButtonImpl`)
    - Signature extended with `modifier`, `colors`, `borderColor`, `labelTextStyle`, `contentAlignment`.
    - Color resolution updated to use provided `*ButtonColors` per variant; falls back to defaults when `colors` is null.
    - Border rendering added for bordered variants (1.dp, configurable via `borderColor`).
    - Content alignment is now configurable (affects container alignment and row arrangement).
  - Primary and Secondary buttons
    - `PrimaryButton` and `SecondaryButton` signatures extended with `modifier`, `colors`, `labelTextStyle`, `contentAlignment`.
  - Existing `PrimaryIconButton`, `PrimaryCircleButton`, `SecondaryIconButton`, and `SecondaryCircleButton` functions were updated to support `modifier`,   `colors`, and other customization parameters.
  - Switch (`SpatialSwitch` and `SwitchImpl`)
    - `SpatialSwitch` signature was updated to accept `colors` and `thumbContent`, and the `modifier` parameter was reordered.
    - `SwitchImpl` updated to accept and render optional `thumbContent`.
    - `SwitchColors` now exposes `iconColor(enabled, checked)` for thumb content tinting.
  - Dropdown
    - `SpatialDropdown` and `SpatialIconDropdown` signatures were extended with `menuModifier`, `showChevron`, and `showDividers` parameters.
    - `SpatialDropdownItem` data class now includes an optional `suffix` composable property.
    - Chevron visibility is now conditional for `DropdownPillVariant.Standard`.
    - `DropdownMenu` styling updated to support custom `menuModifier` and optional dividers.
    - Menu item layout was refined to support `suffix`.
  - Navigation (`SpatialSideNavItem` and defaults)
    - `SpatialSideNavItem` signature extended with `colors`, `primaryTextStyle`, `secondaryTextStyle`, and `selectedBackgroundColor`.
    - Text styling is now configurable via `primaryTextStyle` / `secondaryTextStyle`.
    - Selected background color can be overridden via `selectedBackgroundColor`.
    - Removed fixed icon size wrappers to allow flexible icon sizing.
  - Theme
    - `SpatialShapes` and `SpatialShapeSizes` were extended with an `xLarge` size (32.dp).
- Component XML Schema
  - A new schema definition is available at: [https://developers.meta.com/horizon/spatial-sdk/0.8.0/ComponentSchema.xsd/](https://developers.meta.com/horizon/spatial-sdk/0.8.0/ComponentSchema.xsd/)
- Removed APIs
  - `com.meta.spatial.toolkit.BodyJoint` and `com.meta.spatial.toolkit.TrackedBody` have been removed as they were not implemented

## Spatial SDK Gradle Plugin

The samples all include the Spatial SDK Gradle Plugin in their build files. This plugin is used for the [Spatial Editor integration](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-editor#use-the-spatial-sdk-gradle-plugin) and for build-related features like [custom shaders](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-custom-shaders).

Meta collects telemetry data from the Spatial SDK Gradle Plugin to help improve MPT Products. You can read the [Supplemental Meta Platforms Technologies Privacy Policy](https://www.meta.com/legal/privacy-policy/) to learn more.

## License

The Meta Spatial SDK Samples package is multi-licensed.

The majority of the project is licensed under the [MIT License](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/blob/main/LICENSE), as found in the LICENSE file.

The [Meta Platform Technologies SDK license](https://developer.oculus.com/licenses/oculussdk/) applies to the Meta Spatial SDK and supporting material, and to the assets used in the Meta Spatial SDK Samples package. The [MPT SDK license](https://github.com/meta-quest/Meta-Spatial-SDK-Samples/tree/main/MrukSample/app/src/main/assets/LICENSE.md) can be found in the asset folder of each sample.

Specifically, all the supporting materials in each sample's `app/src/main/res/raw` and `app/src/main/assets` folders including 3D models, videos, sounds, and others, are licensed under the [MPT SDK license](https://developer.oculus.com/licenses/oculussdk/).
