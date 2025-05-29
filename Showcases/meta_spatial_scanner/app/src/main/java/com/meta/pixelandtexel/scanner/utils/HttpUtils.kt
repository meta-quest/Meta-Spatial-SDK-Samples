// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.utils

import android.util.Log
import java.net.NetworkInterface
import java.util.Collections

object HttpUtils {
  fun getIPAddress(): String? {
    try {
      val interfaces: List<NetworkInterface> =
          Collections.list(NetworkInterface.getNetworkInterfaces())

      for (networkInterface in interfaces) {
        val addresses = networkInterface.inetAddresses

        for (address in addresses) {
          if (!address.isLoopbackAddress && address.isSiteLocalAddress) {
            Log.i("HttpUtils", "ip address: ${address?.hostAddress}")
            return address.hostAddress
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }
}
