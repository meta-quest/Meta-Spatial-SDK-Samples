// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.layouts

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.levinriegner.uiset.app.util.view.StatefulWrapper
import com.meta.spatial.uiset.slider.SpatialSliderLarge
import com.meta.spatial.uiset.slider.SpatialSliderMedium
import com.meta.spatial.uiset.slider.SpatialSliderSmall
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.VolumeOn

@Composable
fun SlidersLayout() {
  return PanelScaffold("Slider") {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
      Text(
          modifier = Modifier.weight(1f),
          text = "Slider with No Label",
          textAlign = TextAlign.Center,
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(Modifier.width(32.dp))
      Text(
          modifier = Modifier.weight(1f),
          text = "Slider with Label",
          textAlign = TextAlign.Center,
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(Modifier.width(32.dp))
      Text(
          modifier = Modifier.weight(1f),
          text = "Slider with Plus/Minus",
          textAlign = TextAlign.Center,
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
      Spacer(Modifier.width(74.dp))
      Text(
          modifier = Modifier.weight(0.5f),
          text = "",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
    }
    Spacer(Modifier.size(35.dp))
    Row(
        Modifier.fillMaxWidth(),
    ) {
      StatefulWrapper(0.5f) { value, onChanged ->
        SpatialSliderLarge(
            modifier = Modifier.weight(1f),
            value = value,
            onChanged = onChanged,
            thumbIcon = {
              Icon(
                  imageVector = SpatialIcons.Regular.VolumeOn,
                  contentDescription = "Volume",
              )
            },
        )
      }
      Spacer(Modifier.size(32.dp))
      StatefulWrapper(0.5f) { value, onChanged ->
        SpatialSliderLarge(
            modifier = Modifier.weight(1f),
            value = value,
            onChanged = onChanged,
            thumbIcon = {
              Icon(
                  imageVector = SpatialIcons.Regular.VolumeOn,
                  contentDescription = "Volume",
              )
            },
            helperText = Pair("Label", "Label"),
        )
      }
      Spacer(Modifier.size(32.dp))
      StatefulWrapper(0.5f) { value, onChanged ->
        SpatialSliderLarge(
            modifier = Modifier.weight(1f),
            value = value,
            onChanged = onChanged,
            thumbIcon = {
              Icon(
                  imageVector = SpatialIcons.Regular.VolumeOn,
                  contentDescription = "Volume",
              )
            },
            helperIcons =
                Pair(
                    {
                      Icon(
                          imageVector = Icons.Default.Remove,
                          contentDescription = "Minus",
                          tint = SpatialTheme.colorScheme.secondaryAlphaBackground,
                      )
                    },
                    {
                      Icon(
                          imageVector = Icons.Default.Add,
                          contentDescription = "More",
                          tint = SpatialTheme.colorScheme.secondaryAlphaBackground,
                      )
                    },
                ),
        )
      }
      Spacer(Modifier.size(74.dp))
      Text(
          modifier = Modifier.weight(0.5f),
          text = "Large Slider",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
    }
    Spacer(Modifier.size(100.dp))
    Row(
        Modifier.fillMaxWidth(),
    ) {
      StatefulWrapper(0.5f) { value, onChanged ->
        SpatialSliderMedium(
            modifier = Modifier.weight(1f),
            value = value,
            onChanged = onChanged,
        )
      }
      Spacer(Modifier.size(32.dp))
      StatefulWrapper(0.5f) { value, onChanged ->
        SpatialSliderMedium(
            modifier = Modifier.weight(1f),
            value = value,
            onChanged = onChanged,
            helperText = Pair("Label", "Label"),
        )
      }
      Spacer(Modifier.size(32.dp))
      StatefulWrapper(0.5f) { value, onChanged ->
        SpatialSliderMedium(
            modifier = Modifier.weight(1f),
            value = value,
            onChanged = onChanged,
            thumbIcon = {
              Icon(
                  imageVector = SpatialIcons.Regular.VolumeOn,
                  contentDescription = "Volume",
              )
            },
            helperIcons =
                Pair(
                    {
                      Icon(
                          imageVector = Icons.Default.Remove,
                          contentDescription = "Minus",
                          tint = SpatialTheme.colorScheme.secondaryAlphaBackground,
                      )
                    },
                    {
                      Icon(
                          imageVector = Icons.Default.Add,
                          contentDescription = "More",
                          tint = SpatialTheme.colorScheme.secondaryAlphaBackground,
                      )
                    },
                ),
        )
      }
      Spacer(Modifier.size(74.dp))
      Text(
          modifier = Modifier.weight(0.5f),
          text = "Medium Slider",
          style =
              SpatialTheme.typography.body1.copy(
                  color = SpatialTheme.colorScheme.primaryAlphaBackground
              ),
      )
    }
    Spacer(Modifier.size(100.dp))
    Row(
        Modifier.fillMaxWidth(),
    ) {
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
            },
        )
      }
      Spacer(Modifier.size(32.dp))
      StatefulWrapper(0.5f) { value, onChanged ->
        SpatialSliderSmall(
            modifier = Modifier.weight(1f),
            value = value,
            onChanged = onChanged,
            thumbIcon = {
              Icon(
                  imageVector = Icons.AutoMirrored.Default.VolumeUp,
                  contentDescription = "Volume",
              )
            },
            helperText = Pair("Label", "Label"),
        )
      }
      Spacer(Modifier.size(32.dp))
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
            },
            helperIcons =
                Pair(
                    {
                      Icon(
                          imageVector = Icons.Default.Remove,
                          contentDescription = "Minus",
                          tint = SpatialTheme.colorScheme.secondaryAlphaBackground,
                      )
                    },
                    {
                      Icon(
                          imageVector = Icons.Default.Add,
                          contentDescription = "More",
                          tint = SpatialTheme.colorScheme.secondaryAlphaBackground,
                      )
                    },
                ),
        )
        Spacer(Modifier.size(74.dp))
        Text(
            modifier = Modifier.weight(0.5f),
            text = "Small Slider",
            style =
                SpatialTheme.typography.body1.copy(
                    color = SpatialTheme.colorScheme.primaryAlphaBackground
                ),
        )
      }
    }
  }
}

@Preview(
    widthDp = 1400,
    heightDp = 708,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun SlidersLayoutPreview() {
  SlidersLayout()
}
