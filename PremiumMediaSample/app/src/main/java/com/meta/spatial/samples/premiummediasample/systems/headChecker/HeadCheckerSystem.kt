/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.systems.headChecker

import com.meta.spatial.core.SystemBase
import com.meta.spatial.samples.premiummediasample.getHeadPose

class HeadCheckerSystem(val callback: () -> Unit) : SystemBase() {

  override fun execute() {
    val pose = getHeadPose()
    if (pose.t.x != 0f || pose.t.y != 0f || pose.t.z != 0f) {
      systemManager.unregisterSystem<HeadCheckerSystem>()
      callback()
    }
  }
}
