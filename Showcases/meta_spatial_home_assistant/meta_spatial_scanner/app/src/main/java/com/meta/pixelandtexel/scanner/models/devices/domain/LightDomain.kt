package com.meta.pixelandtexel.scanner.models.devices.domain


data class LightAttributes(
    val minColorTempKelvin: Int? = null,
    val maxColorTempKelvin: Int? = null,
    val minMireds: Int? = null,
    val maxMireds: Int? = null,
    val effectList: List<String>? = null,
    val supportedColorModes: List<String>? = null,
    val effect: String? = null,
    val colorMode: String? = null,
    val brightness: Int? = null,
    val colorTempKelvin: Int? = null,
    val colorTemp: Int? = null,
    val hsColor: List<Double>? = null,
    val rgbColor: List<Int>? = null,
    val xyColor: List<Double>? = null,
    val friendlyName: String? = null,
    val supportedFeatures: Int? = null
)

data class LightDomain(
    override val value: Boolean,
    override val services: List<DomainServices>,
    val attributes: LightAttributes
) : Domain