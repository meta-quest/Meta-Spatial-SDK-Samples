// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.llama.models

// https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-completion
data class OllamaRequest(
    val prompt: String,
    val options: OllamaRequestParams = OllamaRequestParams(),
    val model: String = "llama3",
    val stream: Boolean = true
)

// https://github.com/ollama/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values
data class OllamaRequestParams(
    val temperature: Float = .8f,
    val top_p: Float = .9f,
    val num_predict: Int = 128
)
