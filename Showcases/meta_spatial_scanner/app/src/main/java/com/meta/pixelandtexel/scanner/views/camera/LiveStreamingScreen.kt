// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meta.pixelandtexel.scanner.viewmodels.LiveStreamingViewModel
import com.meta.pixelandtexel.scanner.views.components.Panel
import com.meta.spatial.uiset.R.drawable as UIKitDrawable
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun LiveStreamingScreen(vm: LiveStreamingViewModel = viewModel()) {
  val aspectRatio by vm.aspectRatio
  val permissionGranted by vm.permissionGranted
  val ipAddress by vm.ipAddress

  SpatialTheme {
    Panel {
      Column(
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize()) {
            Column {
              Box(
                  contentAlignment = Alignment.BottomEnd,
                  modifier =
                      Modifier.fillMaxWidth()
                          .aspectRatio(16f / 9f)
                          .background(Color.Black, SpatialTheme.shapes.medium)
                          .clip(SpatialTheme.shapes.medium)
                          .padding(8.dp)) {
                    if (permissionGranted) {
                      CameraPreviewView(
                          onSurfaceAvailable = { vm.onSurfaceAvailable(it) },
                          onSurfaceDestroyed = { vm.onSurfaceDestroyed(it) },
                          modifier =
                              Modifier.fillMaxWidth(0.33f)
                                  .aspectRatio(aspectRatio)
                                  .clip(SpatialTheme.shapes.small))
                    }
                  }
              if (ipAddress != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Streaming on ${ipAddress!!}",
                    style = SpatialTheme.typography.body2,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.End))
              }
            }
            Row {
              IconButton(
                  onClick = vm.requestPermission,
                  enabled = !permissionGranted,
                  colors =
                      IconButtonColors(
                          containerColor = SpatialTheme.colorScheme.secondaryButton,
                          contentColor = Color.White,
                          disabledContainerColor = SpatialTheme.colorScheme.secondaryButton,
                          disabledContentColor = Color.White),
                  modifier = Modifier.size(64.dp)) {
                    Icon(
                        painter = painterResource(UIKitDrawable.ic_video_capture_24),
                        contentDescription = "",
                        tint = if (permissionGranted) SpatialColor.white30 else Color.White,
                        modifier = Modifier.size(48.dp))
                  }
            }
          }
    }
  }
}

@Preview(widthDp = 400, heightDp = 400)
@Composable
private fun LiveStreamingScreenPreview() {
  LiveStreamingScreen(
      LiveStreamingViewModel(
          requestPermission = {}, onSurfaceAvailable = {}, onSurfaceDestroyed = {}))
}

@Preview(widthDp = 400, heightDp = 400)
@Composable
private fun LiveStreamingScreenPermissionGrantedPreview() {
  LiveStreamingScreen(
      LiveStreamingViewModel(
          true, requestPermission = {}, onSurfaceAvailable = {}, onSurfaceDestroyed = {}))
}
