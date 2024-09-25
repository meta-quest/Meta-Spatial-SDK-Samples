// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps

import android.graphics.Bitmap

interface IPanoramaServiceHandler {
  fun onFinished(bitmap: Bitmap)

  fun onError(reason: String)
}
