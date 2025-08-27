/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.entities

import com.meta.spatial.core.Entity
import com.meta.spatial.samples.premiummediasample.PanelLayerAlpha
import com.meta.spatial.samples.premiummediasample.systems.tweenEngine.TweenPanelLayerAlpha
import com.meta.spatial.toolkit.Visible
import dorkbox.tweenEngine.Tween
import dorkbox.tweenEngine.TweenEngine
import dorkbox.tweenEngine.TweenEquations
import dorkbox.tweenEngine.TweenEvents
import kotlin.math.abs

abstract class FadingPanel(val tweenEngine: TweenEngine) {
  protected var currTween: Tween<TweenPanelLayerAlpha>? = null

  abstract val entity: Entity

  private fun fadeTo(
      targetAlpha: Float,
      duration: Float = 1f,
      easing: TweenEquations,
      onComplete: (() -> Unit)? = null,
  ): Tween<TweenPanelLayerAlpha> {
    return tweenEngine
        .to(TweenPanelLayerAlpha(entity), TweenPanelLayerAlpha.ALPHA_VALUE, duration)
        .value(targetAlpha)
        .ease(easing)
        .addCallback(TweenEvents.COMPLETE) { onComplete?.invoke() }
        .start()
  }

  fun setVisible(isVisible: Boolean) {
    currTween?.cancel()
    currTween = null
    entity.setComponent(Visible(isVisible))
    entity.setComponent(PanelLayerAlpha(if (isVisible) 1f else 0f))
  }

  fun fadeVisibility(
      isVisible: Boolean,
      fullDuration: Float,
      easing: TweenEquations,
      onComplete: (() -> Unit)? = null,
  ) {
    val plaComp = getDefaultAlphaComponent(isVisible)

    if (isVisible) {
      entity.setComponent(Visible(true))
    }

    currTween?.cancel()

    val targetAlpha = if (isVisible) 1f else 0f

    currTween =
        fadeTo(targetAlpha, fullDuration * abs(plaComp.layerAlpha - targetAlpha), easing) {
          if (!isVisible) {
            entity.setComponent(Visible(false))
          }
          currTween = null
          onComplete?.invoke()
        }
  }

  private fun getDefaultAlphaComponent(isVisible: Boolean): PanelLayerAlpha {
    var plaComp = entity.tryGetComponent<PanelLayerAlpha>()
    if (plaComp == null) {
      plaComp = PanelLayerAlpha(if (isVisible) 0f else 1f)
      entity.setComponent(plaComp)
    }
    return plaComp
  }
}
