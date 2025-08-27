// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.filter

import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.FilterAppEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.gallery.model.MediaFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

@HiltViewModel
class MediaFilterViewModel
@Inject
constructor(
    private val panelDelegate: PanelDelegate,
    private val eventBus: EventBus,
) : ViewModel() {

  private val _filters =
      MutableStateFlow(
          MediaFilter.entries.map { filter -> UiMediaFilter(filter, filter == MediaFilter.initial) }
      )
  val filters = _filters.asStateFlow()

  fun onFilterSelected(filter: UiMediaFilter) {
    Timber.i("On Filter Selected: ${filter.type}")
    eventBus.post(FilterAppEvent.FilterChanged(filter.type))
    _filters.value = _filters.value.map { it.copy(isSelected = it.type == filter.type) }
  }

  fun onUpload() {
    Timber.i("On Upload")
    panelDelegate.openUploadPanel()
  }
}
