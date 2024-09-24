/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.toolkit.AvatarBody
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.Visible

class MrukInputSystem(var mrukSampleActivity: MrukSampleActivity) : SystemBase() {

  override fun execute() {
    val leftController: Controller? =
        Query.where { has(AvatarBody.id) }
            .eval()
            .toMutableList()
            .first { it.isLocal() && it.getComponent<AvatarBody>().isPlayerControlled }
            .getComponent<AvatarBody>()
            .leftHand
            .tryGetComponent<Controller>()

    if (leftController != null &&
        (leftController.buttonState and leftController.changedButtons and ButtonBits.ButtonMenu) !=
            0) {
      mrukSampleActivity.showUiPanel = !mrukSampleActivity.showUiPanel
      val uiEntity = Entity(R.integer.ui_mruk_id)
      uiEntity.setComponent(Visible(mrukSampleActivity.showUiPanel))
      if (mrukSampleActivity.showUiPanel) {
        mrukSampleActivity.updateUiPanelPosition()
      }
    }
  }
}
