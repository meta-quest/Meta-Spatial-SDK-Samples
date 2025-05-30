// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.data

import java.io.Serializable

data class HomeItem(
    val id: String,
    var description: Description? = null,
    val thumbId: Int,
    val media: MediaSource,
    val showInMenu: Boolean = true,
) : Serializable
