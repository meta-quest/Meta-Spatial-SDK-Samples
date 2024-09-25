// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.meta.pixelandtexel.geovoyage.services.llama.IQueryLlamaServiceHandler
import com.meta.pixelandtexel.geovoyage.services.llama.QueryLlamaService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TodayInHistoryViewModel : ViewModel(), IPlayModeViewModel {
  companion object {
    private const val TAG: String = "TodayInHistoryViewModel"
  }

  private val _title = mutableStateOf("")
  private val _result = mutableStateOf("")
  private val _busy = mutableStateOf(false)

  val title: State<String> = _title
  val result: State<String> = _result
  val busy: State<Boolean> = _busy

  private lateinit var queryTemplate: String

  fun updateBaseQueryString(template: String) {
    queryTemplate = template

    if (_result.value.isEmpty()) {
      startTodayInHistoryQuery()
    }
  }

  fun startTodayInHistoryQuery() {
    if (_busy.value) {
      return
    }

    // Get the current date, formatted
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH)
    val formattedDate = currentDate.format(formatter)

    _title.value = formattedDate
    _busy.value = true

    val query = String.format(queryTemplate, formattedDate)
    Log.d(TAG, "Full query: $query")

    QueryLlamaService.submitQuery(
        query = query,
        creativity = .8f,
        handler =
            object : IQueryLlamaServiceHandler {
              override fun onStreamStart() {
                // TODO hide loading message/graphic
              }

              override fun onPartial(partial: String) {
                _result.value = partial
              }

              override fun onFinished(answer: String) {
                _result.value = answer
                Log.d(TAG, "Received llama response $answer")

                _busy.value = false
              }

              override fun onError(reason: String) {
                _result.value = "Llama error:\n$reason"

                _busy.value = false
              }
            })
  }
}
