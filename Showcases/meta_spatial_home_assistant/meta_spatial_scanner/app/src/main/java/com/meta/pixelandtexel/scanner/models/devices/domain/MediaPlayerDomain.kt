package com.meta.pixelandtexel.scanner.models.devices.domain


data class MediaPlayerAttributes(
    val volumeLevel: Float?,
    val isMuted: Boolean?,
    val source: List<String>?,
)

data class MediaPlayerDomain(
    override val value: Boolean,
    override val services: List<DomainServices>,
    val attributes: MediaPlayerAttributes
) : Domain
