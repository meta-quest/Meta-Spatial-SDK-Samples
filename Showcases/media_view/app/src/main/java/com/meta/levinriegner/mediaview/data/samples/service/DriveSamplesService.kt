// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.samples.service

import com.meta.levinriegner.mediaview.BuildConfig
import com.meta.levinriegner.mediaview.data.samples.model.SamplesList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject
import kotlin.random.Random


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
                Timber.d("Url: ${request.url}")
                throw RuntimeException("(${response.code}) ${response.message}")
            }
            return response.body?.string()?.let { body ->
                json.decodeFromString(SamplesList.serializer(), body)
            } ?: throw RuntimeException("Empty response")
        }
    }

    fun downloadFile(
        fileId: String,
    ): Flow<InputStream> = flow {
        val url = driveDownloadUrl(fileId)

        // Get file size
        val sizeRequest = Request.Builder()
            .url(url)
            .head()
            .build()
        val fileSize = httpClient.newCall(sizeRequest).execute().use {
            it.headers["Content-Length"]?.toLongOrNull()
                ?: throw RuntimeException("Failed to get file size")
        }
        Timber.d("File size: $fileSize")

        // Get file bytes (in chunks)
        val chunkSize = 33 * 1024 * 1024; // 33MB
        val totalChunks = (fileSize / chunkSize) + if (fileSize % chunkSize > 0) 1 else 0
        for (i in 0 until totalChunks) {
            val start = i * chunkSize
            val end = if (i == totalChunks - 1) fileSize - 1 else (i + 1) * chunkSize - 1
            val range = "bytes=$start-$end"
            Timber.d("Downloading chunk $i/$totalChunks: range=$range")
            val chunkRequest = Request.Builder()
                .url(url)
                .header("Range", range)
                .build()
            val response = httpClient.newCall(chunkRequest).execute()
            if (!response.isSuccessful) {
                response.close()
                throw RuntimeException("(${response.code}) ${response.message}")
            }
            emit(response.body?.byteStream() ?: throw RuntimeException("Empty file response"))
        }
        delay(Random.nextLong(200L)) // Be nice to the server
    }

    companion object {
        private const val DRIVE_SAMPLES_INDEX_FILE_ID = "12AjUR1LS8RfqgBMmI3csiCi-AAUp-vbi"
        private fun driveDownloadUrl(fileId: String): String {
            return "https://www.googleapis.com/drive/v3/files/$fileId?alt=media&key=${BuildConfig.DRIVE_API_KEY}"
//            return "https://drive.usercontent.google.com/download?id=$fileId&export=download&authuser=0"
        }
    }
}