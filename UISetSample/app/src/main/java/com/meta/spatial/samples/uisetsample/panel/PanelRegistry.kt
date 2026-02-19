/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.panel

import androidx.compose.runtime.Composable
import com.meta.spatial.compose.composePanel
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.samples.uisetsample.R
import com.meta.spatial.samples.uisetsample.layouts.ButtonShelvesLayout
import com.meta.spatial.samples.uisetsample.layouts.ButtonsLayout
import com.meta.spatial.samples.uisetsample.layouts.ControlsLayout
import com.meta.spatial.samples.uisetsample.layouts.DialogsLayout
import com.meta.spatial.samples.uisetsample.layouts.DropdownsLayout
import com.meta.spatial.samples.uisetsample.layouts.SlidersLayout
import com.meta.spatial.samples.uisetsample.layouts.TextFieldsLayout
import com.meta.spatial.samples.uisetsample.layouts.TextTileButtonsLayout
import com.meta.spatial.samples.uisetsample.layouts.TooltipsLayout
import com.meta.spatial.samples.uisetsample.layouts.patterns.CustomPattern2x4
import com.meta.spatial.samples.uisetsample.layouts.patterns.CustomPattern3x3
import com.meta.spatial.samples.uisetsample.layouts.patterns.CustomPatternThree
import com.meta.spatial.samples.uisetsample.layouts.patterns.CustomPatternTwo
import com.meta.spatial.samples.uisetsample.layouts.patterns.HorizonPatternOne
import com.meta.spatial.samples.uisetsample.layouts.patterns.HorizonPatternThree
import com.meta.spatial.samples.uisetsample.layouts.patterns.HorizonPatternTwo
import com.meta.spatial.samples.uisetsample.layouts.patterns.VideoPlayerPattern
import com.meta.spatial.samples.uisetsample.navigation.NavigationView
import com.meta.spatial.samples.uisetsample.navigation.theme_selector.ThemeSelectorView
import com.meta.spatial.samples.uisetsample.navigation.video.NavigationVideoView
import com.meta.spatial.toolkit.PanelRegistration

class PanelRegistry {

  fun initialPanelRegistration(): List<PanelRegistration> {
    return listOf(
        panelRegistration(PanelRegistrationIds.PANEL_NAVIGATOR) {
          NavigationView(PanelNavigator())
        },
        panelRegistration(PanelRegistrationIds.PANEL_DEMO_VIDEO) { NavigationVideoView() },
        panelRegistration(PanelRegistrationIds.PANEL_THEMES) { ThemeSelectorView() },
        panelRegistration(
            PanelRegistrationIds.PANEL_BUTTONS_LAYOUT,
            layoutWidth = 1136f,
            layoutHeight = 568f,
        ) {
          ButtonsLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_SLIDERS_LAYOUT,
            layoutWidth = 1400f,
            layoutHeight = 708f,
        ) {
          SlidersLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_CONTROLS_LAYOUT,
            layoutWidth = 1136f,
            layoutHeight = 650f,
        ) {
          ControlsLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_TEXT_FIELDS_LAYOUT,
            layoutWidth = 1136f,
            layoutHeight = 402f,
        ) {
          TextFieldsLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_TEXT_TILE_BUTTONS_LAYOUT,
            layoutWidth = 1136f,
            layoutHeight = 558f,
        ) {
          TextTileButtonsLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_BUTTON_SHELVES_LAYOUT,
            layoutWidth = 630f,
            layoutHeight = 480f,
        ) {
          ButtonShelvesLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_TOOLTIPS_LAYOUT,
            layoutWidth = 1136f,
            layoutHeight = 671f,
        ) {
          TooltipsLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_DROPDOWNS_LAYOUT,
            layoutWidth = 1300f,
            layoutHeight = 443f,
        ) {
          DropdownsLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_DIALOGS_LAYOUT,
            layoutWidth = 1400f,
            layoutHeight = 1263f,
        ) {
          DialogsLayout()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN1,
            layoutWidth = 1024f,
            layoutHeight = 668f,
        ) {
          HorizonPatternOne()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN2,
            layoutWidth = 1024f,
            layoutHeight = 668f,
        ) {
          HorizonPatternTwo()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN3,
            layoutWidth = 1024f,
            layoutHeight = 668f,
        ) {
          HorizonPatternThree()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_VIDEO_PLAYER_PATTERN,
            layoutWidth = 1571f,
            layoutHeight = 1056f,
        ) {
          VideoPlayerPattern()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_CUSTOM_UI_PATTERN3x3,
            layoutWidth = 592f,
            layoutHeight = 364f,
        ) {
          CustomPattern3x3()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_CUSTOM_UI_PATTERN2,
            layoutWidth = 660f,
            layoutHeight = 631f,
        ) {
          CustomPatternTwo()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_CUSTOM_UI_PATTERN3,
            layoutWidth = 720f,
            layoutHeight = 541f,
        ) {
          CustomPatternThree()
        },
        panelRegistration(
            PanelRegistrationIds.PANEL_CUSTOM_UI_PATTERN2x4,
            layoutWidth = 408f,
            layoutHeight = 472f,
        ) {
          CustomPattern2x4()
        },
    )
  }

  private fun panelRegistration(
      registrationId: Int,
      layoutWidth: Float? = null,
      layoutHeight: Float? = null,
      content: @Composable () -> Unit,
  ): PanelRegistration {
    return PanelRegistration(registrationId) { _ ->
      config {
        if (layoutWidth != null) {
          layoutWidthInDp = layoutWidth
        }
        if (layoutHeight != null) {
          layoutHeightInDp = layoutHeight
        }
        layerConfig = LayerConfig()
        enableTransparent = true
        includeGlass = false
        themeResourceId = R.style.Theme_UISetSample
      }

      composePanel { setContent { content() } }
    }
  }
}
