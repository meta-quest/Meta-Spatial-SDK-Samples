// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai.enums

/** Values representing different starting results */
enum class WitAiStartResult(val result: Int) {
  SUCCESS(0),
  ALREADY_RUNNING(1),
  PERMISSION_DENIED(2),
  UNKNOWN_ERROR(3)
}
