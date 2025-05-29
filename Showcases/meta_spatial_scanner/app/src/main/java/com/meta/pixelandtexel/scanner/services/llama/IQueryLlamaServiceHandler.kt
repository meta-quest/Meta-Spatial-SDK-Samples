// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services.llama

/**
 * Interface for handling callbacks from the [QueryLlamaService] during interaction with the Llama
 * LLM via AWS Bedrock.
 *
 * Implementations of this interface should react to various stages of the model invocation
 * lifecycle, including the start of a response stream, reception of partial results, completion of
 * the full answer, and any errors encountered.
 */
interface IQueryLlamaServiceHandler {
  /** Called when the response stream from the Llama model begins. (Optional) */
  fun onStreamStart() {}

  /**
   * Called incrementally as parts of the Llama model's response are received, allowing for
   * processing or displaying the response as it's being generated. (Optional)
   *
   * @param partial The partial response string received so far. This accumulates with each call
   *   until [onFinished] is invoked.
   */
  fun onPartial(partial: String) {}

  /**
   * Called when the Llama model has finished generating its complete response.
   *
   * @param answer The final and complete answer string from the model.
   */
  fun onFinished(answer: String)

  /**
   * Called if an error occurs at any point during the query submission or response processing.
   *
   * @param reason A string describing the error that occurred.
   */
  fun onError(reason: String)
}
