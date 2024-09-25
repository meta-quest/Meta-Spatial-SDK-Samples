// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai.enums

enum class WitAiTraitType(val result: String) {
  PNTLocation("pnt_location"),
  PNTQuestion("pnt_question"),
  WitBye("wit/bye"),
  WitGreetings("wit/greetings"),
  WitSentiment("wit/sentiment"),
  WitThanks("wit/thanks");

  companion object {
    fun fromResult(result: String): WitAiTraitType? {
      return entries.find { it.result == result }
    }
  }
}
