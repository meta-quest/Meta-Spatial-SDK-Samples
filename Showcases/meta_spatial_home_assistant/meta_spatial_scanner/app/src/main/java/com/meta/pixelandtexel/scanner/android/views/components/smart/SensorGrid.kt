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
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.EntityUiModel
import com.meta.pixelandtexel.scanner.models.devices.domain.SensorDomain
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

const val MAX_COLUMNS = 3

@Composable
fun SensorGrid(
    sensors: List<EntityUiModel>,
    modifier: Modifier = Modifier
) {
    if (sensors.isNotEmpty()) {
        Column(modifier = modifier.padding(MyPaddings.S)) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = MyPaddings.M)
            )
            Text(
                text = "Sensors",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            sensors.chunked(MAX_COLUMNS).forEach { rowEntities ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowEntities.forEach { entity ->
                        val domain = entity.domain as SensorDomain
                        InfoColumn(
                            label = entity.name,
                            value = domain.value,
                        )
                    }
                }
            }
        }
    }
}
