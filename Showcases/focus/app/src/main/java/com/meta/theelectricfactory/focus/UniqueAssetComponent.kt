// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.EnumAttribute
import com.meta.spatial.core.IntAttribute

// UniqueAsset Component allow us to identify all entities that are unique (Clock, Speaker, AI
// Exchange Panel, Tasks Panel) and save its uuid to identify it later
class UniqueAssetComponent(
    uuid: Int? = 0,
    type: AssetType = AssetType.CLOCK,
) : ComponentBase() {

  var uuid by IntAttribute("uuid", R.id.UniqueAssetComponent_uuid, this, uuid)
  var type by
      EnumAttribute("type", R.id.UniqueAssetComponent_type, this, AssetType::class.java, type)

  override fun typeID(): Int {
    return UniqueAssetComponent.id
  }

  companion object : ComponentCompanion {
    override val id = R.id.UniqueAssetComponent_class
    override val createDefaultInstance = { UniqueAssetComponent() }
  }
}
