// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.llama

interface IQueryLlamaServiceHandler {
  fun onStreamStart() {}

  fun onPartial(partial: String) {}

  fun onFinished(answer: String)

  fun onError(reason: String)
}
