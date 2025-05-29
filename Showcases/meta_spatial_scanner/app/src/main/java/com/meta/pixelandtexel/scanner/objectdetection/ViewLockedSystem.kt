// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection

import android.util.Log
import com.meta.pixelandtexel.scanner.ViewLocked
import com.meta.pixelandtexel.scanner.objectdetection.camera.models.CameraProperties
import com.meta.pixelandtexel.scanner.objectdetection.math.MathUtils
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform

/**
 * Manages entities that are "view-locked," meaning their position and orientation are relative to
 * the user's head pose. This system ensures these entities remain in a fixed position within the
 * user's field of view, optionally adjusting their distance to fill the view if they are panels.
 *
 * @property fov The current field of view of the camera in degrees. Defaults to 72f.
 */
class ViewLockedSystem(private var fov: Float = 72f) : SystemBase() {
  companion object {
    private const val TAG: String = "ViewLockedSystem"
  }

  // key is the entity id
  private var viewLockedEntities = HashMap<Long, ViewLockedInfo>()

  override fun execute() {
    findNewEntities()
    processEntities()
  }

  /**
   * Queries the scene for new entities that have a [ViewLocked] component and a [Transform]
   * component. For each new entity, it retrieves its scene object and initializes its
   * [ViewLockedInfo]. If the entity has a [Panel] component and `fillView` is true in its
   * [ViewLocked] component, it calculates the appropriate distance to make the panel fill the view.
   */
  private fun findNewEntities() {
    val q = Query.where { has(ViewLocked.id, Transform.id) and changed(ViewLocked.id) }
    for (entity in q.eval()) {
      if (viewLockedEntities.contains(entity.id)) {
        continue
      }

      Log.d(TAG, "Found view locked entity ${entity.id}")

      val completable =
          systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity) ?: continue

      completable.thenAccept { sceneObject ->
        val viewLockedComp = entity.getComponent<ViewLocked>()

        var distance = 0f
        var panelWidth = 0f

        if (entity.hasComponent<Panel>() && viewLockedComp.fillView) {
          val panelSceneObject = sceneObject as PanelSceneObject
          val shapeConfig = panelSceneObject.getPanelShapeConfig()

          panelWidth = shapeConfig!!.width
          distance = MathUtils.panelDistanceForSize(fov, shapeConfig.width)
        }

        // initialize our view locked info
        viewLockedEntities[entity.id] = ViewLockedInfo(entity, panelWidth, distance)
      }
    }
  }

  /**
   * Processes all currently tracked view-locked entities, retrieving the user's head transform and
   * updating each entity's transform to be relative to the head pose.
   */
  private fun processEntities() {
    val headPose = getScene().getViewerPose()

    viewLockedEntities.forEach { (_, info) ->
      val viewLockedComp = info.entity.getComponent<ViewLocked>()

      val quat =
          Quaternion(
              viewLockedComp.rotation.x, viewLockedComp.rotation.y, viewLockedComp.rotation.z)
      val newPose = headPose.times(Pose(viewLockedComp.position, quat))
      newPose.t += newPose.forward() * info.distance

      info.entity.setComponent(Transform(newPose))
    }
  }

  /**
   * Called when the camera configuration changes, and updates camera-related properties used by the
   * system. Updates the system's field of view (FOV) and recalculates the distance for view- locked
   * entities that are configured to fill the view. It also updates the position and rotation
   * offsets based on the new head-to-camera pose if `fillView` is enabled.
   *
   * @param properties The new [CameraProperties] containing FOV, head-to-camera offset, and
   *   screen-to-ray conversion functions.
   */
  fun onCameraPropertiesChanged(properties: CameraProperties) {
    fov = properties.fov

    // update each ViewLockedInfo distance with the calculated value using the new fov

    viewLockedEntities.forEach { (_, info) ->
      val viewLockedComp = info.entity.getComponent<ViewLocked>()

      // recalculate the z offset with the new fov
      if (viewLockedComp.fillView) {
        // use the new pose offset values
        val offsetPose = properties.getHeadToCameraPose()
        viewLockedComp.position = offsetPose.t
        viewLockedComp.rotation = offsetPose.q.toEuler()

        info.entity.setComponent(viewLockedComp)
        info.distance = MathUtils.panelDistanceForSize(fov, info.panelWidth)
      }
    }
  }

  /**
   * Data class to store information about a view-locked entity.
   *
   * @property entity The view-locked [Entity] itself.
   * @property panelWidth The width of the panel, if the entity is a panel and `fillView` is true.
   *   Used for FOV-based distance calculations. Defaults to 0f.
   * @property distance The calculated or specified distance from the head to the entity. Can be
   *   dynamically updated, for example, when FOV changes.
   */
  private data class ViewLockedInfo(val entity: Entity, val panelWidth: Float, var distance: Float)
}
