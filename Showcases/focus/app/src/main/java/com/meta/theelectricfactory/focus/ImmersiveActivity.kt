// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
//import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.isdk.IsdkGrabbable
import com.meta.spatial.isdk.IsdkPanelDimensions
import com.meta.spatial.runtime.SceneAudioAsset
import com.meta.spatial.runtime.SceneAudioPlayer
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Box
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import com.meta.spatial.vr.VrInputSystemType
import java.lang.ref.WeakReference
import com.meta.spatial.compose.ComposeFeature
import com.meta.theelectricfactory.focus.data.Project
import com.meta.theelectricfactory.focus.data.environments
import com.meta.theelectricfactory.focus.data.skyboxes
import com.meta.theelectricfactory.focus.managers.DatabaseManager
import com.meta.theelectricfactory.focus.managers.AudioManager
import com.meta.theelectricfactory.focus.managers.PanelManager
import com.meta.theelectricfactory.focus.managers.ProjectManager
import com.meta.theelectricfactory.focus.managers.ToolManager
import com.meta.theelectricfactory.focus.systems.BoardParentingSystem
import com.meta.theelectricfactory.focus.systems.DatabaseUpdateSystem
import com.meta.theelectricfactory.focus.systems.GeneralSystem
import com.meta.theelectricfactory.focus.systems.UpdateTimeSystem
import com.meta.theelectricfactory.focus.utils.addOnSelectListener
import com.meta.theelectricfactory.focus.utils.deleteObject
import com.meta.theelectricfactory.focus.utils.getChildren
import com.meta.theelectricfactory.focus.utils.placeInFront

class ImmersiveActivity : AppSystemActivity() {

    // SCENE ELEMENTS
    lateinit var logo: Entity
    lateinit var environment: Entity
    lateinit var skybox: Entity
    lateinit var clock: Entity
    lateinit var speaker: Entity
    lateinit var speakerState: Entity
    lateinit var deleteButton: Entity

    // VARIABLES
    lateinit var DB: DatabaseManager
    var appStarted: Boolean = false
    var currentEnvironment = 0
    var currentObjectSelected: Entity? = null
    var passthroughEnabled: Boolean = false

    private val currentProject: Project? get() = ProjectManager.instance.currentProject

    override fun registerFeatures(): List<SpatialFeature> {
        val features = mutableListOf(
            VRFeature(this, inputSystemType = VrInputSystemType.SIMPLE_CONTROLLER),
            ComposeFeature(),
            //IsdkFeature(this, spatial, systemManager)
        )
        return features
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instance = WeakReference(this)

        // Register custom systems and components
        componentManager.registerComponent<UniqueAssetComponent>(UniqueAssetComponent.Companion)
        componentManager.registerComponent<ToolComponent>(ToolComponent.Companion)
        componentManager.registerComponent<TimeComponent>(TimeComponent.Companion)
        componentManager.registerComponent<AttachableComponent>(AttachableComponent.Companion)
        componentManager.registerComponent<IsdkGrabbable>(IsdkGrabbable.Companion)
        componentManager.registerComponent<IsdkPanelDimensions>(IsdkPanelDimensions.Companion)

        systemManager.registerSystem(DatabaseUpdateSystem())
        systemManager.registerSystem(UpdateTimeSystem())
        systemManager.registerSystem(GeneralSystem())
        systemManager.registerSystem(BoardParentingSystem())

        AudioManager.instance.ambientSound = SceneAudioAsset.loadLocalFile("audio/ambient.wav")
        AudioManager.instance.createSound = SceneAudioAsset.loadLocalFile("audio/create.wav")
        AudioManager.instance.deleteSound = SceneAudioAsset.loadLocalFile("audio/delete.wav")
        AudioManager.instance.timerSound = SceneAudioAsset.loadLocalFile("audio/timer.wav")
        AudioManager.instance.ambientSoundPlayer = SceneAudioPlayer(scene, AudioManager.instance.ambientSound)
    }

    override fun onSceneReady() {
        super.onSceneReady()
        Log.i("Focus", "Focus> Focus Immersive Activity is ready")

        // Locomotion system disabled for a better interaction between controllers and panels
        systemManager.findSystem<LocomotionSystem>().enableLocomotion(false)
        // Create or load local database
        DB = DatabaseManager(this)

        // Main panels created
        PanelManager.instance.createPanels()
        // Rest of the elements in scene are created
        createSceneElements()
        // Initial state of objects in scene
        setInitialState()
    }

    override fun registerPanels(): List<PanelRegistration> {
         return PanelManager.instance.registerFocusPanels()
    }

    override fun onPause() {
        super.onPause()
        // Stop audio when app is on pause
        if (currentProject != null && AudioManager.instance.audioIsOn) AudioManager.instance.ambientSoundPlayer.stop()
    }

    override fun onResume() {
        super.onResume()
        // Resume audio when app return from pause
        if (currentProject != null && AudioManager.instance.audioIsOn) AudioManager.instance.playAmbientSound()
    }

    //////////////////////////
    // SCENE ELEMENTS CREATION
    //////////////////////////

    private fun createSceneElements() {
        logo =
            Entity.create(
                Mesh(mesh = Uri.parse("focus_logo.glb")),
                Scale(Vector3(0.075f)),
                Visible(false),
                Transform(Pose(Vector3(0f))),
                IsdkGrabbable(enabled = false, billboardOrientation = Vector3(0f, 180f, 0f)),
            )

        environment =
            Entity.create(
                Mesh(mesh = Uri.parse(environments[0])),
                Visible(false),
            )

        createSpeaker()
        createClock()
        createDeleteButton()
    }

    // Skybox is created after the app is initialized to improve performance. More info in GeneralSystem.kt
    fun createSkybox(res: Int) {
        skybox =
            Entity.create(
                Mesh(Uri.parse("mesh://skybox"), hittable = MeshCollision.NoCollision),
                Material().apply {
                    baseTextureAndroidResourceId = res
                    unlit = true // Prevent scene lighting from affecting the skybox
                },
                Visible(false),
                Transform(Pose(Vector3(0f)
                )
            )
        )
    }

    // Speaker composed object created
    private fun createSpeaker() {

        // Create speaker entity with Mesh component
        speaker =
            Entity.create(
                Mesh(mesh = Uri.parse("speaker.glb")),
                Scale(Vector3(0.08f)),
                Transform(Pose(Vector3(0f))),
                Visible(false),
                Grabbable(true, GrabbableType.PIVOT_Y),
                IsdkGrabbable(billboardOrientation = Vector3(0f, 180f, 0f)),
                // Empty UUID since the asset is not linked with any project for now
                UniqueAssetComponent(type = AssetType.SPEAKER)
            )

        val size = 0.05f
        // Image entity showing state On/Off of the speaker.
        // We make this entity child to the speaker entity, to move them together, as one only object
        speakerState =
            Entity.create(
                // hittable property should be NonCollision if we don't want to interact with it, nor block the parent entity
                Mesh(Uri.parse("mesh://box")).apply { hittable = MeshCollision.NoCollision },
                Box(Vector3(-size, -size, 0f), Vector3(size, size, 0f)),
                Material().apply {
                    baseTextureAndroidResourceId = R.drawable.speaker_on
                    alphaMode = 1
                },
                Transform(Pose(Vector3(0f, -0.01f, 0.045f), Quaternion(-30f, 0f, 0f))),
                // We make this entity child to the speaker entity
                TransformParent(speaker)
            )
    }

    // Clock composed object created by two entities, one with a Mesh component and the other with a Panel component
    private fun createClock() {
        val _width = 0.18f
        val _height = 0.18f
        val _dp = 1100f

        registerPanel(
            PanelRegistration(R.layout.clock_layout) {
                config {
                    themeResourceId = R.style.Theme_Focus_Transparent
                    width = _width
                    height = _height
                    layoutWidthInDp = _dp
                    layoutHeightInDp = _dp * (height / width)
                    includeGlass = false
                }
            }
        )

        // Entity with panel component created
        val clockPanel: Entity =
            Entity.create(
                Panel(R.layout.clock_layout).apply { hittable = MeshCollision.NoCollision },
                Transform(Pose(Vector3(0f, 0f, 0.035f), Quaternion(0f, 180f, 0f))),
                // Adding TimeComponent to clock panel to be able to update it. More info in UpdateTimeSystem.kt
                TimeComponent(AssetType.CLOCK),
            )

        // Creating clock entity with Mesh
        clock =
            Entity.create(
                Mesh(mesh = Uri.parse("clock.glb")),
                Scale(Vector3(0.1f)),
                Transform(Pose(Vector3(0f))),
                Grabbable(true, GrabbableType.FACE),
                IsdkGrabbable(billboardOrientation = Vector3(0f, 180f, 0f)),
                Visible(false),
                // Empty UUID since the asset is not linked with any project for now
                UniqueAssetComponent(type = AssetType.CLOCK)
            )

        // Making panel entity child of clock entity
        clockPanel.setComponent(TransformParent(clock))
    }

    // Delete button (trash can) to show when an element has been selected
    private fun createDeleteButton() {
        deleteButton =
            Entity.create(
                Mesh(Uri.parse("mesh://box")),
                Box(Vector3(-0.015f, -0.015f, 0f), Vector3(0.015f, 0.015f, 0f)),
                Transform(Pose(Vector3(0f))),
                IsdkPanelDimensions(Vector2(0.04f, 0.04f)),
                Material().apply {
                    baseTextureAndroidResourceId = R.drawable.delete
                    alphaMode = 1
                    unlit = true
                },
            )

        addOnSelectListener(
            deleteButton,
            fun() {
                deleteObject(currentObjectSelected, true)
            }
        )
    }

    ////////////////
    // SETTING SCENE
    ////////////////

    // Init app is called after app logo has been showed
    fun initApp() {
        appStarted = true
        logo.destroy()
        placeInFront(PanelManager.instance.homePanel)
        PanelManager.instance.homePanel.setComponent(Visible(true))
    }

    fun setInitialState() {
        // Set initial state of objects in scene
        setPassthrough(true)
        setLighting(-1)

        if (appStarted) {
            environment.setComponent(Visible(false))
            skybox.setComponent(Visible(false))
        }

        deleteButton.setComponent(Visible(false))

        if (!PanelManager.instance.homePanel.getComponent<Visible>().isVisible) placeInFront(PanelManager.instance.homePanel)
        PanelManager.instance.homePanel.setComponent(Visible(appStarted))
        PanelManager.instance.toolbarPanel.setComponent(Visible(false))
        PanelManager.instance.tasksPanel.setComponent(Visible(false))
        PanelManager.instance.aiExchangePanel.setComponent(Visible(false))
        PanelManager.instance.closeSubPanels()

        showClock(false)
        showSpeaker(false)

        ToolManager.instance.cleanElements()
    }

    // Set lighting of scene depending on the chosen 3D environment
    private fun setLighting(env: Int) {

        when (env) {
            -1 -> {
                scene.setLightingEnvironment(
                    Vector3(2.5f, 2.5f, 2.5f), // ambient light color (none in this case)
                    Vector3(1.8f, 1.8f, 1.8f), // directional light color
                    -Vector3(1.0f, 3.0f, 2.0f), // directional light direction
                )
            }
            0 -> {
                scene.setLightingEnvironment(
                    Vector3(1.8f, 1.5f, 1.5f),
                    Vector3(1.5f, 1.5f, 1.5f),
                    -Vector3(1.0f, 3.0f, 2.0f),
                )
            }
            1 -> {
                scene.setLightingEnvironment(
                    Vector3(1.5f, 1.5f, 1.5f),
                    Vector3(1.5f, 1.5f, 1.5f),
                    -Vector3(1.0f, 3.0f, 2.0f),
                )
            }
            2 -> {
                scene.setLightingEnvironment(
                    Vector3(3.5f, 3.5f, 3.5f),
                    Vector3(2f, 2f, 2f),
                    -Vector3(1.0f, 3.0f, 2.0f)
                )
            }
        }
    }

    private fun setPassthrough(state: Boolean) {
        passthroughEnabled = state
        scene.enablePassthrough(state)
    }

    // Change environment model and skybox texture when different environment has been selected
    fun selectEnvironment(env: Int) {
        setPassthrough(false)
        setLighting(env)

        currentEnvironment = env
        // Change 3D model of environment entity
        environment.setComponent(Mesh(mesh = Uri.parse(environments[env])))
        environment.setComponent(Visible(true))

        // Change texture of skybox entity
        skybox.setComponent(
            Material().apply {
                baseTextureAndroidResourceId = skyboxes[env]
                unlit = true
            },
        )
        skybox.setComponent(Visible(true))
    }

    // Show passthrough and hide environment and skybox
    fun selectMRMode() {
        setPassthrough(true)
        environment.setComponent(Visible(false))
        skybox.setComponent(Visible(false))
    }

    // Show or hide clock and clock children
    fun showClock(state: Boolean) {
        clock.setComponent(Visible(state))

        for (child in getChildren(clock)) {
            child.setComponent(Visible(state))
        }
    }

    // Show or hide speaker and speaker children
    fun showSpeaker(state: Boolean) {
        speaker.setComponent(Visible(state))

        for (child in getChildren(speaker)) {
            child.setComponent(Visible(state))
        }
    }

    companion object {
        lateinit var instance: WeakReference<ImmersiveActivity>

        fun getInstance(): ImmersiveActivity? =
            if (::instance.isInitialized) instance.get() else null
    }
}