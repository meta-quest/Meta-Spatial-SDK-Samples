// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.immersive.ImmersiveActivity
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import com.meta.levinriegner.mediaview.app.shared.view.LoadingView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

/// Hybrid Activity
@AndroidEntryPoint
class PermissionActivity : ComponentActivity() {

  private val viewModel: PermissionViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    initObservers()
    buildUi()
  }

  private fun initObservers() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.CREATED) {
        viewModel.state.collect { state ->
          when (state) {
            PermissionState.CheckPermissionState -> {
              checkStoragePermissionStatus()
            }

            PermissionState.RequestPermission -> {
              // Do nothing, handle in UI
            }

            PermissionState.PermissionDenied -> {
              // Do nothing, handle in UI
            }

            PermissionState.PermissionAccepted -> {
              Timber.i("Navigating to Immersive Activity")
              // Navigate to Immersive Activity
              val immersiveIntent =
                  Intent(this@PermissionActivity, ImmersiveActivity::class.java).apply {
                    action = Intent.ACTION_MAIN
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                  }
              startActivity(immersiveIntent)
              finish()
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
      // UI
      MediaViewTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (uiState.value) {
              PermissionState.CheckPermissionState -> {
                LoadingView(modifier = Modifier.fillMaxSize())
              }

              PermissionState.RequestPermission -> {
                RequestPermissionRationale(
                    modifier = Modifier.padding(innerPadding),
                    denied = false,
                ) {
                  requestStoragePermission()
                }
              }

              PermissionState.PermissionDenied -> {
                RequestPermissionRationale(
                    modifier = Modifier.padding(innerPadding),
                    denied = true,
                ) {
                  requestStoragePermission()
                }
              }

              PermissionState.PermissionAccepted -> {
                // Do nothing, navigate to Immersive Activity
                Box(Modifier)
              }
            }
          }
        }
      }
    }
  }

  // # region Permission
  private fun checkStoragePermissionStatus() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      viewModel.onCheckPermissionResult(Environment.isExternalStorageManager())
    } else {
      viewModel.onCheckPermissionResult(
          ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
              PackageManager.PERMISSION_GRANTED
      )
    }
  }

  private val storagePermissionActivityResult =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          if (Environment.isExternalStorageManager()) {
            viewModel.onStoragePermissionGranted()
          } else {
            viewModel.onStoragePermissionDenied()
          }
        } else {
          // Ignore unrecognized activity result
          Timber.w("Unexpected activity result for API < 30")
        }
      }

  private val storageRuntimePermissionResult =
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
          viewModel.onStoragePermissionGranted()
        } else {
          viewModel.onStoragePermissionDenied()
        }
      }

  private fun requestStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // Android API 30+ requires manual approval through system settings
      try {
        // Launch intent to open system settings
        val intent =
            Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.fromParts("package", packageName, null),
                )
                .addCategory(Intent.CATEGORY_DEFAULT)
        startActivity(intent)
        storagePermissionActivityResult.launch(intent)
      } catch (e: Exception) {
        Timber.e(e, "Failed to launch permission request")
        viewModel.onStoragePermissionDenied()
      }
    } else {
      // Regular permission request for API < 30
      storageRuntimePermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
  }
  // # endregion

}

@Composable
private fun RequestPermissionRationale(
    modifier: Modifier = Modifier,
    denied: Boolean,
    onRequest: () -> Unit,
) {
  Box(
      contentAlignment = Alignment.Center,
      modifier =
          Modifier.size(300.dp, 250.dp)
              .clip(RoundedCornerShape(40.dp))
              .border(1.dp, AppColor.MetaBlu, RoundedCornerShape(40.dp))
              .background(
                  Brush.verticalGradient(listOf(AppColor.GradientStart, AppColor.GradientEnd))
              ),
  ) {
    Column(
        modifier = modifier.fillMaxSize().padding(Dimens.medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
          text = stringResource(id = R.string.storage_permission_rationale_title),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.titleMedium.copy(
            color = AppColor.White,
          ),
      )
      Spacer(modifier = Modifier.height(Dimens.small))
      Text(
          text =
              stringResource(
                  id =
                      if (denied) R.string.storage_permission_rationale_denied
                      else R.string.storage_permission_rationale_description
              ),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = AppColor.White,
          ),
      )
      Spacer(modifier = Modifier.height(Dimens.large))
      OutlinedButton(
          onClick = onRequest,
          colors =
              ButtonDefaults.buttonColors(
                  contentColor = AppColor.White,
                  containerColor = Color.Transparent,
              ),
      ) {
        Text(text = stringResource(id = R.string.storage_permission_rationale_button))
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
  MediaViewTheme { Text("Android") }
}
