// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.meta.pixelandtexel.scanner.services.settings.SettingsKey
import com.meta.pixelandtexel.scanner.services.settings.SettingsService
import com.meta.pixelandtexel.scanner.views.welcome.Routes

class WelcomeViewModel(
    initialRoute: String = Routes.EMPTY // for @Preview
) : ViewModel() {
  private val _route = mutableStateOf(initialRoute)
  val route: State<String> = _route

  fun checkShouldShowNotice() {
    val hasUserAcceptedNotice = SettingsService.get(SettingsKey.ACCEPTED_NOTICE, false)

    if (hasUserAcceptedNotice) {
      navTo(Routes.CAMERA_CONTROLS_INTRO)
    } else {
      navTo(Routes.NOTICE)
    }
  }

  fun navTo(dest: String) {
    _route.value = dest
  }
}
