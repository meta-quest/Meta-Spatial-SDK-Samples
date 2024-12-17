// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.gallery.samples.SamplesStateView
import com.meta.levinriegner.mediaview.app.gallery.samples.SamplesViewModel
import com.meta.levinriegner.mediaview.app.gallery.samples.UiSamplesState
import com.meta.levinriegner.mediaview.app.gallery.view.GalleryView
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class GalleryActivity : ComponentActivity() {

    private val viewModel: GalleryViewModel by viewModels()

    private val samplesViewModel: SamplesViewModel by viewModels()

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
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                samplesViewModel.checkNewSamples()
            }
        }
        // Refresh media as new samples are downloaded
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                var previousState: UiSamplesState? = null
                samplesViewModel.state.collect { state ->
                    Timber.i("Samples state: $state")
                    if (state is UiSamplesState.DownloadingSamples && previousState is UiSamplesState.DownloadingSamples) {
                        if (state.current > (previousState as UiSamplesState.DownloadingSamples).current) {
                            if (viewModel.filter.value == MediaFilter.SAMPLE_MEDIA) {
                                viewModel.loadMedia()
                            }
                        }
                    }
                    previousState = state
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
            val showMetadata = viewModel.showMetadata.collectAsState()
            val samplesState = samplesViewModel.state.collectAsState()
            // UI
            Box(contentAlignment = Alignment.BottomCenter) {
                GalleryView(
                    uiState = uiState.value,
                    filter = filter.value,
                    sortBy = sortBy.value,
                    showMetadata = showMetadata.value,
                    onRefresh = { viewModel.loadMedia() },
                    onMediaSelected = { viewModel.onMediaSelected(it) },
                    onSortBy = { viewModel.onSortBy(it) },
                    onToggleMetadata = { viewModel.onToggleMetadata(it) },
                )
                if (samplesState.value != UiSamplesState.Idle && filter.value == MediaFilter.SAMPLE_MEDIA)
                    SamplesStateView(
                        modifier = Modifier.padding(Dimens.medium),
                        state = samplesState.value,
                        onDownload = { samplesViewModel.downloadSamples(it) },
                        onRefresh = { samplesViewModel.checkNewSamples() },
                        onDismiss = { samplesViewModel.dismissSamples() },
                    )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MediaViewTheme { Text("Hello World") }
}
