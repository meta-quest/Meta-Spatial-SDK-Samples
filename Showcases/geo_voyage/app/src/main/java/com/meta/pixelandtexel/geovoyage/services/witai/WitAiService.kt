// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.meta.pixelandtexel.geovoyage.BuildConfig
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.services.witai.enums.WitAiResponseType
import com.meta.pixelandtexel.geovoyage.services.witai.enums.WitAiStartResult
import com.meta.pixelandtexel.geovoyage.services.witai.models.WitAiResponse
import com.meta.pixelandtexel.geovoyage.services.witai.models.WitAiStreamingResponse
import com.meta.pixelandtexel.geovoyage.services.witai.models.WitAiUnderstoodResponse
import com.meta.pixelandtexel.geovoyage.utils.AudioUtils
import com.meta.pixelandtexel.geovoyage.utils.NetworkUtils
import com.meta.pixelandtexel.geovoyage.utils.NoiseLevelAdjuster
import com.meta.pixelandtexel.geovoyage.utils.toAmplitudeArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import okio.IOException

/**
 * This static class provides functionality to translate text and the user's speech into understood
 * text using Wit.ai Adapted from: https://github.com/wit-ai/android-voice-demo API docs:
 * https://wit.ai/docs/
 */
object WitAiService {
  private const val TAG: String = "WitAiService"

  // Voice recording
  private const val SAMPLE_RATE = 8000
  private const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
  private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
  private const val BUFFER_SIZE_SEC = SAMPLE_RATE * 1 * 2
  private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT)

  // Silence Detection
  private const val NOISE_THRESHOLD_MULTIPLIER = 1.5
  private const val NOISE_BUFFER_SIZE = (0.5 * BUFFER_SIZE_SEC).toInt()
  private const val SILENCE_DURATION = 1500L
  private const val MIN_SPEAK_THRESHOLD = 1000

  // our http variables
  private val clientAccessToken: String = BuildConfig.WIT_AI_CLIENT_ACCESS_TOKEN
  private val gson = Gson()
  private var requestBuilder: Request.Builder

  /** Setup our http request builder using the client token stored in secrets.properties */
  init {
    if (clientAccessToken.isEmpty()) {
      Log.e(TAG, "Missing wit.ai client access token from secrets.properties")
    }

    // Speech
    val url =
        HttpUrl.Builder()
            .scheme("https")
            .host("api.wit.ai")
            .addPathSegment("speech")
            .addQueryParameter("v", "20240304") // Omit this to hit the most recent api version
            .build()

    requestBuilder =
        Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $clientAccessToken")
            .header("Content-Type", "audio/raw")
            .header("Transfer-Encoding", "chunked")
  }

  private var recorder: AudioRecord? = null
  private val isRecordingInProgress = AtomicBoolean(false)
  private val isRecordingCanceled = AtomicBoolean(false)

  val isRunning: Boolean
    get() {
      return isRecordingInProgress.get()
    }

  val isCanceled: Boolean
    get() {
      return isRecordingCanceled.get()
    }

  /** Start the recording and send the audio data to Wit.ai for processing. */
  fun startSpeechToText(handler: IWitAiServiceHandler): WitAiStartResult {
    if (isRunning) {
      Log.w(TAG, "Speech to text service already running")
      return WitAiStartResult.ALREADY_RUNNING
    }

    try {
      recorder =
          AudioRecord(
              MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL, AUDIO_FORMAT, BUFFER_SIZE)
      recorder?.startRecording()
      isRecordingInProgress.set(true)
      isRecordingCanceled.set(false)

      /** Record the audio and perform the http buffering on a background thread */
      thread {
        try {
          var chunksReceived = 0

          val buffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
          var silenceStartTime = System.currentTimeMillis()

          // https://wit.ai/docs/http/20240304/#post__speech_link
          val requestBody =
              object : RequestBody() {
                override fun contentType(): MediaType? {
                  val mediaTypeString =
                      "audio/raw;encoding=signed-integer;bits=16;rate=8000;endian=little"
                  return mediaTypeString.toMediaTypeOrNull()
                }

                override fun writeTo(sink: BufferedSink) {
                  Log.d(TAG, "Writing audio data to buffer...")

                  // notify that we've started listening to user speech
                  Handler(Looper.getMainLooper()).post { handler.onStartedListening() }

                  var userStartedSpeaking = false
                  val noiseLevelAdjuster =
                      NoiseLevelAdjuster(NOISE_BUFFER_SIZE, NOISE_THRESHOLD_MULTIPLIER)

                  val isSilenceDetectionEnabled =
                      SettingsService.get(SettingsKey.SILENCE_DETECTION_ENABLED, true)

                  // continuously read the audio recorder data, and write to the request
                  while (isRunning) {
                    val result = recorder?.read(buffer, BUFFER_SIZE) ?: -1
                    if (result < 0) {
                      val reason = getBufferReadFailureReason(result)
                      throw RuntimeException("Reading of audio buffer failed: $reason")
                    }

                    // write to our request buffered sink, then reset to read again
                    sink.write(buffer)
                    buffer.flip()

                    // get the rms amplitude of this buffer
                    val amplitudeArray = buffer.toAmplitudeArray()
                    val noiseLevel = AudioUtils.calculateRMS(amplitudeArray)
                    Handler(Looper.getMainLooper()).post {
                      handler.onAmplitudeChanged(noiseLevel.toInt())
                    }

                    if (noiseLevel > MIN_SPEAK_THRESHOLD) {
                      userStartedSpeaking = true
                    }

                    // try to calculate whether or not the user has stopped speaking
                    // using simple silence detection, with rudimentary background
                    // noise compensation

                    val isBufferFull = noiseLevelAdjuster.addAudioData(buffer)
                    if (isBufferFull) {
                      noiseLevelAdjuster.updateBaselineNoiseLevel()
                    }

                    val silenceThreshold = noiseLevelAdjuster.getSilenceThreshold()
                    val isSilent = AudioUtils.isSilence(noiseLevel, silenceThreshold)

                    // Log.v(TAG, "%.0f < %.0f ? $isSilent".format(noiseLevel, silenceThreshold))

                    if (isSilenceDetectionEnabled && userStartedSpeaking && isSilent) {
                      if (System.currentTimeMillis() - silenceStartTime > SILENCE_DURATION) {
                        stop()
                        break
                      }
                    } else {
                      silenceStartTime = System.currentTimeMillis()
                    }

                    buffer.clear()
                  }

                  Log.d(TAG, "Finished writing to buffer")

                  if (isCanceled) {
                    Handler(Looper.getMainLooper()).post { handler.onCanceled() }
                    return
                  }

                  // notify that our request writing has finished
                  Handler(Looper.getMainLooper()).post { handler.onFinishedListening() }
                }
              }

          Log.d(TAG, "Starting Wit.ai request...")

          val request = requestBuilder.post(requestBody).build()
          NetworkUtils.makeHttpRequest(request) { response ->
            if (isCanceled) {
              Handler(Looper.getMainLooper()).post { handler.onCanceled() }
              return@makeHttpRequest
            }

            Log.d(TAG, "Finished Wit.ai streaming request with response code: ${response.code}")

            if (!response.isSuccessful) {
              throw Exception("Wit.ai response: ${response.code} ${response.message}")
            }

            // Read the response body as an InputStream
            response.body.byteStream().use { inputStream ->
              // Use BufferedReader to read the input stream
              BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var chunk = ""
                var depth = 0

                // Read and process each line, keeping track of our json "depth"
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                  if (isCanceled) {
                    Handler(Looper.getMainLooper()).post { handler.onCanceled() }
                    break
                  }

                  // keep track of our depth
                  if (line == "{") {
                    depth++
                  } else if (line == "}") {
                    depth--
                  }

                  chunk += line?.trimIndent()

                  // we've parsed one complete json object; convert to a response
                  if (depth == 0) {
                    val witAiResponse: WitAiStreamingResponse =
                        gson.fromJson(chunk, WitAiStreamingResponse::class.java)

                    Log.d(TAG, chunk)

                    // in case wit.ai returns an error
                    if (witAiResponse.error != null) {
                      throw Exception(
                          "chunk:\ncode: '${witAiResponse.code}'\nerror: '${witAiResponse.error}'")
                    }

                    var understoodResponse: WitAiUnderstoodResponse? = null
                    if (witAiResponse.type == WitAiResponseType.FINAL_UNDERSTANDING) {
                      understoodResponse = gson.fromJson(chunk, WitAiUnderstoodResponse::class.java)
                    }

                    chunk = ""

                    // invoke our callbacks on the main thread

                    chunksReceived++
                    if (chunksReceived == 1) {
                      Handler(Looper.getMainLooper()).post { handler.onStreamStart() }
                    }

                    if (understoodResponse != null) {
                      Log.d(TAG, "Final wit.ai understanding: ${understoodResponse.text}")
                      Handler(Looper.getMainLooper()).post {
                        handler.onFinished(understoodResponse)
                      }
                    } else if (witAiResponse.type == WitAiResponseType.PARTIAL_TRANSCRIPTION ||
                        witAiResponse.type == WitAiResponseType.FINAL_TRANSCRIPTION) {
                      Handler(Looper.getMainLooper()).post { handler.onPartial(witAiResponse.text) }
                    }
                  }
                }
              }
            }
          }
        } catch (e: Exception) {
          e.printStackTrace()

          // call our onError on the main thread
          Handler(Looper.getMainLooper()).post {
            handler.onError("Error while streaming request: ${e.message}")
          }
        }
      }

      return WitAiStartResult.SUCCESS
    } catch (e: SecurityException) {
      Log.e(TAG, "Error starting recording, permission denied: ${e.message}")
      return WitAiStartResult.PERMISSION_DENIED
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e(TAG, "Error attempting to start recording or streaming request: ${e.message}")
      return WitAiStartResult.UNKNOWN_ERROR
    }
  }

  /** Stop the recording and release the recorder native resources */
  fun stop() {
    if (!isRunning) {
      return
    }

    recorder?.let {
      isRecordingInProgress.set(false)
      it.stop()
      it.release()
      recorder = null
    }
  }

  fun cancel() {
    if (!isRunning) {
      return
    }

    isRecordingCanceled.set(true)
    stop()
  }

  /** Returns a string representation of the buffer read failure reason */
  private fun getBufferReadFailureReason(errorCode: Int): String {
    return when (errorCode) {
      AudioRecord.ERROR_INVALID_OPERATION -> "ERROR_INVALID_OPERATION"
      AudioRecord.ERROR_BAD_VALUE -> "ERROR_BAD_VALUE"
      AudioRecord.ERROR_DEAD_OBJECT -> "ERROR_DEAD_OBJECT"
      AudioRecord.ERROR -> "ERROR"
      else -> "Unknown ($errorCode)"
    }
  }

  /*
  Returns a WitAiUnderstoodResponse
  https://wit.ai/docs/http/20240304/#get__message_link
  */
  suspend fun getWitAiUnderstanding(query: String): WitAiResponse {
    return withContext(Dispatchers.IO) {
      try {
        val url =
            HttpUrl.Builder()
                .scheme("https")
                .host("api.wit.ai")
                .addPathSegment("message")
                .addQueryParameter("v", "20240304")
                .addQueryParameter("q", "query")
                .build()

        val request =
            Request.Builder().url(url).header("Authorization", "Bearer $clientAccessToken").build()

        val response = NetworkUtils.client.newCall(request).execute()
        if (!response.isSuccessful) {
          throw IOException("Network request was not ok: ${response.message}")
        }

        val responseBody = response.body.string() ?: throw IOException("Response body is null")

        val understoodResponse = gson.fromJson(responseBody, WitAiUnderstoodResponse::class.java)
        WitAiResponse.Success(understoodResponse)
      } catch (e: Exception) {
        WitAiResponse.Error(e.message ?: "Unknown error")
      }
    }
  }
}
