package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.models.devices.Device

class GetDevicesOfASmarthomeType(
    private val repository: SmartHomeRepository
) {
    suspend fun run(): List<Device> {
        val devices = repository.getDevices()
        return devices
    }

}