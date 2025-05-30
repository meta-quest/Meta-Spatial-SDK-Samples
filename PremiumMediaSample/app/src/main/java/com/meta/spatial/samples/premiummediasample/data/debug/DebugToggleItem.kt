// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.data.debug

data class DebugToggleItem(
    val label: String,
    val initialValue: Boolean,
    var onValueChanged: (value: Boolean) -> Unit
) : DebugItem()
