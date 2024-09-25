// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.mainnavigator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.TitleBar
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.CircleButton
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.NavButton
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.NavButtonState
import com.meta.pixelandtexel.geovoyage.ui.components.panel.PrimaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

@Composable
fun PanelNavContainer(
    titleText: String,
    currentRoute: String,
    navButtonStates: List<NavButtonState>,
    navigateTo: (route: String) -> Unit,
    content: @Composable () -> Unit = {}
) {
  Column {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(194.dp)) {
          Spacer(Modifier.width(230.dp))

          TitleBar(
              label = titleText,
              modifier = Modifier.weight(1f),
          )
          CircleButton(
              iconResId = R.drawable.ic_settings,
              contentDescription = "Settings",
              modifier = Modifier.padding(54.dp, 0.dp)) {
                navigateTo(Routes.SETTINGS_ROUTE)
              }
        }
    Row(modifier = Modifier.padding().fillMaxSize()) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Top,
          modifier = Modifier.width(230.dp).fillMaxHeight().padding(0.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(38.dp),
                modifier = Modifier.fillMaxWidth()) {
                  navButtonStates.forEach { state ->
                    NavButton(state, state.route == currentRoute) { navigateTo(state.route) }
                  }
                }
          }
      Column(modifier = Modifier.fillMaxSize().padding(end = 56.dp, bottom = 94.dp)) {
        content.invoke()
      }
    }
  }
}

@Preview(widthDp = 1210, heightDp = 940)
@Composable
private fun PanelNavContainerPreview() {
  val navButtonStates =
      listOf(
          NavButtonState(
              text = "Explore",
              route = Routes.EXPLORE_ROUTE,
              iconResId = R.drawable.ic_explore,
          ),
          NavButtonState(
              text = "Ask",
              route = Routes.ASK_EARTH_ROUTE,
              iconResId = R.drawable.ic_mic,
          ),
          NavButtonState(
              text = "Today",
              route = Routes.TODAY_IN_HISTORY_ROUTE,
              iconResId = R.drawable.ic_calendar,
          ),
          NavButtonState(
              text = "Quiz",
              route = Routes.DAILY_QUIZ_ROUTE,
              iconResId = R.drawable.ic_question_block,
          ))

  GeoVoyageTheme { PrimaryPanel { PanelNavContainer("Title", "Today", navButtonStates, {}) {} } }
}
