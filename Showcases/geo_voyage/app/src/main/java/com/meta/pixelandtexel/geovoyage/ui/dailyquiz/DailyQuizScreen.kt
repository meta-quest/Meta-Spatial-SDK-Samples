// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.dailyquiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meta.pixelandtexel.geovoyage.models.TriviaQuestion
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.pixelandtexel.geovoyage.viewmodels.QuizViewModel

const val NumQuestionsPerQuiz = 5
const val NumAnswerOptionsPerQuestion = 3

object Routes {
  const val START_ROUTE = "start"
  const val QUESTION_1_ROUTE = "question1"
  const val QUESTION_2_ROUTE = "question2"
  const val QUESTION_3_ROUTE = "question3"
  const val QUESTION_4_ROUTE = "question4"
  const val QUESTION_5_ROUTE = "question5"
  const val RESULTS_ROUTE = "results"
}

val QuestionRoutes =
    listOf(
        Routes.QUESTION_1_ROUTE,
        Routes.QUESTION_2_ROUTE,
        Routes.QUESTION_3_ROUTE,
        Routes.QUESTION_4_ROUTE,
        Routes.QUESTION_5_ROUTE)

@Composable
fun DailyQuizScreen(
    allQuestions: List<TriviaQuestion>,
    vm: QuizViewModel = viewModel(),
    setTitle: ((text: String) -> Unit)? = null,
    navController: NavHostController = rememberNavController()
) {
  val route by vm.route
  val title by vm.title
  val alreadyQuizzedToday by vm.alreadyQuizzedToday
  val numCorrectAnswers by vm.numCorrectAnswers
  val quizQuestions by vm.questions.collectAsState()

  LaunchedEffect(route) { navController.navigate(route) { launchSingleTop = true } }

  LaunchedEffect(title) { setTitle?.invoke(title) }

  NavHost(
      navController = navController,
      startDestination = Routes.START_ROUTE,
  ) {
    composable(Routes.START_ROUTE) {
      StartScreen {
        vm.navTo(Routes.QUESTION_1_ROUTE)
        setTitle?.invoke("Question 1/${NumQuestionsPerQuiz}")
      }
    }
    for (i in QuestionRoutes.indices) {
      composable(QuestionRoutes[i]) {
        QuestionScreen(quizQuestions[i].query, vm.answerOptions[i], vm.answerIndexes[i], i) { idx ->
          vm.answerQuestion(i, idx)
        }
      }
    }
    composable(Routes.RESULTS_ROUTE) { ResultsScreen(numCorrectAnswers, quizQuestions.size) }
  }
}

@Preview
@Composable
fun DailyQuizScreenPreview() {
  GeoVoyageTheme { DailyQuizScreen(listOf<TriviaQuestion>()) }
}
