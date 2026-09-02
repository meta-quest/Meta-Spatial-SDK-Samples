package com.meta.pixelandtexel.scanner.android.datasource.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class DeviceListResponseDto(
    @SerializedName("devices")
    val devices: List<SmartDeviceDto>
)

@Serializable
data class SmartDeviceDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("entities")
    val entities: List<String>
)
