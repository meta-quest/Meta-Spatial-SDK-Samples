package com.meta.pixelandtexel.scanner.android.views.smarthome.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetDevicesOfASmarthomeType
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.smarthomedata.TypeSmartHomeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceSelectionViewModel(
    private val displayInfo: TypeSmartHomeInfo = TypeSmartHomeInfo.UNKNOWN,
    private val getDevicesOfASmarthomeType: GetDevicesOfASmarthomeType
) : ViewModel() {

    private val _options = MutableStateFlow<List<Device>>(emptyList())
    val options: StateFlow<List<Device>> = _options.asStateFlow()

    private val _deviceType = MutableStateFlow(TypeSmartHomeInfo.UNKNOWN)
    val deviceType: StateFlow<TypeSmartHomeInfo> = _deviceType.asStateFlow()

    fun loadOptions() {
        _deviceType.value = displayInfo

        viewModelScope.launch {

            _options.value = getDevicesOfASmarthomeType.run()
        }
    }
}