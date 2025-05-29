// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.meta.pixelandtexel.scanner.models.ObjectInfoRequest
import com.meta.pixelandtexel.scanner.services.llama.IQueryLlamaServiceHandler
import com.meta.pixelandtexel.scanner.services.llama.QueryLlamaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * View model encapsulating information and functionality needed to query Llama for information
 * about an item, and display that info.
 *
 * @property infoRequest The [ObjectInfoRequest] object encapsulating the information needed for the
 *   Llama query invocation.
 * @property queryTemplate The string template into which to inject our query info.
 */
class ObjectInfoViewModel(
    private val infoRequest: ObjectInfoRequest,
    private val queryTemplate: String
) : ViewModel() {
  private val _resultMessage = mutableStateOf("")
  private val _title = mutableStateOf(infoRequest.name.replaceFirstChar { it.uppercaseChar() })
  private val _image = mutableStateOf(infoRequest.image)

  val title: State<String> = _title
  val resultMessage: State<String> = _resultMessage
  val image: State<Bitmap> = _image

  /**
   * Submits a query and image to our Llama invocation service, and updates the resulting response.
   */
  fun queryLlama() {
    CoroutineScope(Dispatchers.Main).launch {
      val query = queryTemplate.replace("{{object_name}}", infoRequest.name)

      QueryLlamaService.submitQuery(
          query,
          infoRequest.image,
          handler =
              object : IQueryLlamaServiceHandler {
                override fun onStreamStart() {}

                override fun onPartial(partial: String) {
                  _resultMessage.value = partial.trim('\n', '\r')
                }

                override fun onFinished(answer: String) {
                  _resultMessage.value = answer.trim('\n', '\r')
                }

                override fun onError(reason: String) {
                  _resultMessage.value = reason
                }
              })
    }
  }
}
