// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.samples.model

import kotlinx.serialization.Serializable

@Serializable
data class SampleItem(
    val url: String?,
    val name: String?,
)

@Serializable
data class SamplesList(
    val version: Int?,
    val items: List<SampleItem>?,
)