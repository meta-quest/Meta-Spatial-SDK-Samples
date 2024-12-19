// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.privacy

import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.NavigationEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class PrivacyPolicyViewModel
@Inject
constructor(
    private val panelDelegate: PanelDelegate,
    private val userRepository: UserRepository,
    private val eventBus: EventBus,
) : ViewModel() {

  fun onCreate() {
    Timber.i("PrivacyPolicyViewModel created")
    // Consider moving this to a dedicated navigator/coordinator
    val privacyPolicyAccepted = userRepository.isPrivacyPolicyAccepted()
    panelDelegate.togglePrivacyPolicy(show = !privacyPolicyAccepted)
    if (privacyPolicyAccepted)  {
        eventBus.post(NavigationEvent.PrivacyPolicyAccepted)
    }
  }

  fun acceptPrivacyPolicy() {
    Timber.i("Accept Privacy Policy")
    userRepository.setPrivacyPolicyAccepted(true)
    panelDelegate.togglePrivacyPolicy(show = false)
    eventBus.post(NavigationEvent.PrivacyPolicyAccepted)
  }
}
