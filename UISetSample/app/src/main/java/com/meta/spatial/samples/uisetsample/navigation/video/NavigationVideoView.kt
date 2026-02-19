/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.navigation.video

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.meta.spatial.samples.uisetsample.R
import com.meta.spatial.samples.uisetsample.util.view.PanelScaffold
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun NavigationVideoView() {
  val context = LocalContext.current
  val player = remember {
    ExoPlayer.Builder(context).build().apply {
      setMediaItem(
          MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.demo_video}")
      )
      repeatMode = Player.REPEAT_MODE_ALL
      prepare()
      playWhenReady = true
    }
  }
  return PanelScaffold(padding = PaddingValues(0.dp)) {
    Column {
      AndroidView(
          factory = { context ->
            PlayerView(context).apply {
              useController = false
              this.player = player

              resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
              layoutParams =
                  FrameLayout.LayoutParams(
                      ViewGroup.LayoutParams.MATCH_PARENT,
                      ViewGroup.LayoutParams.MATCH_PARENT,
                  )
            }
          },
          modifier = Modifier.fillMaxWidth().weight(1f),
      )
      Column(
          modifier = Modifier.padding(36.dp),
      ) {
        Text(
            text = "UI Set based on the Meta Horizon Design System",
            style =
                LocalTypography.current.headline1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text =
                "In the realm of design, UI components serve as the building blocks of user interfaces. They include buttons, sliders, and navigation bars, each crafted to enhance user interaction and experience.",
            style =
                LocalTypography.current.body2Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
            textAlign = TextAlign.Center,
        )
      }
    }
  }
}
