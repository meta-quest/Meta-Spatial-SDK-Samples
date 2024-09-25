// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.enums.PlayMode
import com.meta.pixelandtexel.geovoyage.models.GeoCoordinates
import com.meta.pixelandtexel.geovoyage.models.Landmark
import com.meta.pixelandtexel.geovoyage.models.TriviaQuestion
import com.meta.pixelandtexel.geovoyage.services.SettingsService
import com.meta.pixelandtexel.geovoyage.services.llama.QueryLlamaService
import com.meta.pixelandtexel.geovoyage.ui.askearth.AskEarthScreen
import com.meta.pixelandtexel.geovoyage.ui.components.buttons.NavButtonState
import com.meta.pixelandtexel.geovoyage.ui.components.panel.PrimaryPanel
import com.meta.pixelandtexel.geovoyage.ui.dailyquiz.DailyQuizScreen
import com.meta.pixelandtexel.geovoyage.ui.explore.ExploreScreen
import com.meta.pixelandtexel.geovoyage.ui.interstitial.InterstitialScreen
import com.meta.pixelandtexel.geovoyage.ui.intro.IntroScreen
import com.meta.pixelandtexel.geovoyage.ui.mainnavigator.PanelNavContainer
import com.meta.pixelandtexel.geovoyage.ui.mainnavigator.Routes
import com.meta.pixelandtexel.geovoyage.ui.settings.SettingsScreen
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.pixelandtexel.geovoyage.ui.todayinhistory.TodayInHistoryScreen
import com.meta.pixelandtexel.geovoyage.viewmodels.AskEarthViewModel
import com.meta.pixelandtexel.geovoyage.viewmodels.ExploreViewModel
import com.meta.pixelandtexel.geovoyage.viewmodels.IPlayModeViewModel
import com.meta.pixelandtexel.geovoyage.viewmodels.PanelViewModel
import com.meta.pixelandtexel.geovoyage.viewmodels.QuizViewModel
import com.meta.pixelandtexel.geovoyage.viewmodels.TodayInHistoryViewModel
import java.lang.ref.WeakReference
import kotlin.random.Random
import org.xmlpull.v1.XmlPullParser

class PanelActivity : ActivityCompat.OnRequestPermissionsResultCallback, ComponentActivity() {
  companion object {
    private const val TAG: String = "PanelActivity"

    lateinit var instance: WeakReference<PanelActivity>
  }

  private lateinit var navController: NavHostController

  // view models
  private lateinit var panelVM: PanelViewModel
  private lateinit var exploreVM: ExploreViewModel
  private lateinit var askVM: AskEarthViewModel
  private lateinit var todayVM: TodayInHistoryViewModel
  private lateinit var quizVM: QuizViewModel
  private var currentPlayModeVM: IPlayModeViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    instance = WeakReference(this)

    SettingsService.initialize(baseContext)
    QueryLlamaService.initialize(baseContext)

    val questions = parseQuestionsXml(R.xml.trivia_questions)

    // NOTE: uncomment to test interstitial user notice
    // SettingsService.set(SettingsKey.ACCEPTED_NOTICE, false)

    val navButtonStates =
        listOf(
            NavButtonState(
                text = getString(R.string.explore),
                route = Routes.EXPLORE_ROUTE,
                iconResId = R.drawable.ic_explore,
            ),
            NavButtonState(
                text = getString(R.string.ask),
                route = Routes.ASK_EARTH_ROUTE,
                iconResId = R.drawable.ic_mic,
            ),
            NavButtonState(
                text = getString(R.string.today),
                route = Routes.TODAY_IN_HISTORY_ROUTE,
                iconResId = R.drawable.ic_calendar,
            ),
            NavButtonState(
                text = getString(R.string.quiz),
                route = Routes.DAILY_QUIZ_ROUTE,
                iconResId = R.drawable.ic_question_block,
            ))

    setContent {
      navController = rememberNavController()

      // instantiate our view models
      panelVM = viewModel()
      exploreVM = viewModel()
      askVM = viewModel()
      todayVM = viewModel()
      quizVM = viewModel()

      val title by panelVM.title
      val route by panelVM.route
      val prevRoute by panelVM.prevRoute
      val userAcceptedNotice by panelVM.hasUserAcceptedNotice

      LaunchedEffect(null) { quizVM.setQuestions(questions) }

      LaunchedEffect(route) {
        if (route.isEmpty()) {
          return@LaunchedEffect
        }

        // suspend our current view model
        currentPlayModeVM?.onPlayModeSuspended()

        navController.navigate(route) { launchSingleTop = true }

        // resume our next view model
        currentPlayModeVM =
            when (route) {
              Routes.EXPLORE_ROUTE -> exploreVM
              Routes.ASK_EARTH_ROUTE -> askVM
              Routes.TODAY_IN_HISTORY_ROUTE -> todayVM
              Routes.DAILY_QUIZ_ROUTE -> quizVM
              else -> null
            }
        currentPlayModeVM?.onPlayModeResumed()

        // start our play mode
        val mainActivity = MainActivity.instance.get()
        when (route) {
          Routes.INTRO_ROUTE -> mainActivity?.tryStartMode(PlayMode.INTRO)
          Routes.EXPLORE_ROUTE -> mainActivity?.tryStartMode(PlayMode.EXPLORE)
          Routes.ASK_EARTH_ROUTE -> mainActivity?.tryStartMode(PlayMode.ASK_EARTH)
          Routes.TODAY_IN_HISTORY_ROUTE -> mainActivity?.tryStartMode(PlayMode.TODAY_IN_HISTORY)
          Routes.DAILY_QUIZ_ROUTE -> mainActivity?.tryStartMode(PlayMode.DAILY_QUIZ)
        }
      }

      GeoVoyageTheme {
        Box(modifier = Modifier.size(1210.dp, 940.dp).padding(0.dp)) {
          PrimaryPanel {
            if (userAcceptedNotice) {
              PanelNavContainer(title, route, navButtonStates, { panelVM.navTo(it) }) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.INTRO_ROUTE,
                    modifier = Modifier) {
                      // routes
                      composable(route = Routes.INTRO_ROUTE) { IntroScreen() }
                      composable(route = Routes.EXPLORE_ROUTE) {
                        ExploreScreen(
                            vm = exploreVM,
                            setTitle = { panelVM.setTitle(it) },
                            onReportVRProblem = {
                              val uri = Uri.parse(it)
                              val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                              startActivity(browserIntent)
                            })
                      }
                      composable(route = Routes.ASK_EARTH_ROUTE) {
                        AskEarthScreen(
                            vm = askVM,
                            onUserRejectedPermission = { panelVM.navTo(Routes.INTRO_ROUTE) },
                            setTitle = { panelVM.setTitle(it) })
                      }
                      composable(route = Routes.TODAY_IN_HISTORY_ROUTE) {
                        TodayInHistoryScreen(vm = todayVM, setTitle = { panelVM.setTitle(it) })
                      }
                      composable(route = Routes.DAILY_QUIZ_ROUTE) {
                        DailyQuizScreen(
                            vm = quizVM,
                            allQuestions = questions,
                            setTitle = { panelVM.setTitle(it) })
                      }
                      composable(route = Routes.SETTINGS_ROUTE) { SettingsScreen() }
                    }
              }
            } else {
              InterstitialScreen { panelVM.userAcceptedNotice() }
            }
          }
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()

    MainActivity.instance.get()?.showMainPanel()
  }

  private fun parseQuestionsXml(resourceId: Int): List<TriviaQuestion> {
    val questions = mutableListOf<TriviaQuestion>()
    val parser = resources.getXml(resourceId)

    var eventType = parser.eventType
    var currentQuestion: TriviaQuestion? = null
    var currentTag: String? = null

    var questionNumber: UShort = 0u
    var query: String? = null
    var answer: String? = null
    var option2: String? = null
    var option3: String? = null
    var difficulty: UShort? = null
    var latitude: Float? = null
    var longitude: Float? = null

    try {
      while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
          XmlPullParser.START_TAG -> {
            currentTag = parser.name
            if (currentTag == "question") {
              // reset all fields for the new question
              query = null
              answer = null
              option2 = null
              option3 = null
              difficulty = null
              latitude = null
              longitude = null
            }
          }

          XmlPullParser.TEXT -> {
            val text = parser.text
            when (currentTag) {
              "query" -> query = text
              "answer" -> answer = text
              "option2" -> option2 = text
              "option3" -> option3 = text
              "difficulty" -> difficulty = text.toUShort()
              "latitude" -> latitude = text.toFloat()
              "longitude" -> longitude = text.toFloat()
            }
          }

          XmlPullParser.END_TAG -> {
            if (parser.name == "question") {
              // create the object and add it to the list
              if (query != null &&
                  answer != null &&
                  option2 != null &&
                  option3 !== null &&
                  difficulty != null &&
                  latitude != null &&
                  longitude != null) {
                currentQuestion =
                    TriviaQuestion(
                        ++questionNumber,
                        query = query,
                        answer = answer,
                        option2 = option2,
                        option3 = option3,
                        difficulty = difficulty,
                        latitude = latitude,
                        longitude = longitude)
                questions.add(currentQuestion)
              } else {
                throw Exception("Invalid trivia question xml $query")
              }
            }
          }
        }
        eventType = parser.next()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    // get our shuffled list of questions; use the same seed every time so all users have the
    // same questions every day
    return questions.shuffled(Random(42))
  }

  // calls from MainActivity

  fun startQueryAtCoordinates(coords: GeoCoordinates) {
    val queryTemplate = resources.getString(R.string.explore_screen_base_query)
    exploreVM.startQueryAtCoordinates(coords, queryTemplate)
  }

  fun displayLandmarkInfo(info: Landmark, coords: GeoCoordinates) {
    exploreVM.displayLandmarkInfo(info, coords)
  }
}
