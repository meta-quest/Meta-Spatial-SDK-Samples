// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.enums

enum class LlamaServerType(val value: Int) {
  OLLAMA(0),
  AWS_BEDROCK(1);

  companion object {
    fun fromValue(value: Int): LlamaServerType? {
      return LlamaServerType.entries.find { it.value == value }
    }
  }
}
