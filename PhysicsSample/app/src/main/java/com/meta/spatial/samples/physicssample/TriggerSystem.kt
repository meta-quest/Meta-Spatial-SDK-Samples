/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.physicssample

import com.meta.spatial.core.EntityContext
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Transform
import kotlin.math.abs

class TriggerSystem() : SystemBase() {

  override fun execute() {

    val dataModel = EntityContext.getDataModel()

    val areas = Query.where { has(TriggerArea.id, Transform.id) }
    // for each Trigger Area
    for (areaEntity in areas.eval()) {
      val triggerArea = areaEntity.getComponent<TriggerArea>()
      val transform = areaEntity.getComponent<Transform>()
      val targetPosition = transform.transform.t
      val targetRotation = transform.transform.q
      val targetHalfSize = triggerArea.size / 2f
      val toAreaSpace = targetRotation.inverse()

      val triggers = Query.where { has(Trigger.id, Transform.id) }
      // loop through all the triggers to see if they are inside the target area
      for (triggerEntity in triggers.eval()) {
        val trigger = triggerEntity.getComponent<Trigger>()
        val triggerTransform = triggerEntity.getComponent<Transform>()
        val triggerHalfSize = trigger.size / 2f

        // The trigger's offset from the center of the area, along the area's own axes, so that a
        // rotated area covers the box it is drawn as.
        val offset = toAreaSpace * (triggerTransform.transform.t - targetPosition)

        // Check if the current position is inside the target area
        val isInside =
            abs(offset.x) <= targetHalfSize.x + triggerHalfSize.x &&
                abs(offset.y) <= targetHalfSize.y + triggerHalfSize.y &&
                abs(offset.z) <= targetHalfSize.z + triggerHalfSize.z

        // if the current position has entered inside the trigger
        if (isInside && trigger.insideAreaId != areaEntity.id) {

          // Update the insideAreaId id with the current area id
          trigger.insideAreaId = areaEntity.id
          triggerEntity.setComponent(trigger)

          // rotate a forward vector by the rotation of the target area
          val direction = targetRotation.times(Vector3.Forward).normalize()

          // send event with the direction and value of the target area
          dataModel?.sendEvent(
              triggerEntity,
              triggerArea.eventName,
              TriggerEventArgs(direction, triggerArea.value, dataModel),
          )
        } else if (!isInside && trigger.insideAreaId == areaEntity.id) {
          trigger.insideAreaId = 0
          triggerEntity.setComponent(trigger)
        }
      }
    }
  }
}
