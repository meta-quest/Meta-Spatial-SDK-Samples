/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data

enum class CinemaState {
  Cinema,
  TV,
  Equirect180,
  Home
}

data class LightMultipliers(
    val passthroughMultiplier: Float = 1f,
    val lightingMultiplier: Float = 1f,
) {
  companion object {
    val CINEMA_PLAYING = LightMultipliers(0f, 1f)
    val CINEMA_PAUSED = LightMultipliers(0.1f, 1f)
    val TV_PLAYING = LightMultipliers(0.25f, 0.5f)
    val TV_PAUSED = LightMultipliers(1f, 0.5f)
    val EQUIRECT_180_PLAYING = LightMultipliers(0.25f, 0.5f)
    val EQUIRECT_180_PAUSED = LightMultipliers(1f, 0.5f)
    val HOME_PLAYING = LightMultipliers(0.5f, 0.0f)
    val HOME_PAUSED = LightMultipliers(0.5f, 0.0f)
  }
}

data class SceneLightingSettings(
    val playingLighting: LightMultipliers = LightMultipliers(),
    val pausedLighting: LightMultipliers = LightMultipliers()
) {
  companion object {
    val lightingDefaults =
        mapOf(
            CinemaState.Cinema to
                SceneLightingSettings(
                    LightMultipliers.CINEMA_PLAYING, LightMultipliers.CINEMA_PAUSED),
            CinemaState.TV to
                SceneLightingSettings(LightMultipliers.TV_PLAYING, LightMultipliers.TV_PAUSED),
            CinemaState.Equirect180 to
                SceneLightingSettings(
                    LightMultipliers.EQUIRECT_180_PLAYING, LightMultipliers.EQUIRECT_180_PAUSED),
            CinemaState.Home to
                SceneLightingSettings(LightMultipliers.HOME_PLAYING, LightMultipliers.HOME_PAUSED))
  }
}
