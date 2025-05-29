// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services.llama

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.core.graphics.scale
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest
import com.google.gson.Gson
import com.meta.pixelandtexel.scanner.BuildConfig
import com.meta.pixelandtexel.scanner.services.llama.models.BedrockRequest
import com.meta.pixelandtexel.scanner.services.llama.models.BedrockResponse
import java.io.ByteArrayOutputStream

/**
 * Service object for interacting with a Llama model via AWS Bedrock. Handles the construction of
 * requests, communication with the Bedrock runtime, and processing of streamed responses.
 */
object QueryLlamaService {
  private const val TAG: String = "QueryLlamaService"
  private const val MAX_IMAGE_DIMENSION = 1120

  private val gson = Gson()
  private var bedrockClient: BedrockRuntimeClient

  init {
    val credentials = StaticCredentialsProvider {
      accessKeyId = BuildConfig.AWS_ACCESS_KEY
      secretAccessKey = BuildConfig.AWS_SECRET_KEY
    }

    bedrockClient = BedrockRuntimeClient {
      region = BuildConfig.AWS_REGION
      credentialsProvider = credentials
    }
  }

  /**
   * Submits a text query to the Llama model and streams the response. Constructs a Llama 3
   * instruction-formatted prompt, and calls the internal function to perform the actual request via
   * the AWS Kotlin SDK.
   *
   * See more info regarding the Llama prompt instruction format
   * [here](https://github.com/meta-llama/llama-models/blob/main/models/llama3_2/vision_prompt_format.md).
   *
   * @param query The textual query to send to the model.
   * @param creativity The temperature parameter for the model, controlling randomness (default is
   *   0.1f).
   * @param diversity The top_p parameter for the model, controlling nucleus sampling (default is
   *   0.9f).
   * @param handler An [IQueryLlamaServiceHandler] implementation to process streaming events
   *   (start, partial, finished, error).
   */
  suspend fun submitQuery(
      query: String,
      creativity: Float = .1f, // temperature
      diversity: Float = .9f, // top_p
      handler: IQueryLlamaServiceHandler
  ) {
    // Embed the prompt in Llama 3's instruction format.
    val instruction =
        """
                <|begin_of_text|>
                <|start_header_id|>user<|end_header_id|>
                {{prompt}}
                <|eot_id|>
                <|start_header_id|>assistant<|end_header_id|>
            """
            .trimIndent()
            .replace("{{prompt}}", query)

    val request = BedrockRequest(instruction, null, creativity, diversity)

    internalQuery(request, handler)
  }

  /**
   * Submits a query along with image data to the Llama model and streams the response. Resizes the
   * input image, converts it to Base64, constructs a Llama 3 instruction-formatted prompt, and
   * calls the internal function to perform the actual request via the AWS Kotlin SDK.
   *
   * See more info regarding the Llama prompt instruction format
   * [here](https://github.com/meta-llama/llama-models/blob/main/models/llama3_2/vision_prompt_format.md).
   *
   * @param query The textual query to send to the model.
   * @param imageData The [Bitmap] image to be included with the query.
   * @param creativity The temperature parameter for the model, controlling randomness (default is
   *   0.1f).
   * @param diversity The top_p parameter for the model, controlling nucleus sampling (default is
   *   0.9f).
   * @param handler An [IQueryLlamaServiceHandler] implementation to process streaming events
   *   (start, partial, finished, error).
   */
  suspend fun submitQuery(
      query: String,
      imageData: Bitmap,
      creativity: Float = .1f, // temperature
      diversity: Float = .9f, // top_p
      handler: IQueryLlamaServiceHandler
  ) {
    val resizedImage = resizeToFit(imageData, MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION)
    val imageString = convertBitmapToBase64(resizedImage)

    // Embed the prompt in Llama 3's instruction format.
    val instruction =
        """
                <|begin_of_text|>
                <|start_header_id|>user<|end_header_id|>
                <|image|>{{prompt}}
                <|eot_id|>
                <|start_header_id|>assistant<|end_header_id|>
            """
            .trimIndent()
            .replace("{{prompt}}", query)

    val request = BedrockRequest(instruction, listOf(imageString), creativity, diversity)

    internalQuery(request, handler)
  }

  /**
   * Sends the invoke request to the AWS Bedrock service. Responses are streamed back through the
   * provided [IQueryLlamaServiceHandler] handler.
   *
   * @param request [BedrockRequest] request to send to AWS Bedrock to invoke the Llama model, after
   *   translating to the AWS Kotlin SDK format.
   * @param handler An [IQueryLlamaServiceHandler] implementation to process streaming events
   *   (start, partial, finished, error).
   */
  private suspend fun internalQuery(request: BedrockRequest, handler: IQueryLlamaServiceHandler) {
    try {
      val requestBody = gson.toJson(request)
      val nativeRequest = InvokeModelWithResponseStreamRequest {
        modelId = "us.meta.llama3-2-11b-instruct-v1:0"
        contentType = "application/json"
        accept = "application/json"
        body = requestBody.encodeToByteArray()
      }

      val responseBuilder = StringBuilder()
      var chunksReceived = 0

      bedrockClient.invokeModelWithResponseStream(nativeRequest) { resp ->
        resp.body?.collect { partial ->
          val partialBody = partial.asChunk().bytes?.decodeToString()
          val parsedBody: BedrockResponse = gson.fromJson(partialBody, BedrockResponse::class.java)

          responseBuilder.append(parsedBody.generation)

          // Log.d(TAG, "partial: ${parsedBody.generation}")

          chunksReceived++
          if (chunksReceived == 1) {
            handler.onStreamStart()
          }

          handler.onPartial(responseBuilder.toString())
        }
      }

      Log.d(TAG, "complete: $responseBuilder")

      handler.onFinished(responseBuilder.toString())
    } catch (e: Exception) {
      e.printStackTrace()

      // call our onError on the main thread
      handler.onError("Error: ${e.message}")
    }
  }

  /**
   * Resizes a [Bitmap] to fit within the specified maximum width and height, maintaining the
   * original aspect ratio. If the bitmap is already smaller than or equal to the max dimensions,
   * the original bitmap is returned.
   *
   * @param bitmap The input [Bitmap] to resize.
   * @param maxWidth The maximum desired width for the output bitmap.
   * @param maxHeight The maximum desired height for the output bitmap.
   * @param filter Boolean indicating whether to filter the bitmap when scaling (default is true).
   * @return The resized [Bitmap], or the original if no resizing was necessary.
   */
  private fun resizeToFit(
      bitmap: Bitmap,
      maxWidth: Int,
      maxHeight: Int,
      filter: Boolean = true
  ): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxWidth && height <= maxHeight) {
      return bitmap
    }

    Log.d(TAG, "Resizing ${width}x${height} image to fit ${maxWidth}x${maxHeight}")

    val aspectRatio = width.toFloat() / height.toFloat()
    var newWidth = maxWidth
    var newHeight = (newWidth / aspectRatio).toInt()

    // still too big; recalculate based on maxHeight
    if (newHeight > maxHeight) {
      newHeight = maxHeight
      newWidth = (newHeight * aspectRatio).toInt()
    }

    // make sure it's at least 1 pixel
    if (newWidth <= 0) newWidth = 1
    if (newHeight <= 0) newHeight = 1

    Log.d(TAG, "Resizing to ${newWidth}x${newHeight}, matching aspect ratio $aspectRatio")

    return bitmap.scale(newWidth, newHeight, filter)
  }

  /**
   * Converts a [Bitmap] image to a Base64 encoded string. The bitmap is first compressed into JPEG
   * format. Newline and carriage return characters are removed from the resulting string.
   *
   * @param bitmap The [Bitmap] to convert.
   * @return A Base64 encoded [String] representation of the bitmap.
   */
  private fun convertBitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

    val byteArray = byteArrayOutputStream.toByteArray()

    return Base64.encodeToString(byteArray, Base64.DEFAULT).replace("\n", "").replace("\r", "")
  }
}
