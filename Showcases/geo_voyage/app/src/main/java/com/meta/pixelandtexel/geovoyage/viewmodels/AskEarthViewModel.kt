// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.viewmodels

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.meta.pixelandtexel.geovoyage.activities.MainActivity
import com.meta.pixelandtexel.geovoyage.activities.PanelActivity
import com.meta.pixelandtexel.geovoyage.services.llama.IQueryLlamaServiceHandler
import com.meta.pixelandtexel.geovoyage.services.llama.QueryLlamaService
import com.meta.pixelandtexel.geovoyage.services.witai.IWitAiServiceHandler
import com.meta.pixelandtexel.geovoyage.services.witai.WitAIFlowService
import com.meta.pixelandtexel.geovoyage.services.witai.WitAiService
import com.meta.pixelandtexel.geovoyage.services.witai.enums.WitAiStartResult
import com.meta.pixelandtexel.geovoyage.services.witai.models.WitAiUnderstoodResponse
import com.meta.pixelandtexel.geovoyage.ui.askearth.Routes

class AskEarthViewModel : ViewModel(), IPlayModeViewModel {
  private val _route = mutableStateOf("")
  private val _title = mutableStateOf("")
  private val _userRejectedPermission = mutableStateOf(false)
  private val _successAnswer = mutableStateOf("")
  private val _errorMessage = mutableStateOf("")
  private val _volume = mutableIntStateOf(0)

  val route: State<String> = _route
  val title: State<String> = _title
  val userRejectedPermission: State<Boolean> = _userRejectedPermission
  val successAnswer: State<String> = _successAnswer
  val errorMessage: State<String> = _errorMessage
  val volume: State<Int> = _volume

  private var micPermissionsGranted = false

  init {
    micPermissionsGranted = hasMicPermissions()
    navTo(if (micPermissionsGranted) Routes.LISTENING_ROUTE else Routes.PERMISSIONS_ROUTE)
  }

  override fun onPlayModeSuspended() {
    WitAiService.cancel()
    _userRejectedPermission.value = false // reset this
  }

  private fun hasMicPermissions(): Boolean {
    val recordAudioResult =
        ContextCompat.checkSelfPermission(
            PanelActivity.instance.get()!!, Manifest.permission.RECORD_AUDIO)

    return recordAudioResult == PackageManager.PERMISSION_GRANTED
  }

  fun userDelayedPermissions() {
    // handle user selecting Not Now
    _userRejectedPermission.value = true
  }

  fun userRequestedPermissions() {
    MainActivity.instance.get()?.requestPermissions { granted ->
      micPermissionsGranted = granted

      if (granted) {
        navTo(Routes.LISTENING_ROUTE)
      } else {
        // handle user rejecting mic access
        _userRejectedPermission.value = true
      }
    }
  }

  fun startListening() {
    _title.value = "Speak"

    val startResult =
        WitAiService.startSpeechToText(
            object : IWitAiServiceHandler {
              override fun onStartedListening() {
                MainActivity.instance.get()?.userStartedSpeaking()
              }

              override fun onAmplitudeChanged(amplitude: Int) {
                _volume.intValue = amplitude
              }

              override fun onFinishedListening() {
                _title.value = ""
                _route.value = Routes.THINKING_ROUTE
                MainActivity.instance.get()?.userFinishedSpeaking()
              }

              override fun onPartial(partial: String) {
                _title.value = partial
              }

              override fun onFinished(result: WitAiUnderstoodResponse) {
                if (result.text.isEmpty()) {
                  _route.value = Routes.REJECTED_ROUTE
                  return
                }

                _title.value = result.text

                if (WitAIFlowService.shouldSendResponseToLlama(result)) {
                  askLlamaQuestion(result.text)
                } else {
                  _route.value = Routes.REJECTED_ROUTE
                }
              }

              override fun onCanceled() {
                _title.value = ""
                navTo(Routes.LISTENING_ROUTE)
              }

              override fun onError(reason: String) {
                Log.e("WIT AI ERROR", reason)
                _errorMessage.value = "Wit.ai Error: $reason"
                _route.value = Routes.ERROR_ROUTE
              }
            })

    if (startResult != WitAiStartResult.SUCCESS) {
      Log.e("AUDIO ERROR", startResult.toString())
      _errorMessage.value = "Audio Error: $startResult"
      _route.value = Routes.ERROR_ROUTE
    }
  }

  fun stopListening() {
    WitAiService.stop()
  }

  fun askLlamaQuestion(query: String) {
    _title.value = query

    QueryLlamaService.submitQuery(
        query = query,
        handler =
            object : IQueryLlamaServiceHandler {
              override fun onStreamStart() {
                _route.value = Routes.SUCCESS_ROUTE
              }

              override fun onPartial(partial: String) {
                _successAnswer.value = partial
              }

              override fun onFinished(answer: String) {
                _successAnswer.value = answer
                Log.d("Ask Earth", "Received llama response $answer")
              }

              override fun onError(reason: String) {
                _errorMessage.value = "Llama Error \n $reason"
                _route.value = Routes.ERROR_ROUTE
              }
            })
  }

  fun navTo(dest: String) {
    _route.value = dest
  }
}
