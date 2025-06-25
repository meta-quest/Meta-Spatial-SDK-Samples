/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data.debug

data class DebugToggleItem(
    val label: String,
    val initialValue: Boolean,
    var onValueChanged: (value: Boolean) -> Unit
) : DebugItem()
