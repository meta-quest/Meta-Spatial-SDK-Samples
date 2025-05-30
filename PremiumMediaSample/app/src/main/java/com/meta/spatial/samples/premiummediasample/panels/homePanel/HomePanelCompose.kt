// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.panels.homePanel

import android.util.Log
import androidx.activity.compose.ReportDrawn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.samples.premiummediasample.data.HomeItem
import com.meta.spatial.samples.premiummediasample.entities.ExoVideoEntity.Companion.TAG
import com.meta.spatial.samples.premiummediasample.panels.MetaButton
import com.meta.spatial.samples.premiummediasample.ui.theme.Inter18

object HomePanelConstants {
  // Panel DP
  const val PANEL_WIDTH_DP = 1296f
  const val PANEL_HEIGHT_DP = 650f
  // UI Constants
  val backgroundColour = Color(0xFF1C2B33)
  val linear = Brush.verticalGradient(listOf(Color(0xf0000000), Color(0x40000000)))
  val homePanelDebugHover = false

  // Pre-calculated dimensions
  val padding = 24f.dp
  val cornerRadius = 12f.dp
  val itemWidth = 400f.dp
  val itemHeight = 600f.dp
  val badgeCornerRadius = 6f.dp
  val smallPadding = 5f.dp
  val badgePadding = 6f.dp
  val badgeHorizontalPadding = 10f.dp
  val rowSpacing = 10f.dp

  // Text styles
  val titleStyle =
      TextStyle(
          fontFamily = Inter18,
          fontSize = 28f.sp,
          fontWeight = FontWeight.Bold,
          lineHeight = 40f.sp)

  val descriptionStyle =
      TextStyle(
          fontFamily = Inter18,
          fontSize = 22f.sp,
          fontWeight = FontWeight.Normal,
          lineHeight = 32f.sp)

  val badgeStyle =
      TextStyle(
          fontFamily = Inter18,
          fontSize = 22f.sp,
          fontWeight = FontWeight.Black,
      )
}

@Preview(
    widthDp = HomePanelConstants.PANEL_WIDTH_DP.toInt(),
    heightDp = HomePanelConstants.PANEL_HEIGHT_DP.toInt())
@Composable
fun HomeViewPreview() {
  val viewModelPreview = HomePanelViewModel()
  HomeView(viewModelPreview)
}

@Composable
fun HomeView(homeViewModel: HomePanelViewModel) {
  // Normally used for App benchmarking
  // (https://developer.android.com/topic/performance/vitals/launch-time), this ReportDrawn() will
  // call the reportFullyDrawn() method on the Activity once compose is finished layouting.
  // We can use this to notify the ImmersiveActivity that the panel is ready to be animated.
  ReportDrawn()
  Box(modifier = Modifier.height(HomePanelConstants.PANEL_HEIGHT_DP.dp)) {
    Box(
        modifier =
            Modifier.clip(RoundedCornerShape(HomePanelConstants.padding))
                .background(HomePanelConstants.backgroundColour)) {
          HomeItems(homeViewModel.items, homeViewModel)
        }
  }
}

@Composable
fun HomeItems(items: List<HomeItem>, homeViewModel: HomePanelViewModel) {
  LazyRow(
      verticalAlignment = Alignment.Bottom,
      contentPadding = PaddingValues(HomePanelConstants.padding),
      horizontalArrangement = Arrangement.spacedBy(HomePanelConstants.padding),
      modifier = Modifier.fillMaxWidth()) {
        items(items = items.filter { it.showInMenu }) { homePrototypeItem ->
          HomeItem(homePrototypeItem, homeViewModel)
        }
      }
}

@Composable
fun HomeItem(item: HomeItem, homeViewModel: HomePanelViewModel) {
  val image: Painter = painterResource(id = item.thumbId)
  val interactionSource = remember { MutableInteractionSource() }
  val isHovered by interactionSource.collectIsHoveredAsState()

  Box(
      modifier =
          Modifier.clip(RoundedCornerShape(HomePanelConstants.cornerRadius))
              .width(HomePanelConstants.itemWidth)
              .height(HomePanelConstants.itemHeight)
              .paint(
                  painter = image,
                  contentScale = ContentScale.FillBounds,
              )
              .hoverable(interactionSource = interactionSource)) {
        HoverContent(
            isVisible = (isHovered || HomePanelConstants.homePanelDebugHover),
            item = item,
            homeViewModel = homeViewModel)
      }
}

@Composable
private fun HoverContent(isVisible: Boolean, item: HomeItem, homeViewModel: HomePanelViewModel) {
  AnimatedVisibility(visible = isVisible, enter = fadeIn(initialAlpha = 0.1f), exit = fadeOut()) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().background(HomePanelConstants.linear)) {
          Box(
              modifier =
                  Modifier.fillMaxHeight().fillMaxWidth().padding(HomePanelConstants.padding)) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier =
                        Modifier.matchParentSize()
                            .padding(
                                top = HomePanelConstants.smallPadding,
                                start = HomePanelConstants.smallPadding)) {
                      Text(
                          text = item.description?.title ?: item.id,
                          textAlign = TextAlign.Left,
                          style = HomePanelConstants.titleStyle,
                          color = Color.White,
                          modifier = Modifier.fillMaxWidth())
                      Text(
                          text = item.description?.description ?: "",
                          textAlign = TextAlign.Left,
                          style = HomePanelConstants.descriptionStyle,
                          color = Color(0x99FFFFFF),
                          modifier =
                              Modifier.fillMaxWidth()
                                  .padding(
                                      top = HomePanelConstants.smallPadding,
                                      bottom = HomePanelConstants.smallPadding))
                      Row(
                          horizontalArrangement =
                              Arrangement.spacedBy(HomePanelConstants.rowSpacing),
                      ) {
                        SteroModeBadge(item.media.stereoMode)
                      }
                      Box(
                          modifier = Modifier.weight(1f).fillMaxWidth(),
                          contentAlignment = Alignment.Center) {
                            MetaButton(
                                text = "PLAY",
                                cornerRadius = 36f.dp,
                                paddingHorizontal = 21f.dp,
                                paddingVertical = 10.5f.dp,
                                textSizeModifier = 1f,
                                onClick = {
                                  Log.d(TAG, ">>> PLAY clicked")
                                  homeViewModel.onItemSelectedHandler.invoke(item)
                                })
                          }
                    }
              }
        }
  }
}

@Composable
fun SteroModeBadge(stereoMode: StereoMode) {
  val label =
      if (stereoMode == StereoMode.LeftRight || stereoMode == StereoMode.UpDown) "3D" else "2D"
  BadgeBox(label)
}

@Composable
fun BadgeBox(label: String) {
  Box(
      contentAlignment = Alignment.Center,
      modifier =
          Modifier.padding(top = HomePanelConstants.rowSpacing)
              .wrapContentSize()
              .clip(RoundedCornerShape(HomePanelConstants.badgeCornerRadius))
              .background(Color.White)) {
        Box(
            modifier =
                Modifier.padding(
                    vertical = HomePanelConstants.badgePadding,
                    horizontal = HomePanelConstants.badgeHorizontalPadding)) {
              Text(
                  text = label,
                  textAlign = TextAlign.Center,
                  style = HomePanelConstants.badgeStyle,
                  color = Color(0x99333333),
              )
            }
      }
}
