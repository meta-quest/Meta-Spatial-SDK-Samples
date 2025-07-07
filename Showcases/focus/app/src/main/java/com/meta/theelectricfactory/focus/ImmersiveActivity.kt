// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import java.lang.ref.WeakReference
import kotlinx.coroutines.*

import androidx.compose.runtime.Composable
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.composePanel
import com.meta.spatial.runtime.LayerConfig

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
  var toolButtons: MutableList<ImageButton?> = mutableListOf()
  lateinit var audioButton: ImageButton

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
        registerHomePanel(),
        registerToolbarPanel(),
        registerTasksPanel(),
        registerAIExchangePanel(),
        registerStickySubPanel(),
        registerLabelSubPanel(),
        registerArrowSubPanel(),
        registerBoardSubPanel(),
        registerShapesSubPanel(),
        registerStickerSubPanel(),
        registerTimerSubPanel())
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
    if (appStarted) cleanChats()
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
          if (cursor.getInt(cursor.getColumnIndex(DatabaseManager.PROJECT_MR)) == 1) true else false
      val env = cursor.getInt(cursor.getColumnIndex(DatabaseManager.PROJECT_ENVIRONMENT))
      currentProject = Project(id, projectName, mr, env)
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
            if (speakerIsOn) playAmbientSound() else stopAmbientSound()
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

    audioButton.setImageResource(if (speakerIsOn) R.drawable.sound else R.drawable.sound_off)

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
              scene,
              spatialContext,
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
              deleteButtonHeight = deleteHeight)
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
            scene,
            spatialContext,
            uuid,
            message,
            color!!,
            Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)))

        stickiesCursor.moveToNext()
      }
    }
    stickiesCursor.close()

    // Clean task from previous projects and load corresponding ones
    cleanAndLoadTasks()
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
          tasksPanel.getComponent<Transform>().transform)
      DB.createUniqueAsset(
          aiPanelUUID,
          project.uuid,
          AssetType.AI_PANEL,
          true,
          aiExchangePanel.getComponent<Transform>().transform)
      DB.createUniqueAsset(
          clockUUID, project.uuid, AssetType.CLOCK, true, clock.getComponent<Transform>().transform)
      DB.createUniqueAsset(
          speakerUUID,
          project.uuid,
          AssetType.SPEAKER,
          true,
          speaker.getComponent<Transform>().transform)

      // Initial Web View tool created as an example
      WebView(scene, spatialContext)
      // Clean tasks from previous projects, in case there are
      cleanAndLoadTasks()
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
            -Vector3(1.0f, 3.0f, 2.0f),
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
            UniqueAssetComponent(type = AssetType.SPEAKER))

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
            TransformParent(speaker))

    // Add listener to detect when user is selecting the object and stop or play sound
    addOnSelectListener(
        speaker,
        fun() {
          if (speakerIsOn) {
            stopAmbientSound()
          } else {
            playAmbientSound()
          }
        })
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
        })

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
            UniqueAssetComponent(type = AssetType.CLOCK))

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
    audioButton.setImageResource(R.drawable.sound)
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
    audioButton.setImageResource(R.drawable.sound_off)
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

  // PANELS //
  ///////////

  // Panels registration
  private fun registerHomePanel(): PanelRegistration {
    val _width = 0.58f
    val _height = 0.41f
    val _widthInPx = 1024
    val _dpi = 130

    val panelRegistration =
        PanelRegistration(R.layout.activity_main) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = _widthInPx
            layoutHeightInPx = (_widthInPx * (_height / _width)).toInt()
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          activityClass = MainActivity::class.java
        }
    return panelRegistration
  }

  private fun registerToolbarPanel(): PanelRegistration {
    val _width = 0.65f
    val _height = 0.065f
    val _widthInPx = 1100
    val _dpi = 105

    val panelRegistration =
        PanelRegistration(R.layout.toolbar_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = _widthInPx
            layoutHeightInPx = (_widthInPx * (_height / _width)).toInt()
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            val buttonHome: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonHome)
            buttonHome?.setOnClickListener {

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

            audioButton = rootView!!.findViewById<ImageButton>(R.id.buttonAudio)
            audioButton.setOnClickListener {
              if (speakerIsOn) {
                stopAmbientSound()
              } else {
                playAmbientSound()
              }
            }

            val buttonSettings: ImageButton? =
                rootView?.findViewById<ImageButton>(R.id.buttonSettings)
            buttonSettings?.setOnClickListener {

              // if first fragment is initialized and active, we change to Second Fragment
              try {
                if (FirstFragment.instance.get()?.isCurrentlyVisible() == true) {
                  FirstFragment.instance.get()?.moveToSecondFragment()
                }
              } catch (e: UninitializedPropertyAccessException) {}
              placeInFront(homePanel)
              homePanel.setComponent(Visible(true))
            }

            val buttonTasks: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonTasks)
            buttonTasks?.setOnClickListener {
              placeInFront(tasksPanel, bigPanel = true)
              tasksPanel.setComponent(Visible(true))
              DB.updateUniqueAsset(
                  tasksPanel.getComponent<UniqueAssetComponent>().uuid, state = true)
            }

            val buttonWebView: ImageButton? =
                rootView?.findViewById<ImageButton>(R.id.buttonWebView)
            buttonWebView?.setOnClickListener { WebView(scene, spatialContext) }

            val buttonAI: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonAI)
            if (AIenabled) {
              buttonAI?.setOnClickListener {
                placeInFront(aiExchangePanel, bigPanel = true)
                aiExchangePanel.setComponent(Visible(true))
                DB.updateUniqueAsset(
                    aiExchangePanel.getComponent<UniqueAssetComponent>().uuid, state = true)
              }
            } else {
              val parent = rootView?.findViewById<LinearLayout>(R.id.toolsContainer)
              parent?.removeView(buttonAI)
              val params = parent?.layoutParams as LinearLayout.LayoutParams
              params.weight = 1.15f
            }

            val buttonSticky: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonSticky)
            buttonSticky?.setOnClickListener { openSubPanel(stickySubPanel) }
            toolButtons.add(buttonSticky)

            val buttonLabel: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonLabel)
            buttonLabel?.setOnClickListener { openSubPanel(labelSubPanel) }
            toolButtons.add(buttonLabel)

            val buttonArrow: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonArrow)
            buttonArrow?.setOnClickListener { openSubPanel(arrowSubPanel) }
            toolButtons.add(buttonArrow)

            val buttonBoard: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonBoard)
            buttonBoard?.setOnClickListener { openSubPanel(boardSubPanel) }
            toolButtons.add(buttonBoard)

            val buttonShape: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonShape)
            buttonShape?.setOnClickListener { openSubPanel(shapeSubPanel) }
            toolButtons.add(buttonShape)

            val buttonSticker: ImageButton? =
                rootView?.findViewById<ImageButton>(R.id.buttonSticker)
            buttonSticker?.setOnClickListener { openSubPanel(stickerSubPanel) }
            toolButtons.add(buttonSticker)

            val buttonTimer: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonTimer)
            buttonTimer?.setOnClickListener { openSubPanel(timerSubPanel) }
            toolButtons.add(buttonTimer)
          }
        }
    return panelRegistration
  }

  private fun registerStickySubPanel(): PanelRegistration {
    val _width = 0.26f
    val _height = 0.042f
    val _heigthInPx = 100
    val _dpi = 140

    val panelRegistration =
        PanelRegistration(R.layout.sticky_sub_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = (_heigthInPx / (_height / _width)).toInt()
            layoutHeightInPx = _heigthInPx
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            for (i in 0..stickyButtons.count() - 1) {
              val buttonSticky: ImageButton? = rootView?.findViewById<ImageButton>(stickyButtons[i])
              buttonSticky?.setOnClickListener {
                // Create sticky note
                StickyNote(
                    scene = scene,
                    ctx = spatialContext,
                    message = "",
                    color = StickyColor.entries[i])
                closeSubPanels()
              }
            }
          }
        }
    return panelRegistration
  }

  private fun registerLabelSubPanel(): PanelRegistration {
    val _width = 0.47f
    val _height = 0.042f
    val _heigthInPx = 100
    val _dpi = 140

    val panelRegistration =
        PanelRegistration(R.layout.label_sub_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = (_heigthInPx / (_height / _width)).toInt()
            layoutHeightInPx = _heigthInPx
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            for (i in 0..labelButtons.count() - 1) {
              val buttonLabel: ImageButton? = rootView?.findViewById<ImageButton>(labelButtons[i])
              buttonLabel?.setOnClickListener {
                // Create Label tool
                Tool(
                    type = AssetType.LABEL,
                    source = labels[i].toString(),
                    size = 0.065f,
                    deleteButtonHeight = 0.05f)
                closeSubPanels()
              }
            }
          }
        }
    return panelRegistration
  }

  private fun registerArrowSubPanel(): PanelRegistration {
    val _width = 0.28f
    val _height = 0.042f
    val _heigthInPx = 100
    val _dpi = 140

    val panelRegistration =
        PanelRegistration(R.layout.arrow_sub_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = (_heigthInPx / (_height / _width)).toInt()
            layoutHeightInPx = _heigthInPx
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            for (i in 0..arrowButtons.count() - 1) {
              val buttonArrow: ImageButton? = rootView?.findViewById<ImageButton>(arrowButtons[i])
              buttonArrow?.setOnClickListener {
                // Create Arrow tool
                Tool(
                    type = AssetType.ARROW,
                    source = arrows[i].toString(),
                    size = 0.1f,
                    deleteButtonHeight = getDeleteButtonHeight(AssetType.ARROW, i))
                closeSubPanels()
              }
            }
          }
        }
    return panelRegistration
  }

  private fun registerBoardSubPanel(): PanelRegistration {
    val _width = 0.21f
    val _height = 0.042f
    val _heigthInPx = 100
    val _dpi = 140

    val panelRegistration =
        PanelRegistration(R.layout.board_sub_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = (_heigthInPx / (_height / _width)).toInt()
            layoutHeightInPx = _heigthInPx
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            for (i in 0..boardButtons.count() - 1) {
              val buttonBoard: ImageButton? = rootView?.findViewById<ImageButton>(boardButtons[i])
              buttonBoard?.setOnClickListener {
                // Create Board
                Tool(
                    type = AssetType.BOARD,
                    source = boards[i].toString(),
                    size = getAssetSize(AssetType.BOARD, i),
                    deleteButtonHeight = getDeleteButtonHeight(AssetType.BOARD, i))
                closeSubPanels()
              }
            }
          }
        }
    return panelRegistration
  }

  private fun registerShapesSubPanel(): PanelRegistration {
    val _width = 0.28f
    val _height = 0.042f
    val _heigthInPx = 100
    val _dpi = 140

    val panelRegistration =
        PanelRegistration(R.layout.shape_sub_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = (_heigthInPx / (_height / _width)).toInt()
            layoutHeightInPx = _heigthInPx
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            for (i in 0..shapesButtons.count() - 1) {
              val buttonShape: ImageButton? = rootView?.findViewById<ImageButton>(shapesButtons[i])
              buttonShape?.setOnClickListener {
                // Create Shape tool (2D or 3D)
                val type = if (i % 2 == 0) AssetType.SHAPE_2D else AssetType.SHAPE_3D
                val deleteHeight = if (i % 2 == 0) 0.08f else 0.12f
                Tool(
                    type = type,
                    source = shapes[i].toString(),
                    size = getAssetSize(type, i),
                    deleteButtonHeight = deleteHeight)
                closeSubPanels()
              }
            }
          }
        }
    return panelRegistration
  }

  private fun registerStickerSubPanel(): PanelRegistration {
    val _width = 0.29f
    val _height = 0.042f
    val _heigthInPx = 100
    val _dpi = 140

    val panelRegistration =
        PanelRegistration(R.layout.sticker_sub_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = (_heigthInPx / (_height / _width)).toInt()
            layoutHeightInPx = _heigthInPx
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            for (i in 0..stickerButtons.count() - 1) {
              val buttonSticker: ImageButton? =
                  rootView?.findViewById<ImageButton>(stickerButtons[i])
              buttonSticker?.setOnClickListener {
                // Create Sticker tool
                Tool(type = AssetType.STICKER, source = stickers[i].toString(), size = 0.04f)
                closeSubPanels()
              }
            }
          }
        }
    return panelRegistration
  }

  private fun registerTimerSubPanel(): PanelRegistration {
    val _width = 0.38f
    val _height = 0.042f
    val _heigthInPx = 100
    val _dpi = 140

    val panelRegistration =
        PanelRegistration(R.layout.timer_sub_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = (_heigthInPx / (_height / _width)).toInt()
            layoutHeightInPx = _heigthInPx
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            for (i in 0..timerButtons.count() - 1) {
              val buttonTimer: ImageButton? = rootView?.findViewById<ImageButton>(timerButtons[i])
              buttonTimer?.setOnClickListener {
                // Create Timer
                Timer(scene = scene, ctx = spatialContext, (i + 1) * 5)
                closeSubPanels()
              }
            }
          }
        }
    return panelRegistration
  }

  private fun registerTasksPanel(): PanelRegistration {
    val _width = 0.275f
    val _height = 0.5f
    val _widthInPx = 1120
    val _dpi = 270

    val panelRegistration =
        PanelRegistration(R.layout.tasks_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = _widthInPx
            layoutHeightInPx = (_widthInPx * (_height / _width)).toInt()
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            val buttonClose: ImageButton? =
                rootView?.findViewById<ImageButton>(R.id.closeTasksButton)
            buttonClose?.setOnClickListener {
              tasksPanel.setComponent(Visible(false))
              DB.updateUniqueAsset(
                  tasksPanel.getComponent<UniqueAssetComponent>().uuid, state = false)
            }
          }
        }
    return panelRegistration
  }

  private fun registerAIExchangePanel(): PanelRegistration {
    val _width = 0.3f
    val _height = 0.5f
    val _widthInPx = 1200
    val _dpi = 288

    val panelRegistration =
        PanelRegistration(R.layout.ai_exchange_panel) {
          config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = _width
            height = _height
            layoutWidthInPx = _widthInPx
            layoutHeightInPx = (_widthInPx * (_height / _width)).toInt()
            layoutDpi = _dpi
            includeGlass = false
            enableLayer = true
            enableTransparent = true
          }
          panel {
            val buttonClose: ImageButton? = rootView?.findViewById<ImageButton>(R.id.closeAIButton)
            buttonClose?.setOnClickListener {
              aiExchangePanel.setComponent(Visible(false))
              DB.updateUniqueAsset(
                  aiExchangePanel.getComponent<UniqueAssetComponent>().uuid, state = false)
            }

            val textPrompt: EditText? = rootView?.findViewById<EditText>(R.id.textPrompt)
            val buttonStickyAI: Button? = rootView?.findViewById<Button>(R.id.buttonStickyAI)
            buttonStickyAI?.setOnClickListener {
              // Summarized sticky note can be created with the last response of the AI assistant
              buttonStickyAI.isEnabled = false
              summarize(lastAIResponse)
            }
            buttonStickyAI?.isEnabled = false

            // EditText listener added to detect when the user finished asking a question in the
            // chat
            fun send() {
              val question = textPrompt?.text.toString()
              if (question == "") return
              createMessage(question, false)
              askToAI(question, buttonStickyAI!!, this)
              textPrompt?.setText("")
              activeLoadingState(true, this)
            }
            addEditTextListeners(textPrompt, ::send)

            val buttonSend: ImageButton? = rootView?.findViewById<ImageButton>(R.id.buttonSend)
            buttonSend?.setOnClickListener { send() }

            activeLoadingState(false, this)

            // Add disclaimer text with AI information
            val spannableString =
                SpannableString(
                    "This application uses generative AI to respond to queries, and those responses may be inaccurate or inappropriate. Learn More. \n\n Your queries and the generative AI responses are not retained by Meta.")

            // Create web view showing privacy police information for META AI
            val clickableSpan =
                object : ClickableSpan() {
                  override fun onClick(widget: View) {
                    WebView(scene, spatialContext, "https://www.facebook.com/privacy/guide/genai/")
                  }
                }

            // Set the span for the "Learn More" part
            spannableString.setSpan(clickableSpan, 115, 125, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Set the text to the TextView
            val disclaimerText = rootView?.findViewById<TextView>(R.id.disclaimer)
            disclaimerText?.text = spannableString
            disclaimerText?.movementMethod = LinkMovementMethod.getInstance()
          }
        }
    return panelRegistration
  }

  // Panels creation
  private fun createHomePanel() {
    homePanel =
        Entity.createPanelEntity(
            R.layout.activity_main,
            Transform(Pose(Vector3(0f))),
            Grabbable(true, GrabbableType.FACE),
            Visible(false))
  }

  fun createToolbarPanel() {
    toolbarPanel =
        Entity.createPanelEntity(
            R.layout.toolbar_panel,
            Transform(Pose(Vector3(0f))),
            Grabbable(true, GrabbableType.FACE),
            Visible(false))
  }

  fun createStickySubPanel() {
    stickySubPanel =
        Entity.createPanelEntity(
            R.layout.sticky_sub_panel,
            Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
            Visible(false),
            TransformParent(toolbarPanel))

    subpanels.add(stickySubPanel)
  }

  fun createLabelSubPanel() {
    labelSubPanel =
        Entity.createPanelEntity(
            R.layout.label_sub_panel,
            Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
            Visible(false),
            TransformParent(toolbarPanel))

    subpanels.add(labelSubPanel)
  }

  fun createArrowSubPanel() {
    arrowSubPanel =
        Entity.createPanelEntity(
            R.layout.arrow_sub_panel,
            Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
            Visible(false),
            TransformParent(toolbarPanel))

    subpanels.add(arrowSubPanel)
  }

  fun createBoardSubPanel() {
    boardSubPanel =
        Entity.createPanelEntity(
            R.layout.board_sub_panel,
            Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
            Visible(false),
            TransformParent(toolbarPanel))

    subpanels.add(boardSubPanel)
  }

  fun createShapeSubPanel() {
    shapeSubPanel =
        Entity.createPanelEntity(
            R.layout.shape_sub_panel,
            Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
            Visible(false),
            TransformParent(toolbarPanel))

    subpanels.add(shapeSubPanel)
  }

  fun createStickerSubPanel() {
    stickerSubPanel =
        Entity.createPanelEntity(
            R.layout.sticker_sub_panel,
            Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
            Visible(false),
            TransformParent(toolbarPanel))

    subpanels.add(stickerSubPanel)
  }

  fun createTimerSubPanel() {
    timerSubPanel =
        Entity.createPanelEntity(
            R.layout.timer_sub_panel,
            Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
            Visible(false),
            TransformParent(toolbarPanel))

    subpanels.add(timerSubPanel)
  }

  fun openSubPanel(panel: Entity) {
    val index = subpanels.indexOf(panel)

    val isVisible = panel.getComponent<Visible>().isVisible
    closeSubPanels()
    if (!isVisible) {
      panel.setComponent(Visible(true))
      toolButtons[index]?.setImageResource(pressedToolResources[index])
    }
  }

  fun closeSubPanels() {
    for (i in 0..subpanels.count() - 1) {
      subpanels[i].setComponent(Visible(false))
      if (toolButtons.count() - 1 >= i) toolButtons[i]?.setImageResource(defaultToolResources[i])
    }
  }

  fun createTasksPanel() {
    tasksPanel =
        Entity.createPanelEntity(
            R.layout.tasks_panel,
            Transform(Pose(Vector3(0f))),
            Grabbable(true, GrabbableType.FACE),
            Visible(false),
            // Empty UUID since the asset is not linked with any project for now
            UniqueAssetComponent(type = AssetType.TASKS_PANEL))
  }

  // Clean TasksPanel and load corresponding tasks
  @SuppressLint("Range")
  fun cleanAndLoadTasks(fromSpatialTask: Boolean = false, createSpatial: Boolean = true) {
    // Getting TasksPanel Scene Object
    lateinit var _tasksPanel: PanelSceneObject
    systemManager.findSystem<SceneObjectSystem>().getSceneObject(tasksPanel)?.thenAccept {
        sceneObject ->
      _tasksPanel = sceneObject as PanelSceneObject
    }

    templateState = 0
    templatePriority = 0

    // Giving functionality to template task, in order to create new ones

    val templateLabel1: ImageButton? = _tasksPanel.rootView?.findViewById(R.id.templateLabel1)
    templateLabel1?.setOnClickListener {
      templateState = if (templateState == 2) 0 else (templateState + 1)
      templateLabel1.setImageResource(stateLabels[templateState])
      templateLabel1.layoutParams.width =
          resources
              .getDimension(
                  if (templateState == 1) R.dimen.max_label1_size else R.dimen.min_label1_size)
              .toInt()
    }

    val templateLabel2: ImageButton? = _tasksPanel.rootView?.findViewById(R.id.templateLabel2)
    templateLabel2?.setOnClickListener {
      templatePriority = if (templatePriority == 2) 0 else (templatePriority + 1)
      templateLabel2.setImageResource(priorityLabels[templatePriority])
    }

    val templateTitleText: EditText? =
        _tasksPanel.rootView?.findViewById(R.id.templateEditTaskTitle)
    val templateBodyText: EditText? = _tasksPanel.rootView?.findViewById(R.id.templateEditTaskBody)

    val buttonNewTask: Button? = _tasksPanel.rootView?.findViewById<Button>(R.id.buttonCreateTask)
    buttonNewTask?.setOnClickListener {
      createUITask(
          _tasksPanel,
          null,
          templateTitleText?.text.toString(),
          templateBodyText?.text.toString(),
          templateState,
          templatePriority)

      // reset template task values
      templateTitleText?.setText("")
      templateBodyText?.setText("")
      templateState = 0
      templatePriority = 0

      templateLabel1?.setImageResource(stateLabels[0])
      templateLabel1?.layoutParams?.width = resources.getDimension(R.dimen.min_label1_size).toInt()
      templateLabel2?.setImageResource(priorityLabels[0])

      buttonNewTask.isEnabled = false
    }
    buttonNewTask?.isEnabled = false

    fun onComplete() {
      if (templateTitleText?.text.toString() != "") {
        buttonNewTask?.isEnabled = true
      } else {
        buttonNewTask?.isEnabled = false
      }
    }
    addEditTextListeners(templateTitleText, ::onComplete)
    addEditTextListeners(templateBodyText, ::onComplete)

    // Clean previous tasks from scrollview
    val tableScrollView: TableLayout? =
        _tasksPanel.rootView?.findViewById<TableLayout>(R.id.scrollViewTable)
    tableScrollView?.removeAllViews()

    // Load project corresponding tasks
    val tasksCursor = DB.getTasks(currentProject?.uuid)
    if (tasksCursor.moveToFirst()) {
      while (!tasksCursor.isAfterLast) {
        val uuid = tasksCursor.getInt(tasksCursor.getColumnIndex(DatabaseManager.TASK_UUID))
        val title = tasksCursor.getString(tasksCursor.getColumnIndex(DatabaseManager.TASK_TITLE))
        val body = tasksCursor.getString(tasksCursor.getColumnIndex(DatabaseManager.TASK_BODY))
        val state = tasksCursor.getInt(tasksCursor.getColumnIndex(DatabaseManager.TASK_STATE))
        val priority = tasksCursor.getInt(tasksCursor.getColumnIndex(DatabaseManager.TASK_PRIORITY))
        val detach = tasksCursor.getInt(tasksCursor.getColumnIndex(DatabaseManager.TASK_DETACH))

        val posX = tasksCursor.getFloat(tasksCursor.getColumnIndex(DatabaseManager.TASK_POSITION_X))
        val posY = tasksCursor.getFloat(tasksCursor.getColumnIndex(DatabaseManager.TASK_POSITION_Y))
        val posZ = tasksCursor.getFloat(tasksCursor.getColumnIndex(DatabaseManager.TASK_POSITION_Z))

        val rotW = tasksCursor.getFloat(tasksCursor.getColumnIndex(DatabaseManager.TASK_ROTATION_W))
        val rotX = tasksCursor.getFloat(tasksCursor.getColumnIndex(DatabaseManager.TASK_ROTATION_X))
        val rotY = tasksCursor.getFloat(tasksCursor.getColumnIndex(DatabaseManager.TASK_ROTATION_Y))
        val rotZ = tasksCursor.getFloat(tasksCursor.getColumnIndex(DatabaseManager.TASK_ROTATION_Z))

        // Create task in panel (different from spatial task)
        createUITask(
            panel = _tasksPanel,
            uuid = uuid,
            taskTitle = title,
            taskBody = body,
            state = state,
            priority = priority,
            detach = detach,
            pose = Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)),
            fromSpatialTask = fromSpatialTask,
            createSpatial = createSpatial)
        tasksCursor.moveToNext()
      }
    }
    tasksCursor.close()
  }

  // Create task in panel (different from spatial task)
  @SuppressLint("Range")
  fun createUITask(
      panel: PanelSceneObject?,
      uuid: Int?,
      taskTitle: String,
      taskBody: String,
      state: Int,
      priority: Int,
      detach: Int? = null,
      pose: Pose? = null,
      fromSpatialTask: Boolean = false,
      createSpatial: Boolean = true
  ): View {
    // We get the scrollview from the TasksPanel
    val scrollTable: TableLayout? = panel?.rootView?.findViewById<TableLayout>(R.id.scrollViewTable)
    // We use a layout template (task_layout) to create a new task
    val newTask: View =
        LayoutInflater.from(this.spatialContext).inflate(R.layout.task_layout, scrollTable, false)
    val parentLayout = newTask.findViewById<ConstraintLayout>(R.id.taskLayout)

    val isNewTask = uuid == null
    var _uuid = uuid

    if (isNewTask) _uuid = getNewUUID()

    val titleTextInput: EditText? = newTask.findViewById(R.id.editTaskTitle)
    val bodyTextInput: EditText? = newTask.findViewById(R.id.editTaskBody)
    val buttonLabel1: ImageButton = newTask.findViewById(R.id.label1)
    val buttonLabel2: ImageButton = newTask.findViewById(R.id.label2)

    parentLayout.id = _uuid!!
    titleTextInput?.setText(taskTitle)
    bodyTextInput?.setText(taskBody)
    buttonLabel1.setImageResource(stateLabels[state])
    buttonLabel1.layoutParams.width =
        resources
            .getDimension(if (state == 1) R.dimen.max_label1_size else R.dimen.min_label1_size)
            .toInt()
    buttonLabel2.setImageResource(priorityLabels[priority])

    // If tasks is in Done "state", we strikethrough text
    strikeThroughText(titleTextInput!!, state)
    strikeThroughText(bodyTextInput!!, state)

    // Save new task in database
    if (isNewTask) {
      DB.createTask(
          _uuid, currentProject?.uuid, taskTitle, taskBody, templateState, templatePriority)

      // scroll to the bottom of the scrollview
      val scrollParent: ScrollView? = panel?.rootView?.findViewById(R.id.scrollView)
      scrollParent
          ?.viewTreeObserver
          ?.addOnGlobalLayoutListener(
              object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                  scrollParent.viewTreeObserver.removeOnGlobalLayoutListener(this)
                  scrollParent.postDelayed(
                      { scrollParent.fullScroll(ScrollView.FOCUS_DOWN) },
                      100) // Delay to ensure the scrolling happens after layout
                }
              })
      // if is not a new task, we check if a corresponding Spatial Task should be created
    } else {
      if (detach == 1) {
        val parent = parentLayout?.findViewById<ConstraintLayout>(R.id.taskContainer)
        val detachButton = newTask.findViewById<ImageButton>(R.id.buttonDetach)
        parent?.removeView(detachButton)

        // avoid duplicated spatial task
        if (!fromSpatialTask && createSpatial) {
          SpatialTask(scene, spatialContext, uuid!!, false, pose!!)
        }
      }
    }

    // Tapping on Labels change the labels of tasks
    buttonLabel1.setOnClickListener {
      val taskCursor = DB.getTaskData(_uuid)
      if (taskCursor.moveToFirst()) {
        val dbState = taskCursor.getInt(taskCursor.getColumnIndex(DatabaseManager.TASK_STATE))
        val newState = if (dbState == 2) 0 else (dbState + 1)

        buttonLabel1.setImageResource(stateLabels[newState])
        buttonLabel1.layoutParams.width =
            resources
                .getDimension(
                    if (newState == 1) R.dimen.max_label1_size else R.dimen.min_label1_size)
                .toInt()
        DB.updateTaskData(_uuid, state = newState)
        updateSpatialTask(_uuid)

        strikeThroughText(titleTextInput, newState)
        strikeThroughText(bodyTextInput, newState)
      }
      taskCursor.close()
    }

    buttonLabel2.setOnClickListener {
      val taskCursor = DB.getTaskData(_uuid)
      if (taskCursor.moveToFirst()) {
        val dbPriority = taskCursor.getInt(taskCursor.getColumnIndex(DatabaseManager.TASK_PRIORITY))
        val newPriority = if (dbPriority == 2) 0 else (dbPriority + 1)

        buttonLabel2.setImageResource(priorityLabels[newPriority])
        DB.updateTaskData(_uuid, priority = newPriority)
        updateSpatialTask(_uuid)
      }
      taskCursor.close()
    }

    // EditText listeners are added to detect when a text is being updated by the user. If so, we
    // should update corresponding spatial tasks and database too
    fun onComplete() {
      DB.updateTaskData(
          _uuid, title = titleTextInput.text.toString(), body = bodyTextInput.text.toString())
      updateSpatialTask(_uuid)
    }
    addEditTextListeners(titleTextInput, ::onComplete)
    addEditTextListeners(bodyTextInput, ::onComplete, true)

    // if there is no spatial task, detach button will be shown
    if (detach == 0 || detach == null) {
      val detachButton: ImageButton = newTask.findViewById<ImageButton>(R.id.buttonDetach)
      detachButton.setOnClickListener {
        // Spatial task is created and saved in database when detach button is pressed
        DB.updateTaskData(_uuid, detach = 1)

        val parent = parentLayout?.findViewById<ConstraintLayout>(R.id.taskContainer)
        parent?.removeView(detachButton)

        SpatialTask(scene, spatialContext, _uuid, true)
      }
    }

    // Delete task button will erase task from database, destroy corresponding spatial task and
    // update TasksPanel
    val buttonDelete: ImageButton = newTask.findViewById(R.id.buttonDeleteTask)
    buttonDelete.setOnClickListener {
      DB.deleteTask(_uuid)
      cleanAndLoadTasks(createSpatial = false)

      // Delete correspondent spatial task if exists
      val tools = Query.where { has(ToolComponent.id) }
      for (entity in tools.eval()) {
        val entityUuid = entity.getComponent<ToolComponent>().uuid
        if (_uuid == entityUuid) {

          // if current object selected is spatial task, we detach delete button first.
          if (currentObjectSelected != null && currentObjectSelected!!.equals(entity)) {
            currentObjectSelected = null
            deleteButton.setComponent(TransformParent())
            deleteButton.setComponent(Visible(false))
          }

          entity.destroy()
          break
        }
      }
      scene.playSound(deleteSound, tasksPanel.getComponent<Transform>().transform.t, 1f)
    }

    // New task is added to scrollview in TasksPanel
    scrollTable?.addView(newTask)
    return newTask
  }

  // We strikeThrough text of tasks that are marked as "Done"
  fun strikeThroughText(text: EditText, state: Int) {
    val spannableString = SpannableString(text.text)
    if (state == 2) {
      spannableString.setSpan(StrikethroughSpan(), 0, text.length(), 0)
    } else {
      val spans = spannableString.getSpans(0, text.length(), StrikethroughSpan::class.java)
      for (span in spans) {
        spannableString.removeSpan(span)
      }
    }
    text.setText(spannableString)
  }

  // Update spatial task in case an UI task has changed in TaskPanel scrollview
  fun updateSpatialTask(uuid: Int) {
    val spatialAssets = Query.where { has(ToolComponent.id) }
    for (entity in spatialAssets.eval()) {
      val ent = entity.getComponent<ToolComponent>()
      if (ent.type == AssetType.TASK && ent.uuid == uuid) {

        systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)?.thenAccept {
            sceneObject ->
          updateTask(uuid, sceneObject as PanelSceneObject)
        }
      }
    }
  }

  // Update task layout
  @SuppressLint("Range")
  fun updateTask(uuid: Int, panel: PanelSceneObject) {
    val taskCursor = DB.getTaskData(uuid)
    if (taskCursor.moveToFirst()) {
      val title = taskCursor.getString(taskCursor.getColumnIndex(DatabaseManager.TASK_TITLE))
      val body = taskCursor.getString(taskCursor.getColumnIndex(DatabaseManager.TASK_BODY))
      val state = taskCursor.getInt(taskCursor.getColumnIndex(DatabaseManager.TASK_STATE))
      val priority = taskCursor.getInt(taskCursor.getColumnIndex(DatabaseManager.TASK_PRIORITY))

      panel.rootView?.findViewById<EditText>(R.id.editTaskTitle)?.setText(title)
      panel.rootView?.findViewById<EditText>(R.id.editTaskBody)?.setText(body)
      panel.rootView?.findViewById<ImageButton>(R.id.label1)?.setImageResource(stateLabels[state])
      panel.rootView?.findViewById<ImageButton>(R.id.label1)?.layoutParams?.width =
          resources
              .getDimension(if (state == 1) R.dimen.max_label1_size else R.dimen.min_label1_size)
              .toInt()
      panel.rootView
          ?.findViewById<ImageButton>(R.id.label2)
          ?.setImageResource(priorityLabels[priority])

      strikeThroughText(panel.rootView?.findViewById(R.id.editTaskTitle)!!, state)
      strikeThroughText(panel.rootView?.findViewById(R.id.editTaskBody)!!, state)
    }
    taskCursor.close()
  }

  // We create an AIExchangePanel chat to communicate with an AI assistant
  fun createAIExchangePanel() {
    aiExchangePanel =
        Entity.createPanelEntity(
            R.layout.ai_exchange_panel,
            Transform(Pose(Vector3(0f))),
            Grabbable(true, GrabbableType.FACE),
            Visible(false),
            // Empty UUID since the asset is not linked with any project for now
            UniqueAssetComponent(type = AssetType.AI_PANEL))
  }

  // Loading state of a button while the AI is processing the request
  fun activeLoadingState(state: Boolean, panel: PanelSceneObject) {
    waitingForAI = state
    val buttonSend: ImageButton? = panel.rootView?.findViewById<ImageButton>(R.id.buttonSend)
    val loadingBackground: ImageView? = panel.rootView?.findViewById(R.id.loadingBackground)
    val loading: ImageView? = panel.rootView?.findViewById(R.id.loading)

    if (state) {
      buttonSend?.isEnabled = false
      loadingBackground?.setBackgroundResource(R.drawable.loading_background)
      loading?.setImageResource(R.drawable.loading)
    } else {
      buttonSend?.isEnabled = true
      loadingBackground?.setBackgroundResource(R.drawable.transparent)
      loading?.setImageResource(R.drawable.transparent)
    }
  }

  // This function sends the questions of the user to the AI. More info in AIUtils.kt
  // and creates the corresponding messages in the chat panel
  fun askToAI(question: String, buttonSticky: Button, panel: PanelSceneObject) {
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
        buttonSticky.isEnabled = true
        createMessage(response, true)
        activeLoadingState(false, panel)
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
            scene = scene, ctx = spatialContext, message = response, color = StickyColor.Purple)
      }
    }
  }

  // Function to create a message to show on the chat panel.
  fun createMessage(message: String, ai: Boolean): View {
    lateinit var aiPanel: PanelSceneObject
    systemManager.findSystem<SceneObjectSystem>().getSceneObject(aiExchangePanel)?.thenAccept {
        sceneObject ->
      aiPanel = sceneObject as PanelSceneObject
    }

    // We get the scrollview from the AIExchangePanel
    val scrollViewTable: TableLayout? =
        aiPanel.rootView?.findViewById<TableLayout>(R.id.aiScrollViewTable)
    // We use a layout template (chat_message_layout) to create a new message
    val newMessage: View =
        LayoutInflater.from(this.spatialContext)
            .inflate(R.layout.chat_message_layout, scrollViewTable, false)

    val textView = newMessage.findViewById<TextView>(R.id.chatMessage)
    textView.text = message

    // Wait until the textview is layout to measure the amount of lines and change it's container
    // size
    textView.viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
          override fun onGlobalLayout() {
            // Remove the listener to prevent repeated calls
            textView.viewTreeObserver.removeOnGlobalLayoutListener(this)

            val lineHeight = resources.getDimension(R.dimen.text_line_height).toInt()
            textView.layoutParams.height = (lineHeight * 2 + (lineHeight * textView.lineCount))
            textView.requestLayout()
          }
        })

    val color =
        ColorStateList.valueOf(
            getColor(if (ai) R.color.aiTextView else R.color.userTextView)) // Example color
    textView.backgroundTintList = color

    // Delete layout avatar depending on message is from AI or User
    val parent = newMessage.findViewById<ConstraintLayout>(R.id.messageContainer)
    val avatar = newMessage.findViewById<ImageView>(if (ai) R.id.userAvatar else R.id.aiAvatar)
    parent?.removeView(avatar)

    // Center message depending on avatar deleted
    val layoutParams = textView.layoutParams as ViewGroup.MarginLayoutParams
    if (ai) {
      layoutParams.marginStart = resources.getDimension(R.dimen.label_height).toInt()
      textView.layoutParams = layoutParams
    } else {
      layoutParams.marginEnd = resources.getDimension(R.dimen.label_height).toInt()
      textView.layoutParams = layoutParams
    }

    // Delete AI disclaimer, in case it exists
    val disclaimerParent = aiPanel.rootView?.findViewById<ConstraintLayout>(R.id.scrollParent)
    val aiDisclaimer: TextView? = aiPanel.rootView?.findViewById(R.id.disclaimer)
    if (aiDisclaimer != null) {
      disclaimerParent?.removeView(aiDisclaimer)
    }

    // Scroll to the bottom
    val scrollView: ScrollView? = aiPanel.rootView?.findViewById(R.id.aiScrollView)
    scrollView
        ?.viewTreeObserver
        ?.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
              override fun onGlobalLayout() {
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                scrollView.postDelayed(
                    { scrollView.fullScroll(ScrollView.FOCUS_DOWN) },
                    100) // Delay to ensure the scrolling happens after layout
              }
            })

    // New message added to the AIExchangePanel
    scrollViewTable?.addView(newMessage)
    return newMessage
  }

  // Chats from previous projects are cleaned when a new project has been loaded
  fun cleanChats() {
    lateinit var aiPanel: PanelSceneObject
    systemManager.findSystem<SceneObjectSystem>().getSceneObject(aiExchangePanel)?.thenAccept {
        sceneObject ->
      aiPanel = sceneObject as PanelSceneObject
    }

    val scrollView: TableLayout? =
        aiPanel.rootView?.findViewById<TableLayout>(R.id.aiScrollViewTable)
    // Cleaning chats
    scrollView?.removeAllViews()
    lastAIResponse = ""
    systemManager.findSystem<SceneObjectSystem>().getSceneObject(aiExchangePanel)?.thenAccept {
        sceneObject ->
      aiPanel = sceneObject as PanelSceneObject
    }
    aiPanel.rootView?.findViewById<Button>(R.id.buttonStickyAI)?.isEnabled = false
  }

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
      cleanAndLoadTasks(fromSpatialTask = true)

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

  companion object {
    lateinit public var instance: WeakReference<ImmersiveActivity>
  }
}
