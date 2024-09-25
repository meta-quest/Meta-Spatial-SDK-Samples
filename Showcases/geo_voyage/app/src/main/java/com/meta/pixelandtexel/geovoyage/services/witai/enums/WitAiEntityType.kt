// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai.enums

enum class WitAiEntityType(val result: String) {
  WitContact("wit/contact"),
  WitDatetime("wit/datetime"),
  WitDistance("wit/distance"),
  WitLocation("wit/location"),
  PNTCulture("pnt_culture"),
  PNTEco("pnt_eco"),
  PNTEvent("pnt_event"),
  PNTGeo("pnt_geo");

  companion object {
    fun fromResult(result: String): WitAiEntityType? {
      return entries.find { it.result == result }
    }
  }
}
