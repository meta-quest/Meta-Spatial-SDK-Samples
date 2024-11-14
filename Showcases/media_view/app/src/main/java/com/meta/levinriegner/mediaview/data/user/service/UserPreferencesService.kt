// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.user.service

import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferencesService
@Inject
constructor(
    private val sharedPreferences: SharedPreferences,
) {

  fun isSampleMediaSaved(): Boolean {
    return sharedPreferences.getBoolean(KEY_IS_SAMPLE_MEDIA_SAVED, false)
  }

  fun setSampleMediaSaved(saved: Boolean) {
    sharedPreferences.edit().putBoolean(KEY_IS_SAMPLE_MEDIA_SAVED, saved).apply()
  }

  fun isPrivacyPolicyAccepted(): Boolean {
    return sharedPreferences.getBoolean(KEY_IS_PRIVACY_POLICY_ACCEPTED, false)
  }

  fun setPrivacyPolicyAccepted(accepted: Boolean) {
    sharedPreferences.edit().putBoolean(KEY_IS_PRIVACY_POLICY_ACCEPTED, accepted).apply()
  }

  companion object {
    private const val KEY_IS_SAMPLE_MEDIA_SAVED = "is_sample_media_saved"
    private const val KEY_IS_PRIVACY_POLICY_ACCEPTED = "is_privacy_policy_accepted"
  }
}
