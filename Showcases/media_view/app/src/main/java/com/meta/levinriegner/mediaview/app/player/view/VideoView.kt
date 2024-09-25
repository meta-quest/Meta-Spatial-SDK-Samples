// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.view

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoView(
    uri: Uri,
) {
  // Setup ExoPlayer
  val exoPlayer = ExoPlayer.Builder(LocalContext.current).build()
  val mediaSource = remember(uri) { MediaItem.fromUri(uri) }
  LaunchedEffect(mediaSource) {
    exoPlayer.setMediaItem(mediaSource)
    exoPlayer.prepare()
    exoPlayer.playWhenReady = false
  }
  DisposableEffect(Unit) { onDispose { exoPlayer.release() } }
  Box(Modifier.background(color = Color.Black)) {
    AndroidView(
        factory = { ctx -> PlayerView(ctx).apply { player = exoPlayer } },
        modifier = Modifier.fillMaxSize())
  }
}
