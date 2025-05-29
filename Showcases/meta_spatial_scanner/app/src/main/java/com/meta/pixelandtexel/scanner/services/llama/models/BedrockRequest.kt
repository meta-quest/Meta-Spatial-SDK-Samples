// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services.llama.models

/**
 * The request body passed to the **body** field of invokeModelWithResponseStream in the AWS Kotlin
 * SDK, including all of the currently supported inference parameters.
 *
 * The InvokeModelWithResponseStream API uses the model's native payload. Learn more about the
 * available inference parameters and response fields
 * [here](https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html).
 *
 * @property prompt The text string query to invoke the model with.
 * @property images The (optional) list of images to include in the query, assuming we're invoking a
 *   LLM model that supports Vision analysis.
 * @param temperature The temperature parameter for the model, controlling randomness (default is
 *   0.1f).
 * @param top_p The top_p parameter for the model, controlling nucleus sampling (default is 0.9f).
 * @property max_gen_len The maximum token length for the response.
 */
data class BedrockRequest(
    val prompt: String,
    val images: List<String>?,
    val temperature: Float = .5f,
    val top_p: Float = .9f,
    val max_gen_len: Int = 512
)
