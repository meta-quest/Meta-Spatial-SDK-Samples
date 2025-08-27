// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.dailyquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.QuizButton
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CheckAlt
import com.meta.spatial.uiset.theme.icons.regular.Close

@Composable
fun QuestionScreen(
    question: String,
    answers: List<String>,
    correctAnswerIdx: Int,
    onUserAnswered: (idx: Int) -> Unit,
) {
  val correctAnswerText = stringResource(id = R.string.quiz_answer_correct)
  val incorrectAnswerText = stringResource(id = R.string.quiz_answer_incorrect)

  val titleText = remember { mutableStateOf(question) }
  val answeredCorrectly = remember { mutableStateOf<Boolean?>(null) }

  LaunchedEffect(question) {
    // our question has changed; reset our values

    titleText.value = question
    answeredCorrectly.value = null
  }

  // question text and icon styling

  val questionColor =
      if (answeredCorrectly.value != null && !(answeredCorrectly.value!!))
          colorResource(R.color.quiz_incorrect_red)
      else Color.Black
  val iconColor =
      if (answeredCorrectly.value != null && answeredCorrectly.value!!)
          colorResource(R.color.quiz_correct_green)
      else colorResource(R.color.quiz_incorrect_red)
  val iconImageVector =
      if (answeredCorrectly.value != null && answeredCorrectly.value!!)
          SpatialIcons.Regular.CheckAlt
      else SpatialIcons.Regular.Close

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize(),
  ) {
    SecondaryPanel(
        modifier = Modifier.fillMaxWidth().height(dimensionResource(R.dimen.medium_panel_height))
    ) {
      Column(
          verticalArrangement = Arrangement.SpaceEvenly,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize(),
      ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
        ) {
          if (answeredCorrectly.value != null) {
            Icon(
                imageVector = iconImageVector,
                contentDescription = "Answer icon",
                tint = iconColor,
                modifier = Modifier.size(40.dp),
            )
          }
          Text(
              text = titleText.value,
              color = questionColor,
              style = LocalTypography.current.headline3Strong,
          )
        }

        for (i in 0..2) {
          QuizButton(
              label = answers[i],
              isEnabled = answeredCorrectly.value == null,
              hasSelectedAnswer = answeredCorrectly.value != null,
              didAnswerCorrectly = answeredCorrectly.value == true,
              isCorrectAnswer = i == correctAnswerIdx,
              onClick = {
                onUserAnswered(i)

                val isCorrect = i == correctAnswerIdx
                if (isCorrect) {
                  titleText.value = correctAnswerText
                } else {
                  titleText.value = incorrectAnswerText
                }

                answeredCorrectly.value = isCorrect
              },
          )
        }
      }
    }
  }
}

@Preview(widthDp = 570, heightDp = 480, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun QuestionScreenPreview() {
  GeoVoyageTheme {
    QuestionScreen(
        "What is the longest river in the world?",
        listOf("The Nile", "The Amazon River", "The Mystic River"),
        1,
    ) {}
  }
}
