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
      val targetSize = triggerArea.size

      targetSize.x = targetSize.x / 2
      targetSize.y = targetSize.y / 2
      targetSize.z = targetSize.z / 2

      val triggers = Query.where { has(Trigger.id, Transform.id) }
      // loop through all the triggers to see if they are inside the target area
      for (triggerEntity in triggers.eval()) {
        val trigger = triggerEntity.getComponent<Trigger>()
        val triggerTransform = triggerEntity.getComponent<Transform>()
        val triggerPosition = triggerTransform.transform.t

        val triggerSize = trigger.size
        triggerSize.x = triggerSize.x / 2
        triggerSize.y = triggerSize.y / 2
        triggerSize.z = triggerSize.z / 2

        // Calculate the distance from the current position to the center of the target area
        val distanceX = triggerPosition.x - targetPosition.x
        val distanceY = triggerPosition.y - targetPosition.y
        val distanceZ = triggerPosition.z - targetPosition.z

        // Check if the current position is inside the target area
        var isInside =
            (distanceX >= -(targetSize.x + triggerSize.x) &&
                distanceX <= (targetSize.x + triggerSize.x) &&
                distanceY >= -(targetSize.y + triggerSize.y) &&
                distanceY <= (targetSize.y + triggerSize.y) &&
                distanceZ >= -(targetSize.z + triggerSize.z) &&
                distanceZ <= (targetSize.z + triggerSize.z))

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
              TriggerEventArgs(direction, triggerArea.value, dataModel))
        } else if (!isInside && trigger.insideAreaId == areaEntity.id) {
          trigger.insideAreaId = 0
          triggerEntity.setComponent(trigger)
        }
      }
    }
  }
}
