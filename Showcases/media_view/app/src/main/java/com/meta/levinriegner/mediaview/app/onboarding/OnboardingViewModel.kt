// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.onboarding

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.data.onboarding.model.StepModel
import com.meta.levinriegner.mediaview.data.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.internal.immutableListOf
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Idle)
    val state = _state.asStateFlow()

    fun init() {
        val isCompleted = userRepository.isOnboardingCompleted()

        if (isCompleted) {
            Timber.i("Onboarding already completed")
        } else {
            Timber.i("Starting Onboarding for the first time")
            userRepository.setOnboardingCompleted()
        }

        _state.update {
            OnboardingState.OnboardingStarted(!isCompleted, stepList, 0)
        }
    }

    fun goToNextPage() {
        _state.update {
            OnboardingState.OnboardingStarted(
                isFirstTime = it.isFirstTime,
                currentPage = if (it is OnboardingState.OnboardingStarted) ++it.currentPage else 0,
                steps = stepList
            )
        }
    }

    fun goToPreviousPage() {
        _state.update {
            OnboardingState.OnboardingStarted(
                isFirstTime = it.isFirstTime,
                currentPage = if (it is OnboardingState.OnboardingStarted) --it.currentPage else 0,
                steps = stepList
            )
        }
    }

    companion object {
        private val stepList = immutableListOf<StepModel>(
            StepModel(
                1,
                Uri.EMPTY,
                "Title for step 1",
                "Description for step 1"
            ),
            StepModel(
                2,
                Uri.EMPTY,
                "Title for step 1",
                "Description for step 1"
            ),
            StepModel(
                3,
                Uri.EMPTY,
                "Title for step 1",
                "Description for step 1"
            ),
        )
    }
}
