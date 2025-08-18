// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.todayinhistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.ScrollableTextAreaWithScrollBar
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.pixelandtexel.geovoyage.viewmodels.TodayInHistoryViewModel
import com.meta.spatial.uiset.button.PrimaryButton

@Composable
fun TodayInHistoryScreen(
    vm: TodayInHistoryViewModel = viewModel(),
    setTitle: ((text: String) -> Unit)? = null,
) {
  val title by vm.title
  val result by vm.result
  val busy by vm.busy

  val baseQueryString = stringResource(R.string.today_in_history_base_query)

  LaunchedEffect(null) { vm.updateBaseQueryString(baseQueryString) }

  LaunchedEffect(title) { setTitle?.invoke(title) }

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize(),
  ) {
    SecondaryPanel(
        modifier = Modifier.fillMaxWidth().height(dimensionResource(R.dimen.tall_panel_height))) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.SpaceAround,
              modifier = Modifier.fillMaxSize(),
          ) {
            ScrollableTextAreaWithScrollBar(text = result, modifier = Modifier.weight(1f))
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                label = stringResource(id = R.string.today_show_another),
                isEnabled = !busy,
                onClick = { vm.startTodayInHistoryQuery() },
            )
          }
        }
  }
}

@Preview(widthDp = 570, heightDp = 480, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun TodayInHistoryScreenPreview() {
  GeoVoyageTheme { TodayInHistoryScreen() }
}
