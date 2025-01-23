// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.util

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class HttpRetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialBackoff: Long = 1000,
) : Interceptor {

  // Central error handling block for errors which has impact all over the app
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    var tryCount = 0
    var responseOk = false
    var response: Response? = null

    while (tryCount < maxRetries && !responseOk) {
      try {
        Thread.sleep(initialBackoff * tryCount)
        response = chain.proceed(request)
        // Response is successful if it's not null and has a 2xx, 3xx or 4xx status code
        responseOk = response.code in 200..499
      } catch (e: Exception) {
        Timber.i("Request failed, retrying... ($tryCount)")
        response?.close()
      } finally {
        tryCount++
      }
    }
    return response ?: chain.proceed(request)
  }
}
