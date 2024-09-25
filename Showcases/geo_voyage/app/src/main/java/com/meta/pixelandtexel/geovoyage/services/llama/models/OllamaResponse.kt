// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.llama.models

// https://github.com/ollama/ollama/blob/main/docs/api.md#response
data class OllamaResponse(
    val model: String,
    val created_at: String,
    val response: String,
    val done: Boolean
)
