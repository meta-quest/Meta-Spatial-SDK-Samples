/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.spatialvideosample

import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.audio.ChannelMixingMatrix
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.Transform
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class SpatialAudioSystem(
    val panner: ChannelMixingAudioProcessor,
    val activity: SpatialVideoSampleActivity,
) : SystemBase() {
  override fun execute() {
    val head =
        Query.where { has(AvatarAttachment.id) }
            .filter { by(AvatarAttachment.typeData).isEqualTo("head") }
            .eval()
            .first()
    val headPose = head.getComponent<Transform>().transform
    val q = Query.where { has(SpatializedAudioPanel.id) }
    for (entity in q.eval()) {
      val panelPose = entity.getComponent<Transform>().transform
      // get direction from head to panel
      val direction = panelPose.t - headPose.t
      val distance = direction.length()

      var attenuation = calculateAttenuation(distance)

      val local_direction = headPose.q.inverse() * direction
      val normalized_local_direction = local_direction.normalize()

      // Calculate the angle between the x-axis and the projection of the vector onto the x-z plane.
      // When the x-axis is 0, you are facing forward.
      val angle = atan2(normalized_local_direction.z, normalized_local_direction.x)
      // Map the angle to a range of 0 to 2π.
      val mapped_angle = (angle + PI) % (2.0f * PI)

      // Calculate the left and right audio levels based on the mapped angle and the attenuation.
      // The angle is divided by 2 before being passed to the cos and sin functions. This is because
      // the stereo panning effect is most pronounced when the source is directly to the left or
      // right of the listener, which corresponds to an angle of π/2 or 3π/2 respectively.
      // The square of the cos and sin functions is taken to make the panning effect more
      // pronounced. This is because the cos and sin functions return values between -1 and 1, and
      // squaring these values makes them always positive and emphasizes the differences.
      val left_level = attenuation * cos(mapped_angle / 2) * cos(mapped_angle / 2)
      val right_level = attenuation * sin(mapped_angle / 2) * sin(mapped_angle / 2)
      panner.putChannelMixingMatrix(
          ChannelMixingMatrix(
              2,
              2,
              floatArrayOf(
                  left_level.toFloat(),
                  right_level.toFloat(),
                  left_level.toFloat(),
                  right_level.toFloat(),
              ),
          ))
    }
  }

  /**
   * Inverse relationship between distance and attenuation, where we maximize attenuation at ~1.67
   * within 1.5m and minimize attenuation at 0.25 beyond 10m.
   */
  fun calculateAttenuation(distance: Float): Float {
    if (!activity.inMrMode) {
      return 1.0f
    }
    if (distance <= 1.5f) {
      return 5f / 3f
    }
    if (distance >= 10f) {
      return 0.25f
    }
    return 2.5f / distance
  }
}
