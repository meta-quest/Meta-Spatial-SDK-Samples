// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.feature.objectdetection.android.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ObjectLabelViewModel(objectName: String = "") : ViewModel() {
    private val _name = mutableStateOf(objectName)
    val name: State<String> = _name

    fun updateName(value: String) {
        _name.value = value.replaceFirstChar { it.uppercaseChar() }
    }
}
