/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.entities

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Vector2
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.samples.premiummediasample.AnchorOnLoad
import com.meta.spatial.samples.premiummediasample.Anchorable
import com.meta.spatial.samples.premiummediasample.MAX_SPAWN_DISTANCE
import com.meta.spatial.samples.premiummediasample.PanelLayerAlpha
import com.meta.spatial.samples.premiummediasample.R
import com.meta.spatial.samples.premiummediasample.TIMINGS
import com.meta.spatial.samples.premiummediasample.millisToFloat
import com.meta.spatial.samples.premiummediasample.panels.homePanel.HomePanelActivity
import com.meta.spatial.samples.premiummediasample.panels.homePanel.HomePanelConstants
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import dorkbox.tweenEngine.TweenEngine
import dorkbox.tweenEngine.TweenEquations

class HomePanelEntity(tweenEngine: TweenEngine) : FadingPanel(tweenEngine) {
  companion object {

    private val ratio = HomePanelConstants.PANEL_WIDTH_DP / HomePanelConstants.PANEL_HEIGHT_DP
    private val widthInMeters = 1.1f
    private val heightInMeters = widthInMeters / ratio
    val homePanelFov = 55f

    fun create(): Entity {
      return Entity.create(
          listOf(
              Grabbable(
                  type = GrabbableType.PIVOT_Y,
              ),
              Panel(R.id.HomePanel),
              Transform(),
              Anchorable(0.02f),
              AnchorOnLoad(distanceCheck = MAX_SPAWN_DISTANCE + 0.5f, scaleProportional = true),
              Visible(false),
              PanelDimensions(Vector2(widthInMeters, heightInMeters)),
              PanelLayerAlpha(0f),
          ))
    }

    fun panelRegistration(): PanelRegistration {
      return PanelRegistration(R.id.HomePanel) {
        config {
          mips = 1
          height = heightInMeters
          width = widthInMeters
          layoutWidthInDp = HomePanelConstants.PANEL_WIDTH_DP
          layoutHeightInDp = HomePanelConstants.PANEL_HEIGHT_DP
          layerConfig = LayerConfig()
          enableTransparent = true
          includeGlass = false
          themeResourceId = R.style.PanelAppThemeTransparent
        }
        activityClass = HomePanelActivity::class.java
      }
    }
  }

  override val entity: Entity = create()

  fun fadeVisibility(isVisible: Boolean, onComplete: (() -> Unit)? = null) {
    val easing = if (isVisible) TweenEquations.Circle_In else TweenEquations.Circle_Out
    super.fadeVisibility(
        isVisible, TIMINGS.HOME_PANEL_FADE_BOTH.millisToFloat(), easing, onComplete)
  }
}
