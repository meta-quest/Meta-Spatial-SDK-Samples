/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.service

import android.os.Handler
import android.os.Looper
import android.os.Message

interface IPCMessageHandler {
  fun handleIPCMessage(msg: Message)
}

class IncomingHandler(private val handler: IPCMessageHandler) : Handler(Looper.getMainLooper()) {
  override fun handleMessage(msg: Message) {
    handler.handleIPCMessage(msg)
  }
}
