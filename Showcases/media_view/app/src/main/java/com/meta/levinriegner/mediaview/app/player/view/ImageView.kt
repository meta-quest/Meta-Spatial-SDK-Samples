// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.view

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageView(
    uri: Uri,
) {
  // Full-screen IMAGE from uri
  AsyncImage(
      model = ImageRequest.Builder(LocalContext.current).data(uri).build(),
      contentDescription = "",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Fit,
  )
}
