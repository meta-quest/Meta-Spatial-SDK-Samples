// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.ArrowDown

@Composable
fun ScrollableColumn(content: @Composable () -> Unit) {
  val scrollState = rememberScrollState()

  var contentHeight by remember { mutableIntStateOf(0) }
  var viewportHeight by remember { mutableIntStateOf(0) }
  val showArrow by remember {
    derivedStateOf { scrollState.value == 0 && contentHeight > viewportHeight }
  }

  Box(
      modifier =
          Modifier.fillMaxSize().onGloballyPositioned { coordinates ->
            viewportHeight = coordinates.size.height
          }) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier =
                Modifier.fillMaxSize()
                    .padding(0.dp)
                    .verticalScroll(scrollState)
                    .onGloballyPositioned { coordinates ->
                      contentHeight = coordinates.size.height
                    },
        ) {
          content.invoke()
        }

        if (showArrow) {
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(60.dp)
                      .align(Alignment.BottomCenter)
                      .background(
                          brush =
                              Brush.verticalGradient(
                                  colors =
                                      listOf(
                                          Color.Transparent,
                                          SpatialColor.RLDSpanelBlackGradientBottom,
                                      ))))
          Icon(
              imageVector = SpatialIcons.Regular.ArrowDown,
              contentDescription = "Scroll down",
              tint = Color.White,
              modifier = Modifier.align(Alignment.BottomCenter),
          )
        }
      }
}
