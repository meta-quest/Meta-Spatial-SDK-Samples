// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai.enums

enum class WitAiIntentType(val result: String) {
  GreetingIntent("greeting_intent"),
  PNTEarthFact("pnt_earth_fact"),
  PNTTrivia("pnt_trivia");

  companion object {
    fun fromResult(result: String): WitAiIntentType? {
      return entries.find { it.result == result }
    }
  }
}
