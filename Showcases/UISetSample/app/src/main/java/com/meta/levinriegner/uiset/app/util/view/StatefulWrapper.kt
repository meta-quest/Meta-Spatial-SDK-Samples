// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.util.view

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
