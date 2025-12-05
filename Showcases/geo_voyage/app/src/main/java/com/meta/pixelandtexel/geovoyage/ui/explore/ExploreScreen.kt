// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.ScrollableTextAreaWithScrollBar
import com.meta.pixelandtexel.geovoyage.ui.components.ToggleChip
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.pixelandtexel.geovoyage.viewmodels.ExploreViewModel

@Composable
fun ExploreScreen(
    vm: ExploreViewModel = viewModel(),
    setTitle: ((text: String) -> Unit)? = null,
    onReportVRProblem: (url: String) -> Unit,
) {
  val title by vm.title
  val resultMessage by vm.result
  val landmarksEnabled by vm.landmarksEnabled
  val vrModeEnabled by vm.vrModeEnabled
  val panoData by vm.panoData

  val placeholderMessage = stringResource(R.string.explore_screen_placeholder_message)
  val shouldDisplayCopyright = vrModeEnabled && panoData?.attribution != null

  LaunchedEffect(null) { vm.updatePlaceholderMessage(placeholderMessage) }

  LaunchedEffect(title) { setTitle?.invoke(title) }

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize(),
  ) {
    SecondaryPanel(
        modifier = Modifier.fillMaxWidth().height(dimensionResource(R.dimen.tall_panel_height))
    ) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.SpaceAround,
          modifier = Modifier.fillMaxSize(),
      ) {
        ScrollableTextAreaWithScrollBar(text = resultMessage, modifier = Modifier.weight(1f))
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            ToggleChip(
                selected = vrModeEnabled,
                enabled = panoData != null && panoData!!.hasLocalPanorama,
                text = "VR Mode",
            ) {
              vm.onEnterVRClicked()
            }
          }
          if (shouldDisplayCopyright) {
            val attributionText = "${panoData?.attribution}"
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
              Text(
                  text = attributionText,
                  textAlign = TextAlign.Right,
                  style = MaterialTheme.typography.bodySmall,
              )
            }
          }
        }
      }
    }
  }
}

@Preview(widthDp = 570, heightDp = 480, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun ExploreScreenPreview() {
  GeoVoyageTheme { ExploreScreen {} }
}
