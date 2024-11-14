// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.gallery.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.gallery.filter.iconResId
import com.meta.levinriegner.mediaview.app.immersive.ImmersiveActivity
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.spatial.toolkit.SpatialActivityManager
import kotlinx.coroutines.flow.map

@Composable
fun MediaItemView(
    item: MediaModel,
    showMetadata: Boolean,
    modifier: Modifier = Modifier,
    onItemClicked: (MediaModel) -> Unit,
) {
  var icon = item.mediaFilter?.iconResId()
  if (icon == null) icon = R.drawable.icon_viewall

  val openMediaIds =
      (SpatialActivityManager.getAppSystemActivity() as ImmersiveActivity)
          .openMedia
          .map { it.keys }
          .collectAsState(emptySet())
  Box(
      modifier =
          modifier
              .size(Dimens.galleryItemSize)
              .clip(RoundedCornerShape(Dimens.radiusMedium))
              .clickable { onItemClicked(item) }) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier) {
          AsyncImage(
              model =
                  ImageRequest.Builder(LocalContext.current)
                      .data(item.uri)
                      .size(Size(1000, 1000))
                      .crossfade(true)
                      .build(),
              contentDescription = item.name,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
              alignment = Alignment.Center)
          Column(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.Bottom,
          ) {
            Row(
                modifier = Modifier.padding(Dimens.xSmall),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom,
            ) {
              Icon(
                  painter = painterResource(id = icon),
                  contentDescription = "Button Icon",
                  modifier = Modifier.size(22.dp))

              item.durationHMS()?.let {
                val duration =
                    (it.first.takeIf { it > 0 }?.let { h -> "${h}:" } ?: "") +
                        it.second.let { m -> "${m}:" } +
                        it.third.let { s -> "$s".padStart(2, '0') }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = duration,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Right,
                )
              }
            }
          }
        }

        // TODO: Style
        if (showMetadata) {
          Box(
              modifier =
                  Modifier.matchParentSize()
                      .background(AppColor.GradientInEnvironmentStart)
                      .blur(radius = 16.dp)
                      .clip(RoundedCornerShape(5.dp)),
          )
          Column { Text(text = "Name: ${item.name}") }
        }

        if (openMediaIds.value.contains(item.id)) {
          Box(
              contentAlignment = Alignment.Center,
              modifier =
                  Modifier.matchParentSize()
                      .background(AppColor.GradientInEnvironmentStart)
                      .blur(radius = 16.dp)
                      .clip(RoundedCornerShape(5.dp)),
          ) {}
          Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.matchParentSize().clip(RoundedCornerShape(5.dp)),
          ) {
            Text(
                modifier = Modifier.padding(horizontal = 5.dp).fillMaxWidth(),
                text = "Media in\nEnvironment",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
          }
        }
      }
}
