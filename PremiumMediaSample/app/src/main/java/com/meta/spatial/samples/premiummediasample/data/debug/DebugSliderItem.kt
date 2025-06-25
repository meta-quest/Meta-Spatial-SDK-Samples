/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data.debug

data class DebugSliderItem(
    val label: String,
    val initialValue: Float,
    var onValueChanged: (value: Float) -> Unit,
    var steps: Int = 0,
    var range: ClosedFloatingPointRange<Float> = 0f..1f,
    var roundToInt: Boolean = false,
) : DebugItem()
