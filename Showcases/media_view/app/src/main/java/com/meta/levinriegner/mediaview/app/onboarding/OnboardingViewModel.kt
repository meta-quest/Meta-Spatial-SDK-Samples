// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.onboarding

import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.events.AppEvent
import com.meta.levinriegner.mediaview.app.events.AppEventListener
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.NavigationEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.onboarding.model.StepModel
import com.meta.levinriegner.mediaview.data.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.internal.immutableListOf
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
@Inject
constructor(
  private val userRepository: UserRepository,
  private val panelDelegate: PanelDelegate,
  private val eventBus: EventBus,
) : ViewModel(), AppEventListener {
  private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Idle)
  val state = _state.asStateFlow()

  init {
    eventBus.register(this)
  }

  private fun refreshShouldShow() {
    val isCompleted = userRepository.isOnboardingCompleted()

    if (isCompleted) {
      Timber.i("Onboarding already completed")
    } else {
      Timber.i("Starting Onboarding for the first time")
      panelDelegate.toggleOnboarding(true)
      userRepository.setOnboardingCompleted()
    }

    _state.value = OnboardingState.OnboardingStarted(true, stepList, 0)
  }

  fun close() {
    panelDelegate.toggleOnboarding(false)
  }

  override fun onEvent(event: AppEvent) {
    when (event) {
      is NavigationEvent.PrivacyPolicyAccepted -> {
        refreshShouldShow()
      }
    }
  }

  override fun onCleared() {
    eventBus.unregister(this)
    super.onCleared()
  }

  companion object {
    private val stepList =
        immutableListOf(
            StepModel(
                1,
                R.drawable.logo_and_name,
                "Free and Open Source",
                "Media View is a free and open-source spatial application designed to view various media types on the Meta Quest.\n\nIt was built using the Meta Spatial SDK to contribute to the developer community as a resource for spatialized media viewing experiences within VR",
                false,
            ),
            StepModel(
                2,
                R.raw.browsing,
                "The Browse Panel",
                "The Browse Panel allows you to quickly navigate through your media. Media types are filtered on the right hand side. Use your controller to scroll through the gallery by swiping up and down.",
                true,
            ),
            StepModel(
                4,
                R.raw.placing_media,
                "Place Media in your Environment",
                "To place media in your environment, use the index finger trigger to select the media. Point at the media, tap the trigger once, and it will be loaded into your space. You can have up to 5 files open around you at once.",
                true,
            ),
            StepModel(
                5,
                R.raw.positioning_media,
                "Position your Media",
                "Media in your environment can be viewed, edited and placed all around you. Move it closer to inspect or position it anywhere in your space. To adjust placement, aim your controller at the media and hold the side button to grab and move it to your desired spot.",
                true,
            ),
            StepModel(
                6,
                R.raw.enter_immersive_mode,
                "Enter Immersive View",
                "To fully immerse yourself in a piece of media, select the three dots (...) in the top right corner of the media in your environment to open the action menu. Then, choose 'Immersive View' from the options. You'll be surrounded by the media for a more engaging experience.",
                true,
            ),
            StepModel(
                8,
                R.raw.view_info,
                "Show File Information",
                "To view metadata like file name, type and dimensions, toggle the info icon within the Browse Panel. This will overlay additional details directly on the media, helping you easily identify your files.",
                true,
            ),
            StepModel(
                9,
                R.drawable.logo_and_name,
                "Thank you for Downloading",
                "We are continuously working to make the app a comprehensive tool for viewing, editing and managing media.\n\nWe appreciate all the feedback from the community--feel free to submit additional feedback through our website at mdvw.io",
                false,
            ),
        )
  }
}
