// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.meta.pixelandtexel.geovoyage.activities.MainActivity
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.models.GeoCoordinates
import com.meta.pixelandtexel.geovoyage.models.Landmark
import com.meta.pixelandtexel.geovoyage.models.PanoMetadata
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.services.googlemaps.GoogleMapsService
import com.meta.pixelandtexel.geovoyage.services.googlemaps.GoogleTilesService
import com.meta.pixelandtexel.geovoyage.services.googlemaps.IGeocodeServiceHandler
import com.meta.pixelandtexel.geovoyage.services.llama.IQueryLlamaServiceHandler
import com.meta.pixelandtexel.geovoyage.services.llama.QueryLlamaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel(), IPlayModeViewModel {
  companion object {
    private const val TAG: String = "ExploreViewModel"
  }

  private val _title = mutableStateOf("")
  private val _result = mutableStateOf("")
  private val _landmarksEnabled = mutableStateOf(true)
  private val _vrModeEnabled = mutableStateOf(false)
  private val _panoData = mutableStateOf<PanoMetadata?>(null)

  val title: State<String> = _title
  val result: State<String> = _result
  val landmarksEnabled: State<Boolean> = _landmarksEnabled
  val vrModeEnabled: State<Boolean> = _vrModeEnabled
  val panoData: State<PanoMetadata?> = _panoData

  private var busyQuerying = false
  private var busyFetchingTiles = false
  private var hasVRImage = false
  private var displayingLandmarkInfo = false

  private lateinit var placeholderMessage: String

  init {
    _landmarksEnabled.value = SettingsService.get(SettingsKey.LANDMARKS_ENABLED, true)
  }

  override fun onPlayModeResumed() {
    if (_vrModeEnabled.value) {
      MainActivity.instance.get()?.toggleSkybox(true)
    }
  }

  override fun onPlayModeSuspended() {
    MainActivity.instance.get()?.toggleSkybox(false)

    if (displayingLandmarkInfo) {
      _title.value = ""
      _result.value = ""
      _panoData.value = null
      _vrModeEnabled.value = false
      hasVRImage = false
      displayingLandmarkInfo = false
    }
  }

  fun updatePlaceholderMessage(message: String) {
    placeholderMessage = message

    if (_result.value.isEmpty()) {
      _result.value = message
    }
  }

  fun startQueryAtCoordinates(coords: GeoCoordinates, template: String) {
    if (busyQuerying) {
      return
    }

    hasVRImage = false
    displayingLandmarkInfo = false

    _vrModeEnabled.value = false
    _panoData.value = null
    busyQuerying = true

    _title.value = coords.toCommonNotation()

    CoroutineScope(Dispatchers.Main).launch {
      _panoData.value = GoogleTilesService.getPanoramaDataAt(coords)
    }

    // TODO show loading message/graphic

    GoogleMapsService.getPlace(
        coords,
        object : IGeocodeServiceHandler {
          override fun onFinished(place: String?) {
            if (place.isNullOrEmpty()) {
              _result.value = "Unknown place"
              // TODO hide loading message/graphic
              busyQuerying = false
              return
            }

            val query = String.format(template, coords.toCommonNotation(), place)
            Log.d(TAG, "Full query: $query")

            QueryLlamaService.submitQuery(
                query = query,
                handler =
                    object : IQueryLlamaServiceHandler {
                      override fun onStreamStart() {
                        // TODO hide loading message/graphic
                      }

                      override fun onPartial(partial: String) {
                        _result.value = partial
                      }

                      override fun onFinished(answer: String) {
                        Log.d(TAG, "Received llama response $answer")
                        _result.value = answer
                        busyQuerying = false
                      }

                      override fun onError(reason: String) {
                        _result.value = "Llama error:\n$reason"
                        busyQuerying = false
                      }
                    })
          }

          override fun onError(reason: String) {
            _result.value = "Google Maps error:\n$reason"
            busyQuerying = false
          }
        })
  }

  fun displayLandmarkInfo(info: Landmark, coords: GeoCoordinates) {
    hasVRImage = false
    displayingLandmarkInfo = true

    _vrModeEnabled.value = false
    _panoData.value = null

    CoroutineScope(Dispatchers.Main).launch {
      _panoData.value = GoogleTilesService.getPanoramaDataAt(coords)
    }

    _title.value = coords.toCommonNotation()
    _result.value =
        """
            ### ${info.landmarkName}
            ${info.description}
        """
            .trimIndent()
  }

  fun onLandmarksToggled(enabled: Boolean) {
    _landmarksEnabled.value = enabled

    SettingsService.set(SettingsKey.LANDMARKS_ENABLED, enabled)
    MainActivity.instance.get()?.userToggledLandmarks(enabled)
  }

  fun onEnterVRClicked() {
    if (busyFetchingTiles) {
      return
    }

    _vrModeEnabled.value = !_vrModeEnabled.value

    if (hasVRImage) {
      MainActivity.instance.get()?.toggleSkybox(_vrModeEnabled.value)
      return
    }

    busyFetchingTiles = true

    MainActivity.instance.get()?.tryShowSkyboxAt(_panoData.value!!) { success ->
      busyFetchingTiles = false

      if (_panoData.value == null) {
        // We were showing a landmark and switched play modes while
        // loading the skybox tiles
        return@tryShowSkyboxAt
      }

      hasVRImage = success

      if (!success) {
        _vrModeEnabled.value = false
        _panoData.value = null
      }
    }
  }
}
