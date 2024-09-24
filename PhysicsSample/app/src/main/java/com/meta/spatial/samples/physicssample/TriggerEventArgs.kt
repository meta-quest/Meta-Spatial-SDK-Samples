/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import com.meta.spatial.core.DataModel
import com.meta.spatial.core.EventArgs
import com.meta.spatial.core.Vector3

class TriggerEventArgs(val direction: Vector3, val value: Float, dataModel: DataModel) :
    EventArgs(eventName = TriggerEventArgs.EVENT_NAME, dataModel = dataModel) {

  companion object {
    const val EVENT_NAME = "trigger_event"
  }
}
