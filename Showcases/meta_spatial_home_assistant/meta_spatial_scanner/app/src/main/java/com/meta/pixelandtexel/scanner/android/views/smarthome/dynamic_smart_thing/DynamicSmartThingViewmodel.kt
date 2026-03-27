package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetDeviceInfoUsecase
import com.meta.pixelandtexel.scanner.android.domain.usecases.UseActionDevice
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.EntityUiModel
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.SmartDeviceUiState
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.ThingEntity
import com.meta.pixelandtexel.scanner.models.devices.domain.AttributeServices
import com.meta.pixelandtexel.scanner.models.devices.domain.DomainServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DynamicSmartThingViewmodel(
    private val getDeviceInfoUsecase: GetDeviceInfoUsecase,
    private val useActionDevice: UseActionDevice,
    val onCloseSmartThing: () -> Unit = {},
    val onDisconnectDevice: () -> Unit = {}
) : ViewModel() {
    companion object {
        private const val WAIT_FOR_NEXT_REQUEST_MS = 5000L
        private const val DELAY_FOR_UPDATING_FROM_API_MS = 500L
    }
    private val _uiState = MutableStateFlow(SmartDeviceUiState())
    val uiState: StateFlow<SmartDeviceUiState> = _uiState

    fun initialize(device: Device) {
        val newState = SmartDeviceUiState(
            deviceName = device.name,
            entities = device.entityList.sortedBy { entity -> entity.domain.toString() }
                .map { entity ->
                    EntityUiModel.fromThingEntityWithoutDeviceName(
                        entity,
                        device.name
                    )
                }
        )

        viewModelScope.launch {
            updateAllEntitiesState(newState)
            updateStatePeriodically()
        }

    }

    suspend fun updateStatePeriodically() {
        while (true) {
            updateAllEntitiesState(_uiState.value)
            delay(WAIT_FOR_NEXT_REQUEST_MS)
        }

    }

    fun onActionExecuted(
        entity: EntityUiModel,
        newValue: Any,
        action: DomainServices,
        attribute: AttributeServices? = null
    ) {
        viewModelScope.launch {
            val entityId = entity.id

            if (action !in entity.domain.services) {
                return@launch
            }
            useActionDevice.run(
                thingId = entityId,
                action = action.serviceName,
                newValue = newValue,
                attribute = attribute?.serviceName
            )

            updateAllEntitiesState(_uiState.value)
        }

    }

    private suspend fun updateAllEntitiesState(newState: SmartDeviceUiState) {
        val listThings = newState.entities.map { ThingEntity(id = it.id, domain = it.domain) }
        delay(DELAY_FOR_UPDATING_FROM_API_MS)

        val updatedEntities = getDeviceInfoUsecase.run(listThings)

        val newEntities = updatedEntities.map { entity ->
            EntityUiModel.fromThingEntityWithoutDeviceName(
                thingEntity = entity,
                deviceName = newState.deviceName
            )
        }
        _uiState.update { currentState ->
            currentState.copy(
                entities = newEntities,
                deviceName = newState.deviceName
            )
        }
    }
}