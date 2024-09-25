// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.gallery.view.GalleryView
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class GalleryActivity : ComponentActivity() {

  private val viewModel: GalleryViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    initObservers()
    buildUi()
  }

  private fun initObservers() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.events.collect { event ->
          Timber.i("Event: $event")
          when (event) {
            is GalleryEvent.UploadFailed -> {
              Toast.makeText(
                      this@GalleryActivity,
                      event.error ?: getString(R.string.upload_failed_error),
                      Toast.LENGTH_LONG)
                  .show()
            }
          }
        }
      }
    }
  }

  private fun buildUi() {
    setContent {
      // Observables
      val uiState = viewModel.state.collectAsState()
      val filter = viewModel.filter.collectAsState()
      val sortBy = viewModel.sortBy.collectAsState()
      // UI
      GalleryView(
          uiState = uiState.value,
          filter = filter.value,
          sortBy = sortBy.value,
          onRefresh = { viewModel.loadMedia() },
          onMediaSelected = { viewModel.onMediaSelected(it) },
          onSortBy = { viewModel.onSortBy(it) },
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MediaViewTheme { Text("Hello World") }
}
