// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.views.android

import android.view.Surface

/**
 * A contract for components that can provide an Android [android.view.Surface]. Used by
 * CameraController to facilitate displaying the camera video feed on panels.
 */
interface ISurfaceProvider {
  val surface: Surface?
  val surfaceAvailable: Boolean
    get() = surface != null
}
