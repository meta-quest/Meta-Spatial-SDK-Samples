// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.user.repository

import com.meta.levinriegner.mediaview.data.user.service.UserPreferencesService
import javax.inject.Inject

class UserRepository
@Inject
constructor(
    private val userPreferencesService: UserPreferencesService,
) {

  fun isSampleMediaSaved(): Boolean {
    return userPreferencesService.isSampleMediaSaved()
  }

  fun setSampleMediaSaved(saved: Boolean) {
    userPreferencesService.setSampleMediaSaved(saved)
  }
}
