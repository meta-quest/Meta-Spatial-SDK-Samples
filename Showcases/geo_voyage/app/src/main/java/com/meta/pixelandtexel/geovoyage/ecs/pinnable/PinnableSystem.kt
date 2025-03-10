// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.pinnable

import android.util.Log
import com.meta.pixelandtexel.geovoyage.Pinnable
import com.meta.pixelandtexel.geovoyage.activities.MainActivity
import com.meta.pixelandtexel.geovoyage.models.GeoCoordinates
import com.meta.pixelandtexel.geovoyage.utils.MathUtils
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.lengthSq
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
import com.meta.spatial.toolkit.GLXFManager
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.getAbsoluteTransform

class PinnableSystem(private val glxfManager: GLXFManager) : SystemBase() {
  companion object {
    private const val TAG: String = "PinnableSystem"
  }

  private var pinnableEntities = HashSet<Entity>()
  private var pinEntity: Entity? = null

  private var isPinningEnabled: Boolean = false

  private var lastTime = System.currentTimeMillis()

  private var interactionButtons: Int = ButtonBits.ButtonTriggerL or ButtonBits.ButtonTriggerR
  private var pinTargetScale: Vector3 = Vector3(1f)

  override fun execute() {
    // Doing in execute instead of init to ensure scene has loaded first
    if (pinEntity == null) {
      pinEntity = glxfManager.getGLXFInfo("scene").getNodeByName("pin").entity
    }

    // get our delta time
    val currentTime = System.currentTimeMillis()
    val dt = ((currentTime - lastTime) / 1000f).coerceAtMost(0.1f)
    lastTime = currentTime

    findNewObjects()

    // interpolate our pin scale to the target
    if (pinEntity != null) {
      val scaleComp = pinEntity!!.getComponent<Scale>()

      if ((pinTargetScale - scaleComp.scale).lengthSq() > 0.0001f) {
        val newScale = scaleComp.scale.lerp(pinTargetScale, MathUtils.smoothOver(dt, 0.15f))
        val newScaleComp = Scale(newScale)

        pinEntity!!.setComponent(newScaleComp)
        newScaleComp.recycle()
      }

      scaleComp.recycle()
    }
  }

  override fun delete(entity: Entity) {
    super.delete(entity)

    pinnableEntities.remove(entity)
  }

  private fun findNewObjects() {
    //        // get our pin entity
    //        val pin = Query.where { changed(Mesh.id) and has(Mesh.id, Transform.id) }
    //            .eval()
    //            .filter { it.id == R.integer.pin_id.toLong() }
    //            .firstOrNull()
    //        if (pin != null) {
    //            pinEntity = pin
    //        }

    // get any new pinnable entities
    val q = Query.where { changed(Pinnable.id, Mesh.id) and has(Pinnable.id) }
    for (entity in q.eval()) {
      if (pinnableEntities.contains(entity)) {
        continue
      }

      val systemObject =
          systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity) ?: continue

      // add button clicked listener to spawn pin on pinnable
      systemObject.thenAccept { so ->
        so.addInputListener(
            object : InputListener {
              override fun onInput(
                  receiver: SceneObject,
                  hitInfo: HitInfo,
                  sourceOfInput: Entity,
                  changed: Int,
                  clicked: Int,
                  downTime: Long
              ): Boolean {
                if (hitInfo.entity != entity) {
                  return true
                }

                val anyButtonDown: Int = changed and clicked
                if (clicked == 0 && changed == 0) {
                  return true
                }

                if (pinEntity == null) {
                  return true
                }

                if ((anyButtonDown and interactionButtons) != 0 && isPinningEnabled) {
                  // point to lat and long
                  val pinnablePose = getAbsoluteTransform(entity)
                  val pinnableToHitPoint = (hitInfo.point - pinnablePose.t).normalize()

                  // report the latitude/longitude
                  val rotatedHitPoint = pinnablePose.inverse().q.times(pinnableToHitPoint)
                  val coords =
                      GeoCoordinates.fromCartesianCoords(
                          rotatedHitPoint.x, rotatedHitPoint.y, rotatedHitPoint.z)
                  Log.d(TAG, "pinned ${coords.toCommonNotation()}")
                  MainActivity.instance.get()?.userDroppedPin(coords)

                  // create the new position/rotation, then transform to the pinnable
                  // entity's local coordinates
                  val worldPose =
                      Pose(
                          hitInfo.point,
                          Quaternion.lookRotation(pinnableToHitPoint)
                              .times(Quaternion(90f, 0f, 0f)))
                  val localPose =
                      entity.getComponent<Transform>().transform.inverse().times(worldPose)
                  val newTransform = Transform(localPose)
                  pinEntity!!.setComponent(newTransform)

                  // make sure our pin is visible
                  val visibleComponent = Visible(true)
                  pinEntity!!.setComponent(visibleComponent)

                  // set the scale to 0 so it scales up in execute
                  val scaleComp = Scale(0f)
                  pinEntity!!.setComponent(scaleComp)

                  newTransform.recycle()
                  visibleComponent.recycle()
                  scaleComp.recycle()
                }

                return true
              }
            })

        pinnableEntities.add(entity)
      }
    }
  }

  fun togglePinning(enabled: Boolean) {
    pinEntity?.setComponent(Visible(enabled))

    isPinningEnabled = enabled
  }

  fun hidePin() {
    pinEntity?.setComponent(Visible(false))
  }

  fun resetPin() {
    pinEntity?.setComponent(Transform(Pose(Vector3(0f))))
  }
}
