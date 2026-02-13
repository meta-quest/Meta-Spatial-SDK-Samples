/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.splatsample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.ComposeViewPanelRegistration
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SpatialSDKExperimentalAPI
import com.meta.spatial.core.SpatialSDKInternalTestingAPI
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.splat.SpatialSDKExperimentalSplatAPI
import com.meta.spatial.splat.Splat
import com.meta.spatial.splat.SplatFeature
import com.meta.spatial.splat.SplatLoadEventArgs
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SupportsLocomotion
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.VRFeature
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(SpatialSDKExperimentalSplatAPI::class)
class SplatSampleActivity : AppSystemActivity() {

  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)

  private lateinit var environmentEntity: Entity
  private lateinit var skyboxEntity: Entity
  private lateinit var panelEntity: Entity
  private lateinit var floorEntity: Entity
  // Entity that holds the Splat component for rendering Gaussian Splats
  private lateinit var splatEntity: Entity

  private val splatList: List<String> = listOf("apk://Menlo Park.spz", "apk://Los Angeles.spz")
  private var selectedIndex = mutableStateOf(0)
  /**
   * Controls whether the control panel UI is interactive.
   *
   * When loading a new Splat, we disable panel interaction to prevent users from triggering
   * multiple concurrent load operations, which could cause race conditions or confusing visual
   * states. The panel is re-enabled once the Splat finishes loading.
   *
   * This state is passed to the ControlPanel composable, which uses it to:
   * - Disable click handlers on the preview images
   * - Apply a visual "greyed out" effect to indicate the disabled state
   */
  private var isPanelInteractive = mutableStateOf(true)
  private val defaultSplatPath = splatList[0].toUri()
  private val delayVisibilityMS = 2000L
  // Rotation applied to the Splat to align it with the scene coordinate system
  // -90 degrees on X axis converts from original Splat coordinate space to Spatial SDK space
  private val eulerRotation = Vector3(-90f, 0f, 0f)
  private val panelHeight = 1.5f
  private val panelOffset = 2.5f
  private val laxZ = 4f
  private val mpkZ = 2.5f

  private val headQuery =
      Query.where { has(AvatarAttachment.id) }
          .filter { isLocal() and by(AvatarAttachment.typeData).isEqualTo("head") }

  // Register all features your app needs. Features add capabilities to the Spatial SDK.
  override fun registerFeatures(): List<SpatialFeature> {
    return listOf(
        VRFeature(this), // Enable VR rendering
        // SplatFeature: REQUIRED for rendering Gaussian Splats
        // This feature handles loading, decoding, and rendering .spz Splat files
        // Must be registered before creating any entities with Splat components
        SplatFeature(this.spatialContext, systemManager),
        ComposeFeature(), // Enable Compose UI panels
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    NetworkedAssetLoader.init(
        File(applicationContext.getCacheDir().canonicalPath),
        OkHttpAssetFetcher(),
    )
    loadGLXF { composition ->
      environmentEntity = composition.getNodeByName("Environment").entity
      val environmentMesh = environmentEntity.getComponent<Mesh>()
      environmentMesh.defaultShaderOverride = SceneMaterial.UNLIT_SHADER
      environmentEntity.setComponent(environmentMesh)
      floorEntity = composition.getNodeByName("Floor").entity
      initializeSplat(defaultSplatPath)
      setSplatVisibility(false)
    }
  }

  @OptIn(SpatialSDKInternalTestingAPI::class)
  override fun onSceneReady() {
    super.onSceneReady()
    registerTestingIntentReceivers()
    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f,
    )
    scene.updateIBLEnvironment("environment.env")
    scene.setViewOrigin(0.0f, 0.0f, 2.5f, 90.0f)
    skyboxEntity =
        Entity.create(
            listOf(
                Mesh(Uri.parse("mesh://skybox"), hittable = MeshCollision.NoCollision),
                Material().apply {
                  baseTextureAndroidResourceId = R.drawable.skydome
                  unlit = true
                },
                Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f))),
            )
        )
    panelEntity =
        Entity.createPanelEntity(
            R.id.control_panel,
            Transform(Pose(Vector3(0f, panelHeight, 0f), Quaternion(0f, 180f, 0f))),
            Grabbable(type = GrabbableType.PIVOT_Y, minHeight = 0.75f, maxHeight = 2.5f),
        )
    systemManager.registerSystem(ControllerListenerSystem())
  }

  /**
   * Creates an entity with a Splat component.
   *
   * Gaussian Splats are a 3D representation technique that uses millions of small 3D Gaussians to
   * represent real-world captured scenes with photorealistic quality.
   *
   * To create a Splat entity, you need:
   * 1. Splat component: Points to the .spz or .ply file containing the Gaussian Splat data
   * 2. Transform component: Positions and rotates the Splat in 3D space
   * 3. Scale component: Adjusts the size of the Splat
   *
   * Splat files (.ply or .spz) can be loaded from:
   * - Application assets: "apk://filename.spz"
   * - Network URLs: "https://example.com/splat.spz"
   * - Local files: "file:///path/to/splat.spz"
   */
  private fun initializeSplat(splatPath: Uri) {
    // Disable panel interaction while Splat is loading to prevent concurrent load requests
    // The panel will be re-enabled in onSplatLoaded() once the Splat finishes loading
    setPanelInteractive(false)
    splatEntity =
        Entity.create(
            listOf(
                Splat(splatPath),
                Transform(
                    Pose(
                        Vector3(0.0f, 0.0f, 0.0f),
                        Quaternion(eulerRotation.x, eulerRotation.y, eulerRotation.z),
                    )
                ),
                Scale(Vector3(1f)),
                SupportsLocomotion(),
            )
        )
    splatEntity?.registerEventListener<SplatLoadEventArgs>(SplatLoadEventArgs.EVENT_NAME) { _, _ ->
      Log.d("SplatManager", "Splat loaded EVENT!")
      onSplatLoaded()
    }
  }

  private fun onSplatLoaded() {
    // Smooth out the transition from loading to showing the splat
    // This delay ensures the Splat is fully ready before revealing it to the user
    activityScope.launch {
      delay(500)
      recenterScene()
      setSplatVisibility(true)
      // Re-enable panel interaction now that the Splat has finished loading
      // Users can now select a different Splat without causing race conditions
      setPanelInteractive(true)
    }
  }

  /**
   * Enables or disables interaction with the control panel.
   *
   * This is used to prevent users from selecting a new splat while the current one is still
   * loading. When disabled, the panel images are not clickable.
   *
   * @param isInteractive true to enable panel interaction, false to disable it
   */
  private fun setPanelInteractive(isInteractive: Boolean) {
    isPanelInteractive.value = isInteractive
  }

  /**
   * Loads a new Splat asset into the scene.
   *
   * This function demonstrates how to dynamically change which Splat is displayed. You can call
   * this function to switch between different Splat assets at runtime.
   *
   * @param newSplatPath Path to the .spz Splat file (e.g., "apk://MySplat.spz" or a URL)
   */
  fun loadSplat(newSplatPath: String) {

    if (splatEntity.hasComponent<Splat>()) {
      // Entity exists with a Splat component
      var splatComponent = splatEntity.getComponent<Splat>()

      if (splatComponent.path.toString() == newSplatPath) {
        // Optimization: Already showing this Splat, no need to reload
        // This prevents unnecessary file reloading and memory operations
      } else {
        // Disable panel interaction during loading to prevent concurrent load requests
        // This provides a better user experience by preventing rapid-fire selections
        // The panel will be re-enabled in onSplatLoaded() once loading completes
        setPanelInteractive(false)
        // Replace the existing Splat component with a new one pointing to a different file
        // setComponent() automatically unloads the old Splat from memory and loads the new one
        splatEntity.setComponent(Splat(newSplatPath.toUri()))
        recenterScene()
        setSplatVisibility(false)
      }
    } else {
      // No Splat Component exists yet, create one
      splatEntity.setComponent(Splat(newSplatPath.toUri()))
    }
  }

  /**
   * Controls the visibility of the Splat in the scene.
   *
   * Splats respect the Visible component like other rendered entities. Setting Visible(false) hides
   * the Splat without unloading it from memory, allowing for fast show/hide toggling.
   *
   * @param isSplatVisible true to show the Splat, false to hide it
   */
  fun setSplatVisibility(isSplatVisible: Boolean) {

    // Update the Visible Component on the Entity with a Splat Component
    splatEntity.setComponent(Visible(isSplatVisible))
    // Show environment when the Splat is hidden, hide when the Splat is visible
    setEnvironmentVisiblity(!isSplatVisible)
  }

  fun setEnvironmentVisiblity(isVisible: Boolean) {
    environmentEntity.setComponent(Visible(isVisible))
    skyboxEntity.setComponent(Visible(isVisible))
  }

  fun recenterScene() {
    var z = laxZ
    if (splatEntity.getComponent<Splat>().path.toString() == defaultSplatPath.toString()) {
      z = mpkZ
    }
    scene.setViewOrigin(0f, 0f, z, 0f)
    panelEntity.setComponent(
        Transform(Pose(Vector3(0f, panelHeight, z - panelOffset), Quaternion(0f, 180f, 0f)))
    )
  }

  /**
   * Positions the panel 2 meters in front of the user's current head position.
   *
   * This function:
   * 1. Queries for the user's head entity using AvatarAttachment
   * 2. Gets the head's transform to determine forward direction
   * 3. Projects the forward vector onto the horizontal plane (y = 0)
   * 4. Positions the panel 2 meters forward from the head
   * 5. Rotates the panel to face the user
   */
  private fun positionPanelInFrontOfUser(distance: Float) {
    // Find the user's head entity
    val head = headQuery.eval().firstOrNull()

    if (head != null) {
      // Get the head's current pose (position and rotation)
      val headPose = head.getComponent<Transform>().transform
      // Get the forward direction vector from the head pose
      val forward = headPose.forward()
      // Flatten to horizontal plane by zeroing out the y component
      forward.y = 0f
      val forwardNormalized = forward.normalize()
      // Calculate new position 2 meters in front of the head
      var newPosition = headPose.t + (forwardNormalized * distance)
      newPosition.y = panelHeight // Set y position to panel height
      // Create rotation to make panel face the user
      val lookRotation = Quaternion.lookRotation(forwardNormalized)
      // Update the panel's transform
      panelEntity.setComponent(Transform(Pose(newPosition, lookRotation)))
    }
  }

  /**
   * System that listens for controller button presses and performs actions.
   *
   * This demonstrates how to:
   * - Query for controller entities in the scene
   * - Filter for local and active controllers
   * - Detect button press events (ButtonA and ButtonB)
   * - Access head tracking data to reposition UI panels
   *
   * Button mappings:
   * - A Button: Repositions the UI panel 2 meters in front of the user's current view direction
   * - B Button: Resets the view origin and positions the panel in front of the user
   *
   * This is a useful starting point for implementing controller-based interactions in your Spatial
   * SDK application.
   */
  inner class ControllerListenerSystem : SystemBase() {
    override fun execute() {
      // Query for all entities with Controller component and filter for local controllers
      val controllers = Query.where { has(Controller.id) }.eval().filter { it.isLocal() }

      for (controllerEntity in controllers) {
        val controller = controllerEntity.getComponent<Controller>()
        // Skip inactive controllers
        if (!controller.isActive) continue

        // Filter for right controller only
        val attachment = controllerEntity.tryGetComponent<AvatarAttachment>()
        if (attachment?.type != "right_controller") continue

        // Check if Button A was just pressed (button state changed and is now pressed)
        if (
            (controller.changedButtons and ButtonBits.ButtonA) != 0 &&
                (controller.buttonState and ButtonBits.ButtonA) != 0
        ) {
          positionPanelInFrontOfUser(panelOffset)
        }

        // Check if Button B was just pressed
        if (
            (controller.changedButtons and ButtonBits.ButtonB) != 0 &&
                (controller.buttonState and ButtonBits.ButtonB) != 0
        ) {
          recenterScene()
        }
      }
    }
  }

  @OptIn(SpatialSDKExperimentalAPI::class)
  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        createSimpleComposePanel(
            R.id.control_panel,
            ANIMATION_PANEL_WIDTH,
            ANIMATION_PANEL_HEIGHT,
        ) {
          // Pass the loadSplat function to the UI panel
          // This allows users to select different Splat assets from the UI
          // Pass isPanelInteractive state to control UI interaction during Splat loading
          // When false, the panel images become non-clickable and visually greyed out
          // This prevents users from selecting a new Splat while one is still loading
          ControlPanel(splatList, selectedIndex, isPanelInteractive, ::loadSplat)
        },
    )
  }

  private fun loadGLXF(onLoaded: ((GLXFInfo) -> Unit) = {}): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("apk:///scenes/Composition.glxf"),
          rootEntity = gltfxEntity!!,
          keyName = "example_key_name",
          onLoaded = onLoaded,
      )
    }
  }

  private fun createSimpleComposePanel(
      panelId: Int,
      width: Float,
      height: Float,
      content: @Composable () -> Unit,
  ): ComposeViewPanelRegistration {
    return ComposeViewPanelRegistration(
        panelId,
        composeViewCreator = { _, ctx -> ComposeView(ctx).apply { setContent { content() } } },
        settingsCreator = {
          UIPanelSettings(
              shape = QuadShapeOptions(width = width, height = height),
              style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
              display = DpPerMeterDisplayOptions(),
          )
        },
    )
  }
}
