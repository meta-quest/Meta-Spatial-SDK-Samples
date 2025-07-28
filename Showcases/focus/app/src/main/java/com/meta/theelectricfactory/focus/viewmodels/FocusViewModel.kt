package com.meta.theelectricfactory.focus.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FocusViewModel : ViewModel() {
    private val _tasksListsHasChanged = MutableStateFlow<Int>(0)
    val tasksListsHasChanged = _tasksListsHasChanged.asStateFlow()

    fun refreshTasksPanel() {
        _tasksListsHasChanged.value++
    }

    private val _currentProjectUuid = MutableStateFlow<Int?>(null)
    val currentProjectUuid = _currentProjectUuid.asStateFlow()

    fun updateCurrentProjectUuid(uuid: Int?) {
        _currentProjectUuid.value = uuid
        refreshTasksPanel()
    }

    private val _currentTaskUuid = MutableStateFlow<Int?>(null)
    val currentTaskUuid = _currentTaskUuid.asStateFlow()

    fun setCurrentTaskUuid(uuid: Int?) {
        _currentTaskUuid.value = uuid
    }

    private val _currentTaskUpdated = MutableStateFlow<Int>(0)
    val currentTaskUpdated = _currentTaskUpdated.asStateFlow()

    fun updateCurrentSpatialTask() {
        _currentTaskUpdated.value++
    }

    private val _speakerIsOn = MutableStateFlow<Boolean>(false)
    val speakerIsOn = _speakerIsOn.asStateFlow()

    fun setSpeakerIsOn(isOn: Boolean) {
        _speakerIsOn.value = isOn
    }

    private val _selectedTool = MutableStateFlow<Int>(-1)
    val selectedTool = _selectedTool.asStateFlow()

    fun setSelectedTool(selectedTool: Int) {
        _selectedTool.value = selectedTool
    }

    companion object {
        val instance: FocusViewModel by lazy { FocusViewModel() }
    }
}