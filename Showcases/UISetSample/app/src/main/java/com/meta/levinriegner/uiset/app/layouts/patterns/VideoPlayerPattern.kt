// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.layouts.patterns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.navigation.theme_selector.AppTheme
import com.meta.levinriegner.uiset.app.util.view.PatternScaffold
import com.meta.levinriegner.uiset.app.util.view.StatefulWrapper
import com.meta.spatial.uiset.button.BorderlessCircleButton
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.spatial.uiset.button.TextTileButton
import com.meta.spatial.uiset.slider.SpatialSliderSmall
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.MoreHorizontal
import com.meta.spatial.uiset.theme.icons.regular.Pause
import com.meta.spatial.uiset.theme.icons.regular.Play
import com.meta.spatial.uiset.theme.icons.regular.TenSecondsBackward
import com.meta.spatial.uiset.theme.icons.regular.TenSecondsForward
import com.meta.spatial.uiset.theme.icons.regular.VolumeOn

@Composable
fun VideoPlayerPattern() {
  PatternScaffold(
      isVideo = true,
      leading = {
        BorderlessCircleButton(
            icon = { Icon(SpatialIcons.Regular.MoreHorizontal, "") },
            onClick = {},
        )
      },
      overrideColorScheme = AppTheme.DARK.colorScheme) {
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .background(
                        color = SpatialColor.b50,
                    ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
        ) {
          var isVolumeVisible by remember { mutableStateOf(false) }
          Row(
              modifier =
                  Modifier.size(100.dp)
                      .padding(14.dp)
                      .rotate(-90f)
                      .alpha(if (isVolumeVisible) 1f else 0f),
              verticalAlignment = Alignment.Top) {
                StatefulWrapper(0.5f) { value, onChanged ->
                  SpatialSliderSmall(
                      modifier = Modifier.weight(1f),
                      value = value,
                      onChanged = onChanged,
                      thumbIcon = {
                        Icon(
                            imageVector = SpatialIcons.Regular.VolumeOn,
                            contentDescription = "Volume",
                        )
                      })
                }
              }
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            BorderlessCircleButton(
                icon = { Icon(SpatialIcons.Regular.VolumeOn, "") },
                onClick = { isVolumeVisible = !isVolumeVisible },
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              BorderlessCircleButton(
                  icon = { Icon(SpatialIcons.Regular.TenSecondsBackward, "") },
                  onClick = {},
              )
              StatefulWrapper(initialValue = false) { value, onValueChange ->
                SecondaryCircleButton(
                    icon = {
                      Icon(
                          if (value) SpatialIcons.Regular.Play else SpatialIcons.Regular.Pause,
                          "",
                      )
                    },
                    onClick = { onValueChange(!value) },
                )
              }
              BorderlessCircleButton(
                  icon = { Icon(SpatialIcons.Regular.TenSecondsForward, "") },
                  onClick = {},
              )
            }

            BorderlessCircleButton(
                icon = { Icon(SpatialIcons.Regular.MoreHorizontal, "") },
                onClick = {},
            )
          }

          Box(
              modifier = Modifier.height(32.dp),
          )

          Column {
            StatefulWrapper(0.10f) { value, onChanged ->
              SpatialSliderSmall(
                  helperText =
                      Pair(
                          "0:10",
                          "1:00",
                      ),
                  value = value,
                  onChanged = onChanged,
              )
            }
          }

          Box(
              modifier = Modifier.height(24.dp),
          )

          LazyRow(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            items(10) {
              StatefulWrapper(initialValue = false) { value, onChanged ->
                TextTileButton(
                    modifier =
                        Modifier.size(
                            width = 235.dp,
                            height = 124.dp,
                        ),
                    label = "Label",
                    secondaryLabel = "Secondary",
                    selected = value,
                    onSelectionChange = { onChanged(!value) },
                )
              }
            }
          }
        }
      }
}

@Preview(
    widthDp = 1256,
    heightDp = 833,
)
@Composable
fun VideoPlayerPatternPreview() {
  VideoPlayerPattern()
}
