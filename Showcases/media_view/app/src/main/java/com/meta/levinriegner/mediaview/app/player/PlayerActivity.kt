// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    private val cropRequested = mutableStateOf(false)

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
                        PlayerEvent.OnCropImageRequested -> {
                            cropRequested.value = true
                        }

                        is PlayerEvent.OnImageSaved -> {
                            if (event.success) {
                                Toast.makeText(
                                    this@PlayerActivity,
                                    getString(R.string.save_as_new_image_success),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@PlayerActivity,
                                    event.error ?: getString(R.string.save_as_new_image_error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildUi() {
        setContent {
            // Observables
            val uiState by viewModel.state.collectAsState()
            // UI
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                Box(Modifier.fillMaxSize()) {
                    when (val state = uiState) {
                        PlayerState.Empty -> Box(Modifier)
                        is PlayerState.Image2D -> {
                            ImageView(
                                uri = state.uri,
                                cropState = state.cropState,
                                cropRequested = cropRequested,
                                onImageCropped = { viewModel.onImageCropped(it) },
                            )
//                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
//                              ImageView(uri = state.uri, cropState = state.cropState)
//                              TouchDebugGridView(count = 100)
//                            }
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
                            VideoView(uri = state.uri, is360Video = false)
                        }

                        is PlayerState.Video360 -> {
                            VideoView(uri = state.uri, is360Video = true)
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

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerActivityPreview() {
    MediaViewTheme { Text("PlayerActivityPreview") }
}
