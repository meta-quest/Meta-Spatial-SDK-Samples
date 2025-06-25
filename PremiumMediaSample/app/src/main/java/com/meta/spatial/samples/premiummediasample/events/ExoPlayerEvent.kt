/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.events

import com.meta.spatial.core.DataModel
import com.meta.spatial.core.EventArgs

class ExoPlayerEvent(eventName: String, dataModel: DataModel) :
    EventArgs(eventName = eventName, dataModel = dataModel) {
  companion object {
    const val ON_END = "ExoPlayerEventArgs.ON_END"
  }
}
