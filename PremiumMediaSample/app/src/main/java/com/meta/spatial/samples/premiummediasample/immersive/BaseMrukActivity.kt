/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.immersive

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKLoadDeviceResult
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.vr.VRFeature

abstract class BaseMrukActivity : AppSystemActivity() {
  private lateinit var mrukFeature: MRUKFeature

  override fun registerFeatures(): List<SpatialFeature> {
    mrukFeature = MRUKFeature(this, systemManager)
    return listOf(VRFeature(this), IsdkFeature(this, spatial, systemManager), mrukFeature)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (checkPermissionsThenRequest()) {
      loadMrukScene()
    }
  }

  fun checkPermissionsThenRequest(): Boolean {
    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "MRUK Permission has not been granted, request " + PERMISSION_USE_SCENE)
      requestPermissions(arrayOf(PERMISSION_USE_SCENE), REQUEST_CODE_PERMISSION_USE_SCENE)
      return false
    }
    return true
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    if (requestCode == REQUEST_CODE_PERMISSION_USE_SCENE &&
        permissions.size == 1 &&
        permissions[0] == PERMISSION_USE_SCENE) {
      val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
      if (granted) {
        Log.i(TAG, "Use scene permission has been granted")
        loadMrukScene()
      } else {
        Log.i(TAG, "Use scene permission was DENIED!")
      }
    }
  }

  private fun loadMrukScene() {
    Log.i(TAG, "Loading scene from device")
    mrukFeature.loadSceneFromDevice().whenComplete { result: MRUKLoadDeviceResult, _ ->
      if (result != MRUKLoadDeviceResult.SUCCESS) {
        Log.d(TAG, "Error loading scene from device: ${result}")
      } else {
        onLoadedMrukScene()
        Log.d(TAG, "Scene loaded from device")
      }
    }
  }

  open fun onLoadedMrukScene() {}

  companion object {
    internal const val TAG = "BaseMrukActivity"
    const val PERMISSION_USE_SCENE: String = "com.oculus.permission.USE_SCENE"
    const val REQUEST_CODE_PERMISSION_USE_SCENE: Int = 1
  }
}
