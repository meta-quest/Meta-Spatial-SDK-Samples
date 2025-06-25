/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample

import com.meta.spatial.core.Vector3

const val SPAWN_DISTANCE = 1f
const val MAX_SPAWN_DISTANCE = 3f // 1.5f//5f
const val SCREEN_FOV = 70f // 1.5f//5f
const val ANCHOR_SPAWN_DISTANCE = 1.5f

val LIGHT_AMBIENT_COLOR = Vector3(5f)
val LIGHT_SUN_COLOR = Vector3(0f)
val LIGHT_SUN_DIRECTION = -Vector3(1.0f, 3.0f, 2.0f)

object TIMINGS {
  const val LIGHTING_PLAYING_FADE = 500
  const val LIGHTING_NOT_PLAYING_FADE = 300
  const val HOME_PANEL_FADE_BOTH = 500
  const val CONTROL_PANEL_FADE_BOTH = 400
  const val EXOPLAYER_FADE_BOTH = 500
}
