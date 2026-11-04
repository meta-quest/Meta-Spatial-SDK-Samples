package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state

import com.meta.pixelandtexel.scanner.models.devices.ThingEntity
import com.meta.pixelandtexel.scanner.models.devices.domain.Domain

data class EntityUiModel(
    val id: String,
    val name: String,
    val domain: Domain,
    val isUpdating: Boolean = false
) {
    companion object {
        fun fromThingEntityWithoutDeviceName(
            thingEntity: ThingEntity,
            deviceName: String
        ): EntityUiModel {
            val name =
                thingEntity.id.substringAfterLast('.').replace('_', ' ').replace(deviceName, "")
            return EntityUiModel(
                id = thingEntity.id,
                name = if (name.isBlank()) deviceName else name.trim(),
                domain = thingEntity.domain
            )
        }
    }
}