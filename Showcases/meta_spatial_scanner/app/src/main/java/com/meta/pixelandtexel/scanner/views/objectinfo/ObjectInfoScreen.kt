// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.objectinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meta.pixelandtexel.scanner.models.ObjectInfoRequest
import com.meta.pixelandtexel.scanner.viewmodels.ObjectInfoViewModel
import com.meta.pixelandtexel.scanner.views.components.ObjectInfoView
import com.meta.pixelandtexel.scanner.views.components.Panel
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun ObjectInfoScreen(
    vm: ObjectInfoViewModel = viewModel(),
    onResume: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
  val title by vm.title
  val resultMessage by vm.resultMessage
  val image by vm.image

  LaunchedEffect(null) { vm.queryLlama() }

  SpatialTheme {
    Panel {
      ObjectInfoView(title, BitmapPainter(image.asImageBitmap()), resultMessage, onResume, onClose)
    }
  }
}

@Preview(widthDp = 632, heightDp = 644)
@Composable
private fun ObjectInfoScreenPreviewLoading() {
  ObjectInfoScreen(ObjectInfoViewModel(ObjectInfoRequest("Name", createBitmap(100, 1)), ""))
}

@Preview(widthDp = 632, heightDp = 644)
@Composable
private fun ObjectInfoScreenPreview() {
  ObjectInfoScreen(ObjectInfoViewModel(ObjectInfoRequest("Name", createBitmap(100, 100)), ""))
}
