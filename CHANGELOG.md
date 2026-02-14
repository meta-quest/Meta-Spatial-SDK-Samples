# Changelog

Check out our official
[release notes](https://developers.meta.com/horizon/documentation/spatial-sdk/release-notes).

This format is roughly based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## 0.10.0 - 2026-02-12

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

## 0.9.2 - 2025-12-18

### Added

- Experimental support for body tracking
  - Allows developers to access skeleton joint data including poses.

## 0.9.1 - 2025-12-08

### Fixed

- Panel corruption bug where some panels would appear distorted, invisible, or
  copy content from another panel has been fixed.

## 0.9.0 - 2025-11-20

### Added

- GSplat support
  - Accessible through the experimental Splat Feature.
  - Currently limited to one Splat component at a time.
  - Supported file types are “.spz” and “.ply”.
  - Splats can be loaded from the APK, file system or downloaded from a URL.
  - Splats support collisions and locomotion
- Added High-Fidelity Scene. This allows developers to access a representation
  of the room that extends the existing single floor, ceiling and walls data
  structure. It provides a more detailed version of the room layout that allows
  features such as multiple floors, columns, and slanted ceilings to be queried
  by the developer.

### Changed

- Stereo Audio API
  - AudioSessionStereoOffsets now behaves as expected and rotates as expected
    with the entity.
  - Can set World Transform position of stereo objects instead of relative
    position
- Recentering
  - The onRecenter overridable method in VRActivity now contains an
    isUserInitiated flag. This allows the app to determine if the recenter is
    being driven by a user action (holding the Meta button) or a system recenter
    - To update, replace `override fun onRecenter()` with
      `override fun onRecenter(isUserInitiated: Boolean)`
- Updated UiSet
  - Changed icon files
  - Added new font files
  - Adjusted the argument list order for a few composable components
- QR Code tracking support moved from experimental to public

### Fixed

- Graphics
  - Fixed crash with cubic spline interpolation on rotation in glTF animations
  - Normal map tangents now use the correct axes
- Assume headset is mounted on startup, prevents errant onHMDMounted() call from
  happening during application startup
- Spatial SDK applications now explicitly kill the application process on
  shutdown, if this behavior is not desired you can explicitly turn it off by
  modifying the `killProcessOnDestroy` variable exposed in VrActivity. Not
  killing the process leads to issues upon app restart and is not recommended at
  this time. Example:

```
// In your ImmersiveActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    …
    killProcessOnDestroy = false
}
```

## 0.8.1 - 2025-10-24

### Fixed

- Fix `VrActivity.onRecenter()` not getting called
- Fix `spatial.setPerformanceLevel()` not being respected
- Fix `scene.isSystemPassthroughEnabled()` always returning false
- Fix panel flickering that could sometimes occur in passthrough
- Fix `PanelRenderOptions` creating a layer panel even when the panel render
  mode is set to PanelRenderMode.Mesh

## 0.8.0 - 2025-09-17

### Added

- New Panel Registration APIs
  - `ComposeViewPanelRegistration`: Panel registration for Jetpack Compose-based
    panels.
  - `ViewPanelRegistration`: Panel registration for dynamically created
    View-based panels.
  - `LayoutXMLPanelRegistration`: Panel registration for XML layout view-based
    panels.
  - `IntentPanelRegistration`: Panel registration for Intent-based panels that
    launch Activities.
  - `ActivityPanelRegistration`: Panel registration for Activity-based panels
    that launch specific Activity classes.
  - `VideoSurfacePanelRegistration`: Panel registration for direct-to-surface
    media rendering.
  - `ReadableVideoSurfacePanelRegistration`: Panel registration for when you
    want both post-processing and a simple surface to render on.
  - The new APIs are focused on simplifying panel configuration, for more
    information see our
    [documentation page on panel registration](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-2dpanel-registration)
- Shader hot reload
  - You can now hot reload your custom shader code. More information is
    available in
    [our documentation](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-hot-reload/#shader-hot-reload).
- `preloadMesh()` support
  - This method loads and caches a mesh from the specified URI so that when it's
    later assigned to an entity via the Mesh component, it will be available
    immediately. This is useful for performance optimization, especially during
    app startup when you can front-load your meshes.
  - Example code:

    ```kotlin
    systemManager.findSystem<MeshCreationSystem>()
    meshCreationSystem.preloadMesh(Uri.parse(meshFile))
    ```

- Panel graphics changes
  - New `layerBlendType`, replaces the `enableTransparent` control for panels.
    Now supports the ability to set the layer as:
    - `OPAQUE`: Alpha/transparency is ignored
    - `MASKED`: This is what `enableTransparent` previously enabled, panels can
      become transparent but will not blend with the background (the alpha is
      essentially force to be only 0 or 1)
    - `ALPHA_BLEND`: Alpha is blended accurately, resulting in higher quality
      edges. However, it is slower and can result in blending artifacts when
      overlapping with semitransparent meshes.
  - New `enableLayerFeatheredEdge` option in `PanelRenderSettings`, this will
    allow you to remove sharp black outlines on the edge of panels
- UISet
  - Bordered buttons
    - New composables: `BorderedButton`, `BorderedIconButton`
      (`com.meta.spatial.uiset.button`).
    - New defaults and colors: `BorderedButtonDefaults`, `BorderedButtonColors`
      (`com.meta.spatial.uiset.button.foundation`).
    - New button variants: `ButtonVariant.Regular.Bordered`,
      `ButtonVariant.Circled.Bordered`.
  - Card components
    - New composables: `PrimaryCard`, `SecondaryCard`, `OutlinedCard`
      (`com.meta.spatial.uiset.card.SpatialCard`).
    - Foundation: `CardImpl`, `CardDefaults`, `CardVariant`.
- Experimental: `Entity.markComponentChanged()` API
  - Marks a component as "changed" without setting new component data
  - This is useful in rare cases where you want the component to be considered
    "changed" for change detection purposes, even when the actual component data
    hasn't been modified. This will trigger component change listeners and
    queries that look for changed components.

### Changed

- ISDK
  - [ISDK](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-isdk-overview)
    is now the default input system.
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
    - Some updates to your app may be required - interactions no longer support
      2 sided panels. ensure panels and buttons are oriented with their normal
      vector pointing forwards.

  - `com.meta.spatial.vr.InputSystem` is no longer enabled by default.
    (`com.meta.spatial.isdk.IsdkSystem` now controls input)
  - `com.meta.spatial.toolkit.GrabbableSystem` is no longer enabled by default
    (`com.meta.spatial.isdk.IsdkSystem` now controls grabbing & transforms)
  - `IsdkGrabbableFollowableSystem` fixes behavior when Entity has both
    `Grabbable` and `Followable` components.
  - `IsdkDefaultCursorSystem`: made some non-user-servicable fields private
  - Support for microgestures, check out `MicrogestureBits`

- Cast input forwarding
  - The debug lines (green lines that would appear during input forwarding) have
    now been removed.
  - Cursor placement for input forwarding is most accurate when the eye is set
    to "Right Eye" in MQDH Cast
- Spatial gradle plugin
  - The `export` step of the Gradle build process is now always executed and
    cannot be opted-out. If you don’t specify the Spatial Editor asset folder,
    the export step will not generate a GLXF file.
- UISet
  - Buttons foundation (`ButtonImpl`)
    - Signature extended with `modifier`, `colors`, `borderColor`,
      `labelTextStyle`, `contentAlignment`.
    - Color resolution updated to use provided `*ButtonColors` per variant;
      falls back to defaults when `colors` is null.
    - Border rendering added for bordered variants (1.dp, configurable via
      `borderColor`).
    - Content alignment is now configurable (affects container alignment and row
      arrangement).
  - Primary and Secondary buttons
    - `PrimaryButton` and `SecondaryButton` signatures extended with `modifier`,
      `colors`, `labelTextStyle`, `contentAlignment`.
  - Existing `PrimaryIconButton`, `PrimaryCircleButton`, `SecondaryIconButton`,
    and `SecondaryCircleButton` functions were updated to support `modifier`,
    `colors`, and other customization parameters.
  - Switch (`SpatialSwitch` and `SwitchImpl`)
    - `SpatialSwitch` signature was updated to accept `colors` and
      `thumbContent`, and the `modifier` parameter was reordered.
    - `SwitchImpl` updated to accept and render optional `thumbContent`.
    - `SwitchColors` now exposes `iconColor(enabled, checked)` for thumb content
      tinting.
  - Dropdown
    - `SpatialDropdown` and `SpatialIconDropdown` signatures were extended with
      `menuModifier`, `showChevron`, and `showDividers` parameters.
    - `SpatialDropdownItem` data class now includes an optional `suffix`
      composable property.
    - Chevron visibility is now conditional for `DropdownPillVariant.Standard`.
    - `DropdownMenu` styling updated to support custom `menuModifier` and
      optional dividers.
    - Menu item layout was refined to support `suffix`.
  - Navigation (`SpatialSideNavItem` and defaults)
    - `SpatialSideNavItem` signature extended with `colors`, `primaryTextStyle`,
      `secondaryTextStyle`, and `selectedBackgroundColor`.
    - Text styling is now configurable via `primaryTextStyle` /
      `secondaryTextStyle`.
    - Selected background color can be overridden via `selectedBackgroundColor`.
    - Removed fixed icon size wrappers to allow flexible icon sizing.
  - Theme
    - `SpatialShapes` and `SpatialShapeSizes` were extended with an `xLarge`
      size (32.dp).
- Component XML Schema
  - A new schema definition is available at:
    [https://developers.meta.com/horizon/spatial-sdk/0.8.0/ComponentSchema.xsd/](https://developers.meta.com/horizon/spatial-sdk/0.8.0/ComponentSchema.xsd/)
- Removed APIs
  - `com.meta.spatial.toolkit.BodyJoint` and
    `com.meta.spatial.toolkit.TrackedBody` have been removed as they were not
    implemented

## 0.7.2 - 2025-08-26

### Fixed

- Fixed crash when clicking on panels without `CastInputForward` feature
  included in build
- Fixed extra dependencies getting pulled into `CastInputForward` feature

## 0.7.1 - 2025-08-14

### Added

- MRUK: Added Environment Raycasting
  - Allows users to raycast against the environment without going through space
    setup
  - Available for exploration in the `MrukSample` project
- Added UISet Feature
  - A set of Jetpack Compose components that allow you to build with Meta
    Quest's design system

### Changed

- ISDK pointer only shows up when pointing at an object in range

### Fixed

- Fixed ISDK interaction with scaled panels
- Fixed race condition with `NetworkedAssetLoader`
- Fixed a bug where Gradle daemon would crash after a failed project sync.
- Fixed animation pointers not working on metallic-factor

## 0.7.0 - 2025-06-25

This release is a major version bump, which means some breaking changes were
introduced.

### Added

- Spatial SDK activity lifecycle
  - Add an activity lifecycle callback `onSpatialShutdown`. This lifecycle
    callback is guaranteed to be called during a Spatial activity’s shutdown
    process. Spatial resources such as entities and scene objects should be
    cleaned up here.
- Experimental Feature: `childrenOf` query
  - Add a `childrenOf` query to query the child entities for an entity.
- Experimental Feature: `changedSince` query
  - Add a `changedSince` query to get the changed entities since a certain
    datamodel version.
- CPU/GPU performance level now defaults to `SUSTAINED_HIGH`, added new
  performance level controls
- Support for compositor layer sharpening and super sampling
  (https://developers.meta.com/horizon/documentation/native/android/mobile-openxr-composition-layer-filtering)
  via `LayerFilters`
- Support for
  [Live Edits](https://developer.android.com/develop/ui/compose/tooling/iterative-development)
  on Jetpack Compose UIs.

### Changed

- The Spatial SDK AARs are now built with Kotlin 2.1.0. This is a breaking
  change, so you will need to update the Kotlin version in your application.
- Components are cached by default now, which reduces access to native C++ data
  and improves performance.
- The component GLXF’s attribute `uri` is now a UriAttribute. The component
  Mesh’s `mesh` attribute is now also a UriAttribute.
- Ray intersections with non-BVH meshes now ignore triangle backfaces
  (consistent with BVH intersections).
  - This may break some apps - especially those with panels/buttons. Make sure
    they are oriented correctly.
- Custom shaders should now import base Spatial SDK shader files prefixed with
  “data/shaders”, which matches the path to these files from the APK assets
  directory.
  - `#include <metaSpatialSdkFragmentBase.glsl>` should become
    `#include <data/shaders/metaSpatialSdkFragmentBase.glsl>`
- Experimental feature: panel animations
  - The PanelAnimation component is removed. Use PanelQuadCylinderAnimation,
    PanelScaleInAnimation or PanelScaleOutAnimation instead.
- Experimental feature: ISDK
  - Rename IsdkToolkitBridgeSystem to IsdkComponentCreationSystem
  - Near-field Grabbable entities can be picked up with whole-palm grab
  - Interactor type hints added to PointerEvent.
  - Panel offsets added to IsdkPanelDimensions.
  - Updated sample applications to use IsdkFeature.

### Fixed

- Fixed issue with glTF Animation Pointer node transformations getting applied
  to the wrong nodes
- Experimental feature: ISDK
  - Input now works via Cast Input Forwarding
  - Entities can now be selected in the Data Model Inspector via in-game
    controllers or Cast Input Forwarding
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
- Fixed an issue where setting the passthrough LUT would not work after sleeping
  and waking up the device

## 0.6.0 - 2025-04-22

This release is a major version bump because it has a number of large
improvements, new features, and a small number of breaking changes.

### Added

- Experimental Feature:
  [Interaction SDK](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-isdk-overview/)
  - Using `IsdkFeature` automatically replaces built in toolkit
    components/systems like Grabbable with `Isdk` equivalents
  - Provides interactions that are consistent with the Meta Horizon OS and
    brings parity between controller and hand interactions
    - Interact with panels directly using hands or controllers
    - Grab 3D objects with hands or controllers, directly or using a raycast
    - Advanced grab customization (responsiveness, two-handed, constraints)
  - The `Object3DSampleIsdk` sample app in the
    [samples repo](https://github.com/meta-quest/Meta-Spatial-SDK-Samples)
    demonstrates how to use the new IsdkFeature and other Isdk APIs
- Datamodel Inspector
  - Using `DataModelInspectorFeature` launches a webserver at a specified port
    that provides a live table view of ECS.
  - Connect to a running app via Data Model Inspector Tool Window in the new
    Meta Horizon Android Studio Plugin.
- [Query filters and sorting](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-queries/)
  - [Add filtering API for queries](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-filters/)
    so that developers can refine the entity query results by applying filters
    on attributes.
  - [Add sorting API for queries](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-sorting/)
    so that developers can sort the entity by criteria on attributes.
- GLTF Animation Pointer Support
  - Added the ability to modify material factors and UV transforms via
    `KHR_animation_pointer` support.
  - This can allow you to do things like animate opacity or make moving textures
    and play them with the `Animated()` component.
- [DRM support](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-2dpanel-drm)
  for Activity based panels on v76
  - Using an Activity based panel (not inflated view) along with a `LayerConfig`
    set to `secure=true` will allow you to display DRM content on v76+.
    Previously, you had to render directly to a secure swapchain.

### Changed

- We now support Color4 as an Attribute Type for use directly in components.
  Because of this, Color4 has been moved packages from
  `com.meta.spatial.toolkit` -> `com.meta.spatial.core`
- Uris are now supported as an Attribute Type for use in components
- Component XML is now the preferred method for making Spatial SDK Components
  - Using Components XML increases performance of components and queries.
  - Components XML can be used in Spatial Editor.
  - Components written in Kotlin (instead of XML) will no longer be able to be
    added to objects in Spatial Editor
- `PanelAnimation` and `PanelConfigOptions2` are now marked as experimental
  - These APIs may be unstable and/or subject to change in the future. If you
    want to use them now, you will need to use the `@SpatialSDKExperimentalAPI`
    annotation
- Default cursor has been changed to more closely match the Quest Home
  environment cursor
- Samples now use `libs.versions.toml` for version management (removing the need
  to set the version in the `gradle.properties` file)
- Changed the behavior of the `Layer.setClip()` API
  - If the area of the clip for the left or right eye is 0, there will be no
    layer submitted for that eye.
  - This can allow you to have separate transforms for layers sharing a
    swapchain with the left and right eyes
- Bundled shaders assets have been cleaned up and compressed, decreasing APK
  size.
- Various performance and stability improvements.

### Fixed

- Fixed bug where deleting an entity while grabbed causes a crash.

## 0.5.5 - 2025-02-18

### Added

- Component XML
  - Components can now be defined in XML instead of Kotlin, this is now the
    preferred way to write Components
  - This makes it easier to define Components, and greatly improves the Spatial
    Editor integration with Components
  - The build process will generate Kotlin code and resource files for the XML
    components
- Panel Animation
  - This new feature includes animation timing and callback APIs, enabling you
    to manipulate panel transitions seamlessly
  - A panel zoom in and out animation is now available when creating and
    destroying panels
  - These capabilities enhance the interactivity and aesthetic appeal of your
    panels, providing you with greater control and flexibility
- Panel Native Transition between Quad and Cylinder Shapes
  - We have implemented animations for transitions between Quad and Cylinder
    panels
  - Check out the AnimationSample for an example
- Refined Cylinder Panels
  - We have conducted a comprehensive refinement of our Cylinder Panels to
    deliver enhanced performance and versatility. Key improvements include:
    - Grabbable Bug Fix: We have resolved a bug with Grabbable and cylinder
      panels, grabbing cylinder panels will now be more reliable
    - Backside Transparency: We have added transparency to the backside of the
      cylinder panels, staying consistent with quad panels

### Changed

- com.meta.spatial.toolkit.Controller.kt
  - In the Controller class, property “type” is now an EnumAttribute that
    accepts Enum values of `com.meta.spatial.toolkit.ControllerType`. The
    ControllerType enum is defined in the Java package ControllerType and has
    three values: `ControllerType.CONTROLLER`, `ControllerType.HAND`, and
    `ControllerType.EYES`.
  - The constants `com.meta.spatial.toolkit.Controller.CONTROLLER_TYPE`,
    `com.meta.spatial.toolkit.Controller.HAND_TYPE`, and
    `com.meta.spatial.toolkit.Controller.EYES_TYPE` are removed.
- TimeAttribute has changed from Int->Long

### Deprecated

- Writing Components using Kotlin is now deprecated, please shift to using
  Component XML to define your Components

### Fixed

- Fixed bug with rotation with Locomotion + behavior with updateViewOrigin
- Optimized panel update performance for large panels

## 0.5.4 - 2025-01-23

### Added

- Added `onHeadsetMounted` and `onHeadsetUnmounted` APIs for detecting when a
  user puts on or takes off their headset.

### Changed

- None

### Deprecated

- None

### Fixed

- Fixed Windows specific hot reload bug with "Read-only filesystem" error in
  Gradle task.
- Fixed crash where a `SceneLayer` was being destroyed twice due to garbage
  collection.

## 0.5.3 - 2024-12-16

### Added

- The `meta-spatial-sdk-compose` package is now available, enabling View-based
  panels to render Jetpack Compose UI
- Javadocs are now available for Maven Central released packages (starting with
  0.5.3)
- MRUK
  - Added Scene Raycasting functionality (and a raycast demo in the MrukSample
    project)
  - Optimized scene loading

### Changed

- None

### Deprecated

- None

### Fixed

- Hot reload is now more reliable (previously has issues with parallelization)

## 0.5.2 - 2024-11-14

### Added

- Added `Followable` Component and `FollowableSystem` which allows devs to
  easily tether objects together. See Animations Sample for an example use.
- **Hot Reload**: Adds the ability to reload your `glb`/`gltf`/`glxf` and Meta
  Spatial Editor scenes while running your app via the Gradle plugin.
  - Auto Export from Meta Spatial Editor: Saving in Spatial Editor automatically
    exports to the app and pushes to the headset for hot reload
  - Two Reload Types:
    - Delete all entities and recreate them: more stable but does not work for
      all apps
    - Keep entities and reload meshes only: works for all apps, but less stable
      and does not reload components

### Changed

- `SamplerConfig`s now also apply to layers instead of just non-layer panels
- Cylinder panels now have a transparent back applied to them (instead of just
  being invisible)
- **Gradle Plugin**: References to string paths in plugin configuration are
  replaced with file references.
  - **NOTE:** This requires changes to your `build.gradle.kts`. Example new
    usage can be found in the sample `build.gradle.kts` files.
- **Gradle Plugin**: Telemetry now reports out simple usage statistics.

### Deprecated

- Deprecated `QuadLayerConfig`/`CylinderLayerConfig`/`EquirectLayerConfig`. Use
  `LayerConfig` instead for panel's layer configuration.

### Fixed

- Fixed a crash when garbage collecting a panel.
- Fixed a crash when updating the `Panel` component on an entity that already
  had a `Panel` component.
- Crash fixed in Focus showcase

## 0.5.1 - 2024-10-23

### Added

- Added support for Secure layers Layers can now be marked as secure making them
  appear black while recording. This is possible at a global level or an
  individual layer level.

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

- Removed the old Anchor Systems in toolkit, please use MRUK (Mixed Reality
  Utility Kit) in Meta Spatial SDK

### Fixed

- Init order issue for FeatureManager An overridden registerFeatures() was not
  able to reference the top level class variables. This was because the
  initialization was happening very early in the creation of a Feature. This was
  resolved by moving initialization later in activity startup.
- Memory leak on panel destruction Destroy the activity panel by calling
  lifecycle events onPause, onStop, onDestroy sequentially. Release the panel
  scene texture and mesh as panel destruction, which reclaims the resource
  early.
- Fixed the issue that the panel faced the user backwards when grabbed from
  behind. The panel will now always face the user correctly, regardless of the
  angle of grabbing.
- Fix the grabbable bug for the cylinder panel when the user is close to the
  panel. When the user is very close to the panel, i.e., the center of the
  cylinder is behind the user, the grabbable system does not work well for
  rotation.
- Fix compatibility issues with Android Studio Ladybug.
