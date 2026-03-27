package com.meta.pixelandtexel.scanner.android.domain.repository

import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.ThingEntity

interface SmartHomeRepository {
    suspend fun getDevices(): List<Device>

    suspend fun getThingEntities(thingEntities: List<ThingEntity>): List<ThingEntity>

    suspend fun getActionForThing(
        thingId: String,
        action: String,
        newValue: Pair<String, Any>?
    ): Boolean

}