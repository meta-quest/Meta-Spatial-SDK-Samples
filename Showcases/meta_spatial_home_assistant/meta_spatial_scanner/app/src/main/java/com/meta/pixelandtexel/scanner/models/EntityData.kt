package com.meta.pixelandtexel.scanner.models

import com.meta.pixelandtexel.scanner.models.smarthomedata.SmartHomeInfoRequest

data class EntityData(
    val entityId: Int,
    val data: SmartHomeInfoRequest
)
