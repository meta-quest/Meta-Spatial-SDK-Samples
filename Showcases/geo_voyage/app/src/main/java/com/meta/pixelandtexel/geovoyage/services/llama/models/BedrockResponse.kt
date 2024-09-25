// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.llama.models

// The InvokeModelWithResponseStream API uses the model's native payload.
// Learn more about the available inference parameters and response fields at:
// https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html
data class BedrockResponse(
    val generation: String,
    val prompt_token_count: Int,
    val generation_token_count: Int,
    val stop_reason: String
)
