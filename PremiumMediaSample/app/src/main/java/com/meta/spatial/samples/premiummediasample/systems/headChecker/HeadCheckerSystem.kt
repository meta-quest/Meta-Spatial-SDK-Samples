// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

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
