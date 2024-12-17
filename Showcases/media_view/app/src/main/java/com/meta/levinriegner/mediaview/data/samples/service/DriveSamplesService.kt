package com.meta.levinriegner.mediaview.data.samples.service

import com.meta.levinriegner.mediaview.data.samples.model.SamplesList
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import javax.inject.Inject


class DriveSamplesService @Inject constructor(
    private val httpClient: OkHttpClient,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }

    suspend fun getSamplesList(): SamplesList {
        val request = Request.Builder()
            .url(driveDownloadUrl(DRIVE_SAMPLES_INDEX_FILE_ID))
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("(${response.code}) ${response.message}")
            }
            return response.body?.string()?.let { body ->
                json.decodeFromString(SamplesList.serializer(), body)
            } ?: throw RuntimeException("Empty response")
        }
    }

    suspend fun downloadFile(
        fileId: String,
    ): InputStream {
        val request = Request.Builder()
            .url(driveDownloadUrl(fileId))
            .build()
        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            response.close()
            throw RuntimeException("(${response.code}) ${response.message}")
        }
        return response.body?.byteStream() ?: throw RuntimeException("Empty response")
    }

    companion object {
        private const val DRIVE_SAMPLES_INDEX_FILE_ID = "12AjUR1LS8RfqgBMmI3csiCi-AAUp-vbi"
        private fun driveDownloadUrl(fileId: String): String {
            return "https://drive.usercontent.google.com/download?id=$fileId&export=download&authuser=0"
        }
    }
}