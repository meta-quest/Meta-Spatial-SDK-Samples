package com.meta.levinriegner.mediaview.data.samples.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SampleItem(
    @SerialName("drive_id") val driveId: String?,
    val name: String?,
)

@Serializable
data class SamplesList(
    val version: Int?,
    val items: List<SampleItem>?,
)