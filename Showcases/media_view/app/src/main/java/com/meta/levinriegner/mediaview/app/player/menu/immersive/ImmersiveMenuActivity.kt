// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.menu.immersive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImmersiveMenuActivity : ComponentActivity() {

  private val viewModel by viewModels<ImmersiveMenuViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    buildUi()
  }

  private fun buildUi() {
    setContent {
      MediaViewTheme {
        ImmersiveMenuView(
            state = viewModel.state.collectAsState().value,
            onMinimize = { viewModel.exitImmersiveMedia() },
            onEnterEdit = { viewModel.onEditPressed() },
            onExitEdit = { viewModel.onExitEditPressed() },
            onSaveAsNewImage = { viewModel.onSaveImagePressed() },
        )
      }
    }
  }
}
