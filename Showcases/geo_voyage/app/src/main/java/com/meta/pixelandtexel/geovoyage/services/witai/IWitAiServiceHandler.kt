// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai

import com.meta.pixelandtexel.geovoyage.services.witai.models.WitAiUnderstoodResponse

interface IWitAiServiceHandler {
  fun onStartedListening() {}

  fun onAmplitudeChanged(amplitude: Int) {}

  fun onFinishedListening() {}

  fun onStreamStart() {}

  fun onPartial(partial: String) {}

  fun onFinished(result: WitAiUnderstoodResponse)

  fun onCanceled()

  fun onError(reason: String)
}
