package com.meta.theelectricfactory.focus.panels

import androidx.compose.runtime.Composable
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.toolkit.TransformParent
import com.meta.theelectricfactory.focus.MainActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.utils.FOCUS_DP

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