// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.player.view.ImageView
import com.meta.levinriegner.mediaview.app.player.view.PanoramaImageView
import com.meta.levinriegner.mediaview.app.player.view.VideoView
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import com.meta.levinriegner.mediaview.app.shared.view.ErrorView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {

  private val viewModel: PlayerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    initObservers()
    buildUi()
  }

  private fun initObservers() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.event.collect { event ->
          Timber.i("Event: $event")
          when (event) {
            PlayerEvent.Close -> finish()
          }
        }
      }
    }
  }

  private fun buildUi() {
    setContent {
      // Observables
      val mediaModel = viewModel.state.collectAsState()
      // UI
      Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Box(Modifier.fillMaxSize()) {
          when (val state = mediaModel.value) {
            PlayerState.Empty -> Box(Modifier)
            is PlayerState.Image2D -> {
              ImageView(uri = state.uri)
            }

            is PlayerState.ImagePanorama -> {
              val context = LocalContext.current
              PanoramaImageView(
                  context = context,
                  uri = state.uri,
                  modifier = Modifier.fillMaxSize(),
              )
            }

            is PlayerState.Video2D -> {
              VideoView(uri = state.uri)
            }

            is PlayerState.Error -> {
              ErrorView(
                  modifier = Modifier.fillMaxSize(),
                  description = state.reason,
                  actionButtonText = stringResource(id = R.string.close),
                  onActionButtonPressed = { viewModel.onClose() },
              )
            }
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun PlayerActivityPreview() {
  MediaViewTheme { Text("PlayerActivityPreview") }
}
