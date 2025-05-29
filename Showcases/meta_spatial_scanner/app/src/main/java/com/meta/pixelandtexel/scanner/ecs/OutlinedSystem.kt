// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.ecs

import com.meta.pixelandtexel.scanner.Outlined
import com.meta.pixelandtexel.scanner.R
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector4
import com.meta.spatial.runtime.BlendMode
import com.meta.spatial.runtime.DepthTest
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMaterialAttribute
import com.meta.spatial.runtime.SceneMaterialDataType
import com.meta.spatial.runtime.SceneMesh
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.runtime.SortOrder
import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.uiset.theme.SpatialColor

/**
 * System that creates and manages 9-slice outlines for entities. Observes entities with an
 * [Outlined] component and generates a quad mesh with a custom 9-slice material to render an
 * outline around them.
 *
 * @param activity The current [AppSystemActivity] context.
 */
class OutlinedSystem(activity: AppSystemActivity) : SystemBase() {
  companion object {
    private const val TAG: String = "OutlinedSystem"

    // material params for the 9-slice shader and texture

    // the size of the input texture
    private val SLICE_TEX_SIZE = Vector2(96f, 96f)
    // the slice size of the image – left, top, right, bottom
    private val SLICE_SIZE = Vector4(32f, 32f, 32f, 32f)
    // the pixels per unit multiplier – increasing this scales the outline width
    private const val PPU_MULTIPLIER = 1.6f // increasing this scales the outline width
  }

  // key is the entity id
  private var outlinedObjects = HashMap<Long, OutlinedObjectInfo>()
  private val outlineDrawable = activity.getDrawable(R.drawable.rounded_box_outline)!!

  /**
   * Queries for entities with an [Outlined] component, and for each such entity, creates and
   * applies an outline mesh and material.
   */
  override fun execute() {
    val query = Query.where { changed(Outlined.id) }
    for (entity in query.eval()) {
      val completable = systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)

      completable?.thenAccept {
        val outlinedComponent = entity.getComponent<Outlined>()
        val scale = Vector3(outlinedComponent.size.x, outlinedComponent.size.y, 1f)

        // setup our mesh and material

        val material = createNewOutlineMaterial(scale.x, scale.y)

        val quadMesh = SceneMesh.quad(Vector3(-0.5f, -0.5f, 0f), Vector3(0.5f, 0.5f, 0f), material)
        it.setSceneMesh(quadMesh, "trackedObjectQuad")

        entity.setComponent(Scale(scale))

        // add to our map of outlined objects

        outlinedObjects[entity.id] =
            OutlinedObjectInfo(
                entity,
                material,
            )
      }
    }
  }

  /**
   * Called when an entity is being deleted from the system. Removes the outline information
   * associated with the entity and destroys its outline material to free up resources.
   *
   * @param entity The entity being deleted.
   */
  override fun delete(entity: Entity) {
    super.delete(entity)

    val removed = outlinedObjects.remove(entity.id)
    removed?.outlineMaterial?.destroy()
  }

  /**
   * Creates a new [SceneMaterial] configured for rendering a 9-slice outline.
   *
   * @param width The width of the quad on which the outline will be rendered.
   * @param height The height of the quad on which the outline will be rendered.
   * @return A new [SceneMaterial] instance for the outline.
   */
  private fun createNewOutlineMaterial(width: Float, height: Float): SceneMaterial {
    return SceneMaterial.custom(
            "9slice",
            arrayOf(
                SceneMaterialAttribute("sliceTex", SceneMaterialDataType.Texture2D),
                SceneMaterialAttribute("sliceParams", SceneMaterialDataType.Vector4),
                SceneMaterialAttribute("sliceSize", SceneMaterialDataType.Vector4),
                SceneMaterialAttribute("tintColor", SceneMaterialDataType.Vector4),
                SceneMaterialAttribute("stereoParams", SceneMaterialDataType.Vector4),
            ))
        .apply {
          setBlendMode(BlendMode.TRANSLUCENT)
          setSortOrder(SortOrder.TRANSLUCENT)
          setDepthTest(DepthTest.ALWAYS)
          setStereoMode(StereoMode.None)

          setAttribute("sliceTex", SceneTexture(outlineDrawable))

          // x = quad width, y = quad height, z = texture width, w = texture height
          setAttribute("sliceParams", Vector4(width, height, SLICE_TEX_SIZE.x, SLICE_TEX_SIZE.y))
          // slice size in order: left, right, top, bottom
          setAttribute("sliceSize", SLICE_SIZE)

          // rgb, a = pixels per unit multiplier
          setAttribute(
              "tintColor",
              Vector4(
                  SpatialColor.b70.red,
                  SpatialColor.b70.green,
                  SpatialColor.b70.blue,
                  PPU_MULTIPLIER))
        }
  }

  /**
   * Data class to hold information about an outlined entity.
   *
   * @property outlineEntity The [Entity] entity that has an outline.
   * @property outlineMaterial The [SceneMaterial] used for rendering the entity's outline.
   */
  private data class OutlinedObjectInfo(
      val outlineEntity: Entity,
      val outlineMaterial: SceneMaterial
  )
}
