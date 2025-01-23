// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.onboarding

import com.meta.levinriegner.mediaview.data.onboarding.model.StepModel

sealed class OnboardingState(open val isFirstTime: Boolean) {
  data object Idle : OnboardingState(false)

  data class OnboardingStarted(
      override val isFirstTime: Boolean,
      val steps: List<StepModel>,
      var currentPage: Int
  ) : OnboardingState(isFirstTime)

  data class Error(override val isFirstTime: Boolean, val reason: String) :
      OnboardingState(isFirstTime)
}
