package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.android.views.components.smart.EntityRow
import com.meta.pixelandtexel.scanner.android.views.components.smart.LightComposable
import com.meta.pixelandtexel.scanner.android.views.components.smart.MediaPlayerComposable
import com.meta.pixelandtexel.scanner.android.views.components.smart.SensorGrid
import com.meta.pixelandtexel.scanner.android.views.components.smart.TitleComposable
import com.meta.pixelandtexel.scanner.android.views.components.smart.WeatherGrid
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.EntityUiModel
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.domain.AttributeServices
import com.meta.pixelandtexel.scanner.models.devices.domain.DomainServices
import com.meta.pixelandtexel.scanner.models.devices.domain.LightDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.MediaPlayerDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.SensorDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.SwitchDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.WeatherDomain
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun DynamicSmartThingScreen(
    device: Device,
    viewModel: DynamicSmartThingViewmodel,
    modifier: Modifier = Modifier
) {
    fun onSwitchToggled(
        entity: EntityUiModel,
        newValue: Boolean,
    ) {
        var domainService = DomainServices.TURN_ON
        if (!newValue) {
            domainService = DomainServices.TURN_OFF
        }
        viewModel.onActionExecuted(entity, newValue, domainService)
    }

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(device) {
        viewModel.initialize(device)
    }

    val sensors =
        uiState.entities.filter { it.domain is SensorDomain || it.domain is WeatherDomain }
    val controllers = uiState.entities.filter { it.domain !is SensorDomain }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(MyPaddings.M),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(MyPaddings.M)
        ) {
            TitleComposable(
                title = uiState.deviceName,
                topButtonIcon = Icons.Default.Close,
                onClickTopButton = viewModel.onCloseSmartThing
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(MyPaddings.S)
            ) {
                if (controllers.isNotEmpty()) {
                    item {
                        Text(
                            text = "Controls",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = MyPaddings.S)
                        )
                    }
                }

                items(
                    items = controllers,
                    key = { it.id }
                ) { entity ->
                    if (entity.domain is SwitchDomain) {
                        EntityRow(
                            title = entity.name,
                            isUpdating = entity.isUpdating,
                            actualValue = entity.domain.value,
                            onSwitchToggle = { newValue ->
                                onSwitchToggled(entity, newValue)
                            },
                        )
                    } else if (entity.domain is MediaPlayerDomain) {
                        MediaPlayerComposable(
                            title = entity.name,
                            mediaPlayerDomain = entity.domain,
                            onStartChange = { newValue ->
                                onSwitchToggled(entity, newValue)
                            },
                            onMuteChange = { newValue ->
                                viewModel.onActionExecuted(
                                    entity,
                                    newValue,
                                    DomainServices.VOLUME_MUTE,
                                    AttributeServices.IS_VOLUME_MUTED
                                )
                            },
                            onVolumenChange = { newValue ->
                                viewModel.onActionExecuted(
                                    entity,
                                    newValue,
                                    DomainServices.VOLUME_SET,
                                    AttributeServices.VOLUME_LEVEL
                                )
                            },
                            onPlayChange = { newValue ->
                                viewModel.onActionExecuted(
                                    entity,
                                    true,
                                    DomainServices.MEDIA_PLAY,
                                )
                            },
                        )
                    } else if (entity.domain is LightDomain) {
                        LightComposable(
                            lightDomain = entity.domain,
                            onStateChange = { newValue ->
                                onSwitchToggled(entity, newValue)
                            },
                            onKelvinChange = { newValue ->
                                viewModel.onActionExecuted(
                                    entity,
                                    newValue,
                                    DomainServices.TURN_ON,
                                    AttributeServices.COLOR_TEMP_KELVIN
                                )
                            },
                            onBrightnessChange = { newValue ->
                                viewModel.onActionExecuted(
                                    entity,
                                    newValue,
                                    DomainServices.TURN_ON,
                                    AttributeServices.BRIGHTNESS
                                )
                            },
                        )
                    }
                }

                if (sensors.isNotEmpty()) {
                    item {
                        if (sensors[0].domain is WeatherDomain) {
                            WeatherGrid(
                                domain = sensors[0].domain as WeatherDomain,
                                modifier = Modifier
                                    .padding(top = MyPaddings.L)
                            )
                        } else {
                            SensorGrid(
                                sensors = sensors,
                                modifier = Modifier
                                    .padding(top = MyPaddings.L)
                            )
                        }
                    }
                }



                item {
                    Button(
                        onClick = { viewModel.onDisconnectDevice() }
                    ) {
                        Text(text = "Disconnect Common Device")
                    }
                }
            }
        }
    }


}


@Preview(widthDp = 400, heightDp = 100)
@Composable
fun EntityRowPreview() {
    EntityRow(
        title = "Living Room Light",
        isUpdating = false,
        actualValue = 0.2f,
        onSwitchToggle = {},
        onSliderChange = {}
    )
}