/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.uisetsample.panel

import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible

class PanelNavigator {

  fun setPanelsVisible(registrationIds: List<Int>, otherIds: List<Int>) {
    // Get Navigator Panel
    val navigatorPanel =
        Query.where { has(Panel.id) }
            .eval()
            .first {
              it.getComponent<Panel>().panelRegistrationId == PanelRegistrationIds.PANEL_NAVIGATOR
            }

    // Get all panels and set visibility
    Query.where { has(Panel.id) }
        .eval()
        .filter {
          val id = it.getComponent<Panel>().panelRegistrationId
          otherIds.contains(id) || registrationIds.contains(id)
        }
        .forEach {
          // Mark as visible if in registrationIds
          val id = it.getComponent<Panel>().panelRegistrationId
          it.setComponent(Visible(registrationIds.contains(id)))
          // Position parent based on navigator
          if (id == registrationIds.first()) {
            val transform = navigatorPanel.getComponent<Transform>().transform

            // Set same depth as Navigator coming from the Composition
            transform.t.z += 0.5f

            // Move vertically depending on the Panels id
            transform.t.y +=
                when (id) {
                  PanelRegistrationIds.PANEL_DIALOGS_LAYOUT -> 1.2f

                  PanelRegistrationIds.PANEL_VIDEO_PLAYER_PATTERN -> .9f

                  PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN1 -> 0.73f
                  PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN2 -> 0.73f
                  PanelRegistrationIds.PANEL_HORIZON_OS_PATTERN3 -> 0.73f

                  PanelRegistrationIds.PANEL_TOOLTIPS_LAYOUT -> 0.9f

                  else -> 0.8f
                }

            // Move horizontally depending on the amount of siblings
            transform.t.x +=
                when (registrationIds.count()) {
                  PanelRegistrationIds.PANEL_SLIDERS_LAYOUT -> -1f
                  PanelRegistrationIds.PANEL_TEXT_TILE_BUTTONS_LAYOUT -> 0.6f
                  else -> 0f
                }

            // compensate for the Navigator rotation coming from the Composition
            transform.q = transform.q.times(Quaternion(-30f, 0f, 0f))

            it.setComponent(Transform(transform))
          }
        }
  }
}
