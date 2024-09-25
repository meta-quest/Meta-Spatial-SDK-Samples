// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.EnumAttribute
import com.meta.spatial.core.IntAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute

// Tool Component allow us to identify and save properties of tool assets, as type of tool and
// position to show the delete button.
// Uuid is also saved to identify the object later
class ToolComponent(
    uuid: Int? = 0,
    type: AssetType = AssetType.STICKER,
    deleteButtonPosition: Vector3 = Vector3(0f)
) : ComponentBase() {

  var uuid by IntAttribute("uuid", R.id.ToolComponent_uuid, this, uuid)
  var type by EnumAttribute("type", R.id.ToolComponent_type, this, AssetType::class.java, type)
  var deleteButtonPosition by
      Vector3Attribute(
          "delete_button_position",
          R.id.ToolComponent_delete_button_position,
          this,
          deleteButtonPosition)

  override fun typeID(): Int {
    return ToolComponent.id
  }

  companion object : ComponentCompanion {
    override val id = R.id.ToolComponent_class
    override val createDefaultInstance = { ToolComponent() }
  }
}
