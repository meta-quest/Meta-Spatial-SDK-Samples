// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.IOException
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

object AIUtils {

  private val client = OkHttpClient()
  private val SERVER_URL = "https://api.focus.theelectricfactory.com/"

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
}
