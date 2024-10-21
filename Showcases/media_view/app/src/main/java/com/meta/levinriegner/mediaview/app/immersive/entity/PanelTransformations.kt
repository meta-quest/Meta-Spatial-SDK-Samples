// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.entity

import android.os.Handler
import android.os.Looper
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemManager
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.PanelConfigOptions
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import timber.log.Timber

class PanelTransformations(
    private val environmentEntities: EnvironmentEntities,
    private val systemManager: SystemManager,
) {

  fun applyTransform(
      entity: Entity,
      distance: Float,
      positionOffset: Vector3,
      applyTilt: Boolean = false
  ) {
    val headPose = environmentEntities.getHeadPose()

    val forward = headPose.q * Vector3(0.0f, 0.0f, 1.0f)
    val forwardXZ =
        Vector3(forward.x, 0.0f, forward.z)
            .normalize() // Project forward direction onto the XZ plane to ignore head tilt
    val position = headPose.t + forwardXZ * distance

    // Calculate rotation only on the Y-axis
    val lookAtDirection =
        Vector3(position.x - headPose.t.x, position.y - headPose.t.y, position.z - headPose.t.z)
            .normalize()
    val yRotation = Quaternion.lookRotation(lookAtDirection)

    val finalRotation =
        if (applyTilt) {
          val tiltAngleDegrees = 45.0f
          val tiltRotation = Quaternion(tiltAngleDegrees, 0.0f, 0.0f)
          yRotation * tiltRotation
        } else {
          yRotation
        }

    // Apply position offset in the panel's local space
    val panelForward = finalRotation * Vector3(0.0f, 0.0f, 1.0f)
    val panelRight = finalRotation * Vector3(1.0f, 0.0f, 0.0f)
    val panelUp = Vector3(0.0f, 1.0f, 0.0f)
    val adjustedPosition =
        position +
            panelForward * positionOffset.z +
            panelRight * positionOffset.x +
            panelUp * positionOffset.y

    entity.setComponent(Transform(Pose(adjustedPosition, finalRotation)))
  }

  fun setPanelVisibility(panel: Entity?, isVisible: Boolean) {
    val sceneObjectSystem = systemManager.findSystem<SceneObjectSystem>()
    panel?.let {
      sceneObjectSystem.getSceneObject(it)?.thenAccept { sceneObject ->
        if (sceneObject is PanelSceneObject) {
          sceneObject.setIsVisible(isVisible)
        } else {
          Timber.w("SceneObject is not a PanelSceneObject for entity id ${it.id}")
        }
      } ?: Timber.w("SceneObject not found for entity id ${it.id}")
    }
  }

  fun applyNewPanelConfiguration(entity: Entity, config: PanelConfigOptions) {
    val sceneObjectSystem = systemManager.findSystem<SceneObjectSystem>()
    val sceneObjectFuture = sceneObjectSystem.getSceneObject(entity)
    sceneObjectFuture?.thenAccept { sceneObject ->
      if (sceneObject is PanelSceneObject) {
        sceneObject.reshape(config)
      } else {
        Timber.w("SceneObject is not a PanelSceneObject for entity id ${entity.id}")
      }
    } ?: Timber.w("Failed to retrieve SceneObject for entity id ${entity.id}")
  }

  fun setGrabbable(entity: Entity, enabled: Boolean, type: GrabbableType? = null) {
    entity.setComponent(Grabbable(enabled, type = type ?: GrabbableType.FACE))
  }
}
