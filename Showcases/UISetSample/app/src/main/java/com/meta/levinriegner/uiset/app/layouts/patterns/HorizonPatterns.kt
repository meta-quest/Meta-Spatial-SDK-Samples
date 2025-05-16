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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.util.view.PatternScaffold
import com.meta.spatial.uiset.button.BorderlessCircleButton
import com.meta.spatial.uiset.button.BorderlessIconButton
import com.meta.spatial.uiset.button.SecondaryButton
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.spatial.uiset.button.TextTileButton
import com.meta.spatial.uiset.dropdown.SpatialDropdown
import com.meta.spatial.uiset.dropdown.foundation.SpatialDropdownItem
import com.meta.spatial.uiset.input.SpatialSearchBar
import com.meta.spatial.uiset.navigation.SpatialSideNavItem
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.ArrowLeft
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll
import com.meta.spatial.uiset.theme.icons.regular.MoreHorizontal

@Composable
fun HorizonPatternOne() {
  var selectedNavItem by remember { mutableIntStateOf(0) }

  PatternScaffold() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(48.dp),
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth(fraction = .25f),
          userScrollEnabled = false,
          verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(7) { index ->
          SpatialSideNavItem(
              icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
              primaryLabel = "Title",
              selected = selectedNavItem == index,
              onClick = { selectedNavItem = index },
          )
        }
      }

      Column(
          horizontalAlignment = Alignment.Start,
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
              "Title",
              style = SpatialTheme.typography.headline3Strong,
              color = SpatialTheme.colorScheme.primaryAlphaBackground,
          )

          Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            val firstDropdownItems =
                (1..3).map { index -> SpatialDropdownItem(title = "Title $index") }
            var firstDropdownSelectedItem by remember { mutableStateOf<SpatialDropdownItem?>(null) }

            SpatialDropdown(
                title = "Select",
                items = firstDropdownItems,
                selectedItem = firstDropdownSelectedItem,
                onItemSelected = { firstDropdownSelectedItem = it },
            )

            val secondDropdownItems =
                (1..3).map { index -> SpatialDropdownItem(title = "Title $index") }
            var secondDropdownSelectedItem by remember {
              mutableStateOf<SpatialDropdownItem?>(null)
            }

            SpatialDropdown(
                title = "Select",
                items = secondDropdownItems,
                selectedItem = secondDropdownSelectedItem,
                onItemSelected = { secondDropdownSelectedItem = it },
            )
          }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 221.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          items(
              count = 40,
              itemContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                  Box(
                      modifier =
                          Modifier.background(
                                  color = SpatialTheme.colorScheme.secondaryButton,
                                  shape = SpatialTheme.shapes.medium,
                              )
                              .size(width = 221.dp, height = 120.dp),
                  )

                  Text(
                      "Title",
                      color = SpatialTheme.colorScheme.primaryAlphaBackground,
                      style = SpatialTheme.typography.body2,
                      textAlign = TextAlign.Center,
                  )
                }
              },
          )
        }
      }
    }
  }
}

@Composable
fun HorizonPatternTwo() {
  PatternScaffold(
      leading = {
        BorderlessCircleButton(
            icon = { Icon(SpatialIcons.Regular.MoreHorizontal, "") },
            onClick = {},
        )
      },
  ) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        SpatialSearchBar(
            modifier = Modifier.fillMaxWidth(.95f),
            onQueryChange = {},
            onQuerySubmit = {},
        )

        SecondaryCircleButton(
            icon = { Icon(SpatialIcons.Regular.MoreHorizontal, "") },
            onClick = {},
        )
      }

      LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        items(count = 20) {
          SecondaryButton(
              label = "Label",
              onClick = {},
          )
        }
      }

      LazyRow(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        items(4) {
          TextTileButton(
              modifier =
                  Modifier.size(
                      width = 800.dp,
                      height = 163.dp,
                  ),
              label = "Label",
              onSelectionChange = {},
          )
        }
      }

      Column(
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
            "First Title",
            style = SpatialTheme.typography.headline3Strong,
            color = SpatialTheme.colorScheme.primaryAlphaBackground,
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          items(10) {
            TextTileButton(
                modifier =
                    Modifier.size(
                        width = 235.dp,
                        height = 124.dp,
                    ),
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                label = "Label",
                secondaryLabel = "Secondary",
                onSelectionChange = {},
            )
          }
        }
      }

      Column(
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
            "Second Title",
            style = SpatialTheme.typography.headline3Strong,
            color = SpatialTheme.colorScheme.primaryAlphaBackground,
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          items(10) {
            TextTileButton(
                modifier =
                    Modifier.size(
                        width = 235.dp,
                        height = 124.dp,
                    ),
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                label = "Label",
                secondaryLabel = "Secondary",
                onSelectionChange = {},
            )
          }
        }
      }
    }
  }
}

@Composable
fun HorizonPatternThree() {
  var selectedNavItem by remember { mutableIntStateOf(0) }

  PatternScaffold() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(48.dp),
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth(fraction = .25f),
          userScrollEnabled = false,
          verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(7) { index ->
          SpatialSideNavItem(
              icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
              primaryLabel = "Title",
              selected = selectedNavItem == index,
              onClick = { selectedNavItem = index },
          )
        }
      }

      Column(
          horizontalAlignment = Alignment.Start,
          modifier = Modifier.fillMaxSize().verticalScroll(state = rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
          ) {
            BorderlessIconButton(
                icon = { Icon(SpatialIcons.Regular.ArrowLeft, "") },
                onClick = {},
            )

            Text(
                "Title",
                style = SpatialTheme.typography.headline3Strong,
                color = SpatialTheme.colorScheme.primaryAlphaBackground,
            )
          }

          Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            BorderlessCircleButton(
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                onClick = {},
            )

            BorderlessCircleButton(
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                onClick = {},
            )

            BorderlessCircleButton(
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
                onClick = {},
            )
          }
        }

        TextTileButton(
            modifier = Modifier.fillMaxWidth().height(213.dp),
            onSelectionChange = {},
            label = "Label",
            secondaryLabel = "Secondary",
            icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Text(
              "Second Title",
              style = SpatialTheme.typography.headline3Strong,
              color = SpatialTheme.colorScheme.primaryAlphaBackground,
          )

          Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            TextTileButton(
                modifier = Modifier.width(173.dp).height(100.dp),
                onSelectionChange = {},
                label = "Label",
                secondaryLabel = "Secondary",
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
            )

            TextTileButton(
                modifier = Modifier.width(173.dp).height(100.dp),
                onSelectionChange = {},
                label = "Label",
                secondaryLabel = "Secondary",
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
            )

            TextTileButton(
                modifier = Modifier.width(173.dp).height(100.dp),
                onSelectionChange = {},
                label = "Label",
                secondaryLabel = "Secondary",
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
            )

            TextTileButton(
                modifier = Modifier.height(100.dp),
                onSelectionChange = {},
                label = "Label",
                secondaryLabel = "Secondary",
                icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
            )
          }
        }

        TextTileButton(
            modifier = Modifier.fillMaxWidth().height(213.dp),
            onSelectionChange = {},
            label = "Label",
            secondaryLabel = "Secondary",
            icon = { Icon(SpatialIcons.Regular.CategoryAll, "") },
        )
      }
    }
  }
}

@Preview(
    widthDp = 1024,
    heightDp = 668,
)
@Composable
fun HorizonPatternOneReview() {
  HorizonPatternOne()
}

@Preview(
    widthDp = 1024,
    heightDp = 668,
)
@Composable
fun HorizonPatternTwoReview() {
  HorizonPatternTwo()
}

@Preview(
    widthDp = 1024,
    heightDp = 668,
)
@Composable
fun HorizonPatternThreeReview() {
  HorizonPatternThree()
}
