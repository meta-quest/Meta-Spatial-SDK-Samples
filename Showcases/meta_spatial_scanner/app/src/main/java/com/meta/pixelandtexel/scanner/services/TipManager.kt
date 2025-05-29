// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services

import android.util.Log
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.views.tips.FindObjectsScreen
import com.meta.pixelandtexel.scanner.views.tips.HelpScreen
import com.meta.pixelandtexel.scanner.views.tips.NoObjectsDetectedScreen
import com.meta.pixelandtexel.scanner.views.tips.SelectObjectTipScreen
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Enum representing different user actions or events that can trigger contextual tips. */
enum class UserEvent {
  STARTED_SCANNING,
  DETECTED_OBJECT,
  SELECTED_OBJECT,
  SELECTED_CURATED_OBJECT,
  DISMISSED_INFO_PANEL
}

/**
 * Manages the display of contextual tips to the user based on their interactions within the app.
 * Handles the lifecycle of tip panels, including their creation, display, and dismissal.
 *
 * @param activity The main [AppSystemActivity] used for registering panels and accessing system
 *   features.
 * @param generateCuratedObjects A lambda function to be invoked when the user requests to see
 *   curated objects from a tip panel.
 */
class TipManager(activity: AppSystemActivity, generateCuratedObjects: () -> Unit) {
  companion object {
    private const val TAG = "TipManager"
  }

  private var hasUserScanned = false
  private var hasUserDetectedObject = false
  private var hasUserSelectedCuratedObject = false

  private var lookForFirstObjectTipTimer: Job? = null
  private var howToSelectAnObjectTipTimer: Job? = null

  private var selectObjectPanelEntity: Entity? = null
  private var noObjectsPanelEntity: Entity? = null
  private var findObjectsPanelEntity: Entity? = null
  private var helpPanelEntity: Entity? = null

  /**
   * Initializes the TipManager by registering various tip panels with the provided
   * [AppSystemActivity].
   */
  init {
    activity.registerPanel(
        PanelRegistration(R.integer.select_object_tip_panel_id) { _ ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layoutWidthInDp = 368f
            width = 0.368f
            height = 0.164f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
          composePanel {
            setContent {
              SelectObjectTipScreen {
                selectObjectPanelEntity?.destroy()
                selectObjectPanelEntity = null
              }
            }
          }
        })

    activity.registerPanel(
        PanelRegistration(R.integer.no_objects_tip_panel_id) { _ ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layoutWidthInDp = 368f
            width = 0.368f
            height = 0.440f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
          composePanel {
            setContent {
              NoObjectsDetectedScreen({
                generateCuratedObjects.invoke()
                noObjectsPanelEntity?.destroy()
                noObjectsPanelEntity = null
              }) {
                noObjectsPanelEntity?.destroy()
                noObjectsPanelEntity = null
              }
            }
          }
        })

    activity.registerPanel(
        PanelRegistration(R.integer.find_objects_tip_panel_id) { _ ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layoutWidthInDp = 368f
            width = 0.368f
            height = 0.440f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
          composePanel {
            setContent {
              FindObjectsScreen({
                generateCuratedObjects.invoke()
                findObjectsPanelEntity?.destroy()
                findObjectsPanelEntity = null
              }) {
                findObjectsPanelEntity?.destroy()
                findObjectsPanelEntity = null
              }
            }
          }
        })

    activity.registerPanel(
        PanelRegistration(R.integer.help_tip_panel_id) { _ ->
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            layoutWidthInDp = 368f
            width = 0.368f
            height = 0.440f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
          composePanel {
            setContent {
              HelpScreen({
                generateCuratedObjects.invoke()
                helpPanelEntity?.destroy()
                helpPanelEntity = null
              }) {
                helpPanelEntity?.destroy()
                helpPanelEntity = null
              }
            }
          }
        })
  }

  /**
   * Processes a [UserEvent] and triggers the display or dismissal of relevant tip panels, showing
   * contextual help to the user.
   *
   * @param event The [UserEvent] that occurred.
   */
  fun reportUserEvent(event: UserEvent) {
    when (event) {
      UserEvent.STARTED_SCANNING -> {
        if (!hasUserScanned) {
          Log.d(TAG, "First time scanning; starting tip timer")

          // our first time scanning; start a timer to show tip to look for objects
          lookForFirstObjectTipTimer =
              startTimer(8_000) {
                noObjectsPanelEntity =
                    Entity.createPanelEntity(
                        R.integer.no_objects_tip_panel_id,
                        Transform(getTipSpawnPose()),
                        Grabbable(type = GrabbableType.PIVOT_Y))
              }
        }
        hasUserScanned = true
      }

      UserEvent.DETECTED_OBJECT -> {
        // stop the initial tip timer
        lookForFirstObjectTipTimer?.cancel()
        lookForFirstObjectTipTimer = null

        // destroy the tip panel if it was spawned
        noObjectsPanelEntity?.destroy()
        noObjectsPanelEntity = null

        if (!hasUserDetectedObject) {
          Log.d(TAG, "First time detecting an object; starting tip timer")

          // first time detecting an object, show tip of how to select a detected object
          howToSelectAnObjectTipTimer =
              startTimer(3_000) {
                selectObjectPanelEntity =
                    Entity.createPanelEntity(
                        R.integer.select_object_tip_panel_id,
                        Transform(getTipSpawnPose()),
                        Grabbable(type = GrabbableType.PIVOT_Y))
              }
        }
        hasUserDetectedObject = true
      }

      UserEvent.SELECTED_OBJECT -> {
        // stop the select tip timer
        howToSelectAnObjectTipTimer?.cancel()
        howToSelectAnObjectTipTimer = null

        // destroy the tip panel if it was spawned
        selectObjectPanelEntity?.destroy()
        selectObjectPanelEntity = null
      }

      UserEvent.SELECTED_CURATED_OBJECT -> {
        // stop the select tip timer
        howToSelectAnObjectTipTimer?.cancel()
        howToSelectAnObjectTipTimer = null

        // destroy the tip panel if it was spawned
        selectObjectPanelEntity?.destroy()
        selectObjectPanelEntity = null

        hasUserSelectedCuratedObject = true
      }

      UserEvent.DISMISSED_INFO_PANEL -> {
        if (!hasUserSelectedCuratedObject) {
          // user hasn't found curated object, show tip to look for or select from curated objects
          findObjectsPanelEntity =
              Entity.createPanelEntity(
                  R.integer.find_objects_tip_panel_id,
                  Transform(getTipSpawnPose()),
                  Grabbable(type = GrabbableType.PIVOT_Y))
        }
      }
    }
  }

  /**
   * Displays the help panel. If the help panel is already spawned, it moves the existing panel in
   * front of the user. Otherwise, it creates and spawns a new help panel.
   */
  fun showHelpPanel() {
    // it's already spawned; just move it in front of the user
    if (helpPanelEntity != null) {
      helpPanelEntity!!.setComponent(Transform(getTipSpawnPose()))
      return
    }

    helpPanelEntity =
        Entity.createPanelEntity(
            R.integer.help_tip_panel_id,
            Transform(getTipSpawnPose()),
            Grabbable(type = GrabbableType.PIVOT_Y))
  }

  /** Dismisses all currently active tip panels by destroying their associated entities. */
  fun dismissTipPanels() {
    selectObjectPanelEntity?.destroy()
    selectObjectPanelEntity = null

    noObjectsPanelEntity?.destroy()
    noObjectsPanelEntity = null

    findObjectsPanelEntity?.destroy()
    findObjectsPanelEntity = null

    helpPanelEntity?.destroy()
    helpPanelEntity = null
  }

  /**
   * Starts a coroutine-based timer that executes a given action after a specified delay.
   *
   * @param delayMs The delay in milliseconds before the action is executed.
   * @param onFinished A lambda function to be invoked when the timer finishes.
   * @return The [Job] associated with the started coroutine, allowing for cancellation.
   */
  private fun startTimer(delayMs: Long, onFinished: () -> Unit): Job {
    return CoroutineScope(Dispatchers.Main).launch {
      delay(delayMs)
      onFinished.invoke()
    }
  }

  /**
   * Calculates the appropriate spawn [Pose] pose for tip panels, determined to be 1 meter in front
   * of the user's head, at eye height, and oriented to face the user.
   *
   * @return A [Pose] object representing the calculated spawn location and rotation.
   */
  private fun getTipSpawnPose(): Pose {
    val headEntity =
        Query.where { has(AvatarAttachment.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
            .first()
    val headTransform = headEntity.getComponent<Transform>().transform
    // apply offset to lower the panel to eye height
    val headPosition = headTransform.t - Vector3(0f, 0.1f, 0f)

    val xzForward = (headTransform.forward() * Vector3(1f, 0f, 1f)).normalize()

    // 1 meters in front of the user at eye height, with yaw rotation towards head
    val position = headPosition + xzForward * 1f
    val rotation = Quaternion.lookRotationAroundY(position - headPosition)

    return Pose(position, rotation)
  }
}
