package com.meta.pixelandtexel.scanner.models.devices

import com.meta.pixelandtexel.scanner.models.devices.domain.Domain

data class ThingEntity(
    val id: String,
    val domain: Domain
)
