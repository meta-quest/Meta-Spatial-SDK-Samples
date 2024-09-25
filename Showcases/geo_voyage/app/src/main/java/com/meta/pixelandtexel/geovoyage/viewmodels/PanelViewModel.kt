// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.ui.mainnavigator.Routes

class PanelViewModel : ViewModel() {
  private val _title = mutableStateOf("")
  private val _route = mutableStateOf("")
  private val _prevRoute = mutableStateOf("")
  private val _hasUserAcceptedNotice = mutableStateOf(false)

  val title: State<String> = _title
  val route: State<String> = _route
  val prevRoute: State<String> = _prevRoute
  val hasUserAcceptedNotice: State<Boolean> = _hasUserAcceptedNotice

  init {
    _hasUserAcceptedNotice.value = SettingsService.get(SettingsKey.ACCEPTED_NOTICE, false)

    if (_hasUserAcceptedNotice.value) {
      _route.value = Routes.INTRO_ROUTE
    }
  }

  fun userAcceptedNotice() {
    SettingsService.set(SettingsKey.ACCEPTED_NOTICE, true)
    _hasUserAcceptedNotice.value = true
  }

  fun setTitle(title: String) {
    _title.value = title
  }

  fun navTo(dest: String) {
    _title.value = ""

    _prevRoute.value = _route.value
    _route.value = dest
  }
}
