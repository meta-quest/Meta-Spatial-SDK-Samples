// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.spinnable

import android.util.Log
import com.meta.pixelandtexel.geovoyage.GrabbableNoRotation
import com.meta.pixelandtexel.geovoyage.Spinnable
import com.meta.pixelandtexel.geovoyage.utils.MathUtils
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.PIf
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.clamp
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.fromAxisAngle
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.pitchAngle
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.yawAngle
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.ControllerType
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.getAbsoluteTransform
import kotlin.math.abs

private data class SpinningInfo(
    // the Entity which represents either the user's hand or held controller –
    // whichever "grabbed" the Spinnable
    val inputSource: Entity,

    // the "grabbed" Spinnable Entity
    val entity: Entity,

    // yaw and pitch offsets in radians of the vectors which represents the
    // direction from the "grabbed" Entity to the controller, and the
    // controller's forward vector
    val initialYawOffset: Float,
    val initialPitchOffset: Float,

    // the initial absolute rotation of the "grabbed" Entity. Used in
    // conjunction with the initial offsets to determine the new rotation
    val initialRotation: Quaternion,

    // cached value indicating whether or not the Entity has a
    // GrabbableNoRotation component also attached to it, so we know to disable
    // it until after the spin grab button is released
    val hasGrabbable: Boolean = false,

    // both used for calculating the rotation speed, and spin inertia for
    // gradually slowing down
    var lastYawOffsetDeg: Float = 0f,
    var yawInertiaDeg: Float = 0f
)

class SpinnableSystem : SystemBase() {
  companion object {
    private const val TAG: String = "SpinnableSystem"

    private const val ROTATION_SPEED: Float = .5f
    private const val MAX_PITCH_RAD: Float = 45f * PIf / 180f
  }

  private val interactionButtons: Int = ButtonBits.ButtonA or ButtonBits.ButtonX

  private val spinnableEntities = HashSet<Entity>()
  private val spinningObjects = HashMap<Long, SpinningInfo>()

  private var lastTime = System.currentTimeMillis()

  override fun execute() {
    val currentTime = System.currentTimeMillis()
    val dt = ((currentTime - lastTime) / 1000f).coerceAtMost(0.1f)
    lastTime = currentTime

    findNewObjects()
    processSpinnables(dt)
  }

  override fun delete(entity: Entity) {
    super.delete(entity)

    spinnableEntities.remove(entity)
    spinningObjects.remove(entity.id)
  }

  private fun findNewObjects() {
    val q = Query.where { has(Spinnable.id, Transform.id) and changed(Spinnable.id) }
    for (entity in q.eval()) {
      if (spinnableEntities.contains(entity)) {
        continue
      }

      spinnableEntities.add(entity)

      val completable =
          systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity) ?: continue

      // add button pressed listener to trigger start spinning
      completable.thenAccept { sceneObject ->
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
                // entity already grabbed
                val spinnable = entity.getComponent<Spinnable>()
                if (spinnable.isGrabbed) {
                  return true
                }

                if (!sourceOfInput.isLocal()) {
                  return true
                }

                if (hitInfo.entity != entity) {
                  return true
                }

                val grabbable = entity.tryGetComponent<GrabbableNoRotation>()
                if (grabbable != null && grabbable.isGrabbed) {
                  return true
                }

                // don't process any eye control
                val controller = sourceOfInput.getComponent<Controller>()
                if (controller.type != ControllerType.CONTROLLER &&
                    controller.type != ControllerType.HAND) {
                  return true
                }

                val buttonsDown: Int = changed and clicked

                // user started pressing down
                if (buttonsDown and interactionButtons != 0) {
                  val sourcePose = getAbsoluteTransform(sourceOfInput)
                  val spinnablePose = getAbsoluteTransform(entity)

                  val v1 = hitInfo.point - sourcePose.t
                  val v2 = spinnablePose.t - sourcePose.t

                  val initialYawOffset = v1.yawAngle(v2)
                  val initialPitchOffset = v1.pitchAngle(v2)
                  val yaw = spinnablePose.q.times(Vector3.Forward).yawAngle(Vector3.Forward)

                  Log.d(TAG, "y: $initialYawOffset, x: $initialPitchOffset")

                  // disable the grabbable, if this entity has one
                  if (grabbable != null) {
                    grabbable.enabled = false
                    entity.setComponent(grabbable)
                  }

                  // flag our spinnable component as active
                  spinnable.isGrabbed = true
                  spinnable.isSpinning = false
                  entity.setComponent(spinnable)

                  // add this to our map of spinning objects
                  spinningObjects[entity.id] =
                      SpinningInfo(
                          sourceOfInput,
                          entity,
                          initialYawOffset,
                          initialPitchOffset,
                          Quaternion(0f, yaw * 180f / PIf, 0f),
                          grabbable != null)
                }

                return true
              }
            })
      }
    }
  }

  private fun processSpinnables(dt: Float) {
    spinningObjects.forEach { (id, info) ->
      // get our objects used for spinning
      val spinnablePose = getAbsoluteTransform(info.entity)
      val spinnable = info.entity.getComponent<Spinnable>()

      val controllerPose = getAbsoluteTransform(info.inputSource)
      val controller = info.inputSource.getComponent<Controller>()

      // spinnable is free-spinning; apply drag and slow to 0
      if (spinnable.isSpinning) {
        // stop spinning if we were just grabbed
        if (info.hasGrabbable) {
          val grabbable = info.entity.getComponent<GrabbableNoRotation>()
          if (grabbable.isGrabbed) {
            info.yawInertiaDeg = 0f
          }
          grabbable.recycle()
        }

        // apply drag and slow to 0 spin
        info.yawInertiaDeg = MathUtils.lerp(info.yawInertiaDeg, 0f, dt * spinnable.drag)

        // stop spinning if our inertia is close enough to 0
        if (abs(info.yawInertiaDeg) < 1f) {
          spinnable.isSpinning = false
          info.entity.setComponent(spinnable)

          spinningObjects.remove(id)
        }
        // apply our inertia
        else {
          // calculate our yaw delta to add to our current rotation
          val yawDelta = Quaternion.fromAxisAngle(-Vector3.Up, info.yawInertiaDeg * dt)

          spinnablePose.q = spinnablePose.q.times(yawDelta)
          info.entity.setComponent(Transform(spinnablePose))
        }
      }

      if (spinnable.isGrabbed) {
        // calculate our current yaw offset and delta
        val v1 = controllerPose.q.times(Vector3.Forward)
        val v2 = spinnablePose.t - controllerPose.t
        val yawOffset = v1.yawAngle(v2)
        val yawDelta = yawOffset - info.initialYawOffset

        // calculate our current pitch offset
        val pitchOffset = v1.pitchAngle(v2)
        val pitchDelta = (pitchOffset - info.initialPitchOffset) * 2f
        val newPitch = (spinnable.pitch + pitchDelta).clamp(-MAX_PITCH_RAD, MAX_PITCH_RAD)

        /**
         * Find our "normalized" yaw delta, which is the yaw as a ratio of the arc length of the
         * controller rotation with respect to the distance between the spinnable and the
         * controller, divided by the diameter of the spinnable. Using this makes the rotation a
         * constant as opposed to dependent on the distance from the controller to the earth.
         */
        val yawDeltaArcLength = yawDelta * v2.length()
        val normalizedYawDelta = yawDeltaArcLength / spinnable.size

        // compute our final rotation offset, multiplying with an arbitrary rotation speed
        // scalar, and converting to degrees
        var yawOffsetDeg = normalizedYawDelta * PIf * ROTATION_SPEED
        yawOffsetDeg *= 180f / PIf

        // calculate our new rotation, offset from the initial rotation
        val newOffsetRotation = Quaternion.fromAxisAngle(-Vector3.Up, yawOffsetDeg)
        val newYawRotation = info.initialRotation.times(newOffsetRotation)

        // transform our pitch axis into local space, and calculate the new pitch rotation
        var pitchAxis = v2.cross(Vector3.Up)
        pitchAxis = newYawRotation.inverse().times(pitchAxis)
        val pitchRotation = Quaternion.fromAxisAngle(pitchAxis, newPitch * 180f / PIf)

        // apply the new rotation – add the new yaw and pitch
        spinnablePose.q = newYawRotation.times(pitchRotation)
        info.entity.setComponent(Transform(spinnablePose))

        // user just released spin
        if (controller.buttonState and interactionButtons == 0 &&
            controller.changedButtons and interactionButtons != 0) {
          // save our framerate-independent yaw delta to our info
          info.yawInertiaDeg = (yawOffsetDeg - info.lastYawOffsetDeg) / dt

          // update our spinnable state
          spinnable.isGrabbed = false
          spinnable.isSpinning = true
          spinnable.pitch = newPitch
          info.entity.setComponent(spinnable)

          // re-enable our grabbable, if we have one
          if (info.hasGrabbable) {
            val grabbable = info.entity.getComponent<GrabbableNoRotation>()
            grabbable.enabled = true
            info.entity.setComponent(grabbable)
          }
        }

        info.lastYawOffsetDeg = yawOffsetDeg
      }

      // clean up
      spinnable.recycle()
      controller.recycle()
    }
  }
}
