// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meta.pixelandtexel.scanner.services.settings.SettingsKey
import com.meta.pixelandtexel.scanner.services.settings.SettingsService
import com.meta.pixelandtexel.scanner.viewmodels.WelcomeViewModel
import com.meta.spatial.uiset.theme.SpatialTheme

object Routes {
  const val EMPTY = "EMPTY"
  const val NOTICE = "NOTICE"
  const val CAMERA_CONTROLS_INTRO = "CAMERA_CONTROLS_INTRO"
}

@Composable
fun WelcomeScreen(
    vm: WelcomeViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    dismissPanel: (() -> Unit)? = null
) {
  val route by vm.route

  LaunchedEffect(route) { navController.navigate(route) { launchSingleTop = true } }

  LaunchedEffect(null) { vm.checkShouldShowNotice() }

  SpatialTheme {
    NavHost(navController = navController, startDestination = route) {
      composable(Routes.EMPTY) {
        // purposefully empty
      }
      composable(Routes.NOTICE) {
        NoticeView {
          SettingsService.set(SettingsKey.ACCEPTED_NOTICE, true)
          vm.navTo(Routes.CAMERA_CONTROLS_INTRO)
        }
      }
      composable(Routes.CAMERA_CONTROLS_INTRO) { CameraControlsView { dismissPanel?.invoke() } }
    }
  }
}

@Preview(widthDp = 368, heightDp = 404)
@Composable
private fun WelcomeScreenPreviewInterstitial() {
  WelcomeScreen(WelcomeViewModel(Routes.NOTICE))
}

@Preview(widthDp = 368, heightDp = 404)
@Composable
private fun WelcomeScreenPreviewWelcome() {
  WelcomeScreen(WelcomeViewModel(Routes.CAMERA_CONTROLS_INTRO))
}
