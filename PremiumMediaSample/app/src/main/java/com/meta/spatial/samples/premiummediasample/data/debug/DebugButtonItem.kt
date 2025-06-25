/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data.debug

data class DebugButtonItem(
    val label: String,
    var onClick: () -> Unit,
) : DebugItem()
