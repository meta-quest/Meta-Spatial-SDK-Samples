// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.googlemaps

import android.util.Log
import com.google.gson.Gson
import com.meta.pixelandtexel.geovoyage.BuildConfig
import com.meta.pixelandtexel.geovoyage.models.GeoCoordinates
import com.meta.pixelandtexel.geovoyage.services.googlemaps.enums.GeocodeStatus
import com.meta.pixelandtexel.geovoyage.services.googlemaps.models.GeocodeResponse
import com.meta.pixelandtexel.geovoyage.services.googlemaps.models.GeocodeResponseWrapper
import com.meta.pixelandtexel.geovoyage.services.googlemaps.models.GeocodeResult
import com.meta.pixelandtexel.geovoyage.utils.NetworkUtils
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.Request

object GoogleMapsService {
  private const val TAG: String = "GoogleMapsService"

  private val resultTypeFilter =
      listOf("country", "political", "natural_feature", "point_of_interest")

  private val apiKey: String = BuildConfig.GOOGLE_MAPS_API_KEY
  private val gson = Gson()
  private val urlBuilder: HttpUrl

  init {
    if (apiKey.isEmpty()) {
      Log.e(TAG, "Missing Google Maps API key from secrets.properties")
    }

    urlBuilder =
        HttpUrl.Builder()
            .scheme("https")
            .host("maps.googleapis.com")
            .addPathSegments("maps/api/geocode/json")
            .addQueryParameter("key", apiKey)
            .addQueryParameter("result_type", resultTypeFilter.joinToString("|"))
            .build()
  }

  /** https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding */
  fun getPlace(coords: GeoCoordinates, handler: IGeocodeServiceHandler) {
    CoroutineScope(Dispatchers.Main).launch {
      try {
        val url =
            urlBuilder
                .newBuilder()
                .setQueryParameter("latlng", "${coords.latitude},${coords.longitude}")
                .build()

        val geocodeResponse = reverseGeocodeAt(url)
        if (geocodeResponse !is GeocodeResponseWrapper.Success) {
          val error = geocodeResponse as GeocodeResponseWrapper.Error
          throw Exception(error.message)
        }

        val responseData = geocodeResponse.data
        if (responseData.status == GeocodeStatus.ZERO_RESULTS) {
          handler.onFinished(null)
        }

        when (responseData.status) {
          GeocodeStatus.OK -> Log.d(TAG, "Found ${responseData.results.count()} results")

          GeocodeStatus.ZERO_RESULTS -> {
            Log.w(TAG, "Found no results for geo ${coords.toCommonNotation()}")
            handler.onFinished(null)
            return@launch
          }

          else -> throw Exception(responseData.status.toString())
        }

        // find the first named result whose type belongs to our filtered types
        val name = getNameFromResults(responseData.results)

        handler.onFinished(name)
      } catch (e: Exception) {
        e.printStackTrace()
        handler.onError(e.message ?: "Unknown error")
      }
    }
  }

  private suspend fun reverseGeocodeAt(url: HttpUrl): GeocodeResponseWrapper {
    return withContext(Dispatchers.IO) {
      try {
        val request = Request.Builder().url(url).build()

        val response = NetworkUtils.client.newCall(request).execute()
        if (!response.isSuccessful) {
          throw IOException("Network request was unsuccessful: ${response.message}")
        }

        val responseBody = response.body.string()
        Log.d(TAG, responseBody)
        val geocodeResponse = gson.fromJson(responseBody, GeocodeResponse::class.java)

        GeocodeResponseWrapper.Success(geocodeResponse)
      } catch (e: Exception) {
        e.printStackTrace()
        GeocodeResponseWrapper.Error(e.message ?: "Unknown error")
      }
    }
  }

  private fun getNameFromResults(results: List<GeocodeResult>): String? {
    var name: String? = null
    for (resultType in resultTypeFilter) {
      val result = results.find { it.types.contains(resultType) }
      if (result == null) {
        continue
      }

      if (result.address_components.isEmpty()) {
        name = result.formatted_address
        break
      }

      val component = result.address_components.find { it.types.contains(resultType) }
      if (component != null) {
        name = component.long_name
        break
      }
    }

    return name
  }
}
