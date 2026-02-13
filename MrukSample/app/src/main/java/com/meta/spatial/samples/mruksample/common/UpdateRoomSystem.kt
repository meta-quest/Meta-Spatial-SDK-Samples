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

/**
 * System for monitoring and displaying MRUK room information.
 *
 * This system tracks changes to the current room and the total number of rooms in the MRUK feature.
 * When changes are detected, it updates the provided TextView with the current room count and the
 * UUID of the active room.
 *
 * @property mrukFeature The MRUK feature instance to monitor for room changes
 * @property getRoomTextView Callback function that provides the TextView to update with room
 *   information
 */
class UpdateRoomSystem(
    private val mrukFeature: MRUKFeature,
    private val getRoomTextView: () -> TextView?,
) : SystemBase() {

  private var prevRoom: MRUKRoom? = null
  private var prevRoomCount = -1

  override fun execute() {
    val roomCount = mrukFeature.rooms.size
    val currentRoom = mrukFeature.getCurrentRoom()
    if (roomCount != prevRoomCount || currentRoom != prevRoom) {
      prevRoom = currentRoom
      prevRoomCount = roomCount
      getRoomTextView()?.text =
          """Number of rooms: $roomCount
Current room: ${currentRoom?.anchor?.uuid ?: "None"}"""
    }
  }
}
