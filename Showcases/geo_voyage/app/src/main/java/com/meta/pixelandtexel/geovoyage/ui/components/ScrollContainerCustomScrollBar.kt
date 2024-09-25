// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.pixelandtexel.geovoyage.utils.DisplayUtils.toPx
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * Scrollable text ui component.
 *
 * @param text Text which should populate the main scrollable content area.
 */
@Composable
fun ScrollableTextAreaWithScrollBar(text: String, modifier: Modifier = Modifier) {
  val scrollState = rememberScrollState()
  val coroutineScope = rememberCoroutineScope()
  val containerHeight = remember { mutableIntStateOf(0) }
  val contentHeight = remember { mutableIntStateOf(0) }

  Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(end = 16.dp) // Add some padding to the end to make space for the scrollbar
                .onGloballyPositioned { layoutCoordinates ->
                  contentHeight.intValue = layoutCoordinates.size.height
                }) {
          MarkdownText(
              markdown = text,
              modifier = Modifier.fillMaxWidth(),
              style = MaterialTheme.typography.headlineSmall)
        }

    // Custom Scroll Bar
    if (contentHeight.intValue > containerHeight.intValue) {
      Box(
          modifier =
              Modifier.clip(RoundedCornerShape(20.0.dp))
                  .align(Alignment.CenterEnd)
                  .fillMaxHeight()
                  .width(8.dp)
                  .background(MaterialTheme.colorScheme.secondaryContainer)
                  .onGloballyPositioned { layoutCoordinates ->
                    containerHeight.intValue = layoutCoordinates.size.height
                  }
                  .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                      change.consume()
                      coroutineScope.launch { scrollState.scrollBy(dragAmount) }
                    }
                  }) {
            val minThumbHeight = 48.dp
            val visibleRatio = containerHeight.intValue.toFloat() / contentHeight.intValue.toFloat()
            val thumbHeight =
                (containerHeight.intValue * visibleRatio).coerceAtLeast(minThumbHeight.toPx())

            val maxScroll = scrollState.maxValue.toFloat()
            val scrollFraction = (scrollState.value / maxScroll).coerceIn(0f, 1f)
            val thumbOffset = scrollFraction * (containerHeight.intValue - thumbHeight)

            Box(
                modifier =
                    Modifier.offset {
                          val safeThumbOffset = if (thumbOffset.isNaN()) 0f else thumbOffset
                          IntOffset(x = 0, y = safeThumbOffset.roundToInt())
                        }
                        .clip(RoundedCornerShape(20.0.dp))
                        .width(8.dp)
                        .height(with(LocalDensity.current) { thumbHeight.toDp() })
                        .background(MaterialTheme.colorScheme.secondary))
          }
    }
  }
}

@Preview(
    name = "Scroll Container Custom Scroll Bar",
    showBackground = true,
    backgroundColor = 0xFFB2F0B8,
    widthDp = 868,
    heightDp = 416,
)
@Composable
private fun ScrollableTextAreaPreview(
    @PreviewParameter(ScrollableTextAreaProvider::class) text: String
) {
  GeoVoyageTheme { ScrollableTextAreaWithScrollBar(text) }
}

class ScrollableTextAreaProvider : PreviewParameterProvider<String> {
  override val values =
      sequenceOf(
          "",
          "This is a short answers",
          "This is a medium answer".repeat(50),
          "This is a long answer".repeat(500),
          """  
            # Sample  
            * Markdown  
            * [Link](https://example.com)  
            ![Image](https://example.com/img.png "Image")  
            <a href="https://www.google.com/">Google</a>  
        """
              .trimIndent())
}
