// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services.llama.models

/**
 * The response returned from invokeModelWithResponseStream in the AWS Kotlin SDK, parsed from the
 * JSON response.
 *
 * The InvokeModelWithResponseStream API uses the model's native payload. Learn more about the
 * available inference parameters and response fields
 * [here](https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html).
 *
 * @property generation The generated text.
 * @property prompt_token_count The number of tokens in the prompt.
 * @property generation_token_count The number of tokens in the generated text.
 * @property stop_reason The reason why the response stopped generating text. Possible values are
 *   **stop** and **length**.
 */
data class BedrockResponse(
    val generation: String,
    val prompt_token_count: Int,
    val generation_token_count: Int,
    val stop_reason: String,
)
