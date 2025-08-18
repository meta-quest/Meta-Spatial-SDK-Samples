// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.whatsnew

import androidx.lifecycle.ViewModel
import com.meta.levinriegner.mediaview.BuildConfig
import com.meta.levinriegner.mediaview.app.events.AppEvent
import com.meta.levinriegner.mediaview.app.events.AppEventListener
import com.meta.levinriegner.mediaview.app.events.EventBus
import com.meta.levinriegner.mediaview.app.events.NavigationEvent
import com.meta.levinriegner.mediaview.app.panel.PanelDelegate
import com.meta.levinriegner.mediaview.data.user.repository.UserRepository
import com.meta.levinriegner.mediaview.data.whatsnew.model.NewFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.internal.immutableListOf
import timber.log.Timber

@HiltViewModel
class WhatsNewViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val panelDelegate: PanelDelegate,
    eventBus: EventBus,
) : ViewModel(), AppEventListener {

  private val _releaseNotes = MutableStateFlow(whatsNew)
  val releaseNotes = _releaseNotes.asStateFlow()

  init {
    eventBus.register(this)
  }

  private fun refreshShouldShow() {
    val currentVersion = BuildConfig.VERSION_NAME
    val areReleaseNotesSeenForCurrentVersion = userRepository.areReleaseNotesSeenFor(currentVersion)

    if (!areReleaseNotesSeenForCurrentVersion) {
      if (whatsNew.isEmpty()) {
        Timber.i("No release notes available for version $currentVersion")
      } else {
        Timber.i("Showing Whats New for version $currentVersion")
        panelDelegate.toggleWhatsNew(true)
      }
      userRepository.setReleaseNotesSeenFor(currentVersion)
    } else {
      Timber.i("Release notes already seen for version $currentVersion")
    }
  }

  fun close() {
    panelDelegate.toggleWhatsNew(false)
  }

  companion object {
    private val whatsNew =
        immutableListOf(
            NewFeature(
                1,
                "Free and Open Source",
                "Media View is a free and open-source spatial application designed to view various media types on the Meta Quest.\nIt was built using the Meta Spatial SDK to contribute to the developer community as a resource for spatialized media viewing experiences within VR",
            ),
            NewFeature(
                2,
                "The Browse Panel",
                "The Browse Panel allows you to quickly navigate through your media. Media types are filtered on the right hand side. Use your controller to scroll through the gallery by swiping up and down.",
            ),
            NewFeature(
                3,
                "Add Media to Media View",
                "Media View comes with sample content for you, though you can currently add content by connecting your Google Drive account and downloading it to your Meta Quest.",
            ),
            NewFeature(
                4,
                "Place Media in your Environment",
                "To place media in your environment, use the index finger trigger to select the media. Point at the media, tap the trigger once, and it will be loaded into your space. You can have up to 5 files open around you at once.",
            ),
            NewFeature(
                5,
                "Position your Media",
                "Media in your environment can be viewed, edited and placed all around you. Move it closer to inspect or position it anywhere in your space. To adjust placement, aim your controller at the media and hold the side button to grab and move it to your desired spot.",
            ),
        )
  }

  override fun onEvent(event: AppEvent) {
    when (event) {
      is NavigationEvent.PrivacyPolicyAccepted -> {
        refreshShouldShow()
      }
    }
  }
}
