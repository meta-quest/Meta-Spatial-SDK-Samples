/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.mruksample.qr_code_scanner

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.mruk.TrackedQrCode
import com.meta.spatial.mruk.getPayloadAsString
import com.meta.spatial.samples.mruksample.Menu
import com.meta.spatial.samples.mruksample.R
import com.meta.spatial.samples.mruksample.TransformParentFollow
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform

class MenuSpawner() : SystemBase() {

  private val qrCodeToEntity = mutableMapOf<Entity, Entity>()

  override fun execute() {
    Query.where { changed(TrackedQrCode.id) }
        .eval()
        .forEach { qrCodeEnt ->
          if (!qrCodeToEntity.containsKey(qrCodeEnt)) {
            val menu =
                Entity.create(
                    TransformParentFollow(qrCodeEnt),
                    Transform(),
                    // start with a scale of 0 to scale up once we load in
                    Scale(0.0f),
                    Panel(R.layout.ui_qrcode_scanner),
                    Menu(qrCodeEnt.getComponent<TrackedQrCode>().getPayloadAsString()))
            qrCodeToEntity[qrCodeEnt] = menu
          }
        }
  }
}
