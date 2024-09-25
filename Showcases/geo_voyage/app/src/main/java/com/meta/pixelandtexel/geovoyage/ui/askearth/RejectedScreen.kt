// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.askearth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.PrimaryButton
import com.meta.pixelandtexel.geovoyage.ui.components.panel.SecondaryPanel
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme

/**
 * Display error message when query cannot be answered. Show alternative query suggestions.
 *
 * @param onAskExampleQuestion Function to invoke llama with one of the sample questions.
 */
@Composable
fun RejectedScreen(onAskExampleQuestion: (question: String) -> Unit) {
  val shuffledQuestions = questions.shuffled().take(3)

  SecondaryPanel {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
      Text(
          text = stringResource(id = R.string.rejected_screen_default_query_failure_message),
          style =
              MaterialTheme.typography.headlineSmall.copy(
                  fontSize = 30.sp,
                  fontWeight = FontWeight.Black,
                  fontStyle = FontStyle.Italic,
              ),
          textAlign = TextAlign.Center)
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(30.dp),
          modifier = Modifier.padding(top = 30.dp)) {
            shuffledQuestions.forEach { question ->
              PrimaryButton(text = question, modifier = Modifier.fillMaxWidth()) {
                onAskExampleQuestion(question)
              }
            }
          }
    }
  }
}

@Preview(
    widthDp = 932,
    heightDp = 650,
)
@Composable
private fun RejectedScreenPreview() {
  GeoVoyageTheme { RejectedScreen {} }
}

val questions =
    listOf(
        "What is the capital of France?",
        "Which river is the longest in the world?",
        "What is the highest mountain in Africa?",
        "In which country is the Great Wall of China located?",
        "What is the smallest country by land area?",
        "Which ocean is the largest by surface area?",
        "What is the most populous country in the world?",
        "Which desert is the largest in the world?",
        "What is the capital of Japan?",
        "In which continent is the Amazon Rainforest located?",
        "Which country has the most UNESCO World Heritage Sites?",
        "What is the official language of Brazil?",
        "Which European country is known for its fjords?",
        "What is the capital of Australia?",
        "Which river runs through the city of Cairo?",
        "What is the largest island in the world?",
        "In which country is the city of Istanbul located?",
        "Which country is known as the Land of the Rising Sun?",
        "What is the capital of Canada?",
        "Which African country was formerly known as Abyssinia?",
        "What is the longest river in the United States?",
        "Which country is famous for its pyramids?",
        "What is the capital of Italy?",
        "Which sea is the saltiest in the world?",
        "In which country is the city of Marrakech located?",
        "What is the largest desert in Australia?",
        "Which country has the most time zones?",
        "What is the capital of Russia?",
        "Which country is known for its maple syrup?",
        "What is the highest mountain in North America?",
        "In which country is the Serengeti National Park located?",
        "Which Asian country is made up of thousands of islands?",
        "What is the capital of Argentina?",
        "Which country is known for the tango dance?",
        "What is the longest mountain range in the world?",
        "In which country is the city of Kyoto located?",
        "What is the capital of Egypt?",
        "Which river is the primary water source for Pakistan?",
        "What is the national sport of Canada?",
        "Which country is home to the ancient city of Petra?",
        "What is the capital of Mexico?",
        "Which European country is known for its tulips?",
        "What is the highest peak in South America?",
        "In which country is the historical region of Transylvania located?",
        "Which country has the most volcanoes?",
        "What is the capital of Thailand?",
        "Which desert is located in southern Africa?",
        "In which country is the city of Dubrovnik located?",
        "What is the largest country in South America by land area?",
        "Which river forms part of the border between the United States and Mexico?",
        "What is the capital of India?",
        "Which country is known for its fjords and northern lights?",
        "What is the largest city in Brazil?",
        "In which continent is the Sahara Desert located?",
        "Which country has the most languages spoken?",
        "What is the capital of Spain?",
        "Which country is known for its kangaroos and koalas?",
        "What is the longest river in Africa?",
        "In which country is the city of Dubrovnik located?",
        "What is the national flower of Japan?",
        "Which country is famous for its carnival festival in Rio de Janeiro?",
        "What is the capital of Germany?",
        "Which river flows through Paris?",
        "In which country is the city of Prague located?",
        "What is the largest lake in Africa?",
        "Which country is known for its ancient Greek ruins?",
        "What is the capital of China?",
        "Which African country is known for its ancient pyramids?",
        "What is the longest river in Europe?",
        "In which country is the city of Amsterdam located?",
        "Which country is known for its pasta and pizza?",
        "What is the capital of South Korea?",
        "Which river runs through the Grand Canyon?",
        "In which country is the historical city of Machu Picchu located?",
        "What is the capital of the United Kingdom?",
        "Which country is known for its beer and chocolate?",
        "What is the largest island in the Mediterranean Sea?",
        "In which continent is the Andes Mountain Range located?",
        "Which country is known for its pyramids and sphinx?",
        "What is the capital of New Zealand?",
        "Which European country is known for its castles and medieval towns?",
        "What is the largest lake in North America?",
        "In which country is the city of Reykjavik located?",
        "Which country is famous for its Great Barrier Reef?",
        "What is the capital of Portugal?",
        "Which river is the longest in South America?",
        "In which country is the historical region of Bavaria located?",
        "What is the national dish of Spain?",
        "Which country is known for its wine and baguettes?",
        "What is the capital of Turkey?",
        "Which river is the longest in Canada?",
        "In which country is the city of Vienna located?",
        "What is the largest island in the Caribbean?",
        "Which country is known for its Maori culture?",
        "What is the capital of Greece?",
        "Which African country is known for its safaris and wildlife?",
        "In which country is the city of Lisbon located?",
        "What is the national animal of Australia?",
        "Which country is famous for its maple leaves?",
        "What is the capital of the Netherlands?")
