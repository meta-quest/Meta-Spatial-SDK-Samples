// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.upload.js

import android.webkit.JavascriptInterface

class DriveConfigJavaScriptInterface(private val config: DriveConfig) {
  @JavascriptInterface
  fun getScopes(): String {
    return config.scopes
  }

  @JavascriptInterface
  fun getClientId(): String {
    return config.clientId
  }

  @JavascriptInterface
  fun getApiKey(): String {
    return config.apiKey
  }

  @JavascriptInterface
  fun getAppId(): String {
    return config.appId
  }
}

data class DriveConfig(
    val scopes: String,
    val clientId: String,
    val apiKey: String,
    val appId: String,
) {
  fun toJSONString(): String {
    return """
               {
                   "scopes": "$scopes",
                   "clientId": "$clientId",
                   "apiKey": "$apiKey",
                   "appId": "$appId"
               }
           """
               .trimIndent()
  }
}
