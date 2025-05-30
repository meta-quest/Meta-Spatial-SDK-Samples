// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.systems.scaleChildren

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.samples.premiummediasample.ScaledChild
import com.meta.spatial.samples.premiummediasample.ScaledParent
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent

class ScaleChildrenSystem : SystemBase() {
  override fun execute() {
    val scalesChanged = Query.where { has(ScaledParent.id).and(changed(Scale.id)) }
    for (scaleParent in scalesChanged.eval()) {
      val children = getScaledChildren(scaleParent)
      val parentScale = scaleParent.getComponent<Scale>().scale
      for (child in children) {
        val scaledChild = child.getComponent<ScaledChild>()
        if (!scaledChild.isEnabled) continue

        updateChildTransform(child, scaledChild, parentScale)
      }
    }
  }

  private fun updateChildTransform(child: Entity, scaledChild: ScaledChild, parentScale: Vector3) {
    val transformComponent = child.getComponent<Transform>()
    transformComponent.transform.t.x =
        (scaledChild.localPosition.x * parentScale.x) + scaledChild.pivotOffset.x
    transformComponent.transform.t.y =
        (scaledChild.localPosition.y * parentScale.y) + scaledChild.pivotOffset.y
    transformComponent.transform.t.z =
        (scaledChild.localPosition.z * parentScale.z) + scaledChild.pivotOffset.z
    child.setComponent(transformComponent)
  }

  private fun getScaledChildren(parent: Entity): MutableList<Entity> {
    val children: MutableList<Entity> = mutableListOf()

    val allChildren = Query.where { has(TransformParent.id, ScaledChild.id) }
    for (child in allChildren.eval()) {
      val childParent = child.getComponent<TransformParent>().entity
      if (parent == childParent) {
        children.add(child)
      }
    }
    return children
  }

  fun forceUpdateChildren(parent: Entity) {
    val parentScale = parent.getComponent<Scale>().scale
    val children = getScaledChildren(parent)
    for (child in children) {
      updateChildTransform(child, child.getComponent<ScaledChild>(), parentScale)
    }
  }
}
