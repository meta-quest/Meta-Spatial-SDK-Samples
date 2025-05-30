// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

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
