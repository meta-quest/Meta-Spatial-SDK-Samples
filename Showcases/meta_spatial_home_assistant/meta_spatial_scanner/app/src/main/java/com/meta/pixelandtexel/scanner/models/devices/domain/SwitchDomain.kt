package com.meta.pixelandtexel.scanner.models.devices.domain

data class SwitchDomain(
    override val value: Boolean,
    override val services: List<DomainServices>
) : Domain
