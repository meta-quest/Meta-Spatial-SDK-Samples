// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.privacy

import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel
@Inject
constructor(
    private val panelDelegate: PanelDelegate,
    private val userRepository: UserRepository,
) : ViewModel() {

    fun onCreate() {
        Timber.i("PrivacyPolicyViewModel created")
        // Consider moving this to a dedicated navigator/coordinator
        panelDelegate.togglePrivacyPolicy(show = !userRepository.isPrivacyPolicyAccepted())
    }

    fun acceptPrivacyPolicy() {
        Timber.i("Accept Privacy Policy")
        userRepository.setPrivacyPolicyAccepted(true)
        panelDelegate.togglePrivacyPolicy(show = false)
    }
}
