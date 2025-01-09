// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.player.view

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.meta.levinriegner.mediaview.app.player.view.edit.CropState
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.CropifyOption
import io.moyuru.cropify.CropifySize
import io.moyuru.cropify.rememberCropifyState
import timber.log.Timber

@Composable
fun ImageView(
    uri: Uri,
    cropState: CropState,
) {
    val framePercentage = 0.15f
    val state = rememberCropifyState()
    when (cropState) {
        CropState.NotSupported -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(uri).build(),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        CropState.Disabled, CropState.Enabled -> {
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
                option = when (cropState) {
                    CropState.Enabled -> CropifyOption(
                        frameSize = CropifySize.PercentageSize(framePercentage)
                    )

                    else -> CropifyOption(
                        gridAlpha = 0f,
                        maskAlpha = 0f,
                        frameAlpha = 0f,
                        backgroundColor = Color.Transparent,
                        frameSize = CropifySize.PercentageSize(framePercentage)
                    )
                },
            )
        }
    }

}
