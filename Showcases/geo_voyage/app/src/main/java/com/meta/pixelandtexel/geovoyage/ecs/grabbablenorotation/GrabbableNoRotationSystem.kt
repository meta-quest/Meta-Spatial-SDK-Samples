// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.grabbablenorotation

import com.meta.pixelandtexel.geovoyage.GrabbableNoRotation
import com.meta.pixelandtexel.geovoyage.utils.MathUtils
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.getAbsoluteTransform

private class GrabInfo(
    val inputSource: Entity,
    val grabbedEntity: Entity,
    val grabbedDistance: Float,
    val grabbedLocalOffset: Vector3,
)

class GrabbableNoRotationSystem : SystemBase() {

  // default is just grabbing with left hand's buttons
  var grabButtons: Int = ButtonBits.ButtonSqueezeR

  private var lastTime = System.currentTimeMillis()
  private val grabbingInfo = HashMap<Long, GrabInfo>()
  private var entitiesWithListener = HashSet<Entity>()

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    // clamp the max dt if the interpolation is too large
    val dt = ((currentTime - lastTime) / 1000f).coerceAtMost(0.1f)
    lastTime = currentTime

    findNewObjects()
    processGrabbable(dt)
  }

  private fun findNewObjects() {
    val meshQuery = Query.where { changed(Mesh.id) and has(GrabbableNoRotation.id) }
    for (entity in meshQuery.eval()) {
      entitiesWithListener.remove(entity)
    }

    val q = Query.where { changed(GrabbableNoRotation.id, Mesh.id) and has(GrabbableNoRotation.id) }
    for (entity in q.eval()) {
      val completable =
          systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity) ?: continue

      if (entitiesWithListener.contains(entity)) {
        continue
      }

      completable.thenAccept { sceneObject ->
        entitiesWithListener.add(entity)

        sceneObject.addInputListener(
            object : InputListener {
              override fun onInput(
                  receiver: SceneObject,
                  hitInfo: HitInfo,
                  sourceOfInput: Entity,
                  changed: Int,
                  clicked: Int,
                  downTime: Long
              ): Boolean {
                val anyButtonDown: Int = changed and clicked

                if ((anyButtonDown and grabButtons) != 0) {
                  val grabbed = grabbingInfo[entity.id]
                  if (grabbed != null) {
                    return true
                  }

                  val grabbable = entity.getComponent<GrabbableNoRotation>()
                  if (!grabbable.enabled) {
                    return true
                  }

                  val grabbedTransform = getAbsoluteTransform(entity)
                  grabbedTransform.q = Quaternion(1f, 0f, 0f, 0f)

                  // assess which side of the panel the viewer is
                  // our goal is not to rotate the object more than needed
                  // this may become a Grabbable object configuration
                  if (!sourceOfInput.hasComponent<Controller>()) {
                    return true
                  }

                  // Get controller forward
                  // use current distance as the offset + controller position
                  grabbingInfo[entity.id] =
                      GrabInfo(
                          sourceOfInput,
                          entity,
                          hitInfo.distance,
                          grabbedTransform.inverse() * hitInfo.point,
                      )

                  // flag the grabbable as grabbed
                  grabbable.isGrabbed = true
                  entity.setComponent(grabbable)
                }

                return true
              }
            })
      }
    }
  }

  private fun processGrabbable(dt: Float) {
    grabbingInfo.forEach { (id, info) ->
      val controller = info.inputSource.tryGetComponent<Controller>() ?: return
      val anyButtonReleased: Int = controller.changedButtons and (controller.buttonState.inv())

      if ((anyButtonReleased and grabButtons) != 0) {
        grabbingInfo.remove(id)

        // flag our grabbable as not grabbed anymore
        val grabbable = info.grabbedEntity.getComponent<GrabbableNoRotation>()
        grabbable.isGrabbed = false
        info.grabbedEntity.setComponent(grabbable)
        grabbable.recycle()

        return
      }

      val transform = info.inputSource.getComponent<Transform>()
      val newTranslation = transform.transform * Vector3(0f, 0f, info.grabbedDistance)

      var grabbedTransform = getAbsoluteTransform(info.grabbedEntity)
      val grabbable = info.grabbedEntity.getComponent<GrabbableNoRotation>()

      // After grabbing the object, we want to rotate the object to face the user, especially for
      // the panels

      val worldOffset = info.grabbedLocalOffset
      // 0.15f is an interpolation rate found to be smooth enough but not too floaty
      val interpolationRate = 0.15f
      grabbedTransform =
          grabbedTransform.lerp(
              Pose(t = newTranslation - worldOffset, q = grabbedTransform.q),
              MathUtils.smoothOver(dt, interpolationRate))

      if (info.grabbedEntity.hasComponent<TransformParent>()) {
        val transformParentComponent = info.grabbedEntity.getComponent<TransformParent>()
        val parent = transformParentComponent.entity
        val parentTransform = getAbsoluteTransform(parent)

        grabbedTransform = parentTransform.inverse() * grabbedTransform

        transformParentComponent.recycle()
      }

      info.grabbedEntity.setComponent(Transform(grabbedTransform))

      controller.recycle()
      transform.recycle()
      grabbable.recycle()
    }
  }

  override fun delete(entity: Entity) {
    entitiesWithListener.remove(entity)
  }
}
