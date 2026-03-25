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
  - Android Studio Hedgehog or newer
  - [Meta Spatial Editor](https://developers.meta.com/horizon/downloads/spatial-sdk/)

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
[here](https://developers.meta.com/horizon/documentation/spatial-sdk/release-notes).

## 0.11.1 Updates (hotfix for 0.11.0)

### What's Fixed

- Fixed bug where the Component XML tasks were incorrectly marked as up-to-date on subsequent builds

## 0.11.0 Updates

### Added

#### Entity/Component DSL

- `entity.update<T> { ... }` modifies a component with auto-save.
- `entity.with<T> { ... }` provides scoped read-only access.
- `entity.withOrNull<T> { ... }` for safe read access returning null if missing.
- `entity.updateIfPresent<T> { ... }` safely updates only if the component exists.
- Multi-component variants for accessing 2-4 components together.
- Escape hatches: `entity.raw`, `withDataModel`, `entity.entityId`, `entity.isValid`, `entity.exists`, `entity.isNullEntity`.

#### Physics Constraints

A full constraint system for connecting physics bodies with joints. There are six constraints options

- **`BallSocketConstraint`** — Free rotation around a point (3 DOF). Simplest constraint — just two anchor points.
- **`ConeTwistConstraint`** — Rotation within a cone \+ twist around an axis, with configurable swing/twist limits, motor support, and softness/bias/relaxation tuning. Supports incremental updates — motor/limit changes don't recreate the native constraint.
- **`FixedConstraint`** — Locks two bodies rigidly. Typically paired with `breakForce` for destructible connections.
- **`HingeConstraint`** — Rotation around a single axis with angular limits (radians) and optional motor. Supports incremental updates.
- **`SliderConstraint`** — Linear motion along an axis with distance limits (meters) and optional motor. Supports incremental updates.
- **`SpringConstraint`** — Elastic connection with configurable stiffness (N/m), damping, and rest length. Always destroys and recreates the native constraint on any property change.

#### Physics Colliders & Player Avatar

- **`ScenePhysicsCollider`** — Standalone collision shapes that can be shared across multiple `ScenePhysicsObject` instances. Factory methods cover box, sphere, capsule (X/Y/Z axis), cylinder (X/Y/Z axis), convex hull from glTF, exact triangle mesh from glTF, and compound shapes. Compound colliders support adding child shapes (`addChildBox`, `addChildSphere`, `addChildCylinder`) with local-space poses.

- **`CollisionShapeType`** — Controls how collision geometry is generated from meshes:

  - `PRIMITIVE` (0) — Uses the `shape` attribute (box, sphere, etc.). Fastest.
  - `TRIANGLE_MESH` (1) — Exact triangle mesh from glTF. **Static bodies only.** Most accurate but slowest.
  - `CONVEX_HULL` (2) — Convex hull from mesh vertices. Works for dynamic bodies.
  - `CONVEX_DECOMPOSITION` (3) — V-HACD decomposition into multiple convex hulls for concave shapes. Controlled by `maxConvexHulls`.
  - `COMPOUND` (4) — Multiple primitives via `CompoundChildShape` components on child entities.


- **`PlayerAvatarPhysics` / `PlayerAvatarPhysicsSystem`** — Gives the player physical presence in the scene.
  - Optional hand sphere colliders following.
  - Configurable per-entity: `bodyRadius` , `handRadius`, `friction`, etc.

#### Animation

- **`BlendedAnimation`** — Delta-time-based animation component that crossfades between glTF animation tracks. Unlike the existing `Animated` component (which uses wall-clock time), `BlendedAnimation` accumulates time via frame delta-time and `playbackSpeed`, giving frame-rate-independent control.

#### Toolkit & UI

- **`ResolveInputSystem`** — A late-running ECS system that calls `Scene.resolveInput()` to ensure async input dispatches complete before the Choreographer's `doFrame` finishes. Prevents input events from being lost or delayed by one frame.

#### Gradle Plugin Library Support

- Fixed build failure when project depends on a local Android Library.
- Added `spatial.components` extension
  - `components.xmlDirectory` to configure component XML paths for libraries
  - `components.registrationsClassName` to configure name for `ComponentRegistrations` object. Used for auto-registration of components.
  - See feature\_dev\_sample for code examples.

#### Sample Added

- **`feature_dev_sample`** — Reference architecture sample demonstrating how to build reusable `SpatialFeature` library modules as separate Android Library projects (Gradle modules). Shows two patterns: a pure-Kotlin module (`PulsingFeature` — sine-wave scale animation) and a native C++/JNI module (`NativeBobbingFeature` — JNI-computed Y-position oscillation).

### Changed

#### Physics

- **`Physics` component** — 3 new properties: `collisionShapeType`, `collisionMeshNode`, and `maxConvexHulls` (see above).
- **`ScenePhysicsObject`** — New methods for runtime physics tuning:
  - `setBodyType()` — switch between STATIC/DYNAMIC/KINEMATIC at runtime
  - `setDamping()` — control how quickly velocity decays
  - `setDeactivationTime()` / `setSleepingThresholds()` — tune when resting bodies go to sleep
  - `createFromCollider()` — create a physics body from a reusable `ScenePhysicsCollider`
- **`PhysicsCreationSystem`** — `getPhysicsObject(entity)` gives direct access to the underlying `ScenePhysicsObject` for advanced manipulation. `getPhysicsCreators()` lets you register custom shape factories.

#### SDK

- **`Pose.isApproximatelyEqual()`** — Fuzzy pose comparison with separate distance (meters) and angle (degrees) thresholds.
- **`Query.any()` / `count()` / `none()`** — Check query results without iterating.
- **`SpatialLogger.execStart()` / `execFinish()`** — Structured execution markers for lifecycle tracing.

#### ISDK

- **`setPanelCollisionPriority()`** — Resolve which panel receives input when panels overlap. Set priority to match the panel's `zIndex`.

#### Panels

- **`resolveInput()`** — Ensures async input dispatches complete within the current frame, preventing dropped or delayed input events.

#### Misc

- General app size and performance improvements

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
