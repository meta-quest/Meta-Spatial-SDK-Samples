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

## 0.10.1 Updates (hotfix for 0.10.0)

### Fixed

- Fix crash related to missing soloader symbol.

## 0.10.0 Updates

### Added

- `Entity.removeComponent<T>()` and
  `Entity.tryRemoveComponent<T>()` for removing components from
  entities at runtime.

  ```kotlin
  // Remove a component (throws if not present)
  entity.removeComponent<Grabbable>()

  // Safe removal (returns false if not present)
  val wasRemoved = entity.tryRemoveComponent<Grabbable>()
  ```
- Visual grab handle rendering for panels with animated hover,
  grab, and resize state transitions. Audio feedback (grab and
  drop sounds) for panel grab interactions.
  `IsdkPanelPaddingRenderSystem` exposes `grabbableEdgeMesh`,
  `grabbableCornerMesh`, `resizeCornerMesh`, `grabAudio`, and
  `dropAudio` fields for customization.
- `Entity.toString()` now prints the entity ID and all attached
  components for easier debugging. Optional
  `EntityDebugInfo.includeComponentsInToString` flag enables full
  component value dumps. `EntityDebugInfo.captureCreationCallstack`
  records creation callstacks and timestamps for debugging entity
  lifecycle issues.
- `SpatialLogger` - centralized logging utility with
  runtime-configurable log levels.
  - Supports global and per-category log level configuration via ADB
    properties: `adb shell setprop debug.spatial.log.level <LEVEL>`.
- `Entity.willBeDeleted()` method that returns `true` if
  `destroy()` has been called on the entity in the current frame,
  allowing systems to check whether an entity is scheduled for
  deletion before it is fully removed.
- `SceneTexture.clear(r, g, b, a)` method to clear a texture with
  a solid color specified by RGBA float values.
- Feature-level hit testing support, enabling features such as
  GSplat to participate in ray intersection and ISDK interactions
  alongside standard scene objects.
- Batch node transform update APIs for `SceneObject`
  - `setLocalNodePoses()` for sparse or sequential node updates.
  - `setLocalNodePosesRange()` for contiguous range updates.
  - `setLocalNodeTransformsBatch()` and
    `setLocalNodeTransformsRange()` for direct float array APIs
    with transform component flags.
  - Static companion method for multi-object batch updates.
- World locking for MRUK
  - Keeps virtual content stationary relative to the real world
    without manually parenting every object to anchors.
  - Enabled by default. Toggle with
    `MRUKFeature.setWorldLockingEnabled(Boolean)` and query with
    `MRUKFeature.isWorldLockingEnabled()`.
  - After recentering, scene anchors stay aligned with the real
    world.
- `Entity.requireComponent<T>(errorMessage)` method that throws a
  `RuntimeException` with a custom mßessage if the component is not
  found on the entity.
- `HEAD_LOCKED` and `HEAD_RELATIVE` stereo audio offset modes
  added to the `AudioSessionStereoOffsets` component.
  - `HEAD_LOCKED` anchors audio to the user's head position and
    rotation, ignoring the entity's position.
  - `HEAD_RELATIVE` anchors audio to the user's head position but
    rotates stereo space based on the emitter's horizontal
    orientation.
- Support for playing animations by name in the `Animated`
  component. Set the `animationName` property to the name of the
  animation track in the glTF file instead of using the `track`
  index. When `animationName` is set, it takes precedence over
  `track`.
- Support for 44.1kHz and other non-standard audio sample rates.
  Audio files are no longer restricted to multiples of 16kHz.
  Files with other sample rates are automatically resampled to
  48kHz.
- Experimental: `SceneMaterial.setRenderOrder()` API for
  fine-grained control over material render ordering. Valid range
  is -3 to +3; higher values render later.
- `DataModel.getComponentIdsForEntity()` to query which components
  are attached to an entity.
- `SceneTexture.fromResource()` for loading textures from any
  Android drawable resource type (bitmaps, vectors, shapes) with
  optional scaling. `baseTextureScale` attribute on `Material`
  for scaling textures in component XML.

  ```kotlin
  val texture = SceneTexture.fromResource(context, R.drawable.my_bitmap)
  val scaled = SceneTexture.fromResource(context, R.drawable.my_bitmap, scale = 0.5f)
  ```

- URI-based mesh creators with query parameter support via
  `registerMeshCreator(baseUrl, (entity, uri) -> SceneMesh)`.
  Enables parameterized procedural mesh generation from a single
  registered creator.

  ```kotlin
  registerMeshCreator("mesh://custom/box") { entity, uri ->
      val size = uri.getQueryParameter("size")?.toFloatOrNull() ?: 0.1f
      SceneMesh.box(-size, -size, -size, size, size, size, material)
  }
  // Use: Mesh(Uri.parse("mesh://custom/box?size=0.25"))
  ```
- `Quaternion` factory methods for common rotation patterns
  - `fromAxisAngle(axis, angleDegrees)` and
    `fromAxisAngleRadians(axis, angleRadians)`.
  - `fromEuler(pitch, yaw, roll)` (also accepts `Vector3`).
  - `fromTwoVectors(from, to)` for vector-to-vector rotation.
  - `fromDirection(direction, up)` for look-at style orientation.
- `OVRMetricsSystem` now supports runtime addition and removal of
  metrics and overlay messages.
  - `registerMetric()`, `registerMetrics()`, `unregisterMetric()`
    for dynamic metric management.
  - `registerOverlayMessage()` and `unregisterOverlayMessage()`
    for dynamic overlay messages.
  - `OVRMetricsTicks` provides `ticksPerSecond()` and
    `maxTickTimeMs()` for monitoring ECS tick performance.
- Distance and angle utility methods on `Pose`, `Quaternion`, and
  `Vector3`
  - `Vector3.isWithinDistance(other, distance)` for proximity
    checks using squared distance internally.
  - `Quaternion.isWithinAngle(other, angleRadians)` and
    `isWithinAngleDegrees()` for angular similarity checks.
  - Corresponding `Pose.isWithinDistance()` and
    `Pose.isWithinAngle()` convenience methods.
- Audio focus awareness
  - Override `VrActivity.audioPauseMode()` to choose
    `ACTIVITY_FOCUS` (default), `OPENXR_FOCUS`, or `MANUAL`.
  - `Scene.setAudioEnabled()` for manual audio control.
- `Scene.removeObject()` method to remove a `SceneObject` from a
  scene without destroying it, allowing natural garbage collection.
- Experimental Panel Resize API
  - `PanelSceneObject.resize(newWidthInPx, newHeightInPx)` allows
    dynamically resizing panels at runtime (programmatic).
  - `IsdkPanelResize` component enables interactive user-driven
    panel resizing through corner handles. Adding this component
    auto-creates `IsdkPanelGrabHandle` on the entity.

    ```kotlin
    entity.setComponent(IsdkPanelResize(
        enabled = true,
        resizeMode = ResizeMode.Relayout,
        preserveAspectRatio = false,
        minDimensions = Vector2(0.3f, 0.3f),
        maxDimensions = Vector2(1.5f, 1.5f),
    ))
    ```

  - `ResizeMode` controls resize behavior:
    - `Simple` -- modifies entity `Scale` component (default).
    - `Relayout` -- adjusts panel pixel dimensions and re-renders
      UI, preserving layout quality.
    - `None` -- handles do nothing; write custom resize logic via
      `InputListener` on the `PanelSceneObject`.
  - `HandleSegmentType` enum for identifying grab edges, grab
    corners, and resize corners on panels.
  - Haptic and audio feedback on resize start and end.
- Experimental: Sticky grab interaction support via ISDK
  - Allows users to maintain grip on objects after the grab
    gesture ends, until an explicit release gesture.
  - For hand tracking, release is triggered when all fingers are
    fully extended.
  - For controllers, the grip button toggles grab on and off.
- Experimental: `CachedQuery` for incremental entity tracking
  - Maintains a stable entity set and updates incrementally,
    avoiding full re-queries each frame.
  - Supports `onAdd`, `onUpdate`, and `onDelete` callbacks for
    entity lifecycle events with filtering.
  - Native-level `.filter` DSL for attribute-based filtering.
  - `where { has(...) }` DSL syntax for consistency with `Query`.
  - `hasChangedSince` query opcode for efficient incremental
    change detection.
- Experimental: Full body tracking support
  - Added 14 new lower-body joints to `JointType` (legs, ankles,
    and feet) for full-body skeleton tracking.
  - `JointSet` enum to select between `DEFAULT` (upper body and
    hands) and `FULL_BODY` joint topologies.
  - `Scene.setBodyTrackingJointSet()` to configure which joint set
    the body tracking system provides.
  - `BodyTrackingFidelity` enum with `LOW` and `HIGH` levels, and
    `Scene.setBodyTrackingFidelity()` for quality/performance
    control.
- `AIDebugToolsFeature` for headless, AI-driven app testing and
  debugging via ADB broadcast intents.
  - Debug commands: `get_entities`, `get_viewer_pose`,
    `set_viewer_pose`, `get_crash_history`, `get_stack_trace`.
  - Panel UI inspection: panel discovery, UI element inspection,
    and programmatic view clicking.
  - Entity interaction: lookup by name, detail inspection,
    transform modification, and teleportation.
  - `CrashMonitor`, `LogcatMonitor`, and `TombstoneMonitor` for
    runtime crash detection and diagnostics.

### Changed

- Hand outlines are now brighter.
- Physics APIs have been decoupled from the core library into the
  `com.meta.spatial.physics` feature module. This is a breaking
  change.

  ```kotlin
  // Before
  import com.meta.spatial.runtime.ScenePhysicsObject
  spatial.enablePhysicsDebugLines(true)
  spatial.setGravity(0f, -10f, 0f)

  // After
  import com.meta.spatial.physics.ScenePhysicsObject
  val physicsFeature = PhysicsFeature(spatial)
  // register in registerFeatures()
  physicsFeature.enablePhysicsDebugLines(true)
  physicsFeature.setGravity(Vector3(0f, -10f, 0f))
  ```

- Removed the synthetic GLTF root node from the mesh hierarchy.
  Node indices now correspond directly to their GLTF node indices.
- Entity and component debug logging improvements
  - `getComponent` error messages now include the attribute name
    and entity ID.
  - Mesh creation provides explicit error messages when a required
    component is missing, including the entity ID, component name,
    and a listing of all attached components.
  - Entity debug error messages include human-readable attribute
    names and full entity debug info (creation callstack, time
    alive, component list) when `EntityDebugInfo` flags are
    enabled.
- `CastInputForwardFeature` now accepts an optional `initialPose`
  parameter to customize the starting position of the input
  forwarding virtual camera.
- `IsdkPanelGrabHandle` has been redesigned with a multi-segment
  handle system (edges, corners, resize corners) replacing the
  single box collider. This is a breaking change. The `offset` and
  `padding` properties have been removed and replaced with
  `grabHandleCollisionWidths`, `resizeCornerCollisionSizes`,
  `resizeCornerCollisionInset`, `outset`, `zOffset`, `color`, and
  `scaleFactor`.
- Audio engine now supports up to 256 virtual sounds with
  automatic focus management, selecting the best 16 for
  simultaneous playback based on distance, volume, and priority.
  Sounds transition smoothly to prevent audio artifacts.
- Component registration is now auto-generated during the build.
  Use `ComponentRegistrations.all()` in `componentsToRegister()`
  instead of maintaining manual lists.
- `SceneObject.materials` is now lazily populated on first access,
  providing up to 16x faster entity creation. The property type
  changed from `Array<SceneMaterial>?` to `List<SceneMaterial>?`.
  ```kotlin
  // Before
  sceneObject.materials?.set(0, differentMaterial)

  // After (modify properties in place)
  sceneObject.materials?.get(0)?.setColor(Color4(1f, 0f, 0f, 1f))
  ```

- `Material.sceneTextureCache` is now private. Use
  `Material.registerSceneTexture(key, texture)` to register
  dynamically generated textures for use with the `Material`
  component via `baseTextureAndroidResourceId`. This is a
  breaking change.

  ```kotlin
  // Before
  Material.sceneTextureCache[resourceId] = sceneTexture

  // After
  Material.registerSceneTexture(resourceId, sceneTexture)
  ```

### Deprecated

- `PanelSceneObject.getDisplay()`, `getTexture()`, `getLayer()`,
  `getPanelShapeConfig()`, `getSwapchain()`, and `getSurface()`
  are deprecated. Use property accessors instead.

### Removed

- Removed the experimental `PanelConfigOptions2` API. This is a
  breaking change. All `PanelConfigOptions2` classes, constructors,
  extension functions, and builder methods have been deleted. Use
  `PanelConfigOptions` and `PanelShapeConfig` instead.
  - Replace `PanelSceneObject` constructors that accepted
    `PanelConfigOptions2` with constructors using
    `PanelConfigOptions` or `PanelShapeConfig`.
  - Replace `getPanelConfigOptions2()` with
    `getPanelShapeConfig()`.
  - Replace `reshape(PanelConfigOptions2)` with
    `reshape(PanelShapeConfig)`.
  - Replace `PanelRegistration.fromConfigOptions2 { ... }` with
    the standard `PanelRegistration` constructors.
- Removed `SpatialInterface` physics methods:
  `enablePhysicsDebugLines()`, `setGravity()`,
  `createPhysicsObject()`, `deletePhysics()`, `tickPhysics()`,
  `tickUpdatePhysicsState()`. Use `PhysicsFeature` and
  `PhysicsBridge` instead.
- Removed the deprecated `generateComponents` Gradle task. This
  task has been deprecated since version 0.6.0 and is no longer
  needed. Remove any references to `generateComponents` in your
  `build.gradle`.

### Fixed

- Fixed Compose panels receiving duplicate hover and input events
  when two controllers pointed at the same panel. Now only one
  controller can hover a panel at a time.
- Fixed incorrect 2D `HitInfo` UV coordinates on curved panels.
  UV coordinates from `PointerEvent` were calculated as if the
  panel was flat.
- Fixed a bug where interacting with a panel during a shape
  change (e.g., panel animation) could permanently block
  locomotion and hide the raycast indicator.
- Fixed entity deletion timing so that destroyed entities remain
  accessible during the same frame in which `destroy()` is called.
  Previously, entities were immediately removed, which could cause
  inconsistent behavior when systems accessed entities during the
  same tick.
- Fixed `HitInfo.distance` to return the actual distance from the
  interactor to the hit point instead of a hardcoded value of
  `1.0`.
- Fixed bug in `IsdkSystem` where lambda observers registered with
  `registerObserver()` or `registerInteractableObserver()` would be
  garbage collected after a few seconds, causing them to stop
  receiving events.
- Fixed misleading native assert message when calling
  `getComponent` on an entity that does not have the requested
  component. The error now correctly identifies the missing
  attribute.
- Fixed `SamplerConfig` not being applied to dynamically created
  `SceneTexture` objects. Sampler settings now work correctly for
  textures created in code, not just those loaded from glTF files.
- Fixed `changedSince` queries not correctly detecting newly
  created entities due to incorrect version update ordering in the
  ECS data model.
- Fixed texture bug during texture initialization where textures
  would briefly appear black.


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
