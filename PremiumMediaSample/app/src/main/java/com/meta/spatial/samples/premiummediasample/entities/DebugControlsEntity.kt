/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.entities

import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.Entity
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.samples.premiummediasample.R
import com.meta.spatial.samples.premiummediasample.data.debug.DebugButtonItem
import com.meta.spatial.samples.premiummediasample.data.debug.DebugData
import com.meta.spatial.samples.premiummediasample.data.debug.DebugSliderItem
import com.meta.spatial.samples.premiummediasample.getDisposableID
import com.meta.spatial.samples.premiummediasample.panels.DebugPanel
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Transform

class DebugControlsEntity() {
  companion object {
    private var maxFractionOfScreen = 0.8f
    private var maxPanelWidth = 0.45f
    private var itemHeight = .0475f
    private var panelLayoutDPI = 600

    fun createDebugPanel(controlsPanel: ControlsPanelEntity, closePlayerFn: () -> Unit): Entity {
      val debugSchema =
          DebugData(
              items =
                  mutableListOf(
                      DebugSliderItem(
                          label = "Cinema Controls FOV",
                          initialValue = controlsPanel.controlsFov,
                          range = 10f..80f,
                          roundToInt = true,
                          onValueChanged = { value -> controlsPanel.controlsFov = value },
                      ),
                      DebugSliderItem(
                          label = "Cinema Controls Angle",
                          initialValue = controlsPanel.controlsAngle,
                          roundToInt = true,
                          range = 0f..50f,
                          onValueChanged = { value -> controlsPanel.controlsAngle = value },
                      ),
                      DebugSliderItem(
                          label = "Cinema Controls Distance",
                          range = 0.25f..4f,
                          initialValue = controlsPanel.controlsDistance,
                          onValueChanged = { value -> controlsPanel.controlsDistance = value },
                      ),
                      DebugButtonItem(label = "Exit to Menu", onClick = { closePlayerFn() }),
                  ),
          )

      return create(debugSchema)
    }

    fun create(debugData: DebugData): Entity {
      val id: Int = getDisposableID()

      SpatialActivityManager.executeOnVrActivity<AppSystemActivity> { activity ->
        activity.registerPanel(panelRegistration(id, debugData))
      }

      return Entity.create(
          listOf(
              Grabbable(
                  type = GrabbableType.PIVOT_Y,
              ),
              Panel(id),
              Transform(),
          ))
    }

    private fun panelRegistration(id: Int, debugData: DebugData): PanelRegistration {
      return PanelRegistration(id) {
        config {
          fractionOfScreen = maxFractionOfScreen
          height = itemHeight * debugData.items.count()
          width = maxPanelWidth
          layoutDpi = panelLayoutDPI
          layerConfig = LayerConfig()
          enableTransparent = true
          includeGlass = false
          themeResourceId = R.style.PanelAppThemeTransparent
        }
        composePanel { setContent { DebugPanel(debugData) } }
      }
    }
  }
}
