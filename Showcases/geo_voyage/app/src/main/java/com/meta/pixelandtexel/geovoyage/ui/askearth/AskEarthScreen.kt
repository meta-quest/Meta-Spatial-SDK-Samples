// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.askearth

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.pixelandtexel.geovoyage.viewmodels.AskEarthViewModel

object Routes {
  const val PERMISSIONS_ROUTE = "permissions"
  const val LISTENING_ROUTE = "listening"
  const val THINKING_ROUTE = "thinking"
  const val SUCCESS_ROUTE = "success"
  const val REJECTED_ROUTE = "rejected"
  const val ERROR_ROUTE = "error"
}

@Composable
fun AskEarthScreen(
    vm: AskEarthViewModel = viewModel(),
    onUserRejectedPermission: (() -> Unit)? = null,
    setTitle: ((text: String) -> Unit)? = null,
    navController: NavHostController = rememberNavController()
) {
  val route by vm.route
  val title by vm.title
  val userRejectedPermission by vm.userRejectedPermission
  val successAnswer by vm.successAnswer
  val errorMessage by vm.errorMessage
  val volume by vm.volume

  val isActionButtonVisible =
      when (route) {
        Routes.PERMISSIONS_ROUTE,
        Routes.LISTENING_ROUTE,
        Routes.THINKING_ROUTE -> false

        else -> true
      }

  LaunchedEffect(route) {
    if (route.isEmpty()) {
      return@LaunchedEffect
    }

    navController.navigate(route) { launchSingleTop = true }

    when (route) {
      Routes.LISTENING_ROUTE -> {
        vm.startListening()
      }

      Routes.REJECTED_ROUTE -> setTitle?.invoke("...")
      Routes.ERROR_ROUTE -> setTitle?.invoke("")
    }
  }

  LaunchedEffect(title) { setTitle?.invoke(title) }

  LaunchedEffect(userRejectedPermission) {
    if (userRejectedPermission) {
      onUserRejectedPermission?.invoke()
    }
  }

  Scaffold(
      containerColor = Color.Transparent,
      floatingActionButton = {
        if (isActionButtonVisible) {
          FloatingActionButton(
              onClick = { vm.navTo(Routes.LISTENING_ROUTE) },
              modifier = Modifier.size(70.dp, 70.dp).offset((-40).dp, (-40).dp),
              containerColor = MaterialTheme.colorScheme.tertiary) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = "Ask another question",
                    modifier = Modifier.size(32.dp))
              }
        }
      }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.PERMISSIONS_ROUTE,
            modifier = Modifier.padding(innerPadding)) {
              composable(Routes.PERMISSIONS_ROUTE) {
                PermissionsScreen(
                    onNotNowClicked = { vm.userDelayedPermissions() },
                    onEnableClicked = { vm.userRequestedPermissions() })
              }
              composable(Routes.LISTENING_ROUTE) { ListeningScreen(volume) { vm.stopListening() } }
              composable(Routes.THINKING_ROUTE) { ThinkingScreen(title) }
              composable(Routes.SUCCESS_ROUTE) { SuccessScreen(successAnswer) }
              composable(Routes.REJECTED_ROUTE) {
                RejectedScreen { query ->
                  vm.navTo(Routes.THINKING_ROUTE)
                  vm.askLlamaQuestion(query)
                }
              }
              composable(Routes.ERROR_ROUTE) { ErrorScreen(errorMessage) }
            }
      }
}

@Preview(widthDp = 932, heightDp = 650, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
fun AskEarthScreenPreview() {
  GeoVoyageTheme { AskEarthScreen() }
}
