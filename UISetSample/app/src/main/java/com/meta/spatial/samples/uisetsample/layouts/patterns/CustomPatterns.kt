/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.layouts.patterns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.spatial.samples.uisetsample.util.view.PanelScaffold
import com.meta.spatial.samples.uisetsample.util.view.StatefulWrapper
import com.meta.spatial.uiset.button.ButtonShelf
import com.meta.spatial.uiset.button.TextTileButton
import com.meta.spatial.uiset.control.SpatialCheckbox
import com.meta.spatial.uiset.control.SpatialSwitch
import com.meta.spatial.uiset.navigation.SpatialSideNavItem
import com.meta.spatial.uiset.slider.SpatialSliderSmall
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll

@Composable
fun CustomPattern3x3() {
  PanelScaffold(
      padding =
          PaddingValues(
              24.dp,
          ),
  ) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items(
          count = 9,
      ) {
        StatefulWrapper(initialValue = false) { value, onChanged ->
          TextTileButton(
              icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
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

@Composable
fun CustomPatternTwo() {
  PanelScaffold(
      padding =
          PaddingValues(
              top = 24.dp,
              start = 24.dp,
              end = 24.dp,
          ),
  ) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Column(
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
            "Label",
            style = SpatialTheme.typography.body1Strong,
            color = SpatialTheme.colorScheme.primaryAlphaBackground,
        )

        Column(
            modifier =
                Modifier.background(
                        color = SpatialColor.black15,
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Row(
              modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                  text = "Label",
                  style =
                      LocalTypography.current.body1.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
              Text(
                  text = "Secondary text",
                  style =
                      LocalTypography.current.body2.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
            }
            StatefulWrapper(false) { value, onChanged ->
              SpatialSwitch(
                  checked = value,
                  onCheckedChange = onChanged,
              )
            }
          }

          Row(
              modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                  text = "Label",
                  style =
                      LocalTypography.current.body1.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
              Text(
                  text = "Secondary text",
                  style =
                      LocalTypography.current.body2.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
            }
            StatefulWrapper(false) { value, onChanged ->
              SpatialSwitch(
                  checked = value,
                  onCheckedChange = onChanged,
              )
            }
          }

          Row(
              modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                  text = "Label",
                  style =
                      LocalTypography.current.body1.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
              Text(
                  text = "Secondary text",
                  style =
                      LocalTypography.current.body2.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
            }
            StatefulWrapper(false) { value, onChanged ->
              SpatialSwitch(
                  checked = value,
                  onCheckedChange = onChanged,
              )
            }
          }
        }
      }

      Column(
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Column {
          Text(
              "Label",
              style = SpatialTheme.typography.body1,
              color = SpatialTheme.colorScheme.primaryAlphaBackground,
          )
          Text(
              "Description",
              style = SpatialTheme.typography.body2,
              color = SpatialTheme.colorScheme.primaryAlphaBackground,
          )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          StatefulWrapper(0f) { value, onChanged ->
            SpatialSliderSmall(
                helperText =
                    Pair(
                        "Low",
                        "High",
                    ),
                value = value,
                onChanged = onChanged,
            )
          }

          StatefulWrapper(0f) { value, onChanged ->
            SpatialSliderSmall(
                helperText =
                    Pair(
                        "Low",
                        "High",
                    ),
                value = value,
                onChanged = onChanged,
            )
          }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          StatefulWrapper(initialValue = false) { value, onChanged ->
            ButtonShelf(
                modifier =
                    Modifier.size(
                        width = 147.dp,
                        height = 72.dp,
                    ),
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                label = "Label",
                selected = value,
                onSelectionChange = { onChanged(!value) },
            )
          }
          StatefulWrapper(initialValue = false) { value, onChanged ->
            ButtonShelf(
                modifier =
                    Modifier.size(
                        width = 147.dp,
                        height = 72.dp,
                    ),
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                label = "Label",
                selected = value,
                onSelectionChange = { onChanged(!value) },
            )
          }
          StatefulWrapper(initialValue = false) { value, onChanged ->
            ButtonShelf(
                modifier =
                    Modifier.size(
                        width = 147.dp,
                        height = 72.dp,
                    ),
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                label = "Label",
                selected = value,
                onSelectionChange = { onChanged(!value) },
            )
          }
          StatefulWrapper(initialValue = false) { value, onChanged ->
            ButtonShelf(
                modifier =
                    Modifier.size(
                        width = 147.dp,
                        height = 72.dp,
                    ),
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                label = "Label",
                selected = value,
                onSelectionChange = { onChanged(!value) },
            )
          }
        }
      }

      Column(
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
            "Label",
            style = SpatialTheme.typography.body1Strong,
            color = SpatialTheme.colorScheme.primaryAlphaBackground,
        )

        Column(
            modifier =
                Modifier.background(
                        color = SpatialColor.black15,
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Row(
              modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                  text = "Label",
                  style =
                      LocalTypography.current.body1.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
              Text(
                  text = "Secondary text",
                  style =
                      LocalTypography.current.body2.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
            }
            StatefulWrapper(false) { value, onChanged ->
              SpatialCheckbox(
                  checked = value,
                  onCheckedChange = onChanged,
              )
            }
          }

          Row(
              modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                  text = "Label",
                  style =
                      LocalTypography.current.body1.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
              Text(
                  text = "Secondary text",
                  style =
                      LocalTypography.current.body2.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
            }
            StatefulWrapper(false) { value, onChanged ->
              SpatialCheckbox(
                  checked = value,
                  onCheckedChange = onChanged,
              )
            }
          }

          Row(
              modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                  text = "Label",
                  style =
                      LocalTypography.current.body1.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
              Text(
                  text = "Secondary text",
                  style =
                      LocalTypography.current.body2.copy(
                          color = LocalColorScheme.current.primaryAlphaBackground,
                      ),
              )
            }
            StatefulWrapper(false) { value, onChanged ->
              SpatialCheckbox(
                  checked = value,
                  onCheckedChange = onChanged,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CustomPatternThree() {
  var selectedNavItem by remember { mutableIntStateOf(0) }

  PanelScaffold(
      padding =
          PaddingValues(
              top = 24.dp,
              start = 24.dp,
              end = 24.dp,
          ),
  ) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth(fraction = .1f),
          userScrollEnabled = false,
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(7) { index ->
          SpatialSideNavItem(
              icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
              primaryLabel = "Title",
              collapsed = true,
              selected = selectedNavItem == index,
              onClick = { selectedNavItem = index },
          )
        }
      }

      LazyVerticalGrid(
          modifier = Modifier.fillMaxSize(),
          columns = GridCells.Fixed(2),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(
            count = 8,
        ) {
          StatefulWrapper(initialValue = false) { value, onChanged ->
            TextTileButton(
                modifier =
                    Modifier.height(
                        190.dp,
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

@Composable
fun CustomPattern2x4() {
  PanelScaffold(
      padding =
          PaddingValues(
              24.dp,
          ),
  ) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items(
          count = 8,
      ) {
        StatefulWrapper(initialValue = false) { value, onChanged ->
          TextTileButton(
              icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
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

@Preview(
    widthDp = 592,
    heightDp = 364,
)
@Composable
fun CustomPattern3x3Preview() {
  CustomPattern3x3()
}

@Preview(
    widthDp = 660,
    heightDp = 631,
)
@Composable
fun CustomPatternTwoPreview() {
  CustomPatternTwo()
}

@Preview(
    widthDp = 720,
    heightDp = 541,
)
@Composable
fun CustomPatternThreePreview() {
  CustomPatternThree()
}

@Preview(
    widthDp = 408,
    heightDp = 472,
)
@Composable
fun CustomPattern2x4Preview() {
  CustomPattern2x4()
}
