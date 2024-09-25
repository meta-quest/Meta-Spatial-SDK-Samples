// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.menu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.meta.levinriegner.mediaview.app.immersive.ImmersiveActivity
import com.meta.levinriegner.mediaview.app.shared.theme.MediaViewTheme
import com.meta.spatial.toolkit.SpatialActivityManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class GalleryMenuActivity : ComponentActivity() {

  private val viewModel by viewModels<GalleryMenuViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    buildUi()
  }

  private fun buildUi() {
    setContent {
      // Observables
      val openMediaIds =
          (SpatialActivityManager.getAppSystemActivity() as ImmersiveActivity)
              .openMedia
              .map { it.keys }
              .collectAsState(emptySet())
      // UI
      MediaViewTheme {
        GalleryMenuView(
            openCount = openMediaIds.value.count(),
            canOpenMore = openMediaIds.value.count() < ImmersiveActivity.MAX_OPEN_MEDIA,
        ) {
          viewModel.closeAll()
        }
      }
    }
  }
}
