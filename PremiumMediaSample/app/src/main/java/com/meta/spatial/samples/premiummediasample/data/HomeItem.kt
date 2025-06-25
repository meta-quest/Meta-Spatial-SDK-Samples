/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data

import java.io.Serializable

data class HomeItem(
    val id: String,
    var description: Description? = null,
    val thumbId: Int,
    val media: MediaSource,
    val showInMenu: Boolean = true,
) : Serializable
