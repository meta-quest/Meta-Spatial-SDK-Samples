/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.util.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun <T> StatefulWrapper(
    initialValue: T,
    content: @Composable (T, (T) -> Unit) -> Unit,
) {
  var state by remember { mutableStateOf(initialValue) }
  content(state) { newState -> state = newState }
}
