// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.managers

import android.annotation.SuppressLint
import android.util.Log
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.UniqueAssetComponent
import com.meta.theelectricfactory.focus.data.Project
import com.meta.theelectricfactory.focus.data.StickyColor
import com.meta.theelectricfactory.focus.tools.StickyNote
import com.meta.theelectricfactory.focus.tools.Tool
import com.meta.theelectricfactory.focus.tools.WebView
import com.meta.theelectricfactory.focus.utils.getNewUUID
import com.meta.theelectricfactory.focus.utils.placeInFront
import com.meta.theelectricfactory.focus.viewmodels.FocusViewModel

class ProjectManager {

  var currentProject: Project? = null
  val immA: ImmersiveActivity?
    get() = ImmersiveActivity.getInstance()

  companion object {
    val instance: ProjectManager by lazy { ProjectManager() }
  }

  fun newProject() {
    // New project variables reset
    immA?.currentEnvironment = 0

    currentProject = null
    immA?.currentObjectSelected = null
    immA?.passthroughEnabled = false
    AudioManager.instance.audioIsOn = false
    AIManager.instance.lastAIResponse = ""
    AIManager.instance.waitingForAI = false
    immA?.setInitialState()
  }

  // Load project from scroll view in First Fragment
  @SuppressLint("Range")
  fun loadProject(id: Int) {
    PanelManager.instance.homePanel.setComponent(Visible(false))

    if (currentProject?.uuid == id) return

    // Clean elements from previous projects
    ToolManager.instance.cleanElements()

    // Load project settings
    val cursor = immA?.DB!!.getProject(id)
    if (cursor.moveToFirst()) {
      val projectName = cursor.getString(cursor.getColumnIndex(DatabaseManager.PROJECT_NAME))
      val mr =
          if (cursor.getInt(cursor.getColumnIndex(DatabaseManager.PROJECT_MR)) == 1) true else false
      val env = cursor.getInt(cursor.getColumnIndex(DatabaseManager.PROJECT_ENVIRONMENT))
      ProjectManager.instance.currentProject = Project(id, projectName, mr, env)
      FocusViewModel.instance.updateCurrentProjectUuid(id)
    }
    cursor.close()

    if (currentProject?.MR == true) {
      immA?.selectMRMode()
    } else {
      immA?.selectEnvironment(currentProject?.environment!!)
    }

    // Position Unique Assets
    val uniqueAssetsCursor = immA?.DB!!.getUniqueAssets(currentProject?.uuid)
    if (uniqueAssetsCursor.moveToFirst()) {
      while (!uniqueAssetsCursor.isAfterLast) {
        val uuid =
            uniqueAssetsCursor.getInt(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_UUID)
            )
        val rawType =
            uniqueAssetsCursor.getString(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_TYPE)
            )
        val type = AssetType.entries.find { it.name == rawType }
        val state =
            uniqueAssetsCursor.getInt(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_STATE)
            )

        val posX =
            uniqueAssetsCursor.getFloat(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_POSITION_X)
            )
        val posY =
            uniqueAssetsCursor.getFloat(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_POSITION_Y)
            )
        val posZ =
            uniqueAssetsCursor.getFloat(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_POSITION_Z)
            )

        val rotW =
            uniqueAssetsCursor.getFloat(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_ROTATION_W)
            )
        val rotX =
            uniqueAssetsCursor.getFloat(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_ROTATION_X)
            )
        val rotY =
            uniqueAssetsCursor.getFloat(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_ROTATION_Y)
            )
        val rotZ =
            uniqueAssetsCursor.getFloat(
                uniqueAssetsCursor.getColumnIndex(DatabaseManager.UNIQUE_ASSET_ROTATION_Z)
            )

        when (type) {
          AssetType.TASKS_PANEL -> {
            PanelManager.instance.tasksPanel.setComponent(
                UniqueAssetComponent(uuid, AssetType.TASKS_PANEL)
            )
            PanelManager.instance.tasksPanel.setComponent(Visible(if (state == 1) true else false))
            PanelManager.instance.tasksPanel.setComponent(
                Transform(Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)))
            )
          }
          AssetType.AI_PANEL -> {
            PanelManager.instance.aiExchangePanel.setComponent(
                UniqueAssetComponent(uuid, AssetType.AI_PANEL)
            )
            PanelManager.instance.aiExchangePanel.setComponent(
                Visible(if (state == 1 && AIManager.instance.AIenabled) true else false)
            )
            PanelManager.instance.aiExchangePanel.setComponent(
                Transform(Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)))
            )
          }
          AssetType.CLOCK -> {
            immA?.clock?.setComponent(UniqueAssetComponent(uuid, AssetType.CLOCK))
            immA
                ?.clock
                ?.setComponent(
                    Transform(Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)))
                )
          }
          AssetType.SPEAKER -> {
            immA?.speaker?.setComponent(UniqueAssetComponent(uuid, AssetType.SPEAKER))
            immA
                ?.speaker
                ?.setComponent(
                    Transform(Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)))
                )
            AudioManager.instance.audioIsOn = if (state == 1) true else false
            if (AudioManager.instance.audioIsOn) AudioManager.instance.playAmbientSound()
            else AudioManager.instance.stopAmbientSound()
          }
          else -> {
            Log.e("Focus", "Focus> Unknown Unique Asset")
          }
        }
        uniqueAssetsCursor.moveToNext()
      }
    }

    placeInFront(PanelManager.instance.toolbarPanel)
    PanelManager.instance.toolbarPanel.setComponent(Visible(true))

    immA?.showClock(true)
    immA?.showSpeaker(true)

    // Load and create Tool Assets
    val toolsCursor = immA?.DB!!.getToolAssets(currentProject?.uuid)
    if (toolsCursor.moveToFirst()) {
      while (!toolsCursor.isAfterLast) {
        val uuid = toolsCursor.getInt(toolsCursor.getColumnIndex(DatabaseManager.TOOL_UUID))
        val rawType = toolsCursor.getString(toolsCursor.getColumnIndex(DatabaseManager.TOOL_TYPE))
        val type = AssetType.entries.find { it.name == rawType }
        val source = toolsCursor.getString(toolsCursor.getColumnIndex(DatabaseManager.TOOL_SOURCE))
        val size = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_SIZE))
        val deleteHeight =
            toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_DELETE_HEIGHT))
        val parentUuid = toolsCursor.getInt(toolsCursor.getColumnIndex(DatabaseManager.TOOL_PARENT))

        val posX = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_POSITION_X))
        val posY = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_POSITION_Y))
        val posZ = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_POSITION_Z))

        val rotW = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_ROTATION_W))
        val rotX = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_ROTATION_X))
        val rotY = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_ROTATION_Y))
        val rotZ = toolsCursor.getFloat(toolsCursor.getColumnIndex(DatabaseManager.TOOL_ROTATION_Z))

        if (type == AssetType.WEB_VIEW) {
          WebView(source, uuid, Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)))
        } else {
          Tool(
              type = type,
              source = source,
              size = size,
              uuid = uuid,
              pose = Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)),
              deleteButtonHeight = deleteHeight,
              parentUuid = parentUuid,
          )
        }
        toolsCursor.moveToNext()
      }
    }
    toolsCursor.close()

    // Load and create Stickies
    val stickiesCursor = immA?.DB!!.getStickies(currentProject?.uuid)
    if (stickiesCursor.moveToFirst()) {
      while (!stickiesCursor.isAfterLast) {
        val uuid = stickiesCursor.getInt(stickiesCursor.getColumnIndex(DatabaseManager.STICKY_UUID))
        val message =
            stickiesCursor.getString(stickiesCursor.getColumnIndex(DatabaseManager.STICKY_MESSAGE))
        val rawColor =
            stickiesCursor.getString(stickiesCursor.getColumnIndex(DatabaseManager.STICKY_COLOR))
        val color = StickyColor.entries.find { it.name == rawColor }
        val parentUuid =
            stickiesCursor.getInt(stickiesCursor.getColumnIndex(DatabaseManager.STICKY_PARENT))

        val posX =
            stickiesCursor.getFloat(
                stickiesCursor.getColumnIndex(DatabaseManager.STICKY_POSITION_X)
            )
        val posY =
            stickiesCursor.getFloat(
                stickiesCursor.getColumnIndex(DatabaseManager.STICKY_POSITION_Y)
            )
        val posZ =
            stickiesCursor.getFloat(
                stickiesCursor.getColumnIndex(DatabaseManager.STICKY_POSITION_Z)
            )

        val rotW =
            stickiesCursor.getFloat(
                stickiesCursor.getColumnIndex(DatabaseManager.STICKY_ROTATION_W)
            )
        val rotX =
            stickiesCursor.getFloat(
                stickiesCursor.getColumnIndex(DatabaseManager.STICKY_ROTATION_X)
            )
        val rotY =
            stickiesCursor.getFloat(
                stickiesCursor.getColumnIndex(DatabaseManager.STICKY_ROTATION_Y)
            )
        val rotZ =
            stickiesCursor.getFloat(
                stickiesCursor.getColumnIndex(DatabaseManager.STICKY_ROTATION_Z)
            )

        StickyNote(
            uuid,
            message,
            color!!,
            Pose(Vector3(posX, posY, posZ), Quaternion(rotW, rotX, rotY, rotZ)),
            parentUuid,
        )

        stickiesCursor.moveToNext()
      }
    }
    stickiesCursor.close()

    ToolManager.instance.linkToolsWithParentBoards()
  }

  // Save project settings in second fragment
  fun saveProjectSettings(MRMode: Boolean? = null, projectName: String) {
    var mrMode = MRMode != null && MRMode == true

    // Settings if it is a new project...
    if (currentProject == null) {
      Log.i("Focus", "Focus> New project created")

      val project = Project(getNewUUID(), projectName, mrMode, immA!!.currentEnvironment)
      currentProject = project
      immA?.DB?.createProject(project)
      FocusViewModel.instance.updateCurrentProjectUuid(project.uuid)

      placeInFront(PanelManager.instance.toolbarPanel)
      PanelManager.instance.toolbarPanel.setComponent(Visible(true))
      PanelManager.instance.tasksPanel.setComponent(Visible(true))
      if (AIManager.instance.AIenabled)
          PanelManager.instance.aiExchangePanel.setComponent(Visible(true))
      immA?.showClock(true)
      immA?.showSpeaker(true)

      // Unique elements created to store in database
      val tasksPanelUUID = getNewUUID()
      val aiPanelUUID = getNewUUID()
      val clockUUID = getNewUUID()
      val speakerUUID = getNewUUID()

      PanelManager.instance.tasksPanel.setComponent(
          UniqueAssetComponent(tasksPanelUUID, AssetType.TASKS_PANEL)
      )
      PanelManager.instance.aiExchangePanel.setComponent(
          UniqueAssetComponent(aiPanelUUID, AssetType.AI_PANEL)
      )
      immA?.clock?.setComponent(UniqueAssetComponent(clockUUID, AssetType.CLOCK))
      immA?.speaker?.setComponent(UniqueAssetComponent(speakerUUID, AssetType.SPEAKER))

      // Initial configuration of panels for a new project
      placeInFront(PanelManager.instance.toolbarPanel)
      placeInFront(PanelManager.instance.tasksPanel, Vector3(-0.45f, -0.04f, 0.8f))
      placeInFront(PanelManager.instance.aiExchangePanel, Vector3(0.45f, -0.05f, 0.8f))
      placeInFront(immA?.clock, Vector3(0f, 0.23f, 0.9f))
      placeInFront(immA?.speaker, Vector3(-0.65f, -0.3f, 0.65f))

      // Unique elements created in database
      immA
          ?.DB
          ?.createUniqueAsset(
              tasksPanelUUID,
              project.uuid,
              AssetType.TASKS_PANEL,
              true,
              PanelManager.instance.tasksPanel.getComponent<Transform>().transform,
          )

      immA
          ?.DB
          ?.createUniqueAsset(
              aiPanelUUID,
              project.uuid,
              AssetType.AI_PANEL,
              true,
              PanelManager.instance.aiExchangePanel.getComponent<Transform>().transform,
          )

      immA
          ?.DB
          ?.createUniqueAsset(
              clockUUID,
              project.uuid,
              AssetType.CLOCK,
              true,
              immA?.clock!!.getComponent<Transform>().transform,
          )

      immA
          ?.DB
          ?.createUniqueAsset(
              speakerUUID,
              project.uuid,
              AssetType.SPEAKER,
              true,
              immA?.speaker!!.getComponent<Transform>().transform,
          )

      // Initial Web View tool created as an example
      WebView()
      PanelManager.instance.homePanel.setComponent(Visible(false))
      AudioManager.instance.playAmbientSound()

      // if it is not a new project, we only update project settings in database (name, environment)
    } else {

      if (projectName != "") currentProject?.name = projectName
      currentProject?.MR = mrMode
      currentProject?.environment = immA!!.currentEnvironment

      immA?.DB?.updateProject(currentProject)
    }
  }
}
