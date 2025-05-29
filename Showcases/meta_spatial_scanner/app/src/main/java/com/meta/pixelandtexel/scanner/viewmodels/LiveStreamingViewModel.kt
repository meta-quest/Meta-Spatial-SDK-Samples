// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.viewmodels

import android.view.Surface
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LiveStreamingViewModel(
    initialPermissionGranted: Boolean = false, // for @Preview
    val requestPermission: () -> Unit,
    val onSurfaceAvailable: (Surface) -> Unit,
    val onSurfaceDestroyed: (Surface) -> Unit,
) : ViewModel() {
  private val _permissionGranted = mutableStateOf(initialPermissionGranted)
  private val _ipAddress = mutableStateOf<String?>(null)
  private val _aspectRatio = mutableFloatStateOf(16f / 9f)

  val permissionGranted: State<Boolean> = _permissionGranted
  val ipAddress: State<String?> = _ipAddress
  val aspectRatio: State<Float> = _aspectRatio

  fun setPermissionGranted(granted: Boolean) {
    _permissionGranted.value = granted
  }

  fun updateStreamingInfo(ip: String, port: Int) {
    _ipAddress.value = "http://$ip:$port"
  }

  fun updateAspectRatio(newAspectRatio: Float) {
    _aspectRatio.floatValue = newAspectRatio
  }
}
