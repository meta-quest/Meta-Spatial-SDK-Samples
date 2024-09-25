// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey
import com.meta.pixelandtexel.geovoyage.models.TriviaQuestion
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.ui.dailyquiz.NumAnswerOptionsPerQuestion
import com.meta.pixelandtexel.geovoyage.ui.dailyquiz.NumQuestionsPerQuiz
import com.meta.pixelandtexel.geovoyage.ui.dailyquiz.QuestionRoutes
import com.meta.pixelandtexel.geovoyage.ui.dailyquiz.Routes
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Timer
import kotlin.concurrent.schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuizViewModel : ViewModel(), IPlayModeViewModel {
  private val _route = mutableStateOf(Routes.START_ROUTE)
  private val _title = mutableStateOf("")
  private val _alreadyQuizzedToday = mutableStateOf(false)
  private val _numCorrectAnswers = mutableIntStateOf(0)
  private val _questions = MutableStateFlow(arrayListOf<TriviaQuestion>())

  val route: State<String> = _route
  val title: State<String> = _title
  val alreadyQuizzedToday: State<Boolean> = _alreadyQuizzedToday
  val numCorrectAnswers: State<Int> = _numCorrectAnswers
  val questions = _questions.asStateFlow()

  private var allQuestions: List<TriviaQuestion>? = null
  val answerOptions = arrayListOf<List<String>>()
  val answerIndexes = arrayListOf<Int>()

  init {
    for (i in 0 until NumQuestionsPerQuiz) {
      val options = List(NumAnswerOptionsPerQuestion) { "" }
      answerOptions.add(options)
      answerIndexes.add(0)
    }
  }

  override fun onPlayModeResumed() {
    refresh()
    updateTitle()
  }

  fun setQuestions(questionsList: List<TriviaQuestion>) {
    allQuestions = questionsList
  }

  private fun refresh() {
    if (allQuestions == null) {
      return
    }

    // use an offset based on the date so any users playing the app on the same day will have
    // the same questions
    val daysOffset = ChronoUnit.DAYS.between(LocalDate.of(2024, 1, 1), LocalDate.now())
    val start =
        ((daysOffset.toInt() % allQuestions!!.size) * NumQuestionsPerQuiz) % allQuestions!!.size

    val lastQuizDayOffset = SettingsService.get(SettingsKey.LAST_DAILY_QUIZ, 0L)

    if (_questions.value.isEmpty() || daysOffset > lastQuizDayOffset) {
      // our questions for today
      val newQuestions = mutableListOf<TriviaQuestion>()
      for (i in 0 until NumQuestionsPerQuiz) {
        val idx = (start + i) % allQuestions!!.size
        newQuestions.add(allQuestions!![idx])
      }
      updateQuestions(newQuestions)
    }

    // This is a new day; nav back to start, update view model questions
    if (daysOffset > lastQuizDayOffset) {
      // reset our local state values
      _alreadyQuizzedToday.value = false
      _numCorrectAnswers.intValue = 0

      // nav back to start
      navTo(Routes.START_ROUTE)

      SettingsService.set(SettingsKey.LAST_DAILY_QUIZ, daysOffset)
    }

    // NOTE: uncomment this to test
    // SettingsService.set(SettingsKey.LAST_DAILY_QUIZ, 0L)
  }

  private fun updateQuestions(newQuestions: Iterable<TriviaQuestion>) {
    // shuffle the answers for each question
    answerOptions.clear()
    answerIndexes.clear()
    newQuestions.forEach {
      val shuffledOptions = listOf(it.answer, it.option2, it.option3).shuffled()
      val answerIdx = shuffledOptions.indexOf(it.answer)

      answerOptions.add(shuffledOptions)
      answerIndexes.add(answerIdx)
    }

    // first clear it out
    _questions.value.clear()
    _questions.value.addAll(newQuestions)
  }

  fun answerQuestion(questionIdx: Int, selectedAnswerIdx: Int) {
    val correct = selectedAnswerIdx == answerIndexes[questionIdx]

    if (correct) {
      _numCorrectAnswers.intValue++
    }

    Timer().schedule(if (correct) 1200 else 1800) {
      Handler(Looper.getMainLooper()).post {
        if (questionIdx == QuestionRoutes.size - 1) {
          _alreadyQuizzedToday.value = true
          navTo(Routes.RESULTS_ROUTE)
        } else {
          navTo(QuestionRoutes[questionIdx + 1])
        }
      }
    }
  }

  private fun updateTitle() {
    _title.value =
        when (_route.value) {
          Routes.START_ROUTE -> "Quiz Start"
          Routes.QUESTION_1_ROUTE -> "Question 1/${QuestionRoutes.size}"
          Routes.QUESTION_2_ROUTE -> "Question 2/${QuestionRoutes.size}"
          Routes.QUESTION_3_ROUTE -> "Question 3/${QuestionRoutes.size}"
          Routes.QUESTION_4_ROUTE -> "Question 4/${QuestionRoutes.size}"
          Routes.QUESTION_5_ROUTE -> "Question 5/${QuestionRoutes.size}"
          else -> "Quiz Complete"
        }
  }

  fun navTo(dest: String) {
    _route.value = dest
    updateTitle()
  }
}
