// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.dailyquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.PrimaryButton
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

@Composable
fun QuestionScreen(
    question: String,
    answers: List<String>,
    correctAnswerIdx: Int,
    questionNum: Int,
    onUserAnswered: (idx: Int) -> Unit
) {
  val defaultCardColor = MaterialTheme.colorScheme.surfaceVariant
  val correctCardColor = Color(0xFFADEFB1)
  val incorrectCardColor = MaterialTheme.colorScheme.errorContainer
  val correctAnswerText = stringResource(id = R.string.quiz_answer_correct)
  val incorrectAnswerText = stringResource(id = R.string.quiz_answer_incorrect)

  val titleText = remember { mutableStateOf(question) }
  val backgroundColor = remember { mutableStateOf(defaultCardColor) }
  val answeredCorrectly = remember { mutableStateOf<Boolean?>(null) }

  LaunchedEffect(question) {
    // our question has changed; reset our values

    titleText.value = question
    backgroundColor.value = defaultCardColor
    answeredCorrectly.value = null
  }

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        SecondaryPanel(modifier = Modifier.fillMaxWidth().height(510.dp)) {
          Column(
              verticalArrangement = Arrangement.SpaceEvenly,
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.fillMaxSize().padding(40.dp, 0.dp)) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp)) {
                      val textColor =
                          if (answeredCorrectly.value != null && !(answeredCorrectly.value!!))
                              Color(0xFFB01919) // dark red
                          else MaterialTheme.colorScheme.onPrimaryContainer

                      val iconColor =
                          if (answeredCorrectly.value != null && answeredCorrectly.value!!)
                              Color(0xFF2F5E37) // dark green
                          else Color(0xFFB01919) // dark red

                      val iconImageVector =
                          if (answeredCorrectly.value != null && answeredCorrectly.value!!)
                              Icons.Filled.Check
                          else Icons.Filled.Close

                      if (answeredCorrectly.value != null) {
                        Icon(
                            imageVector = iconImageVector,
                            contentDescription = "Answer icon",
                            tint = iconColor,
                            modifier = Modifier.size(60.dp))
                      }
                      Text(
                          text = titleText.value,
                          color = textColor,
                          style =
                              MaterialTheme.typography.headlineSmall.copy(
                                  fontWeight = FontWeight.Black))
                    }

                for (i in 0..2) {
                  val buttonColor =
                      if (answeredCorrectly.value != null) {
                        if (i == correctAnswerIdx) {
                          Color(0xFF2F5E37) // dark green
                        } else if (answeredCorrectly.value!!) {
                          Color(0x442F5E37) // faded green
                        } else Color(0xFFB01919) // dark red
                      } else MaterialTheme.colorScheme.tertiary

                  PrimaryButton(
                      modifier = Modifier.fillMaxWidth(),
                      text = answers[i],
                      color = buttonColor,
                      onClick = {
                        // we've already answered this question
                        if (answeredCorrectly.value != null) {
                          return@PrimaryButton
                        }

                        onUserAnswered(i)

                        val isCorrect = i == correctAnswerIdx
                        if (isCorrect) {
                          titleText.value = correctAnswerText
                          backgroundColor.value = correctCardColor
                        } else {
                          titleText.value = incorrectAnswerText
                          backgroundColor.value = incorrectCardColor
                        }

                        answeredCorrectly.value = isCorrect
                      })
                }
              }
        }
      }
}

@Preview(widthDp = 932, heightDp = 650, showBackground = true, backgroundColor = 0xFFECEFE8)
@Composable
private fun QuestionScreenPreview() {
  GeoVoyageTheme {
    QuestionScreen(
        "What is the longest river in the world?",
        listOf("The Nile", "The Amazon River", "The Mystic River"),
        1,
        1) {}
  }
}
