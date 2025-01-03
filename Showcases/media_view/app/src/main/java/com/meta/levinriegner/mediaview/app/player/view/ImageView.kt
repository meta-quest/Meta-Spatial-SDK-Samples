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
import com.meta.levinriegner.mediaview.app.player.view.edit.CropState
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.CropifyOption
import io.moyuru.cropify.CropifySize.PercentageSize
import io.moyuru.cropify.rememberCropifyState
import timber.log.Timber

@Composable
fun ImageView(
    uri: Uri,
    cropState: CropState,
) {
    // https://github.com/MoyuruAizawa/Cropify
    val state = rememberCropifyState()
    when (cropState) {
        CropState.NotSupported, CropState.Disabled -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(uri).build(),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        CropState.Enabled -> {
            Cropify(
                uri = uri,
                state = state,
                modifier = Modifier.fillMaxSize(),
                onImageCropped = {
                    Timber.i("Image cropped!")
                },
                onFailedToLoadImage = {
                    Timber.w("Failed to load image")
                },
                option = CropifyOption(
                    frameSize = PercentageSize(0.15f)
                ),
            )
        }
    }

}
