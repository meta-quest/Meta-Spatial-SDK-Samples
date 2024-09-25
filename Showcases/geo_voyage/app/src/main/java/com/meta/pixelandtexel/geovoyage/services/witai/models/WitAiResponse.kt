// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai.models

/*
The response from WIT ai when it fully understands an `utterance`
 */
data class WitAiUnderstoodResponse(
    val text: String,
    val entities: Map<String, List<WitAiEntity>>,
    val intents: List<WitAiIntent>,
    val traits: Map<String, List<WitAiTrait>>,
)

sealed class WitAiResponse {
  data class Success(val data: WitAiUnderstoodResponse) : WitAiResponse()

  data class Error(val message: String) : WitAiResponse()
}
