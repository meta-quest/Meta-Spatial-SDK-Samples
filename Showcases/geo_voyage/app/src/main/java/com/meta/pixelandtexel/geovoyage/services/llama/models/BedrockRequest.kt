// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.llama.models

// The InvokeModelWithResponseStream API uses the model's native payload.
// Learn more about the available inference parameters and response fields at:
// https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html
data class BedrockRequest(
    val prompt: String,
    val temperature: Float = .5f,
    val top_p: Float = .9f,
    val max_gen_len: Int = 512
)
