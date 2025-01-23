// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.data.samples.model.SamplesList

@Composable
fun SamplesStateView(
    modifier: Modifier,
    state: UiSamplesState,
    onDownload: (SamplesList) -> Unit,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
) {
  Box(
      modifier =
          modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(Dimens.radiusMedium))
              .border(1.dp, AppColor.MetaBlu, RoundedCornerShape(Dimens.radiusMedium))
              .background(
                  Brush.horizontalGradient(listOf(AppColor.GradientStart, AppColor.GradientEnd)))) {
        when (state) {
          is UiSamplesState.Idle -> {
            Box(Modifier)
          }

          is UiSamplesState.NoInternet -> {
            Row(
                modifier = Modifier.padding(Dimens.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                  text = stringResource(R.string.sample_media_no_internet),
                  modifier = Modifier.weight(1f),
                  color = AppColor.White,
              )
              Box(Modifier.padding(Dimens.medium))
              OutlinedButton(
                  colors =
                      ButtonDefaults.buttonColors(
                          contentColor = AppColor.White,
                          containerColor = Color.Transparent,
                      ),
                  onClick = onRefresh,
              ) {
                Text(
                    text = stringResource(R.string.retry),
                    color = AppColor.White,
                )
              }
              Box(Modifier.padding(Dimens.small))
              TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = AppColor.White,
                )
              }
            }
          }

          is UiSamplesState.Loading -> {
            Row(
                modifier = Modifier.padding(Dimens.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                  text = stringResource(R.string.sample_media_loading),
                  modifier = Modifier.weight(1f),
                  color = AppColor.White,
              )
              Box(Modifier.padding(Dimens.medium))
              CircularProgressIndicator(
                  color = AppColor.White,
              )
            }
          }

          is UiSamplesState.NewSamplesAvailable -> {
            Row(
                modifier = Modifier.padding(Dimens.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                  text = stringResource(R.string.sample_media_available),
                  modifier = Modifier.weight(1f),
                  color = AppColor.White,
              )
              Box(Modifier.padding(Dimens.medium))
              OutlinedButton(
                  colors =
                      ButtonDefaults.buttonColors(
                          contentColor = AppColor.White,
                          containerColor = Color.Transparent,
                      ),
                  onClick = { onDownload(state.samples) },
              ) {
                Text(
                    text = stringResource(R.string.download),
                    color = AppColor.White,
                )
              }
              Box(Modifier.padding(Dimens.small))
              TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = AppColor.White,
                )
              }
            }
          }

          is UiSamplesState.DownloadingSamples -> {
            Row(
                modifier = Modifier.padding(Dimens.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                  text =
                      stringResource(R.string.sample_media_downloading, state.current, state.total),
                  modifier = Modifier.weight(1f),
                  color = AppColor.White,
              )
              Box(Modifier.padding(Dimens.medium))
              CircularProgressIndicator(
                  color = AppColor.White,
              )
            }
          }

          is UiSamplesState.DownloadError -> {
            Row(
                modifier = Modifier.padding(Dimens.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                  text = stringResource(R.string.sample_media_error, state.message),
                  modifier = Modifier.weight(1f),
                  color = AppColor.White,
              )
              Box(Modifier.padding(Dimens.medium))
              OutlinedButton(
                  colors =
                      ButtonDefaults.buttonColors(
                          contentColor = AppColor.White,
                          containerColor = Color.Transparent,
                      ),
                  onClick = onRefresh,
              ) {
                Text(
                    text = stringResource(R.string.retry),
                    color = AppColor.White,
                )
              }
              Box(Modifier.padding(Dimens.small))
              TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = AppColor.White,
                )
              }
            }
          }

          is UiSamplesState.DownloadSuccess -> {
            Row(
                modifier = Modifier.padding(Dimens.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                  text = stringResource(R.string.sample_media_success),
                  modifier = Modifier.weight(1f),
                  color = AppColor.White,
              )
              Box(Modifier.padding(Dimens.medium))
              TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = AppColor.White,
                )
              }
            }
          }
        }
      }
}
