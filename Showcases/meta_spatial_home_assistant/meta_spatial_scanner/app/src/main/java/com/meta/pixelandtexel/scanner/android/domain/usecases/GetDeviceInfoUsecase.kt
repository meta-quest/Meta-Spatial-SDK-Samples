package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.models.devices.ThingEntity

class GetDeviceInfoUsecase(
    private val repository: SmartHomeRepository
) {
    suspend fun run(thingEntities: List<ThingEntity>): List<ThingEntity> {
        return repository.getThingEntities(thingEntities)
    }

}