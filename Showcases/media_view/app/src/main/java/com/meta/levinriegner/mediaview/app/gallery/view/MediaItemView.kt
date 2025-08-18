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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.gallery.filter.iconResId
import com.meta.levinriegner.mediaview.app.immersive.ImmersiveActivity
import com.meta.levinriegner.mediaview.app.shared.theme.AppColor
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.shared.view.component.AnimatedOpacity
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
  val icon = item.mediaFilter?.iconResId() ?: R.drawable.icon_viewall

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
              alignment = Alignment.Center,
          )

          AnimatedOpacity(visible = !showMetadata) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
              Row(
                  modifier =
                      Modifier.wrapContentSize()
                          .padding(
                              vertical = 6.dp,
                              horizontal = 8.dp,
                          ),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Button Icon",
                    modifier = Modifier.size(18.dp),
                )

                item.durationHMS()?.let {
                  val duration =
                      (it.first.takeIf { it > 0 }?.let { h -> "${h}:" } ?: "") +
                          it.second.let { m -> "${m}:" } +
                          it.third.let { s -> "$s".padStart(2, '0') }

                  Text(
                      modifier = Modifier.fillMaxWidth(),
                      text = duration,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      textAlign = TextAlign.Right,
                  )
                }
              }
            }
          }
        }

        AnimatedOpacity(visible = showMetadata) {
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(
                          vertical = 6.dp,
                          horizontal = 8.dp,
                      ),
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.SpaceBetween,
          ) {
            Column {
              Text(
                  text = "Name: ${item.nameLabel()}",
                  style =
                      TextStyle(
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White,
                      ),
                  maxLines = 3,
                  overflow = TextOverflow.Ellipsis,
              )
              Text(
                  text = "Date: ${item.dateAdded}",
                  style =
                      TextStyle(
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White,
                      ),
                  maxLines = 1,
                  overflow = TextOverflow.Visible,
              )
              Text(
                  text = "Kind: ${item.mimeTypeLabel()}",
                  style =
                      TextStyle(
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White,
                      ),
                  maxLines = 1,
              )
              Text(
                  text = "Size: ${item.sizeLabel()}",
                  style =
                      TextStyle(
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White,
                      ),
                  maxLines = 1,
              )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                  painter = painterResource(id = icon),
                  contentDescription = "Button Icon",
                  modifier = Modifier.size(18.dp),
              )

              item.durationHMS()?.let {
                val duration =
                    (it.first.takeIf { it > 0 }?.let { h -> "${h}:" } ?: "") +
                        it.second.let { m -> "${m}:" } +
                        it.third.let { s -> "$s".padStart(2, '0') }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = duration,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                )
              }
            }
          }

          Box(
              modifier =
                  Modifier.fillMaxSize()
                      .background(Color.Black.copy(alpha = .5f))
                      .blur(100.dp)
                      .zIndex(-1f))
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
