// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.view

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

@Composable
fun PanoramaImageView(
    context: Context,
    uri: Uri,
    modifier: Modifier = Modifier,
) {
  AndroidView(
      factory = { ImageView(context).apply { scaleType = ImageView.ScaleType.FIT_CENTER } },
      update = { imageView ->
        Glide.with(context)
            .load(uri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(4096, 2048) // Load the image at its original size
                    .fitCenter() // Ensure the image fits within the view boundaries
                )
            .into(imageView)
      },
      modifier = modifier.fillMaxSize(),
  )
}
