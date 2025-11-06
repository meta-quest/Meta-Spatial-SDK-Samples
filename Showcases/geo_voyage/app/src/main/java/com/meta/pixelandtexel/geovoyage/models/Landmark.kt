// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.models

import androidx.annotation.DrawableRes

data class Landmark(
    val meshName: String,
    val scale: Float,
    val yaw: Float,
    val zOffset: Float,
    val latitude: Float,
    val longitude: Float,
    val landmarkName: String,
    val description: String,
    @DrawableRes val panoResId: Int,
    val attribution: String,
) {
  val hasPanorama = panoResId != 0
}
