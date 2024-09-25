// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai.models

data class WitAiEntity(
    val id: String,
    val name: String,
    val role: String,
    val start: Int,
    val end: Int,
    val body: String,
    val confidence: Float,
    val value: Any,
)
