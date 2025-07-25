// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.isdk.IsdkGrabbable
import com.meta.spatial.isdk.IsdkPanelDimensions
import com.meta.spatial.runtime.PanelSceneObject
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
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.lang.ref.WeakReference
import kotlinx.coroutines.*

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.composePanel
import com.meta.spatial.runtime.LayerConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ImmersiveActivity : AppSystemActivity() {

    // Enable or disable AI in project
    val AIenabled: Boolean = true

    lateinit var DB: DatabaseManager
    var appStarted: Boolean = false

    // PROJECT DATA
    lateinit var logo: Entity
    lateinit var environment: Entity
    lateinit var skybox: Entity
    lateinit var clock: Entity
    lateinit var speaker: Entity
    lateinit var speakerState: Entity

    // PANELS
    lateinit var homePanel: Entity
    lateinit var toolbarPanel: Entity
    lateinit var tasksPanel: Entity
    lateinit var aiExchangePanel: Entity

    lateinit var stickySubPanel: Entity
    lateinit var labelSubPanel: Entity
    lateinit var arrowSubPanel: Entity
    lateinit var boardSubPanel: Entity
    lateinit var shapeSubPanel: Entity
    lateinit var stickerSubPanel: Entity
    lateinit var timerSubPanel: Entity
    var subpanels: MutableList<Entity> = mutableListOf()

    lateinit var deleteButton: Entity

    // Sounds
    lateinit var ambientSound: SceneAudioAsset
    lateinit var createSound: SceneAudioAsset
    lateinit var deleteSound: SceneAudioAsset
    lateinit var timerSound: SceneAudioAsset
    lateinit var ambientSoundPlayer: SceneAudioPlayer

    // VARIABLES
    var templateState = 0
    var templatePriority = 0
    var currentEnvironment = 0

    var currentProject: Project? = null
    var currentObjectSelected: Entity? = null
    var passthroughEnabled: Boolean = false
    var speakerIsOn = false
    var lastAIResponse = ""
    var waitingForAI = false

    val focusViewModel = FocusViewModel()

    override fun registerFeatures(): List<SpatialFeature> {
        val features =
            mutableListOf<SpatialFeature>(VRFeature(this), ComposeFeature(), IsdkFeature(this, spatial, systemManager))
        return features
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Focus", "Focus> onCreate")

        instance = WeakReference(this)

        // Register custom systems and components
        componentManager.registerComponent<UniqueAssetComponent>(UniqueAssetComponent.Companion)
        componentManager.registerComponent<ToolComponent>(ToolComponent.Companion)
        componentManager.registerComponent<TimeComponent>(TimeComponent.Companion)
        componentManager.registerComponent<AttachableComponent>(AttachableComponent.Companion)

        systemManager.registerSystem(DatabaseUpdateSystem())
        systemManager.registerSystem(UpdateTimeSystem())
        systemManager.registerSystem(GeneralSystem())
        systemManager.registerSystem(BoardParentingSystem())

        ambientSound = SceneAudioAsset.loadLocalFile("audio/ambient.wav")
        createSound = SceneAudioAsset.loadLocalFile("audio/create.wav")
        deleteSound = SceneAudioAsset.loadLocalFile("audio/delete.wav")
        timerSound = SceneAudioAsset.loadLocalFile("audio/timer.wav")
        ambientSoundPlayer = SceneAudioPlayer(scene, ambientSound)
    }

    override fun onSceneReady() {
        super.onSceneReady()
        Log.i("Focus", "Focus> onSceneReady")

        // Locomotion system disabled for a better interaction between controllers and panels
        systemManager.findSystem<LocomotionSystem>().enableLocomotion(false)
        // Create or load local database
        DB = DatabaseManager(this)

        // Main panels created
        createPanels()
        // Rest of the elements in scene are created
        createSceneElements()
        // Initial state of objects in scene
        setInitState()
    }

    override fun registerPanels(): List<PanelRegistration> {
        return listOf(
            PanelRegistration(PanelRegistrationIds.HomePanel, 0.58f, 0.41f, true) {},
            PanelRegistration(PanelRegistrationIds.Toolbar, 0.65f, 0.065f) { ToolbarPanel() },
            PanelRegistration(PanelRegistrationIds.TasksPanel, 0.275f, 0.5f) { TasksPanel() },
            PanelRegistration(PanelRegistrationIds.AIPanel, 0.3f, 0.5f) { AIPanel() },
            PanelRegistration(PanelRegistrationIds.StickySubPanel, 0.26f, 0.042f) { StickySubPanel() },
            PanelRegistration(PanelRegistrationIds.LabelSubPanel, 0.44f, 0.042f) { LabelSubPanel() },
            PanelRegistration(PanelRegistrationIds.ArrowSubPanel, 0.28f, 0.042f) { ArrowSubPanel() },
            PanelRegistration(PanelRegistrationIds.BoardSubPanel, 0.21f, 0.042f) { BoardSubPanel() },
            PanelRegistration(PanelRegistrationIds.ShapesSubPanel, 0.28f, 0.042f) { ShapeSubPanel() },
            PanelRegistration(PanelRegistrationIds.StickerSubPanel, 0.29f, 0.042f) { StickerSubPanel() },
            PanelRegistration(PanelRegistrationIds.TimerSubPanel, 0.38f, 0.042f) { TimerSubPanel() },
        )
    }

    object PanelRegistrationIds {
        const val HomePanel = 22 //TODO
        const val AIPanel = 23
        const val TasksPanel = 24
        const val Toolbar = 25
        const val StickySubPanel = 26
        const val LabelSubPanel = 27
        const val ArrowSubPanel = 28
        const val BoardSubPanel = 29
        const val ShapesSubPanel = 30
        const val StickerSubPanel = 31
        const val TimerSubPanel = 32
    }

    fun PanelRegistration(
        registrationId: Int,
        widthInMeters: Float,
        heightInMeters: Float,
        homePanel: Boolean = false,
        content: @Composable () -> Unit,
    ): PanelRegistration {
        return PanelRegistration(registrationId) { _ ->
            config {
                width = widthInMeters
                height = heightInMeters
                // layoutWidthInPx = (3960 * width).toInt()
                layoutWidthInDp = focusDP * width
                layerConfig = LayerConfig() // TODO maybe this is too expensive
                enableTransparent = true
                includeGlass = false
                themeResourceId = R.style.Theme_Focus_Transparent
            }

            if (homePanel) {
                activityClass = MainActivity::class.java
            } else {
                composePanel { setContent { content() } }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop audio when app is on pause
        if (currentProject != null && speakerIsOn) ambientSoundPlayer.stop()
    }

    override fun onResume() {
        super.onResume()
        // Resume audio when app return from pause
        if (currentProject != null && speakerIsOn) playAmbientSound()
    }

    private fun setInitState() {
        // Set initial state of objects in scene
        setPassthrough(true)
        setLighting(-1)

        if (appStarted) {
            environment.setComponent(Visible(false))
            skybox.setComponent(Visible(false))
        }

        deleteButton.setComponent(Visible(false))

        if (!homePanel.getComponent<Visible>().isVisible) placeInFront(homePanel)
        homePanel.setComponent(Visible(appStarted))
        toolbarPanel.setComponent(Visible(false))
        tasksPanel.setComponent(Visible(false))
        aiExchangePanel.setComponent(Visible(false))

        closeSubPanels()

        showClock(false)
        showSpeaker(false)

        cleanElements()
    }

    fun newProject() {
        // New project variables reset
        templateState = 0
        templatePriority = 0
        currentEnvironment = 0

        currentProject = null
        currentObjectSelected = null
        passthroughEnabled = false
        speakerIsOn = false
        lastAIResponse = ""
        waitingForAI = false
        setInitState()
    }

    private fun cleanElements() {
        // Clean previous elements in project, if there is any
        val toolAssets = Query.where { has(ToolComponent.id) }
        for (entity in toolAssets.eval()) {
            deleteObject(entity, false, true)
        }
    }

    // Load project from scroll view in First Fragment
    @SuppressLint("Range")
    public fun loadProject(id: Int) {
        homePanel.setComponent(Visible(false))

        if (currentProject?.uuid == id) return

        // Clean elements from previous projects
        cleanElements()

        // Load project settings
        val cursor = DB.getProject(id)
        if (cursor.moveToFirst()) {
            val projectName = cursor.getString(cursor.getColumnIndex(DatabaseManager.PROJECT_NAME))
            val mr =
                if (cursor.getInt(cursor.getColumnIndex(DatabaseManager.PROJECT_MR)) == 1) true
                else false
            val env = cursor.getInt(cursor.getColumnIndex(DatabaseManager.PROJECT_ENVIRONMENT))
            currentProject = Project(id, projectName, mr, env)
            focusViewModel.updateCurrentProjectUuid(id)
        }
        cursor.close()

        if (currentProject?.MR == true) {
            selectMRMode()
        } else {
            selectEnvironment(currentProject?.environment!!)
        }

        // Position Unique Assets
        val uniqueAssetsCursor = DB.getUniqueAssets(currentProject?.uuid)
        if (uniqueAssetsCursor.moveToFirst()) {
            while (!uniqueAssetsCursor.isAfterLast) {
                val uuid =
                    uniqueAssetsCursor.getInt(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_UUID))
                val rawType =
                    uniqueAssetsCursor.getString(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_TYPE))
                val type = AssetType.entries.find { it.name == rawType }
                val state =
                    uniqueAssetsCursor.getInt(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_STATE))

                val posX =
                    uniqueAssetsCursor.getFloat(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_POSITION_X))
                val posY =
                    uniqueAssetsCursor.getFloat(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_POSITION_Y))
                val posZ =
                    uniqueAssetsCursor.getFloat(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_POSITION_Z))

                val rotW =
                    uniqueAssetsCursor.getFloat(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_ROTATION_W))
                val rotX =
                    uniqueAssetsCursor.getFloat(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_ROTATION_X))
                val rotY =
                    uniqueAssetsCursor.getFloat(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_ROTATION_Y))
                val rotZ =
                    uniqueAssetsCursor.getFloat(
                        uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_ROTATION_Z))

                when (type) {
                    AssetType.TASKS_PANEL -> {
                        tasksPanel.setComponent(UniqueAssetComponent(uuid, AssetType.TASKS_PANEL))
                        tasksPanel.setComponent(Visible(if (state == 1) true else false))
                        tasksPanel.setComponent(
                            Transform(Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ))))
                    }
                    AssetType.AI_PANEL -> {
                        aiExchangePanel.setComponent(UniqueAssetComponent(uuid, AssetType.AI_PANEL))
                        aiExchangePanel.setComponent(Visible(if (state == 1) true else false))
                        aiExchangePanel.setComponent(
                            Transform(Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ))))
                    }
                    AssetType.CLOCK -> {
                        clock.setComponent(UniqueAssetComponent(uuid, AssetType.CLOCK))
                        clock.setComponent(
                            Transform(Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ))))
                    }
                    AssetType.SPEAKER -> {
                        speaker.setComponent(UniqueAssetComponent(uuid, AssetType.SPEAKER))
                        speaker.setComponent(
                            Transform(Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ))))
                        speakerIsOn = if (state == 1) true else false
                        if (speakerIsOn) playAmbientSound()
                        else stopAmbientSound()
                    }
                    else -> {
                        Log.e("Focus", "Focus> Unknown Unique Asset")
                    }
                }
                uniqueAssetsCursor.moveToNext()
            }
        }

        placeInFront(toolbarPanel)
        toolbarPanel.setComponent(Visible(true))

        showClock(true)
        showSpeaker(true)

        //audioButton.setImageResource(if (speakerIsOn) R.drawable.sound else R.drawable.sound_off)

        // Load and create Tool Assets
        val toolsCursor = DB.getToolAssets(currentProject?.uuid)
        if (toolsCursor.moveToFirst()) {
            while (!toolsCursor.isAfterLast) {
                val uuid = toolsCursor.getInt(toolsCursor.getColumnIndex(DatabaseManager.TOOL_UUID))
                val rawType = toolsCursor.getString(toolsCursor.getColumnIndex(DatabaseManager.TOOL_TYPE))
                val type = AssetType.entries.find { it.name == rawType }
                val source = toolsCursor.getString(toolsCursor.getColumnIndex(DatabaseManager.TOOL_SOURCE))
                val size = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_SIZE))
                val deleteHeight =
                    toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_DELETE_HEIGHT))

                val posX = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_POSITION_X))
                val posY = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_POSITION_Y))
                val posZ = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_POSITION_Z))

                val rotW = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_ROTATION_W))
                val rotX = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_ROTATION_X))
                val rotY = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_ROTATION_Y))
                val rotZ = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_ROTATION_Z))

                if (type == AssetType.WEB_VIEW) {
                      WebView(
                          source,
                          uuid,
                          Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)))
                } else {
                    Tool(
                        type = type,
                        source = source,
                        size = size,
                        uuid = uuid,
                        pose = Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)),
                        deleteButtonHeight = deleteHeight
                    )
                }
                toolsCursor.moveToNext()
            }
        }
        toolsCursor.close()

        // Load and create Stickies
        val stickiesCursor = DB.getStickies(currentProject?.uuid)
        if (stickiesCursor.moveToFirst()) {
            while (!stickiesCursor.isAfterLast) {
                val uuid = stickiesCursor.getInt(stickiesCursor.getColumnIndex(DatabaseManager.STICKY_UUID))
                val message =
                    stickiesCursor.getString(stickiesCursor.getColumnIndex(DatabaseManager.STICKY_MESSAGE))
                val rawColor =
                    stickiesCursor.getString(stickiesCursor.getColumnIndex(DatabaseManager.STICKY_COLOR))
                val color = StickyColor.entries.find { it.name == rawColor }

                val posX =
                    stickiesCursor.getFloat(
                        stickiesCursor.getColumnIndex(DatabaseManager.STICKY_POSITION_X))
                val posY =
                    stickiesCursor.getFloat(
                        stickiesCursor.getColumnIndex(DatabaseManager.STICKY_POSITION_Y))
                val posZ =
                    stickiesCursor.getFloat(
                        stickiesCursor.getColumnIndex(DatabaseManager.STICKY_POSITION_Z))

                val rotW =
                    stickiesCursor.getFloat(
                        stickiesCursor.getColumnIndex(DatabaseManager.STICKY_ROTATION_W))
                val rotX =
                    stickiesCursor.getFloat(
                        stickiesCursor.getColumnIndex(DatabaseManager.STICKY_ROTATION_X))
                val rotY =
                    stickiesCursor.getFloat(
                        stickiesCursor.getColumnIndex(DatabaseManager.STICKY_ROTATION_Y))
                val rotZ =
                    stickiesCursor.getFloat(
                        stickiesCursor.getColumnIndex(DatabaseManager.STICKY_ROTATION_Z))

                StickyNote(
                    uuid,
                    message,
                    color!!,
                    Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ))
                )

                stickiesCursor.moveToNext()
            }
        }
        stickiesCursor.close()

        // Clean task from previous projects and load corresponding ones
        //cleanAndLoadTasks()
    }

    // Save project settings in second fragment
    fun saveProjectSettings(MRMode: Boolean? = null, projectName: String) {
        var mrMode = MRMode != null && MRMode == true

        // Settings if it is a new project...
        if (currentProject == null) {
            Log.i("Focus", "Focus> New project created")

            val project = Project(getNewUUID(), projectName, mrMode, currentEnvironment)
            currentProject = project
            DB.createProject(project)
            focusViewModel.updateCurrentProjectUuid(project.uuid)

            placeInFront(toolbarPanel)
            toolbarPanel.setComponent(Visible(true))
            tasksPanel.setComponent(Visible(true))
            if (AIenabled) aiExchangePanel.setComponent(Visible(true))
            showClock(true)
            showSpeaker(true)

            // Unique elements created to store in database
            val tasksPanelUUID = getNewUUID()
            val aiPanelUUID = getNewUUID()
            val clockUUID = getNewUUID()
            val speakerUUID = getNewUUID()

            tasksPanel.setComponent(UniqueAssetComponent(tasksPanelUUID, AssetType.TASKS_PANEL))
            aiExchangePanel.setComponent(UniqueAssetComponent(aiPanelUUID, AssetType.AI_PANEL))
            clock.setComponent(UniqueAssetComponent(clockUUID, AssetType.CLOCK))
            speaker.setComponent(UniqueAssetComponent(speakerUUID, AssetType.SPEAKER))

            // Initial configuration of panels for a new project
            placeInFront(toolbarPanel)
            placeInFront(tasksPanel, Vector3(-0.45f, -0.04f, 0.8f))
            placeInFront(aiExchangePanel, Vector3(0.45f, -0.05f, 0.8f))
            placeInFront(clock, Vector3(0f, 0.23f, 0.9f))
            placeInFront(speaker, Vector3(-0.65f, -0.3f, 0.65f))

            // Unique elements created in database
            DB.createUniqueAsset(
                tasksPanelUUID,
                project.uuid,
                AssetType.TASKS_PANEL,
                true,
                tasksPanel.getComponent<Transform>().transform
            )

            DB.createUniqueAsset(
                aiPanelUUID,
                project.uuid,
                AssetType.AI_PANEL,
                true,
                aiExchangePanel.getComponent<Transform>().transform
            )

            DB.createUniqueAsset(
                clockUUID,
                project.uuid,
                AssetType.CLOCK,
                true,
                clock.getComponent<Transform>().transform
            )

            DB.createUniqueAsset(
                speakerUUID,
                project.uuid,
                AssetType.SPEAKER,
                true,
                speaker.getComponent<Transform>().transform
            )

            // Initial Web View tool created as an example
            WebView()
            // Clean tasks from previous projects, in case there are
            //cleanAndLoadTasks()
            homePanel.setComponent(Visible(false))
            playAmbientSound()

        // if it is not a new project, we only update project settings in database (name, environment)
        } else {

            if (projectName != "") currentProject?.name = projectName
            currentProject?.MR = mrMode
            currentProject?.environment = currentEnvironment

            DB.updateProject(currentProject)
        }
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
                // hittable property should be NonCollision if we don't want to interact with it, nor
                // block the parent entity
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

        // Add listener to detect when user is selecting the object and stop or play sound
        addOnSelectListener(
            speaker,
            fun() {
              if (speakerIsOn) {
                stopAmbientSound()
              } else {
                playAmbientSound()
              }
            }
        )
    }

    // Clock composed object created by two entities, one with a Mesh component and the other with a
    // Panel component
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
                // Adding TimeComponent to clock panel to be able to update it. More info in
                // UpdateTimeSystem.kt
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

    // Delete button (trash can) to show when an element has been selected
    private fun createDeleteButton() {
        deleteButton =
            Entity.create(
                Mesh(Uri.parse("mesh://box")),
                Box(Vector3(-0.02f, -0.02f, 0f), Vector3(0.02f, 0.02f, 0f)),
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
            })
    }

    // All panels in scene are created and referenced in variables to control them
    private fun createPanels() {
        createHomePanel()
        createToolbarPanel()
        createTasksPanel()
        createAIExchangePanel()

        createStickySubPanel()
        createLabelSubPanel()
        createArrowSubPanel()
        createBoardSubPanel()
        createShapeSubPanel()
        createStickerSubPanel()
        createTimerSubPanel()
    }

    // Creation of scene elements
    fun createSceneElements() {

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

    // Skybox is created after the app is initialized to improve performance. More info in
    // GeneralSystem.kt
    fun createSkybox(res: Int) {
        skybox =
            Entity.create(
                Mesh(Uri.parse("mesh://skybox"), hittable = MeshCollision.NoCollision),
                Material().apply {
                    baseTextureAndroidResourceId = res
                    unlit = true // Prevent scene lighting from affecting the skybox
                },
                Visible(false),
                Transform(Pose(Vector3(0f))))
    }

    // Init app is called after app logo has been showed
    fun initApp() {
        appStarted = true
        logo.destroy()
        placeInFront(homePanel)
        homePanel.setComponent(Visible(true))
    }

    private fun setPassthrough(state: Boolean) {
        passthroughEnabled = state
        scene.enablePassthrough(state)
    }

    // Play ambient audio and save its state in database
    private fun playAmbientSound() {
        speakerIsOn = true

        // Playing spatial sound in loop
        ambientSoundPlayer.play(speaker.getComponent<Transform>().transform.t, 0.2f, true)

        // Change texture to speaker state image
        speakerState.setComponent(
            Material().apply {
                baseTextureAndroidResourceId = R.drawable.speaker_on
                alphaMode = 1
            })
        // Project audio state updated in database
        DB.updateUniqueAsset(speaker.getComponent<UniqueAssetComponent>().uuid, state = speakerIsOn)
        // Change texture to audio button state image in toolbar
        focusViewModel.setSpeakerIsOn(true)
    }

    // Stop ambient audio and save its state in database
    private fun stopAmbientSound() {
        speakerIsOn = false
        ambientSoundPlayer.stop()

        // Change texture to speaker state image
        speakerState.setComponent(
            Material().apply {
                baseTextureAndroidResourceId = R.drawable.speaker_off
                alphaMode = 1
            })
        // Project audio state updated in database
        DB.updateUniqueAsset(speaker.getComponent<UniqueAssetComponent>().uuid, state = speakerIsOn)
        // Change texture to audio button state image in toolbar
        focusViewModel.setSpeakerIsOn(false)
    }

    // Play sound when a tool has been created
    fun playCreationSound(position: Vector3) {
        scene.playSound(createSound, position, 1f)
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

    fun deleteTask(uuid: Int) {
        DB.deleteTask(uuid)
        focusViewModel.refreshTasksPanel()

        // Delete correspondent spatial task if exists
        var ent = getSpatialTask(uuid)
        if (ent != null) {
            // if current object selected is spatial task, we detach delete button first.
            if (currentObjectSelected != null && currentObjectSelected!!.equals(ent)) {
                currentObjectSelected = null
                deleteButton.setComponent(TransformParent())
                deleteButton.setComponent(Visible(false))
            }

            ent.destroy()
            scene.playSound(deleteSound, tasksPanel.getComponent<Transform>().transform.t, 1f)
        }
    }

    fun getSpatialTask(uuid: Int): Entity? {
        val tools = Query.where { has(ToolComponent.id) }
        for (entity in tools.eval()) {
            val entityUuid = entity.getComponent<ToolComponent>().uuid
            if (uuid == entityUuid) {
                return entity
            }
        }
        return null
    }

    fun OpenHomePanel() {
        // if second fragment is initialized and active, we change to First Fragment
        try {
            if (SecondFragment.instance.get()?.isCurrentlyVisible() == true) {
                SecondFragment.instance.get()?.moveToFirstFragment()
            } else {
                FirstFragment.instance.get()?.refreshProjects()
            }
        } catch (e: UninitializedPropertyAccessException) {}
        placeInFront(homePanel)
        homePanel.setComponent(Visible(true))
        ambientSoundPlayer.stop()

        newProject()
    }

    fun SwitchAudio() {
        if (speakerIsOn) {
            stopAmbientSound()
        } else {
            playAmbientSound()
        }
    }

    fun OpenSettingsPanel() {
        // if first fragment is initialized and active, we change to Second Fragment
        try {
            if (FirstFragment.instance.get()?.isCurrentlyVisible() == true) {
                FirstFragment.instance.get()?.moveToSecondFragment()
            }
        } catch (e: UninitializedPropertyAccessException) {}
        placeInFront(homePanel)
        homePanel.setComponent(Visible(true))
    }

    fun OpenWebView() {
        WebView()
    }

    fun ShowAIPanel(state: Boolean = true) {
        if (state) placeInFront(aiExchangePanel, bigPanel = true)
        aiExchangePanel.setComponent(Visible(state))
        DB.updateUniqueAsset(aiExchangePanel.getComponent<UniqueAssetComponent>().uuid, state = state)
    }

    fun ShowTasksPanel(state: Boolean = true) {
        if (state) placeInFront(tasksPanel, bigPanel = true)
        tasksPanel.setComponent(Visible(state))
        DB.updateUniqueAsset(tasksPanel.getComponent<UniqueAssetComponent>().uuid, state = state)
    }

    fun CreateStickyNote(index: Int) {
        StickyNote(
            message = "",
            color = StickyColor.entries[index])
        closeSubPanels()
    }

    fun CreateLabelTool(index: Int) {
        // Create Label tool
        Tool(
            type = AssetType.LABEL,
            source = labels[index].toString(),
            size = 0.065f,
            deleteButtonHeight = 0.05f)
        closeSubPanels()
    }

    fun CreateArrowTool(index: Int) {
        // Create Arrow tool
        Tool(
            type = AssetType.ARROW,
            source = arrows[index].toString(),
            size = 0.1f,
            deleteButtonHeight = getDeleteButtonHeight(AssetType.ARROW, index))
        closeSubPanels()
    }

    fun CreateBoard(index: Int) {
        // Create Board
        Tool(
            type = AssetType.BOARD,
            source = boards[index].toString(),
            size = getAssetSize(AssetType.BOARD, index),
            deleteButtonHeight = getDeleteButtonHeight(AssetType.BOARD, index))
        closeSubPanels()
    }

    fun CreateShape(index: Int) {
        // Create Shape tool (2D or 3D)
        val type = if (index % 2 == 0) AssetType.SHAPE_2D else AssetType.SHAPE_3D
        val deleteHeight = if (index % 2 == 0) 0.08f else 0.12f
        Tool(
            type = type,
            source = shapes[index].toString(),
            size = getAssetSize(type, index),
            deleteButtonHeight = deleteHeight)
        closeSubPanels()
    }

    fun CreateSticker(index: Int) {
        // Create Sticker tool
        Tool(type = AssetType.STICKER, source = stickers[index].toString(), size = 0.04f)
        closeSubPanels()
    }

    fun CreateTimer(index: Int) {
        // Create Timer
        Timer(scene = scene, ctx = spatialContext, (index + 1) * 5)
        closeSubPanels()
    }

    // Panels creation
    private fun createHomePanel() {
        homePanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.HomePanel,
                Transform(Pose(Vector3(0f))),
                Grabbable(true, GrabbableType.FACE),
                Visible(false)
            )
    }

    fun createToolbarPanel() {
        toolbarPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.Toolbar,
                Transform(Pose(Vector3(0f))),
                Grabbable(true, GrabbableType.FACE),
                Visible(false))
    }

    fun createStickySubPanel() {
        stickySubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.StickySubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel))

        subpanels.add(stickySubPanel)
    }

    fun createLabelSubPanel() {
        labelSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.LabelSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel))

        subpanels.add(labelSubPanel)
    }

    fun createArrowSubPanel() {
        arrowSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.ArrowSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel))

        subpanels.add(arrowSubPanel)
    }

    fun createBoardSubPanel() {
        boardSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.BoardSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel))

        subpanels.add(boardSubPanel)
    }

    fun createShapeSubPanel() {
        shapeSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.ShapesSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel))

        subpanels.add(shapeSubPanel)
    }

    fun createStickerSubPanel() {
        stickerSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.StickerSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel))

        subpanels.add(stickerSubPanel)
    }

    fun createTimerSubPanel() {
        timerSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.TimerSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel))

        subpanels.add(timerSubPanel)
    }

    fun openSubPanel(panel: Entity) {
        val isVisible = panel.getComponent<Visible>().isVisible
        closeSubPanels()
        if (!isVisible) {
            panel.setComponent(Visible(true))
        }
    }

    fun closeSubPanels() {
        for (i in 0..subpanels.count() - 1) {
            subpanels[i].setComponent(Visible(false))
        }
    }

    fun createTasksPanel() {
        tasksPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.TasksPanel,
                Transform(Pose(Vector3(0f))),
                Grabbable(true, GrabbableType.FACE),
                Visible(false),
                // Empty UUID since the asset is not linked with any project for now
                UniqueAssetComponent(type = AssetType.TASKS_PANEL))
    }

    // We create an AIExchangePanel chat to communicate with an AI assistant
    fun createAIExchangePanel() {
        aiExchangePanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.AIPanel,
                Transform(Pose(Vector3(0f))),
                Grabbable(true, GrabbableType.FACE),
                Visible(false),
                // Empty UUID since the asset is not linked with any project for now
                UniqueAssetComponent(type = AssetType.AI_PANEL))
    }

    // This function sends the questions of the user to the AI. More info in AIUtils.kt
    // and creates the corresponding messages in the chat panel
    fun askToAI(question: String, onComplete: () -> (Unit)) {
        var response = ""
        GlobalScope.launch(Dispatchers.IO) {
            val result = AIUtils.askQuestion(question)
            withContext(Dispatchers.Main) {
                if (result.success && result.data != null) {
                    response = result.data.answer
                } else if (result.errmsg != null) {
                    response = "Error: " + result.errmsg
                } else {
                    response = "Error: Empty response"
                }
                lastAIResponse = response
                onComplete()
            }
        }
    }

    // This function summarizes the las response of the AI
    // in order to create a Sticky Note with that information
    fun summarize(message: String) {
        var response = ""
        GlobalScope.launch(Dispatchers.IO) {
            val result = AIUtils.summarizeText(message)
            withContext(Dispatchers.Main) {
                if (result.success && result.data != null) {
                    response = result.data.summary
                } else if (result.errmsg != null) {
                    response = "Error: " + result.errmsg
                } else {
                    response = "Error: Empty response"
                }
                StickyNote(
                    message = response, color = StickyColor.Purple)
            }
        }
    }

//    // Chats from previous projects are cleaned when a new project has been loaded
//    fun cleanChats() {
//        lateinit var aiPanel: PanelSceneObject
//        systemManager.findSystem<SceneObjectSystem>().getSceneObject(aiExchangePanel)?.thenAccept {
//                sceneObject ->
//            aiPanel = sceneObject as PanelSceneObject
//        }
//
//        val scrollView: TableLayout? =
//            aiPanel.rootView?.findViewById<TableLayout>(R.id.aiScrollViewTable)
//        // Cleaning chats
//        scrollView?.removeAllViews()
//        lastAIResponse = ""
//        systemManager.findSystem<SceneObjectSystem>().getSceneObject(aiExchangePanel)?.thenAccept {
//                sceneObject ->
//            aiPanel = sceneObject as PanelSceneObject
//        }
//        aiPanel.rootView?.findViewById<Button>(R.id.buttonStickyAI)?.isEnabled = false
//    }

    // When an object is selected, the delete button is shown in the specified position
    fun selectElement(ent: Entity) {
        currentObjectSelected = ent
        val billboardOrientationEuler: Vector3 =
            ent.tryGetComponent<IsdkGrabbable>()?.billboardOrientation ?: Vector3(0f, 0f, 0f)
        deleteButton.setComponent(
            Transform(
                Pose(
                    ent.getComponent<ToolComponent>().deleteButtonPosition,
                    Quaternion(
                        billboardOrientationEuler.x,
                        billboardOrientationEuler.y,
                        billboardOrientationEuler.z))))
        deleteButton.setComponent(TransformParent(ent))
        deleteButton.setComponent(Visible(true))
    }

    // Delete object depending on the type
    fun deleteObject(
        entity: Entity?,
        deleteFromDB: Boolean = false,
        cleaningProject: Boolean = false
    ) {
        val position = entity!!.getComponent<Transform>().transform.t

        if (currentObjectSelected != null && currentObjectSelected!!.equals(entity)) {
            currentObjectSelected = null
        }

        // deleteButton is detached from the object
        deleteButton.setComponent(TransformParent())
        deleteButton.setComponent(Visible(false))

        // Checking type of object to know how to delete it.
        val asset = entity.getComponent<ToolComponent>()
        // Check if we have to delete it from database as well or we are just cleaning the space to show
        // a different project
        if (deleteFromDB && asset.type != AssetType.TASK && asset.type != AssetType.TIMER) {
            when (asset.type) {
                AssetType.STICKY_NOTE -> {
                    DB.deleteSticky(asset.uuid)
                }
                AssetType.BOARD -> {
                    var children = getChildren(entity)
                    for (i in children.count() - 1 downTo 0) {
                        deleteObject(children[i], true)
                    }
                    DB.deleteToolAsset(asset.uuid)
                }
                else -> {
                    DB.deleteToolAsset(asset.uuid)
                }
            }
        } else if (asset.type == AssetType.TASK && !cleaningProject) {
            DB.updateTaskData(asset.uuid, detach = 0)
            focusViewModel.refreshTasksPanel()

            // In case of some object, we need to delete its children too
        } else if (asset.type == AssetType.TIMER || asset.type == AssetType.BOARD) {
            var children = getChildren(entity)
            for (i in children.count() - 1 downTo 0) {
                children[i].destroy()
            }
        }
        entity.destroy()
        if (!cleaningProject) scene.playSound(deleteSound, position, 1f)
    }

    class FocusViewModel : ViewModel() {
        private val _tasksListsHasChanged = MutableStateFlow<Int>(0)
        val tasksListsHasChanged = _tasksListsHasChanged.asStateFlow()

        fun refreshTasksPanel() {
            _tasksListsHasChanged.value++
        }

        private val _currentProjectUuid = MutableStateFlow<Int?>(null)
        val currentProjectUuid = _currentProjectUuid.asStateFlow()

        fun updateCurrentProjectUuid(uuid: Int?) {
            _currentProjectUuid.value = uuid
            refreshTasksPanel()
        }

        private val _currentTaskUuid = MutableStateFlow<Int?>(null)
        val currentTaskUuid = _currentTaskUuid.asStateFlow()

        fun setCurrentTaskUuid(uuid: Int?) {
            _currentTaskUuid.value = uuid
        }

        private val _currentTaskUpdated = MutableStateFlow<Int>(0)
        val currentTaskUpdated = _currentTaskUpdated.asStateFlow()

        fun updateCurrentSpatialTask() {
            _currentTaskUpdated.value++
        }

        private val _speakerIsOn = MutableStateFlow<Boolean>(false)
        val speakerIsOn = _speakerIsOn.asStateFlow()

        fun setSpeakerIsOn(isOn: Boolean) {
            _speakerIsOn.value = isOn
        }
    }

    companion object {
        lateinit var instance: WeakReference<ImmersiveActivity>

        fun getInstance(): ImmersiveActivity? =
            if (::instance.isInitialized) instance.get() else null
    }
}
