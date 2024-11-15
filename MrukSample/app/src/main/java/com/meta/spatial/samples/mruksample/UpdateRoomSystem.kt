/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample

import com.meta.spatial.core.SystemBase
import com.meta.spatial.mruk.MRUKRoom

class UpdateRoomSystem(var mrukSampleActivity: MrukSampleActivity) : SystemBase() {

  private var prevRoom: MRUKRoom? = null
  private var prevRoomCount = -1

  override fun execute() {
    val roomsCount = mrukSampleActivity.mrukFeature.rooms.size
    val currentRoom = mrukSampleActivity.mrukFeature.getCurrentRoom()
    if (roomsCount != prevRoomCount || currentRoom != prevRoom) {
      prevRoom = currentRoom
      prevRoomCount = roomsCount
      mrukSampleActivity.currentRoomTextView?.setText(
          "Number of rooms: $roomsCount\nCurrent room: ${currentRoom?.anchor?.uuid ?: "None"}")
    }
  }
}
