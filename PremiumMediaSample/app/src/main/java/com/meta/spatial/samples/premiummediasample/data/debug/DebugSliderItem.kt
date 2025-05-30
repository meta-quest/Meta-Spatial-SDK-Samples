// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.data.debug

data class DebugSliderItem(
    val label: String,
    val initialValue: Float,
    var onValueChanged: (value: Float) -> Unit,
    var steps: Int = 0,
    var range: ClosedFloatingPointRange<Float> = 0f..1f,
    var roundToInt: Boolean = false,
) : DebugItem()
