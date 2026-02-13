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

/** System that spawns menu UI panels for newly detected QR codes. */
class MenuSpawner : SystemBase() {
  /** Maps QR code entities to their corresponding menu entities. */
  private val qrCodeToEntity = mutableMapOf<Entity, Entity>()

  /** Monitors for newly tracked QR codes and spawns a menu panel for each one. */
  override fun execute() {
    Query.where { changed(TrackedQrCode.id) }
        .eval()
        .forEach { qrCodeEntity ->
          if (!qrCodeToEntity.containsKey(qrCodeEntity)) {
            val menu =
                Entity.create(
                    TransformParentFollow(qrCodeEntity),
                    Transform(),
                    // Start with a scale of 0 to scale up once we load in.
                    Scale(0.0f),
                    Panel(R.layout.ui_qrcode_scanner),
                    Menu(qrCodeEntity.getComponent<TrackedQrCode>().getPayloadAsString()),
                )
            qrCodeToEntity[qrCodeEntity] = menu
          }
        }
  }
}
