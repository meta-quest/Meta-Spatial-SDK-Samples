/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

/**
 * SplatControlPanel.kt
 *
 * OVERVIEW: This file defines the UI control panel for the Splat demo app using Jetpack Compose.
 * The panel provides interactive buttons to manipulate splats in real-time.
 *
 * KEY CONCEPTS:
 * - Jetpack Compose: Modern declarative UI framework for Android
 * - @Composable functions: UI building blocks that can be composed together
 * - SpatialTheme: Meta's design system for spatial computing UIs
 *
 * USAGE IN YOUR APP:
 * 1. Import this ControlPanel composable
 * 2. Pass your SplatManager instance to it
 * 3. Register it as a panel in your Activity (see SplatSampleActivity.kt)
 * 4. The panel will appear in your 3D scene as an interactive surface
 */
package com.meta.spatial.samples.splatsample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.SpatialColorScheme
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.darkSpatialColorScheme
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

/**
 * Physical dimensions of the panel in 3D space (in meters).
 *
 * PANEL SIZING: These constants define the panel's size when rendered in the 3D scene.
 * - Width: 2.048 meters (~6.7 feet)
 * - Height: 1.254 meters (~4.1 feet)
 *
 * ASPECT RATIO: Width/Height ≈ 1.63:1 (close to golden ratio for pleasant visuals)
 *
 * CUSTOMIZATION: Adjust these values to make the panel larger/smaller in your scene. Maintain
 * aspect ratio to prevent UI distortion.
 */
const val ANIMATION_PANEL_WIDTH = 2.048f
const val ANIMATION_PANEL_HEIGHT = 1.254f

private val panelHeadingText = "Splat Sample"
private val panelInstructionText = buildAnnotatedString {
  append("Press ")
  withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("A") }
  append(" to snap the panel in front of you. \nPress ")
  withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("B") }
  append(" to recenter the view.")
}

@Composable
fun ControlPanel(
    splatList: List<String>,
    selectedIndex: MutableState<Int>,
    isPanelInteractive: State<Boolean>,
    loadSplatFunction: (String) -> Unit,
) {
  // Apply SpatialTheme to ensure consistent design across the panel
  SpatialTheme(colorScheme = getPanelTheme()) {
    // Main container column with styling
    Column(
        modifier =
            Modifier.fillMaxSize() // Fill the panel's allocated space
                .clip(SpatialTheme.shapes.large) // Rounded corners
                .background(brush = LocalColorScheme.current.panel) // Themed background
                .padding(36.dp), // Inner padding for content
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Content column with consistent spacing between elements
      Column(
          verticalArrangement = Arrangement.spacedBy(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        // Panel title
        Text(
            text = panelHeadingText,
            style = SpatialTheme.typography.headline1Strong,
            color = LocalColorScheme.current.primaryAlphaBackground,
        )
        // Instructions for panel controls
        Text(
            text = panelInstructionText,
            style = SpatialTheme.typography.body1,
            color = LocalColorScheme.current.primaryAlphaBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp),
        )

        // Dynamically generated load options with large preview images
        // DYNAMIC UI GENERATION:
        // - Creates large, clickable preview images side by side
        // - Images are the dominant UI element with blue border when selected
        // - Labels appear below each corresponding image
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        ) {
          // val currentIndex = splatManager.getCurrentSplatIndex()
          splatList.forEachIndexed { index, option ->
            // Each splat option is displayed as a column with image above button
            val isSelected = (index == selectedIndex.value)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              // Large clickable preview image
              val previewResource = getSplatPreviewResource(option)
              // Visual feedback for loading state:
              // When panel is not interactive (Splat is loading), reduce opacity to 40%
              // This provides a clear visual cue that the panel is temporarily disabled
              val imageAlpha = if (isPanelInteractive.value) 1f else 0.4f
              if (previewResource != null) {
                Image(
                    painter = painterResource(id = previewResource),
                    contentDescription = "Preview of $option",
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(200.dp)
                            // Apply alpha modifier for visual loading state feedback
                            // 1.0 = fully visible (interactive), 0.4 = greyed out (loading)
                            .alpha(imageAlpha)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 4.dp else 2.dp,
                                color =
                                    if (isSelected) Color(0xFF1877F2)
                                    else LocalColorScheme.current.primaryAlphaBackground,
                                shape = RoundedCornerShape(12.dp),
                            )
                            // Disable click handling while Splat is loading
                            // This prevents race conditions from concurrent load requests
                            .clickable(enabled = isPanelInteractive.value) {
                              loadSplatFunction(option)
                              selectedIndex.value = index
                            },
                    contentScale = ContentScale.Crop,
                )
              }

              // Label below image
              Text(
                  text = getSplatDisplayName(option),
                  style = SpatialTheme.typography.headline2Strong,
                  color =
                      if (isSelected) Color(0xFF1877F2)
                      else LocalColorScheme.current.primaryAlphaBackground,
              )
            }
          }
        }
      } // End content column
    } // End main container
  } // End SpatialTheme
} // End ControlPanel composable

/**
 * Determines the appropriate color scheme based on system theme.
 *
 * THEMING IN SPATIAL UIS: Spatial apps should respect the user's system theme preference. This
 * function checks if dark mode is enabled and returns the matching theme.
 *
 * @return SpatialColorScheme - Either dark or light color scheme
 */
@Composable
fun getPanelTheme(): SpatialColorScheme =
    if (isSystemInDarkTheme()) darkSpatialColorScheme() else lightSpatialColorScheme()

/**
 * Maps splat file names to their corresponding drawable resource IDs. Used to display preview
 * images for each splat option.
 */
fun getSplatPreviewResource(splatPath: String): Int? {
  return when {
    splatPath.contains("Menlo Park", ignoreCase = true) -> R.drawable.mpk_room
    splatPath.contains("Los Angeles", ignoreCase = true) -> R.drawable.lax_room
    else -> null
  }
}

/**
 * Extracts a clean display name from the splat file path. Removes "apk://" prefix and ".spz"
 * extension.
 */
fun getSplatDisplayName(splatPath: String): String {
  return splatPath.replace("apk://", "").replace(".spz", "")
}
