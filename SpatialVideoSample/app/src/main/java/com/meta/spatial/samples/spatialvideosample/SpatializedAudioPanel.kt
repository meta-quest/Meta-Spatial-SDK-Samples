/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.spatialvideosample

import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion

class SpatializedAudioPanel() : ComponentBase() {

  override fun typeID(): Int {
    return SpatializedAudioPanel.id
  }

  companion object : ComponentCompanion {
    override val id = R.id.spatialized_audio_panel
    override val createDefaultInstance = { SpatializedAudioPanel() }
  }
}
