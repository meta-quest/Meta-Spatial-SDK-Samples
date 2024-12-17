// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.samples

import com.meta.levinriegner.mediaview.data.samples.model.SamplesList

sealed class UiSamplesState {

    data object Idle : UiSamplesState()
    data object NoInternet : UiSamplesState()
    data object Loading : UiSamplesState()
    data class NewSamplesAvailable(val samples: SamplesList) : UiSamplesState()
    data class DownloadingSamples(val current: Int, val total: Int) : UiSamplesState() // [1, n]
    data class DownloadError(val message: String) : UiSamplesState()
    data object DownloadSuccess : UiSamplesState()
}