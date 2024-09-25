// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import com.google.gson.Gson
import com.meta.pixelandtexel.geovoyage.BuildConfig
import com.meta.pixelandtexel.geovoyage.models.GeoCoordinates
import com.meta.pixelandtexel.geovoyage.models.PanoMetadata
import com.meta.pixelandtexel.geovoyage.services.googlemaps.models.SessionRequest
import com.meta.pixelandtexel.geovoyage.services.googlemaps.models.SessionResponse
import com.meta.pixelandtexel.geovoyage.utils.NetworkUtils
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.ceil
import kotlin.math.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object GoogleTilesService {
  private const val TAG: String = "GoogleTilesService"

  private const val MAX_TILE_FETCHES_PER_PANO = 32

  private val apiKey: String = BuildConfig.GOOGLE_MAPS_API_KEY
  private val gson = Gson()
  private val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

  private val sessionUrl: HttpUrl
  private val panoIdsUrl: HttpUrl
  private val metadataUrl: HttpUrl
  private val tileUrlBuilder: HttpUrl

  private var sessionToken: String? = null
  private var sessionExpiry: ZonedDateTime? = null

  init {
    if (apiKey.isEmpty()) {
      Log.e(TAG, "Missing Google Maps API key from secrets.properties")
    }

    // create our base urls

    val baseUrl =
        HttpUrl.Builder()
            .scheme("https")
            .host("tile.googleapis.com")
            .addPathSegments("v1")
            .addQueryParameter("key", apiKey)
            .build()
    sessionUrl = baseUrl.newBuilder().addPathSegments("createSession").build()
    panoIdsUrl = baseUrl.newBuilder().addPathSegments("streetview/panoIds").build()
    metadataUrl = baseUrl.newBuilder().addPathSegments("streetview/metadata").build()
    tileUrlBuilder = baseUrl.newBuilder().addPathSegments("streetview/tiles").build()
  }

  suspend fun getPanoramaDataAt(
      coords: GeoCoordinates,
      radius: Int = 100000 // 100 km
  ): PanoMetadata? {
    try {
      // get our session if we don't have one

      if (needTokenRefresh()) {
        val session = getSession() ?: throw Exception("Failed to get valid session")
        sessionToken = session.session

        val instant = Instant.ofEpochSecond(session.expiry)
        sessionExpiry = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())

        Log.d(TAG, "Refreshed session token")
      }

      // fetch metadata for the panoramic image nearest to our location

      val metadata = getPanoMetadataAt(coords, radius)

      return metadata
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }

  fun getPanoramaBitmapFor(metadata: PanoMetadata, handler: IPanoramaServiceHandler) {
    CoroutineScope(Dispatchers.Main).launch {
      try {
        // get our session if we don't have one

        if (needTokenRefresh()) {
          val session = getSession() ?: throw Exception("Failed to get valid session")
          sessionToken = session.session

          val instant = Instant.ofEpochSecond(session.expiry)
          sessionExpiry = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())

          Log.d(TAG, "Refreshed session token")
        }

        Log.d(TAG, "Using panorama ${metadata.panoId} near coordinates")

        // calculate the maximum x and y values by dividing the imageHeight
        // and imageWidth by the tileHeight and tileWidth

        val maxTilesXf = metadata.imageWidth.toFloat() / metadata.tileWidth
        val maxTilesYf = metadata.imageHeight.toFloat() / metadata.tileHeight

        // first, calculate our max zoom level

        var maxZoom = -1
        for (i in 0..5) {
          val pot = 2f.pow(i)
          if (pot >= maxTilesXf) {
            maxZoom = i
            break
          }
        }
        if (maxZoom == -1) {
          throw Exception("Failed to calculate max zoom level")
        }

        Log.d(TAG, "Calculated max zoom $maxZoom")

        // now, from max zoom, decrease our zoom to our max number of fetches

        var zoom = 0
        var numTilesXf = maxTilesXf
        var numTilesYf = maxTilesYf
        for (i in maxZoom downTo 0) {
          val totalFetches = ceil(numTilesXf) * ceil(numTilesYf)
          if (totalFetches <= MAX_TILE_FETCHES_PER_PANO) {
            zoom = i
            break
          }
          numTilesXf /= 2
          numTilesYf /= 2
        }

        val numTilesX = ceil(numTilesXf).toInt()
        val numTilesY = ceil(numTilesYf).toInt()
        val numTotalTiles = numTilesX * numTilesY

        Log.d(TAG, "Begin fetching $numTotalTiles image tiles at zoom $zoom")

        // fetch all of the tiles

        val tilesFetches =
            (0 until numTotalTiles).map { i ->
              val x = i % numTilesX
              val y = i / numTilesX
              async(Dispatchers.IO) { getTileImage(metadata.panoId, x, y, zoom) }
            }
        val tiles = tilesFetches.mapNotNull { it.await() }

        if (tiles.size < numTotalTiles) {
          throw Exception("Failed to get all tile images")
        }

        // stitch the tiles together

        val skyboxTex =
            combineTiles(tiles, metadata.tileWidth, metadata.tileHeight, numTilesXf, numTilesYf)

        handler.onFinished(skyboxTex)
      } catch (e: Exception) {
        e.printStackTrace()
        handler.onError(e.message ?: "Unknown error")
      }
    }
  }

  private fun needTokenRefresh(): Boolean {
    val now = ZonedDateTime.now()
    return sessionToken.isNullOrEmpty() || sessionExpiry == null || sessionExpiry!!.isBefore(now)
  }

  /** https://developers.google.com/maps/documentation/tile/session_tokens */
  private suspend fun getSession(): SessionResponse? {
    return withContext(Dispatchers.IO) {
      try {
        val sessionRequest = SessionRequest("streetview")
        val requestBody = gson.toJson(sessionRequest).toRequestBody(jsonMediaType)

        val request = Request.Builder().url(sessionUrl).post(requestBody).build()

        val response = NetworkUtils.client.newCall(request).execute()
        if (!response.isSuccessful) {
          val message = response.body.string()
          throw IOException("Network request was unsuccessful: $message")
        }

        val responseBody = response.body.string()
        // Log.d(TAG, responseBody)

        gson.fromJson(responseBody, SessionResponse::class.java)
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }

  private suspend fun getPanoMetadataAt(
      coords: GeoCoordinates,
      radius: Int = 10000
  ): PanoMetadata? {
    return withContext(Dispatchers.IO) {
      try {
        val url =
            metadataUrl
                .newBuilder()
                .addQueryParameter("session", sessionToken)
                .addQueryParameter("lat", coords.latitude.toString())
                .addQueryParameter("lng", coords.longitude.toString())
                .addQueryParameter("radius", radius.toString())
                .build()

        val request = Request.Builder().url(url).get().build()

        val response = NetworkUtils.client.newCall(request).execute()
        if (!response.isSuccessful) {
          val message = response.body.string()
          throw IOException("Network request was unsuccessful: $message")
        }

        val responseBody = response.body.string()
        // Log.d(TAG, responseBody)

        gson.fromJson(responseBody, PanoMetadata::class.java)
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }

  private suspend fun getTileImage(id: String, x: Int, y: Int, zoom: Int): Bitmap? {
    return withContext(Dispatchers.IO) {
      try {
        val url =
            tileUrlBuilder
                .newBuilder()
                .addPathSegments("$zoom/$x/$y")
                .addQueryParameter("session", sessionToken)
                .addQueryParameter("panoId", id)
                .build()

        val request = Request.Builder().url(url).get().build()

        Log.d(TAG, "Fetching tile ($x, $y) at zoom $zoom")

        val response = NetworkUtils.client.newCall(request).execute()
        if (!response.isSuccessful) {
          Log.d(TAG, "Failed fetching tile ($x, $y) at zoom $zoom for image $id")
          val message = response.body.string()
          throw IOException("Network request was unsuccessful: $message")
        }

        // check if the response is of type image
        val contentType = response.header("Content-Type")
        if (contentType == null || !contentType.startsWith("image")) {
          // handle case where the content is not an image
          return@withContext null
        }

        response.body.byteStream().use { inputStream -> BitmapFactory.decodeStream(inputStream) }
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }

  private fun combineTiles(
      tiles: List<Bitmap>,
      tileWidth: Int,
      tileHeight: Int,
      numTilesXf: Float,
      numTilesYf: Float
  ): Bitmap {
    val fullWidth = (numTilesXf * tileWidth).toInt()
    val fullHeight = (numTilesYf * tileHeight).toInt()

    val numTilesX = ceil(numTilesXf).toInt()
    val numTilesY = ceil(numTilesYf).toInt()

    Log.d(TAG, "Calculated total bitmap size of $fullWidth x $fullHeight")

    val combinedBitmap = Bitmap.createBitmap(fullWidth, fullHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(combinedBitmap)

    var src: Rect
    var dst: Rect
    for (y in 0 until numTilesY) {
      for (x in 0 until numTilesX) {
        // calculate the right and bottom boundaries for partial tiles
        val rightEdge = minOf((x + 1) * tileWidth, fullWidth)
        val bottomEdge = minOf((y + 1) * tileHeight, fullHeight)

        // skip tiles outside the bounds of the combined bitmap
        if (x * tileWidth >= fullWidth || y * tileHeight >= fullHeight) {
          continue
        }

        val tileIdx = y * numTilesX + x
        val tile = tiles[tileIdx]

        // now calculate the source and destination rects, only drawing the visible portion
        dst = Rect(x * tileWidth, y * tileHeight, rightEdge, bottomEdge)
        src = Rect(0, 0, dst.width(), dst.height())

        Log.d(TAG, "draw src rect $src from tile[$tileIdx] to combined bitmap at dst rect $dst")

        canvas.drawBitmap(tile, src, dst, null)
      }
    }

    return combinedBitmap
  }
}
