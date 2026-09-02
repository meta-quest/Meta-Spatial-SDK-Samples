package com.meta.pixelandtexel.scanner.android.views.components.smart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meta.pixelandtexel.scanner.models.devices.domain.WeatherDomain
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun WeatherGrid(
    domain: WeatherDomain,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(MyPaddings.S)) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = MyPaddings.M)
        )
        Text(
            text = "Sensors",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        InfoColumn(
            label = "Weather Condition",
            value = domain.value,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (domain.attributes.temperature != null) {
                InfoColumn(
                    label = "Temperature",
                    value = "${domain.attributes.temperature} ${domain.attributes.temperatureUnit ?: "°C"}",
                )
            }
            if (domain.attributes.humidity != null) {
                InfoColumn(
                    label = "Humidity",
                    value = "${domain.attributes.humidity}%",
                )
            }

            if (domain.attributes.windSpeed != null) {
                InfoColumn(
                    label = "Wind Speed",
                    value = "${domain.attributes.windSpeed} ${domain.attributes.windSpeedUnit ?: "m/s"}",
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (domain.attributes.pressure != null) {
                InfoColumn(
                    label = "Pressure",
                    value = "${domain.attributes.pressure} ${domain.attributes.pressureUnit ?: "hPa"}",
                )
            }
            if (domain.attributes.uvIndex != null) {
                InfoColumn(
                    label = "UV Index",
                    value = domain.attributes.uvIndex.toString(),
                )
            }
            if (domain.attributes.cloudCoverage != null) {
                InfoColumn(
                    label = "Cloud Coverage",
                    value = "${domain.attributes.cloudCoverage}%",
                )
            }
        }
    }
}


