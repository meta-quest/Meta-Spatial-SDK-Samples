/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample

import com.meta.spatial.core.Entity
import com.meta.spatial.core.SystemBase
import com.meta.spatial.toolkit.Visible

class UIPositionSystem(
    var mrukSampleActivity: MrukSampleActivity,
) : SystemBase() {

  override fun execute() {
    // We need to wait until the HMD pose is initialized before we can position the UI panel.
    // Keep trying until it succeeds.
    if (!mrukSampleActivity.uiPositionInitialized) {
      mrukSampleActivity.uiPositionInitialized = mrukSampleActivity.updateUiPanelPosition()
      if (mrukSampleActivity.uiPositionInitialized) {
        mrukSampleActivity.showUiPanel = true
        val uiEntity = Entity(R.integer.ui_mruk_id)
        uiEntity.setComponent(Visible(mrukSampleActivity.showUiPanel))
      }
    }
  }
}
