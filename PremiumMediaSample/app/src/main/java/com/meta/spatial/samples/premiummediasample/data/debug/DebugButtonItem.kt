// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.data.debug

data class DebugButtonItem(
    val label: String,
    var onClick: () -> Unit,
) : DebugItem()
