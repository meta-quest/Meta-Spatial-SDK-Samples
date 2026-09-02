package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state

data class SmartDeviceUiState(
    val deviceName: String = "",
    val isLoading: Boolean = false,
    val entities: List<EntityUiModel> = emptyList()
)