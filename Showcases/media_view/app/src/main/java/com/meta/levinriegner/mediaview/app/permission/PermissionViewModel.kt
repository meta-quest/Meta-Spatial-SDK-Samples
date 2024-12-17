// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.permission

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<PermissionState>(PermissionState.CheckPermissionState)
    val state = _state.asStateFlow()

    fun onCheckPermissionResult(granted: Boolean) {
        Timber.i("Storage permission status granted: $granted")
        if (granted) {
            _state.value = PermissionState.PermissionAccepted
        } else {
            _state.value = PermissionState.RequestPermission
        }
    }

    fun onStoragePermissionGranted() {
        Timber.i("Storage permission granted")
        _state.value = PermissionState.PermissionAccepted
    }

    fun onStoragePermissionDenied() {
        Timber.i("Storage permission denied")
        _state.value = PermissionState.PermissionDenied
    }
}
