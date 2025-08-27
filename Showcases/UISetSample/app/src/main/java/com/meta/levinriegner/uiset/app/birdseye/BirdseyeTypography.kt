// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.birdseye

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme

@Composable
fun BirdseyeTypography() {

  val lorem1 =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
  val lorem2 =
      "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

  PanelScaffold("Typography") {
    Column() {
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Headline 1 — Strong",
              style =
                  LocalTypography.current.headline1Strong.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Bold",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "32/36 | -0.2%",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Headline 1",
              style =
                  LocalTypography.current.headline1.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Light",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "32/36 | -0.2%",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Headline 2 — Strong",
              style =
                  LocalTypography.current.headline2Strong.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Bold",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "24/28 | -0.1%",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Headline 2",
              style =
                  LocalTypography.current.headline2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Light",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "24/28 | -0.1%",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Headline 3 — Strong",
              style =
                  LocalTypography.current.headline3Strong.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Bold",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "20/24",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Headline 3",
              style =
                  LocalTypography.current.headline3.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Light",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "20/24",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Body 1 — Strong",
              style =
                  LocalTypography.current.body1Strong.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Bold",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "14/20",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Body 1",
              style =
                  LocalTypography.current.body1.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Light",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "14/20",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)

      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Body 2 — Strong",
              style =
                  LocalTypography.current.body2Strong.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Bold",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "11/16",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.fillMaxWidth().height(65.dp),
      ) {
        Box(modifier = Modifier.width(320.dp)) {
          Text(
              "Body 2",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
        Text(
            "Light",
            style =
                LocalTypography.current.body2.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.width(300.dp)) {
          Text(
              "11/16",
              style =
                  LocalTypography.current.body2.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground
                  ),
          )
        }
      }
      HorizontalDivider(color = SpatialTheme.colorScheme.primaryAlphaBackground)
      Spacer(Modifier.height(30.dp))

      // Examples
      Row {
        Text(
            "Examples",
            style =
                LocalTypography.current.headline1Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Spacer(Modifier.height(30.dp))
      Row {
        Text(
            "Paragraph Spacing",
            style =
                LocalTypography.current.headline2Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Spacer(Modifier.height(30.dp))
      Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.width(500.dp)) {
          Column {
            Text(
                "10dp Paragraph Spacing",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                lorem1,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                lorem2,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
          }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Box {
          Column {
            Text(
                "8dp Paragraph Spacing",
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                lorem1,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                lorem2,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
          }
        }
      }

      Spacer(Modifier.height(30.dp))
      Row {
        Text(
            "Headline Spacing",
            style =
                LocalTypography.current.headline2Strong.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground
                ),
        )
      }
      Spacer(Modifier.height(30.dp))
      Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.width(500.dp)) {
          Column {
            Text(
                "Headline 1 — Strong",
                style =
                    LocalTypography.current.headline1Strong.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                lorem1,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                lorem2,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
          }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Box {
          Column {
            Text(
                "Headline 1 — Strong",
                style =
                    LocalTypography.current.headline1Strong.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                lorem1,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                lorem2,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
          }
        }
      }

      Spacer(Modifier.height(30.dp))
      Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.width(500.dp)) {
          Column {
            Text(
                "Headline 2 — Strong",
                style =
                    LocalTypography.current.headline2Strong.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                lorem1,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                lorem2,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
          }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Box {
          Column {
            Text(
                "Headline 2 — Strong",
                style =
                    LocalTypography.current.headline2Strong.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                lorem1,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                lorem2,
                style =
                    LocalTypography.current.body1.copy(
                        color = LocalColorScheme.current.primaryAlphaBackground
                    ),
            )
          }
        }
      }
    }
  }
}

@Preview(
    widthDp = 1080,
    heightDp = 1949,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdsEyeTypographyPreview() {
  BirdseyeTypography()
}
