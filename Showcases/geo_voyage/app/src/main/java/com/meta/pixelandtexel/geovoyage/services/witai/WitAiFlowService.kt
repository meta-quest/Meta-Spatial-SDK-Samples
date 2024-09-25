// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai

import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.services.witai.enums.WitAiEntityType
import com.meta.pixelandtexel.geovoyage.services.witai.enums.WitAiIntentType
import com.meta.pixelandtexel.geovoyage.services.witai.enums.WitAiTraitType
import com.meta.pixelandtexel.geovoyage.services.witai.models.WitAiUnderstoodResponse

/*
Decide which path the app should flow based upon returns from wit ai service
 */
object WitAIFlowService {
  private const val TAG: String = "WitAIFlowService"

  // allowed entities for llama
  private val allowedEntitiesForLlama = WitAiEntityType.entries.toSet()
  private val allowedIntentsForLLama = WitAiIntentType.entries.toSet()
  private val allowedTraitsForLlama =
      setOf(
          WitAiTraitType.PNTLocation,
          WitAiTraitType.PNTQuestion,
          WitAiTraitType.WitSentiment,
      )

  fun shouldSendResponseToLlama(response: WitAiUnderstoodResponse): Boolean {
    val isFilteringEnabled = SettingsService.get(SettingsKey.WIT_AI_FILTERING_ENABLED, true)
    if (!isFilteringEnabled) {
      return true
    }

    // accumulate the Wit.ai objects found within our response
    val responseEntities = getNamedEntitiesFromResponse(response)
    val responseIntents = getNamedIntentsFromResponse(response)

    // determine how many of our response Wit.ai objects intersect with our
    // predetermined list of objects which are acceptable for a Llama query
    val numEntitiesMatch = responseEntities.intersect(allowedEntitiesForLlama).size
    val numIntentsMatch = responseIntents.intersect(allowedIntentsForLLama).size

    return numEntitiesMatch > 0
  }

  // looks at the entities in each entity group on the response and then returns all the matches
  private fun getNamedEntitiesFromResponse(
      response: WitAiUnderstoodResponse
  ): Set<WitAiEntityType> {
    return response.entities.values
        .flatten()
        .mapNotNull { WitAiEntityType.fromResult(it.name) }
        .toSet()
  }

  // looks at the name of each response intent and returns all of the matches
  private fun getNamedIntentsFromResponse(response: WitAiUnderstoodResponse): Set<WitAiIntentType> {
    return response.intents.mapNotNull { WitAiIntentType.fromResult(it.name) }.toSet()
  }

  // looks at the named key for each response trait and returns all the matches
  private fun getNamedTraitsFromResponse(response: WitAiUnderstoodResponse): Set<WitAiTraitType> {
    return response.traits.keys.mapNotNull { WitAiTraitType.fromResult(it) }.toSet()
  }
}
