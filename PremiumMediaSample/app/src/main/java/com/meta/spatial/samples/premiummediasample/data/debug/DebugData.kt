/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data.debug

import java.io.Serializable

data class DebugData(var items: MutableList<DebugItem>) : Serializable
