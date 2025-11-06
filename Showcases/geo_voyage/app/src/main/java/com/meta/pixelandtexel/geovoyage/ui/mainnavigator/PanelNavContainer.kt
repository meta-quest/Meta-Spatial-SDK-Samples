// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.mainnavigator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.TitleBar
import com.meta.pixelandtexel.geovoyage.ui.components.panel.PrimaryPanel
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.MicrophoneOn
import com.meta.spatial.uiset.theme.icons.regular.StarFull
import com.meta.spatial.uiset.theme.icons.regular.Trophy
import com.meta.spatial.uiset.theme.icons.regular.World

class NavButtonState(val text: String, val route: String, val iconImage: ImageVector)

@Composable
fun PanelNavContainer(
    titleText: String,
    currentRoute: String,
    navButtonStates: List<NavButtonState>,
    navigateTo: (route: String) -> Unit,
    content: @Composable () -> Unit = {},
) {
  Column {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) {
      TitleBar(
          label = titleText,
          modifier = Modifier.weight(1f),
      )
    }
    Row(modifier = Modifier.fillMaxSize()) {
      Column(
          modifier =
              Modifier.fillMaxSize()
                  .padding(
                      top = dimensionResource(R.dimen.standard_margin),
                  )
      ) {
        content.invoke()
      }
    }
  }
}

@Preview(widthDp = 642, heightDp = 600)
@Composable
private fun PanelNavContainerPreview() {
  val navButtonStates =
      listOf(
          NavButtonState(
              text = "Explore",
              route = Routes.EXPLORE_ROUTE,
              iconImage = SpatialIcons.Regular.World,
          ),
          NavButtonState(
              text = "Ask",
              route = Routes.ASK_EARTH_ROUTE,
              iconImage = SpatialIcons.Regular.MicrophoneOn,
          ),
          NavButtonState(
              text = "Today",
              route = Routes.TODAY_IN_HISTORY_ROUTE,
              iconImage = SpatialIcons.Regular.StarFull,
          ),
          NavButtonState(
              text = "Quiz",
              route = Routes.DAILY_QUIZ_ROUTE,
              iconImage = SpatialIcons.Regular.Trophy,
          ),
      )

  GeoVoyageTheme {
    PrimaryPanel {
      PanelNavContainer("Title", "Today", navButtonStates, {}) {
        SecondaryPanel(modifier = Modifier.fillMaxSize()) {}
      }
    }
  }
}
