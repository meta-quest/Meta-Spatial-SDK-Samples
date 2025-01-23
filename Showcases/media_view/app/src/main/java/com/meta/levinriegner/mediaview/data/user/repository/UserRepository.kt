// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.user.repository

import com.meta.levinriegner.mediaview.data.user.service.UserPreferencesService
import javax.inject.Inject

class UserRepository
@Inject
constructor(
    private val userPreferencesService: UserPreferencesService,
) {

  fun isPrivacyPolicyAccepted(): Boolean {
    return userPreferencesService.isPrivacyPolicyAccepted()
  }

  fun setPrivacyPolicyAccepted(accepted: Boolean) {
    userPreferencesService.setPrivacyPolicyAccepted(accepted)
  }

  fun getSampleMediaVersion(): Int? {
    return userPreferencesService.getSampleMediaVersion()
  }

  fun setSampleMediaVersion(version: Int) {
    userPreferencesService.setSampleMediaVersion(version)
  }

  fun areReleaseNotesSeenFor(version: String): Boolean {
    return userPreferencesService.areReleaseNotesSeenFor(version)
  }

  fun setReleaseNotesSeenFor(version: String) {
    userPreferencesService.setReleaseNotesSeenFor(version)
  }

  fun isOnboardingCompleted(): Boolean {
    return userPreferencesService.isOnboardingCompleted()
  }

  fun setOnboardingCompleted() {
    userPreferencesService.setOnboardingCompleted()
  }
}
