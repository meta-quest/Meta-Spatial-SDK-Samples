package com.meta.pixelandtexel.scanner.models.devices.domain

import com.meta.pixelandtexel.scanner.android.datasource.dto.Attributes


data class WeatherDomainAttributes(
    val temperature: Double? = null,
    val dewPoint: Double? = null,
    val temperatureUnit: String? = null,
    val humidity: Int? = null,
    val cloudCoverage: Double? = null,
    val uvIndex: Double? = null,
    val pressure: Double? = null,
    val pressureUnit: String? = null,
    val windBearing: Double? = null,
    val windSpeed: Double? = null,
    val windSpeedUnit: String? = null,
    val visibilityUnit: String? = null,
    val precipitationUnit: String? = null,
    val attribution: String? = null,
    val friendlyName: String? = null,
    val supportedFeatures: Int? = null,
) {
    companion object {
        fun fromAttributes(attributes: Attributes): WeatherDomainAttributes {
            return WeatherDomainAttributes(
                temperature = attributes.temperature,
                dewPoint = attributes.dewPoint,
                temperatureUnit = attributes.temperatureUnit,
                humidity = attributes.humidity,
                cloudCoverage = attributes.cloudCoverage,
                uvIndex = attributes.uvIndex,
                pressure = attributes.pressure,
                pressureUnit = attributes.pressureUnit,
                windBearing = attributes.windBearing,
                windSpeed = attributes.windSpeed,
                windSpeedUnit = attributes.windSpeedUnit,
                visibilityUnit = attributes.visibilityUnit,
                precipitationUnit = attributes.precipitationUnit,
                attribution = attributes.friendlyName,
                friendlyName = attributes.friendlyName,
                supportedFeatures = attributes.supportedFeatures
            )
        }
    }
}

data class WeatherDomain(
    override val value: String,
    override val services: List<DomainServices> = emptyList(),
    val attributes: WeatherDomainAttributes
) : Domain {}
