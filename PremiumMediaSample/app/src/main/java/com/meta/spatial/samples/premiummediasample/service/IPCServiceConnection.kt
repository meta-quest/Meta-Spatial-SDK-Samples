/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.service

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException

// Handler and ipcServiceID are nullable in case process wants to only send messages.
class IPCServiceConnection(
    private val context: Context,
    handler: IPCMessageHandler? = null,
    private val ipcChannel: Int? = null,
) : ServiceConnection {

  private var toServiceMessenger: Messenger? = null

  private val fromServiceMessenger: Messenger? =
      if (handler != null) Messenger(IncomingHandler(handler)) else null
  private var isBound: Boolean = false

  override fun onServiceConnected(className: ComponentName, service: IBinder) {
    toServiceMessenger = Messenger(service)
    if (fromServiceMessenger != null && ipcChannel != null) {
      messageService(IPCService.REGISTER_CLIENT, ipcChannel, mReplyTo = fromServiceMessenger)
    }
    isBound = true
  }

  override fun onServiceDisconnected(className: ComponentName) {
    toServiceMessenger = null
    isBound = false
  }

  fun bindService() {
    Intent(context, IPCService::class.java).also { intent ->
      context.bindService(intent, this, BIND_AUTO_CREATE)
    }
    isBound = true
  }

  fun unbindService() {
    if (isBound) {
      if (toServiceMessenger != null && ipcChannel != null) {
        messageService(IPCService.UNREGISTER_CLIENT, ipcChannel, mReplyTo = fromServiceMessenger)
      }
      context.unbindService(this)
      isBound = false
    }
  }

  fun messageService(
      mWhat: Int,
      mArg1: Int? = null,
      mArg2: Int? = null,
      bundle: Bundle? = null,
      mReplyTo: Messenger? = null
  ) {
    if (toServiceMessenger != null) {
      try {
        val msg: Message =
            Message.obtain().apply {
              what = mWhat
              if (mArg1 != null) {
                arg1 = mArg1
              }
              if (mArg2 != null) {
                arg2 = mArg2
              }
              data = bundle
              if (mReplyTo != null) {
                replyTo = mReplyTo
              }
            }
        toServiceMessenger!!.send(msg)
      } catch (e: RemoteException) {
        // Exception here is if Service crashed. Nothing to do.
      }
    }
  }

  fun messageProcess(toProcess: Int, messageCode: Int, bundle: Bundle? = null) {
    messageService(IPCService.PASS_MSG, toProcess, messageCode, bundle)
  }
}
