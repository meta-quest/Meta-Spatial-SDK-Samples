// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.managers

import androidx.compose.runtime.Composable
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.PanelShapeLayerBlendType
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.MainActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.UniqueAssetComponent
import com.meta.theelectricfactory.focus.fragments.FirstFragment
import com.meta.theelectricfactory.focus.fragments.SecondFragment
import com.meta.theelectricfactory.focus.panels.AIPanel
import com.meta.theelectricfactory.focus.panels.ArrowSubPanel
import com.meta.theelectricfactory.focus.panels.BoardSubPanel
import com.meta.theelectricfactory.focus.panels.LabelSubPanel
import com.meta.theelectricfactory.focus.panels.ShapeSubPanel
import com.meta.theelectricfactory.focus.panels.StickerSubPanel
import com.meta.theelectricfactory.focus.panels.StickySubPanel
import com.meta.theelectricfactory.focus.panels.TasksPanel
import com.meta.theelectricfactory.focus.panels.TimerSubPanel
import com.meta.theelectricfactory.focus.panels.ToolbarPanel
import com.meta.theelectricfactory.focus.utils.FOCUS_DP
import com.meta.theelectricfactory.focus.utils.placeInFront
import com.meta.theelectricfactory.focus.viewmodels.FocusViewModel

class PanelManager {

    val immA: ImmersiveActivity? get() = ImmersiveActivity.getInstance()

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

    companion object {
        val instance: PanelManager by lazy { PanelManager() }
    }

    object PanelRegistrationIds {
        const val HomePanel = 20
        const val AIPanel = 21
        const val TasksPanel = 22
        const val Toolbar = 23
        const val StickySubPanel = 24
        const val LabelSubPanel = 25
        const val ArrowSubPanel = 26
        const val BoardSubPanel = 27
        const val ShapesSubPanel = 28
        const val StickerSubPanel = 29
        const val TimerSubPanel = 30
    }

    fun registerFocusPanels(): List<PanelRegistration>  {
        return listOf(
            panelRegistration(PanelRegistrationIds.HomePanel, 0.58f, 0.41f, true) {},
            panelRegistration(PanelRegistrationIds.Toolbar, 0.65f, 0.065f) { ToolbarPanel() },
            panelRegistration(PanelRegistrationIds.TasksPanel, 0.275f, 0.5f) { TasksPanel() },
            panelRegistration(PanelRegistrationIds.AIPanel, 0.3f, 0.5f) { AIPanel() },
            panelRegistration(PanelRegistrationIds.StickySubPanel, 0.26f, 0.042f) { StickySubPanel() },
            panelRegistration(PanelRegistrationIds.LabelSubPanel, 0.46f, 0.042f) { LabelSubPanel() },
            panelRegistration(PanelRegistrationIds.ArrowSubPanel, 0.24f, 0.042f) { ArrowSubPanel() },
            panelRegistration(PanelRegistrationIds.BoardSubPanel, 0.18f, 0.042f) { BoardSubPanel() },
            panelRegistration(PanelRegistrationIds.ShapesSubPanel, 0.25f, 0.042f) { ShapeSubPanel() },
            panelRegistration(PanelRegistrationIds.StickerSubPanel, 0.25f, 0.042f) { StickerSubPanel() },
            panelRegistration(PanelRegistrationIds.TimerSubPanel, 0.35f, 0.042f) { TimerSubPanel() },
        )
    }

    fun panelRegistration(
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
                layoutWidthInDp = FOCUS_DP * width
                layerConfig = LayerConfig()
                layerBlendType = PanelShapeLayerBlendType.MASKED
                includeGlass = false
                themeResourceId = R.style.Theme_Focus_Transparent
                enableLayerFeatheredEdge = true
            }

            if (homePanel) {
                activityClass = MainActivity::class.java
            } else {
                composePanel { setContent { content() } }
            }
        }
    }

    // All panels in scene are created and referenced in variables to control them
    fun createPanels() {
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

    fun createHomePanel() {
        homePanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.HomePanel,
                Transform(Pose(Vector3(0f))),
                Grabbable(true, GrabbableType.FACE),
                Visible(false)
            )
    }

    fun createTasksPanel() {
        tasksPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.TasksPanel,
                Transform(Pose(Vector3(0f))),
                Grabbable(true, GrabbableType.FACE),
                Visible(false),
                // Empty UUID since the asset is not linked with any project for now
                UniqueAssetComponent(type = AssetType.TASKS_PANEL)
            )
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
                UniqueAssetComponent(type = AssetType.AI_PANEL)
            )
    }

    fun createToolbarPanel() {
        toolbarPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.Toolbar,
                Transform(Pose(Vector3(0f))),
                Grabbable(true, GrabbableType.FACE),
                Visible(false)
            )
    }

    fun createStickySubPanel() {
        stickySubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.StickySubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel)
            )

        subpanels.add(stickySubPanel)
    }

    fun createLabelSubPanel() {
        labelSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.LabelSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel)
            )

        subpanels.add(labelSubPanel)
    }

    fun createArrowSubPanel() {
        arrowSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.ArrowSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel)
            )

        subpanels.add(arrowSubPanel)
    }

    fun createBoardSubPanel() {
        boardSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.BoardSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel)
            )

        subpanels.add(boardSubPanel)
    }

    fun createShapeSubPanel() {
        shapeSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.ShapesSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel)
            )

        subpanels.add(shapeSubPanel)
    }

    fun createStickerSubPanel() {
        stickerSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.StickerSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel)
            )

        subpanels.add(stickerSubPanel)
    }

    fun createTimerSubPanel() {
        timerSubPanel =
            Entity.createPanelEntity(
                PanelRegistrationIds.TimerSubPanel,
                Transform(Pose(Vector3(0f, 0.06f, -0.05f))),
                Visible(false),
                TransformParent(toolbarPanel)
            )

        subpanels.add(timerSubPanel)
    }

    fun openSubPanel(panel: Entity) {
        val isVisible = panel.getComponent<Visible>().isVisible
        closeSubPanels(true)
        if (!isVisible) {
            panel.setComponent(Visible(true))
        }
    }

    fun closeSubPanels(keepSelectedTool: Boolean = false) {
        for (i in 0..subpanels.count() - 1) {
            subpanels[i].setComponent(Visible(false))
        }
        if (!keepSelectedTool) FocusViewModel.instance.setSelectedTool(-1)
    }

    fun openHomePanel() {
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
        AudioManager.instance.ambientSoundPlayer.stop()
        ProjectManager.instance.newProject()
    }

    fun openSettingsPanel() {
        // if first fragment is initialized and active, we change to Second Fragment
        try {
            if (FirstFragment.instance.get()?.isCurrentlyVisible() == true) {
                FirstFragment.instance.get()?.moveToSecondFragment()
            }
        } catch (e: UninitializedPropertyAccessException) { }
        placeInFront(homePanel)
        homePanel.setComponent(Visible(true))
    }

    fun showTasksPanel(state: Boolean = true) {
        if (state) placeInFront(tasksPanel, bigPanel = true)
        tasksPanel.setComponent(Visible(state))
        immA?.DB?.updateUniqueAsset(tasksPanel.getComponent<UniqueAssetComponent>().uuid, state = state)
    }

    fun showAIPanel(state: Boolean = true) {
        if (state) placeInFront(aiExchangePanel, bigPanel = true)
        aiExchangePanel.setComponent(Visible(state))
        immA?.DB?.updateUniqueAsset(aiExchangePanel.getComponent<UniqueAssetComponent>().uuid, state = state)
    }
}