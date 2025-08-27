// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.dailyquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CheckAlt

@Composable
fun ResultsScreen(numCorrectAnswers: Int, numTotalAnswers: Int) {
  val resultsTitleMap =
      mapOf(
          0 to stringResource(R.string.quiz_result_0),
          1 to stringResource(R.string.quiz_result_1),
          2 to stringResource(R.string.quiz_result_2),
          3 to stringResource(R.string.quiz_result_3),
          4 to stringResource(R.string.quiz_result_4),
          5 to stringResource(R.string.quiz_result_5),
      )

  val tallyText =
      String.format(
          stringResource(id = R.string.quiz_result_tally),
          numCorrectAnswers,
          numTotalAnswers,
      )
  val reminderText =
      String.format(
          stringResource(id = R.string.quiz_result_reminder),
          resultsTitleMap[numCorrectAnswers],
      )

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize(),
  ) {
    SecondaryPanel(
        modifier = Modifier.fillMaxWidth().height(dimensionResource(R.dimen.short_panel_height))
    ) {
      Column(
          verticalArrangement = Arrangement.SpaceEvenly,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize(),
      ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
          Icon(
              imageVector = SpatialIcons.Regular.CheckAlt,
              contentDescription = "Answer icon",
              tint = colorResource(R.color.quiz_correct_green),
              modifier = Modifier.size(40.dp),
          )
          Text(text = tallyText, style = LocalTypography.current.headline1Strong)
        }
        Text(
            text = reminderText,
            style = LocalTypography.current.headline3Strong,
            textAlign = TextAlign.Center,
        )
      }
    }
  }
}

@Preview(widthDp = 570, heightDp = 480, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun ResultsScreenPreview() {
  GeoVoyageTheme { ResultsScreen(2, 5) }
}
