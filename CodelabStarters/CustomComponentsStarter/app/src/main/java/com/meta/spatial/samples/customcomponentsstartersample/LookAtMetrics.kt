/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.customcomponentsstartersample

import android.util.Log
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.Vector3
import com.meta.spatial.ovrmetrics.OVRMetric
import com.meta.spatial.ovrmetrics.OVRMetricDefinition
import com.meta.spatial.ovrmetrics.OVRMetricsGroup
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import java.text.DecimalFormat

class LookAtMetrics(init: LookAtMetrics.() -> Unit = {}) : OVRMetricsGroup() {

  companion object {
    private const val TAG = "OVRMetricsSystem"
  }

  val groupName = "LookAt"

  init {
    init()
  }

  fun pos() {
    overlayMessages.add({
      val pos = getTargetPosition()
      val formater = DecimalFormat("#,##0.00")
      "Target Pos: (${formater.format(pos.x)}, ${formater.format(pos.y)}, ${formater.format(pos.z)})"
    })
  }

  fun pitch() {
    metrics.add(
        OVRMetric(
            OVRMetricDefinition(
                Name = "pitch",
                DisplayName = "pitch",
                Group = groupName,
                RangeMax = 360,
                ShowStat = false,
            ),
            { angle(getRotation().x) }))
  }

  fun yaw() {
    metrics.add(
        OVRMetric(
            OVRMetricDefinition(
                Name = "yaw",
                DisplayName = "yaw",
                Group = groupName,
                RangeMax = 360,
                ShowStat = false,
            ),
            { angle(getRotation().y) }))
  }

  fun roll() {
    metrics.add(
        OVRMetric(
            OVRMetricDefinition(
                Name = "roll",
                DisplayName = "roll",
                Group = groupName,
                RangeMax = 360,
                ShowStat = false,
            ),
            { angle(getRotation().z) }))
  }

  private fun getLookAtEnt(): Entity? {
    return runCatching { Query.where { has(LookAt.id, Transform.id, Mesh.id) }.eval().last() }
        .getOrNull()
  }

  private fun getTargetPosition(): Vector3 {
    try {
      return getLookAtEnt()
          ?.getComponent<LookAt>()
          ?.target
          ?.getComponent<Transform>()
          ?.transform
          ?.t!!
    } catch (e: Exception) {
      Log.e(TAG, "getTargetPosition Exception: ${e}" + e.message)
      return Vector3(0f)
    }
  }

  private fun getRotation(): Vector3 {
    try {
      val ent = getLookAtEnt()
      val lookAt = ent?.getComponent<LookAt>()
      val eyePose = ent?.getComponent<Transform>()?.transform
      val targetPose = lookAt?.target?.getComponent<Transform>()?.transform
      if (eyePose != null && targetPose != null) {
        return Quaternion.lookRotation((eyePose.t - targetPose.t)).toEuler()
      }
    } catch (e: Exception) {
      Log.e(TAG, "getRotation Exception: ${e}")
    }
    return Vector3(0f)
  }

  private fun angle(x: Float): Int {
    return ((x.toInt() % 360) + 360) % 360
  }
}
