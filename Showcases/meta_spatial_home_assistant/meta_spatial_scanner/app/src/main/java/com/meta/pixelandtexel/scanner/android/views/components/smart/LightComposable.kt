package com.meta.pixelandtexel.scanner.android.views.components.smart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.models.devices.domain.LightDomain

@Composable
fun LightComposable(
    modifier: Modifier = Modifier,
    lightDomain: LightDomain,
    onStateChange: (Boolean) -> Unit,
    onKelvinChange: (Float) -> Unit,
    onBrightnessChange: (Float) -> Unit
) {
    val attributes = lightDomain.attributes
    val isLightOn = lightDomain.value

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EntityRow(
                title = "Light State",
                isUpdating = false,
                actualValue = lightDomain.value,
                onSwitchToggle = onStateChange,
            )

            if (attributes.minColorTempKelvin != null && attributes.maxColorTempKelvin != null && lightDomain.attributes.colorTempKelvin != null) {
                EntityRow(
                    title = "Temperature (K)",
                    isUpdating = false,
                    actualValue = lightDomain.attributes.colorTempKelvin.toFloat(),
                    onSliderChange = onKelvinChange,
                    minMaxSlider = attributes.minColorTempKelvin.toFloat()..attributes.maxColorTempKelvin.toFloat(),
                    enabled = isLightOn
                )
            }

            if (attributes.brightness != null) {
                EntityRow(
                    title = "Brightness",
                    isUpdating = false,
                    actualValue = lightDomain.attributes.brightness.toFloat(),
                    onSliderChange = onBrightnessChange,
                    minMaxSlider = 0f..255f,
                    enabled = isLightOn
                )
            }
        }
    }
}
