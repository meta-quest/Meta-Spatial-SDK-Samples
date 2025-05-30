// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import com.meta.spatial.samples.premiummediasample.immersive.ImmersiveActivity

class IPCService : Service() {
  companion object {
    const val IMMERSIVE_CHANNEL = 0
    const val CONTROL_PANEL_CHANNEL = 1

    const val REGISTER_CLIENT: Int = 1
    const val UNREGISTER_CLIENT: Int = 2
    const val PASS_MSG: Int = 3
    const val NOTIFY_HOME_PANEL_DRAWN: Int = 4
  }

  /** Keeps track of all current registered clients. */
  val clientMap = mutableMapOf<Int, MutableList<Messenger>>()
  private val receiver: Messenger = Messenger(IncomingHandler())

  private var notifyImmersiveChannelFlag = false

  /** Handler of incoming messages from clients. */
  inner class IncomingHandler : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
      when (msg.what) {
        REGISTER_CLIENT -> {
          if (clientMap[msg.arg1] == null) {
            clientMap[msg.arg1] = mutableListOf()
          }
          clientMap[msg.arg1]?.add(msg.replyTo)

          if (msg.arg1 == IMMERSIVE_CHANNEL && notifyImmersiveChannelFlag) {
            notifyImmersiveOfHomePanelContent()
          }
        }
        UNREGISTER_CLIENT -> {
          if (clientMap[msg.arg1] == null || clientMap[msg.arg1]!!.size == 0) {
            return
          }
          clientMap[msg.arg1]?.remove(msg.replyTo)
        }
        PASS_MSG -> {
          val recipientID = msg.arg1
          // If sender is requesting a bad ID do nothing
          if (clientMap[recipientID] == null || clientMap[recipientID]!!.size == 0) {
            return
          }
          // Storing the ID of the message in arg2, moving this to what for the receiving process
          val outMsg =
              Message.obtain().apply {
                what = msg.arg2
                data = msg.data
              }

          sendToChannel(outMsg, recipientID)
        }
        NOTIFY_HOME_PANEL_DRAWN -> {
          if (clientMap[IMMERSIVE_CHANNEL] == null || clientMap[IMMERSIVE_CHANNEL]!!.size == 0) {
            notifyImmersiveChannelFlag = true
          } else {
            notifyImmersiveOfHomePanelContent()
          }
        }
        else -> super.handleMessage(msg)
      }
    }
  }

  private fun notifyImmersiveOfHomePanelContent() {
    notifyImmersiveChannelFlag = false
    val outMsg =
        Message.obtain().apply {
          what = ImmersiveActivity.Companion.ImmersiveActivityCodes.HOME_PANEL_CONNECTED.ordinal
        }
    sendToChannel(outMsg, IMMERSIVE_CHANNEL)
  }

  override fun onBind(intent: Intent): IBinder {
    return receiver.binder
  }

  fun sendToChannel(msg: Message, channel: Int) {
    val clientIterator = clientMap[channel]?.iterator() ?: return
    while (clientIterator.hasNext()) {
      val recipient = clientIterator.next()
      try {
        recipient.send(msg)
      } catch (e: RemoteException) {
        clientMap.remove(channel)
      }
    }
  }
}
