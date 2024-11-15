/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.animationssample

import com.meta.spatial.core.BooleanAttribute
import com.meta.spatial.core.ComponentBase
import com.meta.spatial.core.ComponentCompanion
import com.meta.spatial.core.FloatAttribute
import com.meta.spatial.core.StringAttribute
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector3Attribute

class FollowerComponent(
    expDecay: Float = 8f,
    targetName: String = "",
    enabled: Boolean = true,
    followOffset: Vector3 = Vector3(0f, 0f, 0f),
    followDistance: Float = 0.5f
) : ComponentBase() {

  var expDecay: Float by FloatAttribute("expDecay", R.string.follower_exp_decay, this, expDecay)
  var targetName: String by
      StringAttribute("targetName", R.string.follower_target_name, this, targetName)
  var enabled: Boolean by BooleanAttribute("enabled", R.string.follower_enabled, this, enabled)

  var followOffset: Vector3 by
      Vector3Attribute("followOffset", R.string.follower_follow_offset, this, followOffset)

  var followDistance: Float by
      FloatAttribute("followDistance", R.string.follower_follow_distance, this, followDistance)

  override fun typeID(): Int {
    return FollowerComponent.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.follower_class
    override val createDefaultInstance = { FollowerComponent() }
  }
}

class FollowerTarget(
    targetName: String = "followerTarget",
) : ComponentBase() {

  var targetName: String by StringAttribute("name", R.string.follower_target_name, this, targetName)

  var isBuiltInFollower: Boolean by
      BooleanAttribute("isBuiltInFollower", R.id.is_built_in, this, false)

  override fun typeID(): Int {
    return FollowerTarget.id
  }

  companion object : ComponentCompanion {
    override val id = R.string.follower_target_class
    override val createDefaultInstance = { FollowerTarget() }
  }
}
