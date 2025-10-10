// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.navigation

import com.meta.levinriegner.uiset.app.panel.PanelRegistrationIds

enum class NavigationUiSection(val displayText: String) {
  Components("UI Components"),
  Patterns("UI Patterns"),
}

enum class NavigationUiItem(
    val section: NavigationUiSection,
    val panelRegistrationIds: List<Int>,
    val label: String,
    val secondaryLabel: String,
) {
  Button(
      NavigationUiSection.Components,
      listOf(
          PanelRegistrationIds.PANEL_BUTTONS_LAYOUT,
          PanelRegistrationIds.PANEL_BUTTON_SHELVES_LAYOUT,
          PanelRegistrationIds.PANEL_TEXT_TILE_BUTTONS_LAYOUT,
      ),
      "Button",
      "UI Component - Buttons",
  ),
  Controls(
      NavigationUiSection.Components,
      listOf(
          PanelRegistrationIds.PANEL_CONTROLS_LAYOUT,
          PanelRegistrationIds.PANEL_DROPDOWNS_LAYOUT,
      ),
      "Controls",
      "Switch, Checkbox, Radio",
  ),
  Slider(
      NavigationUiSection.Components,
      listOf(PanelRegistrationIds.PANEL_SLIDERS_LAYOUT),
      "Slider",
      "Secondary",
  ),
  InputField(
      NavigationUiSection.Components,
      listOf(
          PanelRegistrationIds.PANEL_TOOLTIPS_LAYOUT,
          PanelRegistrationIds.PANEL_TEXT_FIELDS_LAYOUT,
      ),
      "Input Field",
      "Text Field, Search Box",
  ),
  Dialog(
      NavigationUiSection.Components,
      listOf(PanelRegistrationIds.PANEL_DIALOGS_LAYOUT),
      "Dialog",
      "Secondary",
  ),
  Card(
      NavigationUiSection.Components,
      listOf(PanelRegistrationIds.PANEL_CARDS_LAYOUT),
      "Card",
      "Secondary",
  ),
  HorizonOsPatterns(
      NavigationUiSection.Patterns,
      listOf(
          PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN1,
          PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN2,
          PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN3,
      ),
      "Horizon OS Patterns",
      "Description",
  ),
  VideoPlayer(
      NavigationUiSection.Patterns,
      listOf(PanelRegistrationIds.PANEL_VIDEO_PLAYER_PATTERN),
      "Video Player",
      "Description",
  ),
  CustomUi(
      NavigationUiSection.Patterns,
      listOf(
          PanelRegistrationIds.PANEL_CUSTOM_UI_PATTERN3,
          PanelRegistrationIds.PANEL_CUSTOM_UI_PATTERN3x3,
          PanelRegistrationIds.PANEL_CUSTOM_UI_PATTERN2,
          PanelRegistrationIds.PANEL_CUSTOM_UI_PATTERN2x4,
      ),
      "Custom UI",
      "Description",
  ),
}
