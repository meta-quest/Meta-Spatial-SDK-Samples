// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.data

import androidx.compose.ui.graphics.Color
import com.meta.theelectricfactory.focus.R

// Project class to manage project data
data class Project(val uuid: Int, var name: String, var MR: Boolean, var environment: Int)

// State and priority labels for tasks
data class Label(
    val description: String,
    val containerColor: Color,
    val contentColor: Color
)

val stateLabels = arrayOf(
    Label( "To do", Color(0xFFFFDBFA), Color(0xFFA6008D)),
    Label("In progress", Color(0xFFF1DAFF), Color(0xFF6500A3)),
    Label("Done", Color(0xFFE9E4FB), Color(0xFF2B167E)),
)

val priorityLabels = arrayOf(
    Label("Low priority", Color(0xFFFFF5DB), Color(0xFFA67700)),
    Label("Medium priority", Color(0xFFFFE8DA), Color(0xFFA33C00)),
    Label("High priority", Color(0xFFFFDADA), Color(0xFFA30000)),
)

// Enum to save sticky notes possible colors
enum class StickyColor {
    Yellow,
    Green,
    Pink,
    Orange,
    Blue,
    Purple
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
        R.drawable.arrow6
    )
val labels =
    arrayOf(
        R.drawable.label1,
        R.drawable.label2,
        R.drawable.label3,
        R.drawable.label4,
        R.drawable.label5,
        R.drawable.label6
    )
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
        R.drawable.sticker6
    )

// Environments and skyboxes assets
val environments = arrayOf("environment1.glb", "environment2.glb", "environment3.glb")
val skyboxes = arrayOf(R.drawable.skybox1, R.drawable.skybox2, R.drawable.skybox3)

// Sizes of tools
val boardSizeArray = arrayOf(0.335f, 0.45f, 0.397f, 0.45f)
val shape2DSizeArray = arrayOf(0.075f, 0.075f, 0.075f, 0.075f, 0.075f, 0.075f)
val shape3DSizeArray = arrayOf(0.075f, 0.075f, 0.075f, 0.075f, 0.075f, 0.075f)

// Heights of delete button for tools
val arrowHeightArray = arrayOf(0.05f, 0.05f, 0.14f, 0.14f, 0.14f, 0.14f)
val boardHeightArray = arrayOf(0.4f, 0.41f, 0.41f, 0.5f)
