/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.entities

import com.meta.spatial.core.Entity
import com.meta.spatial.core.SpatialContext
import com.meta.spatial.core.Vector2
import com.meta.spatial.runtime.Scene
import com.meta.spatial.samples.premiummediasample.AnchorOnLoad
import com.meta.spatial.samples.premiummediasample.Anchorable
import com.meta.spatial.samples.premiummediasample.MAX_SPAWN_DISTANCE
import com.meta.spatial.samples.premiummediasample.PanelLayerAlpha
import com.meta.spatial.samples.premiummediasample.R
import com.meta.spatial.samples.premiummediasample.TIMINGS
import com.meta.spatial.samples.premiummediasample.millisToFloat
import com.meta.spatial.samples.premiummediasample.panels.homePanel.HomePanelActivity
import com.meta.spatial.samples.premiummediasample.panels.homePanel.HomePanelConstants
import com.meta.spatial.toolkit.ActivityPanelRegistration
import com.meta.spatial.toolkit.DpPerMeterDisplayOptions
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelDimensions
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.PanelStyleOptions
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.toolkit.Visible
import dorkbox.tweenEngine.TweenEngine
import dorkbox.tweenEngine.TweenEquations

class HomePanelEntity(tweenEngine: TweenEngine) : FadingPanel(tweenEngine) {
  companion object {

    private const val WIDTH_IN_METERS =
        HomePanelConstants.PANEL_WIDTH_DP / HomePanelConstants.PANEL_DP_PER_METER
    private const val HEIGHT_IN_METERS =
        HomePanelConstants.PANEL_HEIGHT_DP / HomePanelConstants.PANEL_DP_PER_METER
    const val homePanelFov = 55f

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
              PanelDimensions(Vector2(WIDTH_IN_METERS, HEIGHT_IN_METERS)),
              PanelLayerAlpha(0f),
          )
      )
    }

    fun panelRegistration(scene: Scene, context: SpatialContext): PanelRegistration {
      return ActivityPanelRegistration(
          R.id.HomePanel,
          classIdCreator = { HomePanelActivity::class.java },
          settingsCreator = {
            UIPanelSettings(
                shape = QuadShapeOptions(width = WIDTH_IN_METERS, height = HEIGHT_IN_METERS),
                display =
                    DpPerMeterDisplayOptions(dpPerMeter = HomePanelConstants.PANEL_DP_PER_METER),
                style = PanelStyleOptions(themeResourceId = R.style.PanelAppThemeTransparent),
            )
          },
      )
    }
  }

  override val entity: Entity = create()

  fun fadeVisibility(isVisible: Boolean, onComplete: (() -> Unit)? = null) {
    val easing = if (isVisible) TweenEquations.Circle_In else TweenEquations.Circle_Out
    super.fadeVisibility(
        isVisible,
        TIMINGS.HOME_PANEL_FADE_BOTH.millisToFloat(),
        easing,
        onComplete,
    )
  }
}
