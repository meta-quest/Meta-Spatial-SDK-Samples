package com.meta.pixelandtexel.scanner.models.devices.domain

data class SensorDomain(
    override val value: String,
    override val services: List<DomainServices>
) : Domain
