// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.managers

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.meta.theelectricfactory.focus.data.StickyColor
import com.meta.theelectricfactory.focus.tools.StickyNote
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject

// Class to manage connection with the AI
data class AIResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("errmsg") val errmsg: String?,
)

data class AskQuestionResponse(@SerializedName("answer") val answer: String)

data class SummarizeTextResponse(@SerializedName("summary") val summary: String)

class AIManager {

  // Enable or disable AI in project
  val AIenabled: Boolean = false

  private val client = OkHttpClient()
  private val SERVER_URL = "https://api.focus.theelectricfactory.com/"

  var lastAIResponse = ""
  var waitingForAI = false

  companion object {
    val instance: AIManager by lazy { AIManager() }
  }

  private fun <T> fetchAIMethod(method: String, body: JSONObject, clazz: Class<T>): AIResponse<T> {
    try {
      val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), body.toString())
      val request = Request.Builder().url(SERVER_URL + "/" + method + "/").post(requestBody).build()
      val response: Response = client.newCall(request).execute()
      if (!response.isSuccessful) throw IOException("Unexpected server error")
      val gson = Gson()
      val type = TypeToken.getParameterized(AIResponse::class.java, clazz).type
      return gson.fromJson(response.body?.string(), type)
    } catch (e: IOException) {
      val ret = AIResponse<T>(false, null, e.message)
      return ret
    }
  }

  fun askQuestion(question: String): AIResponse<AskQuestionResponse> {
    val body = JSONObject().apply { put("question", question) }
    val response = fetchAIMethod("askQuestion", body, AskQuestionResponse::class.java)
    return response
  }

  fun summarizeText(text: String): AIResponse<SummarizeTextResponse> {
    val body = JSONObject().apply { put("text", text) }
    val response = fetchAIMethod("summarizeText", body, SummarizeTextResponse::class.java)
    return response
  }

  // This function sends the questions of the user to the AI and creates the corresponding messages
  // in the chat panel
  fun askToAI(question: String, onComplete: () -> (Unit)) {
    var response = ""
    GlobalScope.launch(Dispatchers.IO) {
      val result = askQuestion(question)
      withContext(Dispatchers.Main) {
        if (result.success && result.data != null) {
          response = result.data.answer
        } else if (result.errmsg != null) {
          response = "Error: " + result.errmsg
        } else {
          response = "Error: Empty response"
        }
        lastAIResponse = response
        onComplete()
      }
    }
  }

  // This function summarizes the las response of the AI
  // in order to create a Sticky Note with that information
  fun summarize(message: String) {
    var response = ""
    GlobalScope.launch(Dispatchers.IO) {
      val result = summarizeText(message)
      withContext(Dispatchers.Main) {
        if (result.success && result.data != null) {
          response = result.data.summary
        } else if (result.errmsg != null) {
          response = "Error: " + result.errmsg
        } else {
          response = "Error: Empty response"
        }
        StickyNote(message = response, color = StickyColor.Purple)
      }
    }
  }
}
