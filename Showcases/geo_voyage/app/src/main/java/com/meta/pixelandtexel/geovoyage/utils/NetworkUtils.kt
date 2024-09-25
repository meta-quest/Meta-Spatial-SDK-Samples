// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.utils

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/** Our utility class for all network requests to have some common OkHttpClient settings */
object NetworkUtils {
  private const val TAG: String = "NetworkUtils"

  val client: OkHttpClient = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build()

  fun makeHttpRequest(request: Request, onResponse: (response: Response) -> Unit) {
    client.newCall(request).execute().use(onResponse)
  }
}
