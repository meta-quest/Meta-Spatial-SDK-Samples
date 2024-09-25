// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.llama

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest
import com.google.gson.Gson
import com.meta.pixelandtexel.geovoyage.BuildConfig
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.enums.LlamaServerType
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.services.ISettingsKeyChangeReceiver
import com.meta.pixelandtexel.geovoyage.services.PreferenceValue
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.services.llama.models.BedrockRequest
import com.meta.pixelandtexel.geovoyage.services.llama.models.BedrockResponse
import com.meta.pixelandtexel.geovoyage.services.llama.models.OllamaRequest
import com.meta.pixelandtexel.geovoyage.services.llama.models.OllamaRequestParams
import com.meta.pixelandtexel.geovoyage.services.llama.models.OllamaResponse
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.clamp01
import com.meta.pixelandtexel.geovoyage.utils.NetworkUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * This static class provides functionality to query llama3 served by ollama at a remote url. API
 * docs: https://github.com/ollama/ollama/blob/main/docs/api.md
 */
object QueryLlamaService {
  private const val TAG: String = "QueryLlamaService"

  // our http variables
  private val gson = Gson()
  private var ollamaRequestBuilder: Request.Builder
  private var bedrockClient: BedrockRuntimeClient

  private var queryTemplate: String? = null

  /** Setup our http request builder using the remote url stored in secrets.properties */
  init {
    // Ollama querying setup

    val ollamaServerURL: String = SettingsService.get(SettingsKey.OLLAMA_URL, "")
    if (ollamaServerURL.isEmpty()) {
      Log.e(TAG, "Missing ollama server URL from secrets.properties")
    }
    val ollamaApiEndpoint = "${ollamaServerURL}/api/generate"

    ollamaRequestBuilder = Request.Builder().url(ollamaApiEndpoint)

    SettingsService.subscribeToKeyUpdate(
        SettingsKey.OLLAMA_URL.value,
        object : ISettingsKeyChangeReceiver {
          override fun onKeyUpdated(newValue: PreferenceValue) {
            // user updated the ollama url in settings; update our request builder
            val newURL = (newValue as PreferenceValue.StringValue).value
            ollamaRequestBuilder = Request.Builder().url("${newURL}/api/generate")
          }
        })

    // AWS Bedrock querying setup

    val credentials = StaticCredentialsProvider {
      accessKeyId = BuildConfig.AWS_BEDROCK_ACCESS_KEY
      secretAccessKey = BuildConfig.AWS_BEDROCK_SECRET_KEY
    }

    bedrockClient = BedrockRuntimeClient {
      region = "us-east-1"
      credentialsProvider = credentials
    }
  }

  fun initialize(context: Context) {
    queryTemplate = context.getString(R.string.base_query_template)
  }

  fun submitQuery(
      query: String,
      creativity: Float = .1f, // temperature
      diversity: Float = .9f, // top_p
      handler: IQueryLlamaServiceHandler
  ) {
    if (queryTemplate.isNullOrEmpty()) {
      throw Exception("Llama query template not created")
    }

    val fullQuery = String.format(queryTemplate!!, query)
    val temperature = creativity.clamp01()
    val top_p = diversity.clamp01()

    val serverType =
        SettingsService.get(SettingsKey.LLAMA_SERVER_TYPE, LlamaServerType.AWS_BEDROCK.value)
    when (serverType) {
      LlamaServerType.OLLAMA.value -> queryOllama(fullQuery, temperature, top_p, handler)

      LlamaServerType.AWS_BEDROCK.value -> queryAWSBedrock(fullQuery, temperature, top_p, handler)
    }
  }

  /** Submit a string query and start receiving chunked results */
  private fun queryOllama(
      query: String,
      temp: Float,
      top_p: Float,
      handler: IQueryLlamaServiceHandler
  ) {
    /** Perform the network request in a background thread */
    thread {
      try {
        // Build the request body
        // https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-completion
        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val nativeRequest = OllamaRequest(query, OllamaRequestParams(temp, top_p))
        val requestBody = gson.toJson(nativeRequest).toRequestBody(jsonMediaType)

        val request = ollamaRequestBuilder.post(requestBody).build()
        NetworkUtils.makeHttpRequest(request) { response ->
          Log.d(TAG, "Finished ollama request with response code: ${response.code}")

          if (!response.isSuccessful) {
            throw Exception("ollama server response: ${response.code} ${response.message}")
          }

          // Read the response body as an InputStream
          response.body.byteStream().use { inputStream ->
            // Use BufferedReader to read the input stream
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
              val responseBuilder = StringBuilder()
              var chunksReceived = 0

              // Read and process each line
              var line: String?
              while (reader.readLine().also { line = it } != null) {
                val ollamaResponse: OllamaResponse = gson.fromJson(line, OllamaResponse::class.java)

                responseBuilder.append(ollamaResponse.response)

                // Log.d(TAG, "partial: ${llamaResponse.response}")

                // invoke our callbacks on the main thread

                chunksReceived++
                if (chunksReceived == 1) {
                  Handler(Looper.getMainLooper()).post { handler.onStreamStart() }
                }

                Handler(Looper.getMainLooper()).post {
                  handler.onPartial(responseBuilder.toString())
                }
              }

              // Log.d(TAG, "complete: $responseBuilder")

              Handler(Looper.getMainLooper()).post {
                handler.onFinished(responseBuilder.toString())
              }
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()

        // call our onError on the main thread
        Handler(Looper.getMainLooper()).post { handler.onError("Error: ${e.message}") }
      }
    }
  }

  /**  */
  private fun queryAWSBedrock(
      query: String,
      temp: Float,
      top_p: Float,
      handler: IQueryLlamaServiceHandler
  ) {
    thread {
      runBlocking {
        try {
          // Embed the prompt in Llama 3's instruction format.
          val instruction =
              """
                        <|begin_of_text|>
                        <|start_header_id|>user<|end_header_id|>
                        {{prompt}} <|eot_id|>
                        <|start_header_id|>assistant<|end_header_id|>
                    """
                  .trimIndent()
                  .replace("{{prompt}}", query)

          val nativeRequest = BedrockRequest(instruction, temp, top_p)
          val requestBody = gson.toJson(nativeRequest)

          val request = InvokeModelWithResponseStreamRequest {
            modelId = "meta.llama3-8b-instruct-v1:0"
            contentType = "application/json"
            accept = "application/json"
            body = requestBody.encodeToByteArray()
          }

          val responseBuilder = StringBuilder()
          var chunksReceived = 0

          bedrockClient.invokeModelWithResponseStream(request) { resp ->
            resp.body?.collect { partial ->
              val partialBody = partial.asChunk().bytes?.decodeToString()
              val parsedBody: BedrockResponse =
                  gson.fromJson(partialBody, BedrockResponse::class.java)

              responseBuilder.append(parsedBody.generation)

              // Log.d(TAG, "partial: ${parsedBody.generation}")

              chunksReceived++
              if (chunksReceived == 1) {
                Handler(Looper.getMainLooper()).post { handler.onStreamStart() }
              }

              Handler(Looper.getMainLooper()).post { handler.onPartial(responseBuilder.toString()) }
            }
          }

          // Log.d(TAG, "complete: $responseBuilder")

          Handler(Looper.getMainLooper()).post { handler.onFinished(responseBuilder.toString()) }
        } catch (e: Exception) {
          e.printStackTrace()

          // call our onError on the main thread
          Handler(Looper.getMainLooper()).post { handler.onError("Error: ${e.message}") }
        }
      }
    }
  }
}
