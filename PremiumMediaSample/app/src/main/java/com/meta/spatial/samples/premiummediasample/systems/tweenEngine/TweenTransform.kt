/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.tweenEngine

import com.meta.spatial.core.Entity

data class TweenTransform(val entity: Entity) {
  companion object {
    const val POSITION_XYZ = 1
    const val ROTATION_WXYZ =
        2 // Warning, this precision can't rebuild and gets invalid pose errors in Open XR sometimes
    const val ROTATION_XYZ = 3
    const val POSE_XYZ_XYZ = 4
    const val POSE_XYZ_WXYZ = 5
    /*
    const val GLOBAL_POSITION_XYZ = 6
    const val GLOBAL_ROTATION_WXYZ = 7
    const val GLOBAL_ROTATION_XYZ = 8
    const val GLOBAL_POSE_XYZWXYZ = 9
     */
  }
}
