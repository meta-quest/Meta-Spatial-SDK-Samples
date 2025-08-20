/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.metrics

import com.meta.spatial.ovrmetrics.OVRMetric
import com.meta.spatial.ovrmetrics.OVRMetricDefinition
import com.meta.spatial.ovrmetrics.OVRMetricsGroup

class HeapMetrics() : OVRMetricsGroup() {

  companion object {
    private const val TAG = "OVRMetricsSystem"
  }

  init {
    addUsedMemory()
  }

  val groupName = "Custom"

  fun addUsedMemory() {
    val runtime =
        metrics.add(
            OVRMetric(
                OVRMetricDefinition(
                    Name = "heap",
                    DisplayName = "HEAP",
                    Group = groupName,
                    RangeMax = (Runtime.getRuntime().maxMemory() / (1024 * 1024)).toInt(),
                    ShowStat = true,
                )
            ) {
              usedMemory()
            }
        )
  }

  private fun usedMemory(): Int {
    val runtime = Runtime.getRuntime()
    val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
    return usedMemory.toInt()
  }
}
