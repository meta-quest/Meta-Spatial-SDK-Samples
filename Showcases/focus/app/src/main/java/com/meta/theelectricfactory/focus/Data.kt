// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

// Project class to manage project data
data class Project(val uuid: Int, var name: String, var MR: Boolean, var environment: Int)

// Enum with all asset types in the app
enum class AssetType {
  TASKS_PANEL,
  AI_PANEL,
  CLOCK,
  SPEAKER,
  TASK,
  STICKY_NOTE,
  STICKER,
  WEB_VIEW,
  LABEL,
  SHAPE_3D,
  SHAPE_2D,
  TIMER,
  ARROW,
  BOARD
}

// GENERAL DATA

// Textures and models for tools
val arrows =
    arrayOf(
        R.drawable.arrow1,
        R.drawable.arrow2,
        R.drawable.arrow3,
        R.drawable.arrow4,
        R.drawable.arrow5,
        R.drawable.arrow6)
val labels =
    arrayOf(
        R.drawable.label1,
        R.drawable.label2,
        R.drawable.label3,
        R.drawable.label4,
        R.drawable.label5,
        R.drawable.label6)
val boards = arrayOf(R.drawable.board1, R.drawable.board2, R.drawable.board3, R.drawable.board4)
val shapes =
    arrayOf(
        R.drawable.shape1,
        "torus.glb",
        R.drawable.shape3,
        "cube.glb",
        R.drawable.shape5,
        "rhombus.glb")
val stickers =
    arrayOf(
        R.drawable.sticker1,
        R.drawable.sticker2,
        R.drawable.sticker3,
        R.drawable.sticker4,
        R.drawable.sticker5,
        R.drawable.sticker6)

// toolbar button references
val stickyButtons =
    arrayOf(
        R.id.buttonSticky1,
        R.id.buttonSticky2,
        R.id.buttonSticky3,
        R.id.buttonSticky4,
        R.id.buttonSticky5)
val labelButtons =
    arrayOf(
        R.id.buttonLabel1,
        R.id.buttonLabel2,
        R.id.buttonLabel3,
        R.id.buttonLabel4,
        R.id.buttonLabel5,
        R.id.buttonLabel6)
val arrowButtons =
    arrayOf(
        R.id.buttonArrow1,
        R.id.buttonArrow2,
        R.id.buttonArrow3,
        R.id.buttonArrow4,
        R.id.buttonArrow5,
        R.id.buttonArrow6)
val boardButtons =
    arrayOf(R.id.buttonBoard1, R.id.buttonBoard2, R.id.buttonBoard3, R.id.buttonBoard4)
val shapesButtons =
    arrayOf(
        R.id.buttonShape1,
        R.id.buttonShape2,
        R.id.buttonShape3,
        R.id.buttonShape4,
        R.id.buttonShape5,
        R.id.buttonShape6)
val stickerButtons =
    arrayOf(
        R.id.buttonSticker1,
        R.id.buttonSticker2,
        R.id.buttonSticker3,
        R.id.buttonSticker4,
        R.id.buttonSticker5,
        R.id.buttonSticker6)
val timerButtons =
    arrayOf(
        R.id.buttonTimer1,
        R.id.buttonTimer2,
        R.id.buttonTimer3,
        R.id.buttonTimer4,
        R.id.buttonTimer5,
        R.id.buttonTimer6)

// State and priority labels for tasks
val stateLabels =
    arrayOf(R.drawable.label_to_do, R.drawable.label_in_progress, R.drawable.label_done)
val priorityLabels = arrayOf(R.drawable.label_low, R.drawable.label_medium, R.drawable.label_high)

// Environments and skyboxes assets
val environments = arrayOf("environment1.glb", "environment2.glb", "environment3.glb")
val skyboxes = arrayOf(R.drawable.skybox1, R.drawable.skybox2, R.drawable.skybox3)

// Textures for toolbar buttons
val defaultToolResources =
    arrayOf(
        R.drawable.sticky,
        R.drawable.labels,
        R.drawable.arrows,
        R.drawable.boards,
        R.drawable.shapes,
        R.drawable.stickers,
        R.drawable.timer)
val pressedToolResources =
    arrayOf(
        R.drawable.sticky_pressed,
        R.drawable.label_pressed,
        R.drawable.arrow_pressed,
        R.drawable.board_pressed,
        R.drawable.shape_pressed,
        R.drawable.sticker_pressed,
        R.drawable.timer_pressed)

// Sizes of tools
val boardSizeArray = arrayOf(0.335f, 0.45f, 0.397f, 0.45f)
val shape2DSizeArray = arrayOf(0.075f, 0.075f, 0.075f, 0.075f, 0.075f, 0.075f)
val shape3DSizeArray = arrayOf(0.075f, 0.075f, 0.075f, 0.075f, 0.075f, 0.075f)

// Heights of delete button for tools
val arrowHeightArray = arrayOf(0.05f, 0.05f, 0.14f, 0.14f, 0.14f, 0.14f)
val boardHeightArray = arrayOf(0.4f, 0.41f, 0.41f, 0.5f)
