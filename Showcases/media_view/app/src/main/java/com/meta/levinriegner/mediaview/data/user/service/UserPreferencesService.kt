// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.user.service

import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferencesService
@Inject
constructor(
    private val sharedPreferences: SharedPreferences,
) {

    fun isPrivacyPolicyAccepted(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_PRIVACY_POLICY_ACCEPTED, false)
    }

    fun setPrivacyPolicyAccepted(accepted: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_PRIVACY_POLICY_ACCEPTED, accepted).apply()
    }

    fun getSampleMediaVersion(): Int? {
        return sharedPreferences.getInt(KEY_SAMPLE_MEDIA_VERSION, -1).takeIf { it != -1 }
    }

    fun setSampleMediaVersion(version: Int) {
        sharedPreferences.edit().putInt(KEY_SAMPLE_MEDIA_VERSION, version).apply()
    }

    companion object {
        private const val KEY_IS_PRIVACY_POLICY_ACCEPTED = "is_privacy_policy_accepted"
        private const val KEY_SAMPLE_MEDIA_VERSION = "sample_media_version"
    }
}
