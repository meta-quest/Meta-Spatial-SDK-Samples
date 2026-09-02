// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.feature.objectdetection.android.views.android

import android.view.Surface

/**
 * A contract for components that can provide an Android [Surface]. Used by
 * CameraController to facilitate displaying the camera video feed on panels.
 */
interface ISurfaceProvider {
    val surface: Surface?
    val surfaceAvailable: Boolean
        get() = surface != null
}
