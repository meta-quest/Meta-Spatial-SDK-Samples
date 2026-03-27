package com.meta.pixelandtexel.scanner.android.datasource.dto

import com.google.gson.annotations.SerializedName

data class ThingsResponseDto(
    @SerializedName("entity_id") val entityId: String,
    val state: String,
    @SerializedName("attributes") val attributes: Attributes?,
    @SerializedName("last_changed") val lastChanged: String?,
    @SerializedName("last_reported") val lastReported: String?,
    @SerializedName("last_updated") val lastUpdated: String?,
    val context: Context?
)

data class Attributes(
    @SerializedName("device_class") val deviceClass: String? = null,
    @SerializedName("friendly_name") val friendlyName: String? = null,
    @SerializedName("state_class") val stateClass: String? = null,
    @SerializedName("unit_of_measurement") val unitOfMeasurement: String? = null,
    @SerializedName("volume_level") val volumeLevel: Float? = null,
    @SerializedName("is_volume_muted") val isVolumeMuted: Boolean? = null,
    @SerializedName("source_list") val source: List<String>? = null,
    @SerializedName("min_color_temp_kelvin") val minColorTempKelvin: Int? = null,
    @SerializedName("max_color_temp_kelvin") val maxColorTempKelvin: Int? = null,
    @SerializedName("min_mireds") val minMireds: Int? = null,
    @SerializedName("max_mireds") val maxMireds: Int? = null,
    @SerializedName("effect_list") val effectList: List<String>? = null,
    @SerializedName("supported_color_modes") val supportedColorModes: List<String>? = null,
    @SerializedName("effect") val effect: String? = null,
    @SerializedName("color_mode") val colorMode: String? = null,
    @SerializedName("brightness") val brightness: Int? = null,
    @SerializedName("color_temp_kelvin") val colorTempKelvin: Int? = null,
    @SerializedName("color_temp") val colorTemp: Int? = null,
    @SerializedName("hs_color") val hsColor: List<Double>? = null,
    @SerializedName("rgb_color") val rgbColor: List<Int>? = null,
    @SerializedName("xy_color") val xyColor: List<Double>? = null,
    @SerializedName("supported_features") val supportedFeatures: Int? = null,
    @SerializedName("temperature") val temperature: Double? = null,
    @SerializedName("dew_point") val dewPoint: Double? = null,
    @SerializedName("temperature_unit") val temperatureUnit: String? = null,
    @SerializedName("humidity") val humidity: Int? = null,
    @SerializedName("cloud_coverage") val cloudCoverage: Double? = null,
    @SerializedName("uv_index") val uvIndex: Double? = null,
    @SerializedName("pressure") val pressure: Double? = null,
    @SerializedName("pressure_unit") val pressureUnit: String? = null,
    @SerializedName("wind_bearing") val windBearing: Double? = null,
    @SerializedName("wind_speed") val windSpeed: Double? = null,
    @SerializedName("wind_speed_unit") val windSpeedUnit: String? = null,
    @SerializedName("visibility_unit") val visibilityUnit: String? = null,
    @SerializedName("precipitation_unit") val precipitationUnit: String? = null,
    @SerializedName("attribution") val attribution: String? = null,
)

data class Context(
    val id: String,
    @SerializedName("parent_id") val parentId: String? = null,
    @SerializedName("user_id") val userId: String? = null
)