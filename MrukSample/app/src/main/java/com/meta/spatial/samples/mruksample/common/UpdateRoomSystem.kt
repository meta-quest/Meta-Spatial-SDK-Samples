/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.common

import android.widget.TextView
import com.meta.spatial.core.SystemBase
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKRoom

class UpdateRoomSystem(
    private val mrukFeature: MRUKFeature,
    private val getRoomTextView: () -> TextView?
) : SystemBase() {

  private var prevRoom: MRUKRoom? = null
  private var prevRoomCount = -1

  override fun execute() {
    val roomsCount = mrukFeature.rooms.size
    val currentRoom = mrukFeature.getCurrentRoom()
    if (roomsCount != prevRoomCount || currentRoom != prevRoom) {
      prevRoom = currentRoom
      prevRoomCount = roomsCount
      getRoomTextView()
          ?.setText(
              "Number of rooms: $roomsCount\nCurrent room: ${currentRoom?.anchor?.uuid ?: "None"}")
    }
  }
}
