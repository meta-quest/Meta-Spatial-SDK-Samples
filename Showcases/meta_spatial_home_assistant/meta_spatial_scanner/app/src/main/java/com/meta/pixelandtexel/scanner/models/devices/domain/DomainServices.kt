package com.meta.pixelandtexel.scanner.models.devices.domain

enum class DomainServices(val serviceName: String) {
    TURN_ON("turn_on"),
    TURN_OFF("turn_off"),
    VOLUME_SET("volume_set"),
    VOLUME_MUTE("volume_mute"),
    MEDIA_PLAY("media_play"),
}