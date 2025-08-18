// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.meta.pixelandtexel.scanner.models.PanelContentBase
import com.meta.spatial.core.Entity
import com.meta.spatial.toolkit.Animated
import com.meta.spatial.toolkit.PlaybackState
import com.meta.spatial.toolkit.PlaybackType

/**
 * View model encapsulating information about a curated object, or one with pre-assembled images and
 * copy, and to handle navigating to different sections of that content.
 *
 * @property uiContent The list of [PanelContentBase] panels to be displayed.
 * @property meshEntity The scene 3D mesh [Entity] associated with this curated object.
 */
class CuratedObjectInfoViewModel(
    val uiContent: List<PanelContentBase>,
    private val meshEntity: Entity? = null,
) : ViewModel() {
  private val _route = mutableStateOf("0_${uiContent.first().title}")
  val route: State<String> = _route

  val routes: List<String> = uiContent.mapIndexed { i, content -> "${i}_${content.title}" }

  /**
   * Navigates to the designated route, and triggers the 3D object animation associated with that
   * route.
   *
   * @param dest The route name, which is the string from the the routes generated above.
   * @param routeIdx The corresponding int index of the route.
   */
  fun navTo(dest: String, routeIdx: Int) {
    if (_route.value == dest) {
      return
    }

    _route.value = dest

    meshEntity?.let {
      if (it.hasComponent<Animated>()) {
        // trigger different animations depending on the nav item index
        val track = uiContent[routeIdx].animationTrack ?: (routeIdx % 2)

        it.setComponent(
            Animated(
                startTime = System.currentTimeMillis(),
                playbackState = PlaybackState.PLAYING,
                playbackType = PlaybackType.CLAMP,
                track = track,
            ))
      }
    }
  }

  /**
   * Handle the user selecting a tile on a panel of
   * [com.meta.pixelandtexel.scanner.models.PanelContentType] type TILES.
   *
   * @param panelIdx The index of the current panel in the list of panels.
   * @param tileIdx The index of the selected tile in the current panel content.
   */
  fun onTileSelected(panelIdx: Int, tileIdx: Int) {
    Log.d("CuratedObjectInfoViewModel", "TODO: handle tile $tileIdx of panel $panelIdx selected")
  }
}
