// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.data.debug

data class DebugEnumItem(
    val label: String,
    val initialValue: Enum<*>,
    var onValueChanged: (value: Enum<*>) -> Unit,
) : DebugItem()
