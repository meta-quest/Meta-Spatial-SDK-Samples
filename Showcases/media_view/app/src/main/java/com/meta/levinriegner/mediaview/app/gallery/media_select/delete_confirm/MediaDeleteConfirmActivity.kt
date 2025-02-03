// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.media_select.delete_confirm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaDeleteConfirmActivity: ComponentActivity() {
  private val mediaDeleteConfirmViewModel: MediaDeleteConfirmViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    buildUi()
  }

  private fun buildUi() {
    setContent {
      // Observables
      val mediaToDelete by mediaDeleteConfirmViewModel.mediaToDelete.collectAsState()

      MediaViewTheme {
        Box(
            modifier =
            Modifier.fillMaxSize()
                .border(1.dp, AppColor.MetaBlu, RoundedCornerShape(Dimens.radiusMedium))
                .clip(RoundedCornerShape(Dimens.radiusMedium))) {
          MediaDeleteConfirmView (
              modifier = Modifier.fillMaxSize().background(AppColor.BackgroundSweep),
              onConfirmed = { mediaDeleteConfirmViewModel.confirm() },
              onCanceled = { mediaDeleteConfirmViewModel.cancel() },
              mediaToDelete = mediaToDelete,
          )
        }
      }
    }
  }
}
