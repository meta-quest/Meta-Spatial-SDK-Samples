// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.managers

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.toolkit.TransformParent
import com.meta.theelectricfactory.focus.AssetType
import com.meta.theelectricfactory.focus.AttachableComponent
import com.meta.theelectricfactory.focus.ToolComponent
import com.meta.theelectricfactory.focus.data.StickyColor
import com.meta.theelectricfactory.focus.data.arrows
import com.meta.theelectricfactory.focus.data.boards
import com.meta.theelectricfactory.focus.data.labels
import com.meta.theelectricfactory.focus.data.shapes
import com.meta.theelectricfactory.focus.data.stickers
import com.meta.theelectricfactory.focus.tools.StickyNote
import com.meta.theelectricfactory.focus.tools.Timer
import com.meta.theelectricfactory.focus.tools.Tool
import com.meta.theelectricfactory.focus.tools.WebView
import com.meta.theelectricfactory.focus.utils.deleteObject
import com.meta.theelectricfactory.focus.utils.getAssetSize
import com.meta.theelectricfactory.focus.utils.getDeleteButtonHeight

class ToolManager {

    companion object {
        val instance: ToolManager by lazy { ToolManager() }
    }

    fun createStickyNote(index: Int) {
        StickyNote(
            message = "",
            color = StickyColor.entries[index])
        PanelManager.instance.closeSubPanels()
    }

    fun createLabelTool(index: Int) {
        // Create Label tool
        Tool(
            type = AssetType.LABEL,
            source = labels[index].toString(),
            size = 0.065f,
            deleteButtonHeight = 0.05f)
        PanelManager.instance.closeSubPanels()
    }

    fun createArrowTool(index: Int) {
        // Create Arrow tool
        Tool(
            type = AssetType.ARROW,
            source = arrows[index].toString(),
            size = 0.1f,
            deleteButtonHeight = getDeleteButtonHeight(AssetType.ARROW, index)
        )
        PanelManager.instance.closeSubPanels()
    }

    fun createBoard(index: Int) {
        // Create Board
        Tool(
            type = AssetType.BOARD,
            source = boards[index].toString(),
            size = getAssetSize(AssetType.BOARD, index),
            deleteButtonHeight = getDeleteButtonHeight(AssetType.BOARD, index)
        )
        PanelManager.instance.closeSubPanels()
    }

    fun createShape(index: Int) {
        // Create Shape tool (2D or 3D)
        val type = if (index % 2 == 0) AssetType.SHAPE_2D else AssetType.SHAPE_3D
        val deleteHeight = if (index % 2 == 0) 0.08f else 0.12f
        Tool(
            type = type,
            source = shapes[index].toString(),
            size = getAssetSize(type, index),
            deleteButtonHeight = deleteHeight,
        )
        PanelManager.instance.closeSubPanels()
    }

    fun createSticker(index: Int) {
        // Create Sticker tool
        Tool(type = AssetType.STICKER, source = stickers[index].toString(), size = 0.04f)
        PanelManager.instance.closeSubPanels()
    }

    fun createTimer(index: Int) {
        // Create Timer
        Timer((index + 1) * 5)
        PanelManager.instance.closeSubPanels()
    }

    fun createWebView() {
        WebView()
    }

    // Link objects sticked to boards with their parent entities
    fun linkToolsWithParentBoards(spatialTask: Entity? = null) {
        var boards: MutableList<Entity> = mutableListOf()

        val toolAssets = Query.where { has(AttachableComponent.id) }
        for (entity in toolAssets.eval()) {

            val type = entity.getComponent<AttachableComponent>().type
            if (type == 1) {
                boards.add(entity)
            }
        }

        if (spatialTask != null) {
            val parentUuid = spatialTask.getComponent<AttachableComponent>().parentUuid
            for (board in boards) {
                val boardUuid = board.getComponent<ToolComponent>().uuid
                if (boardUuid == parentUuid) {
                    spatialTask.setComponent(TransformParent(board))
                }
            }
        } else {
            for (entity in toolAssets.eval()) {
                val parentUuid = entity.getComponent<AttachableComponent>().parentUuid
                if (parentUuid != -1) {
                    for (board in boards) {
                        val boardUuid = board.getComponent<ToolComponent>().uuid
                        if (boardUuid == parentUuid) entity.setComponent(TransformParent(board))
                    }
                }
            }
        }
    }

    fun cleanElements() {
        // Clean previous elements in project, if there is any
        val toolAssets = Query.where { has(ToolComponent.id) }
        for (entity in toolAssets.eval()) {
            deleteObject(entity, false, true)
        }
    }
}