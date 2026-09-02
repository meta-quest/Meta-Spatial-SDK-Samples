package com.meta.pixelandtexel.scanner.android.datasource.mapper

import com.meta.pixelandtexel.scanner.android.datasource.dto.DeviceListResponseDto
import com.meta.pixelandtexel.scanner.android.datasource.dto.SmartDeviceDto
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.ThingEntity

object DeviceMapper {
    fun map(dto: DeviceListResponseDto): List<Device> {
        return dto.devices.map { mapDevice(it) }
    }

    private fun mapDevice(dto: SmartDeviceDto): Device {
        return Device(
            name = dto.name,
            entityList = dto.entities.mapNotNull { mapEntity(it) }
        )
    }

    private fun mapEntity(entityId: String): ThingEntity? {
        val domain = DomainMapper.fromEntityId(entityId) ?: return null

        return ThingEntity(
            id = entityId,
            domain = domain
        )
    }
}