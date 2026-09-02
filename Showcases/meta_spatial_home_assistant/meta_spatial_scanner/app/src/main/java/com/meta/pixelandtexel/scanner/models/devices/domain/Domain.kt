package com.meta.pixelandtexel.scanner.models.devices.domain

sealed interface Domain {
    val value: Any
    val services: List<DomainServices>
}