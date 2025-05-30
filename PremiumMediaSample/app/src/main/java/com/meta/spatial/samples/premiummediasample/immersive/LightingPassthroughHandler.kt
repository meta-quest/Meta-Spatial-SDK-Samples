// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.immersive

import android.util.Log
import com.meta.spatial.core.Color4
import com.meta.spatial.core.Lut
import com.meta.spatial.runtime.Scene
import com.meta.spatial.samples.premiummediasample.TIMINGS
import com.meta.spatial.samples.premiummediasample.data.CinemaState
import com.meta.spatial.samples.premiummediasample.data.LightMultipliers
import com.meta.spatial.samples.premiummediasample.data.SceneLightingSettings
import com.meta.spatial.samples.premiummediasample.millisToFloat
import com.meta.spatial.samples.premiummediasample.systems.heroLighting.HeroLightingSystem
import dorkbox.tweenEngine.TweenAccessor
import dorkbox.tweenEngine.TweenEngine

class LightingPassthroughHandler(
    private val scene: Scene,
    private val heroLightingSystem: HeroLightingSystem,
    private val tweenEngine: TweenEngine
) {

  // Base passthrough. Slider on controls panel modifies this.
  // 0 = full passthrough, 1 = no passthrough
  private var currentPassthrough: Float = 1f

  // Modifier on passthrough. Based on CinemaState default values.
  // 1 = normal, < 1 = reduced opacity
  var tintMultiplier: Float = 1f
    get() = field
    set(value) {
      val clampedValue = value.coerceIn(0f, 1f)
      if (field != clampedValue) {
        field = clampedValue
        tintPassthrough(currentPassthrough)
      }
    }

  private var _currentLighting: Float = 0.5f
  val currentLighting
    get() = _currentLighting

  var lightingMultiplier: Float = 1f
    get() = field
    set(value) {
      if (field != value) {
        field = value
        setLighting(currentLighting)
      }
    }

  fun fadeLightingMultiplier() {
    transitionLighting(
        LightMultipliers(lightingMultiplier = 0f, passthroughMultiplier = tintMultiplier),
        TIMINGS.LIGHTING_PLAYING_FADE.millisToFloat())
  }

  fun setLighting(value: Float) {
    _currentLighting = value
    try {
      val multiplied = value * lightingMultiplier
      heroLightingSystem.lightingAlpha = multiplied
    } catch (e: Exception) {
      Log.e("LightingPassthroughHandler", "Error setLighting", e)
    }
  }

  fun transitionLighting(cinemaState: CinemaState, isPlaying: Boolean) {
    val lightMultipliers =
        if (isPlaying) SceneLightingSettings.lightingDefaults[cinemaState]?.playingLighting
        else SceneLightingSettings.lightingDefaults[cinemaState]?.pausedLighting

    if (lightMultipliers == null) {
      return
    }

    val tweenDuration =
        if (isPlaying) TIMINGS.LIGHTING_PLAYING_FADE.millisToFloat()
        else TIMINGS.LIGHTING_NOT_PLAYING_FADE.millisToFloat()
    transitionLighting(lightMultipliers, tweenDuration)
  }

  fun transitionLighting(lightingData: LightMultipliers, tweenDuration: Float) {
    if (tintMultiplier != lightingData.passthroughMultiplier) {
      // Passthrough tween
      tweenEngine
          .to(
              TweenLightingPassthrough(this),
              TweenLightingPassthrough.TINT_MULTIPLIER,
              tweenDuration)
          .value(lightingData.passthroughMultiplier)
          .start()
    }

    if (lightingMultiplier != lightingData.lightingMultiplier) {
      // Light tween
      tweenEngine
          .to(
              TweenLightingPassthrough(this),
              TweenLightingPassthrough.LIGHTING_MULTIPLIER,
              tweenDuration)
          .value(lightingData.lightingMultiplier)
          .start()
    }
  }

  fun tintPassthrough(passthroughValue: Float) {
    currentPassthrough = passthroughValue
    // Make slider move from passthrough to opaque when sliding left to right
    val multipliedPassthrough = currentPassthrough * tintMultiplier
    tintPassthrough(Color4(multipliedPassthrough, multipliedPassthrough, multipliedPassthrough, 1f))
  }

  fun tintPassthrough(color: Color4) {
    val tint_r = (16 * color.red).toInt()
    val tint_g = (16 * color.green).toInt()
    val tint_b = (16 * color.blue).toInt()

    // initialize lut
    val tbl = Lut()
    for (r in 0..15) {
      for (g in 0..15) {
        for (b in 0..15) {
          // set a mapping color for each RGB value in the (0-15)^3 range
          tbl.setMapping(r, g, b, r * tint_r + r / 4, g * tint_g + g / 4, b * tint_b + b / 4)
        }
      }
    }
    try {
      // apply it to the passthrough
      scene.setPassthroughLUT(tbl)
    } catch (e: Exception) {
      Log.e("LightingPassthroughHandler", "Error setPassthroughLUT", e)
    }
  }
}

data class TweenLightingPassthrough(val handler: LightingPassthroughHandler) {
  companion object {
    const val TINT_MULTIPLIER = 1
    const val LIGHTING_MULTIPLIER = 2
  }
}

class TweenLightingPassthroughAccessor : TweenAccessor<TweenLightingPassthrough> {
  override fun getValues(
      target: TweenLightingPassthrough,
      tweenType: Int,
      returnValues: FloatArray
  ): Int {
    // val component = target.entity.getComponent<Scale>()
    return when (tweenType) {
      TweenLightingPassthrough.TINT_MULTIPLIER -> {
        returnValues[0] = target.handler.tintMultiplier
        1 // Return number of values
      }
      TweenLightingPassthrough.LIGHTING_MULTIPLIER -> {
        returnValues[0] = target.handler.lightingMultiplier
        1 // Return number of values
      }
      else -> 0
    }
  }

  override fun setValues(target: TweenLightingPassthrough, tweenType: Int, newValues: FloatArray) {
    when (tweenType) {
      TweenLightingPassthrough.TINT_MULTIPLIER -> {
        target.handler.tintMultiplier = newValues[0]
      }
      TweenLightingPassthrough.LIGHTING_MULTIPLIER -> {
        target.handler.lightingMultiplier = newValues[0]
      }
    }
  }
}
