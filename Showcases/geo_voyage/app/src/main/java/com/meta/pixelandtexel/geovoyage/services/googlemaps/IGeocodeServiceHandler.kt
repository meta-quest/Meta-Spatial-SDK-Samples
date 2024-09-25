// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps

interface IGeocodeServiceHandler {
  fun onFinished(place: String?)

  fun onError(reason: String)
}
