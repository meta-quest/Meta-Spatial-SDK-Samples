// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services

import android.content.Context
import android.content.res.XmlResourceParser
import android.net.Uri
import android.util.Log
import com.meta.pixelandtexel.scanner.Outlined
import com.meta.pixelandtexel.scanner.models.CuratedObject
import com.meta.pixelandtexel.scanner.models.ImageCopyPanelContent
import com.meta.pixelandtexel.scanner.models.PanelContentBase
import com.meta.pixelandtexel.scanner.models.PanelContentType
import com.meta.pixelandtexel.scanner.models.TileContent
import com.meta.pixelandtexel.scanner.models.TilesPanelContent
import com.meta.pixelandtexel.scanner.utils.MathUtils.fromAxisAngle
import com.meta.pixelandtexel.scanner.utils.MathUtils.isValid
import com.meta.spatial.core.Bound3D
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.toolkit.Animated
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Hittable
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.Named
import com.meta.spatial.toolkit.PlaybackState
import com.meta.spatial.toolkit.PlaybackType
import com.meta.spatial.toolkit.Quad
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import kotlin.math.max
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser

/**
 * Manages curated 3D objects, including their loading from XML, display, interaction, and
 * selection. This class handles the lifecycle of curated objects, from parsing their definitions to
 * presenting them to the user for selection and notifying a listener upon selection.
 *
 * @property context The Android application context.
 * @property onSelectedCuratedObjectFromSelection A callback function invoked when a curated object
 *   is selected by the user. Provides the selected [CuratedObject] and its [Pose].
 * @property xmlId The optional resource ID of an XML file defining curated objects. If provided,
 *   objects are loaded during initialization.
 */
class CuratedObjectHandler(
    context: Context,
    private val onSelectedCuratedObjectFromSelection: (CuratedObject, Pose) -> Unit,
    xmlId: Int? = null,
) {
  companion object {
    private const val TAG = "CuratedObjectHandler"
  }

  private val curatedObjects = HashMap<String, CuratedObject>()
  private val pendingObjectEntities = HashMap<String, String>()
  private val curatedObjectEntities: List<Entity>
    get() = curatedObjects.values.sortedBy { it.order }.mapNotNull { it.meshEntity }

  private var viewingCuratedObjectSelection = false
  private var outlineEntities = mutableListOf<Entity>()

  init {
    if (xmlId != null) {
      context.resources.getXml(xmlId).use { // auto close
        loadObjectData(it, context)
      }
      Log.d(TAG, "Parsed ${curatedObjects.size} curated objects from xml")
    }
  }

  /**
   * Retrieves and configures the mesh entities for curated objects based on a GLXF composition.
   * This function matches object names to entity names in the composition, sets up input listeners
   * for selection, and caches animation and bounds information.
   *
   * @param activity The current [AppSystemActivity] to access system managers.
   * @param composition The [GLXFInfo] containing the scene graph and nodes.
   */
  fun getObjectMeshEntities(activity: AppSystemActivity, composition: GLXFInfo) {
    pendingObjectEntities.forEach { (name, entityName) ->
      val node = composition.tryGetNodeByName(entityName)
      val entity = node?.entity ?: return@forEach

      curatedObjects[name]!!.meshEntity = entity

      entity.setComponent(Named(name))

      // add an input listener to report to the main activity when a selection has been made

      activity.systemManager.findSystem<SceneObjectSystem>().let { system ->
        val completable = system.getSceneObject(entity)
        completable?.thenAccept { sceneObject ->
          sceneObject.addInputListener(
              object : InputListener {
                override fun onInput(
                    receiver: SceneObject,
                    hitInfo: HitInfo,
                    sourceOfInput: Entity,
                    changed: Int,
                    buttonState: Int,
                    downTime: Long,
                ): Boolean {
                  val selectButtons: Int =
                      ButtonBits.ButtonA or
                          ButtonBits.ButtonX or
                          ButtonBits.ButtonTriggerR or
                          ButtonBits.ButtonTriggerL or
                          ButtonBits.ButtonSqueezeR or
                          ButtonBits.ButtonSqueezeL

                  // one of the select buttons, triggers, or squeeze buttons was pushed
                  if ((selectButtons and buttonState and changed) != 0) {
                    selectedCuratedObject(receiver.entity!!)
                  }

                  return true
                }
              })

          // cache our animation track info

          sceneObject.mesh?.let {
            curatedObjects[name]!!.animationNameToTrack = it.animationNameToTrack

            // there was no bounds provided in the xml; compute it from the mesh
            if (curatedObjects[name]!!.meshBounds == null) {
              var bounds = it.computeCombinedBounds()
              curatedObjects[name]!!.meshBounds =
                  if (bounds.isValid()) bounds
                  else {
                    Log.w(TAG, "Failed to compute valid bounds for object $name")
                    Bound3D() // default to empty
                  }
            }
          }
        }
      }
    }

    hideCuratedObjects()
  }

  /**
   * Checks if a given label corresponds to any known curated object.
   *
   * @param label The label string to check.
   * @return True if a curated object matches the label, false otherwise.
   */
  fun isCuratedObject(label: String): Boolean {
    return findMatchingCuratedObject(label) != null
  }

  /**
   * Retrieves the [CuratedObject] information for a given label. Assumes that a matching object
   * exists.
   *
   * @param label The label string to find the object for.
   * @return The [CuratedObject] associated with the label.
   * @throws NullPointerException if no matching object is found.
   */
  fun getObjectInfo(label: String): CuratedObject {
    val info = findMatchingCuratedObject(label)
    return info!!
  }

  /**
   * Finds the first [CuratedObject] whose `matchingLabels` contain the given label. The comparison
   * is case-insensitive.
   *
   * @param label The label to search for.
   * @return The matching [CuratedObject], or null if no match is found.
   */
  private fun findMatchingCuratedObject(label: String): CuratedObject? {
    return curatedObjects.values.firstOrNull { obj ->
      obj.matchingLabels.any { label.contains(it, true) }
    }
  }

  /**
   * Spawns the curated objects in a radial arrangement in front of the user for selection. Objects
   * are made visible, hittable, and outlines are created for them.
   *
   * @param spawnZDistance The distance in front of the user at which to spawn the objects.
   * @param spacingAngleDeg The angular spacing in degrees between each spawned object.
   */
  fun spawnCuratedObjectsForSelection(spawnZDistance: Float = 1.5f, spacingAngleDeg: Float = 40f) {
    if (!curatedObjectEntities.any()) {
      Log.w(TAG, "No curated object mesh entities to spawn")
      return
    }

    val headEntity =
        Query.where { has(AvatarAttachment.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
            .first()
    val headTransform = headEntity.getComponent<Transform>().transform
    // apply offset to lower the panel to eye height
    val headPosition = headTransform.t - Vector3(0f, 0.1f, 0f)

    // zero out the head forward so it's eye level
    val headForwardYaw = (headTransform.forward() * Vector3(1f, 0f, 1f)).normalize()

    val numEntities = curatedObjectEntities.size
    curatedObjectEntities.forEachIndexed { i, entity ->
      val name = entity.getComponent<Named>().name
      val curatedObject = curatedObjects[name]!!

      // space out the objects radially in front of the users at eye level
      val angle0 = numEntities / 2f - 0.5f
      val angleI = -angle0 * spacingAngleDeg + i * spacingAngleDeg
      val position =
          headPosition +
              (Quaternion.lookRotationAroundY(headForwardYaw)
                  .times(Quaternion.fromAxisAngle(Vector3.Up, angleI))
                  .times(Vector3.Forward)) * spawnZDistance

      // have the objects face the user
      val rotation = Quaternion.lookRotationAroundY(position - headPosition)

      entity.setComponent(Transform(Pose(position, rotation)))
      entity.setComponent(Hittable(MeshCollision.LineTest))
      entity.setComponent(Visible(true))

      // spawn an outline object entity for each

      val bounds = curatedObject.meshBounds ?: Bound3D() // default to empty
      val size = bounds.size() + Vector3(0.1f)

      outlineEntities.add(
          Entity.create(
              Transform(Pose(position, rotation)),
              Scale(Vector3(size.x, size.y, 1f)),
              Outlined(Vector2(size.x, size.y)),
              Mesh(Uri.parse("mesh://quad")),
              Quad(),
              Material(),
              Hittable(MeshCollision.NoCollision),
          ))
    }

    viewingCuratedObjectSelection = true
  }

  /**
   * Dismisses the curated object selection view, hiding the curated objects (except for an optional
   * specified one) and destroys their outline entities.
   *
   * @param except An optional [Entity] to exclude from being hidden. Typically, this is the
   *   selected object.
   */
  fun dismissCuratedObjectsSelection(except: Entity? = null) {
    if (!viewingCuratedObjectSelection) {
      return
    }

    // hide the curated objects again

    hideCuratedObjects(except)
    viewingCuratedObjectSelection = false

    // destroy our outline entities

    while (outlineEntities.any()) {
      val ent = outlineEntities.removeAt(0)
      ent.destroy()
    }
  }

  /**
   * Handles the selection of a curated object by the user, dismissing the selection view and
   * invoking the `onSelectedCuratedObjectFromSelection` callback. Calculates an appropriate [Pose]
   * pose for displaying information related to the selected object.
   *
   * @param entity The [Entity] of the selected curated object.
   */
  private fun selectedCuratedObject(entity: Entity) {
    if (!viewingCuratedObjectSelection) {
      return
    }

    dismissCuratedObjectsSelection(entity)

    // report to MainActivity to show the info panel and 3D model

    val name = entity.getComponent<Named>().name
    val curatedObject = curatedObjects[name]!!

    val headEntity =
        Query.where { has(AvatarAttachment.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
            .first()
    val headPosition = headEntity.getComponent<Transform>().transform.t

    val pose = entity.getComponent<Transform>().transform

    val bounds =
        curatedObject.meshBounds ?: Bound3D(-Vector3(0.5f), Vector3(0.5f)) // default to unit cube

    // construct a pose so that the position is the head position, and the
    // rotation is the direction vector from the head to the right edge of the
    // curated object in the user's view, at eye level (no pitch or roll)

    val vHeadToObject = (pose.t - headPosition).normalize()
    val vObjectRight = vHeadToObject.cross(-Vector3.Up)

    val position = pose.t + vObjectRight * max(bounds.max.x, bounds.max.z)

    position.y = headPosition.y

    val rotation = Quaternion.lookRotationAroundY(position - headPosition)

    onSelectedCuratedObjectFromSelection(curatedObject, Pose(headPosition, rotation))
  }

  /**
   * Hides all curated object entities, optionally excluding one, calling calls
   * [dismissCuratedObject] for each entity to be hidden.
   *
   * @param except An optional [Entity] to keep visible.
   */
  private fun hideCuratedObjects(except: Entity? = null) {
    curatedObjectEntities.forEach {
      if (except != null && it.id == except.id) {
        return@forEach
      }

      dismissCuratedObject(it)
    }
  }

  /**
   * Dismisses a single curated object entity by making it invisible, non-hittable, moving it out of
   * view, and pausing its animations.
   *
   * @param entity The [Entity] of the curated object to dismiss.
   */
  fun dismissCuratedObject(entity: Entity) {
    val name = entity.tryGetComponent<Named>()?.name ?: return
    val curatedObject = curatedObjects[name] ?: return

    entity.setComponent(Visible(false))
    entity.setComponent(Hittable(MeshCollision.NoCollision))
    entity.setComponent(Transform(Pose(t = Vector3(0f, -100f, 0f))))

    // pause any animations, if applicable

    if (entity.hasComponent<Animated>()) {
      entity.setComponent(
          Animated(
              startTime = System.currentTimeMillis(),
              pausedTime = 0f,
              playbackState = PlaybackState.PAUSED,
              playbackType = PlaybackType.CLAMP,
              curatedObject.initialAnimationTrack ?: 0,
          ))
    }
  }

  /**
   * Enables a curated object entity, making it visible and interactive, pausing any animations at
   * their initial state and re-enables [Hittable]. A small delay is used before enabling [Hittable]
   * as a workaround for an ISDK bug.
   *
   * @param entity The [Entity] of the curated object to enable.
   */
  fun enableCuratedObject(entity: Entity) {
    val name = entity.tryGetComponent<Named>()?.name ?: return
    val curatedObject = curatedObjects[name] ?: return

    entity.setComponent(Visible(true))

    // pause any animations, if applicable

    if (entity.hasComponent<Animated>()) {
      entity.setComponent(
          Animated(
              startTime = System.currentTimeMillis(),
              pausedTime = 0f,
              playbackState = PlaybackState.PAUSED,
              playbackType = PlaybackType.CLAMP,
              curatedObject.initialAnimationTrack ?: 0,
          ))
    }

    // resets the internal state of the entity collider
    entity.setComponent(Hittable(MeshCollision.NoCollision))
    // NOTE adding a delay works around a crash bug which should be fixed in the next patch
    CoroutineScope(Dispatchers.Main)
        .launch { delay(100L) }
        .invokeOnCompletion { entity.setComponent(Hittable(MeshCollision.LineTest)) }
  }

  /**
   * Loads curated object data from an XML resource, parsing the XML to define [CuratedObject]
   * instances, including their names, matching labels, mesh entity information, UI panel content,
   * and animation details.
   *
   * @param parser The [XmlResourceParser] for the XML file.
   * @param context The Android application [Context] used to resolve resources like strings and
   *   drawables.
   */
  private fun loadObjectData(parser: XmlResourceParser, context: Context) {
    // temp variables per curated object
    var currentObjectName: String? = null
    var currentMatchingLabels: MutableList<String>? = null
    var currentMeshEntityName: String? = null
    var currentMeshPositionOffset: Vector3? = null
    var currentMeshRotationOffset: Quaternion? = null
    var currentMeshBounds: Bound3D? = null
    var currentMeshInitialAnimationTrack: Int? = null
    var currentUiPanels: MutableList<PanelContentBase>? = null

    // variables for parsing a specific panel

    var currentPanelTitle: String? = null
    var currentPanelLayout: PanelContentType = PanelContentType.UNKNOWN
    var currentPanelAnimationTrack: Int? = null
    var currentPanelTiles: MutableList<TileContent>? = null
    var currentPanelImageResId: Int? = null
    var currentPanelCopyResId: Int? = null

    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
      val tagName = parser.name

      when (eventType) {
        XmlPullParser.START_TAG -> {
          when (tagName) {
            "object" -> {
              currentObjectName = parser.getAttributeValue(null, "name")
              assert(currentObjectName != null)

              // reset
              currentMatchingLabels = mutableListOf()
              currentMeshEntityName = null
              currentMeshPositionOffset = null
              currentMeshRotationOffset = null
              currentMeshBounds = null
              currentMeshInitialAnimationTrack = null
              currentUiPanels = mutableListOf()
            }

            "label" -> {
              assert(parser.next() == XmlPullParser.TEXT)
              currentMatchingLabels?.add(parser.text)
            }

            "mesh-entity" -> {
              currentMeshEntityName = parser.getAttributeValue(null, "name")
              assert(currentMeshEntityName != null)

              val positionOffsetStr = parser.getAttributeValue(null, "positionOffset")
              val positionOffsetArray = positionOffsetStr?.split(",".toRegex(), 3)
              positionOffsetArray?.let {
                val x = it[0].toFloat()
                val y = it[1].toFloat()
                val z = it[2].toFloat()
                currentMeshPositionOffset = Vector3(x, y, z)
              }

              val rotationOffsetStr = parser.getAttributeValue(null, "rotationOffset")
              val rotationOffsetArray = rotationOffsetStr?.split(",".toRegex(), 3)
              rotationOffsetArray?.let {
                val pitch = it[0].toFloat()
                val yaw = it[1].toFloat()
                val roll = it[2].toFloat()
                currentMeshRotationOffset = Quaternion(pitch, yaw, roll)
              }

              val meshBoundsStr = parser.getAttributeValue(null, "bounds")
              val meshBoundsArray = meshBoundsStr?.split(",".toRegex(), 3)
              meshBoundsArray?.let {
                val x = it[0].toFloat()
                val y = it[1].toFloat()
                val z = it[2].toFloat()
                currentMeshBounds =
                    Bound3D(Vector3(-x / 2f, -y / 2f, -z / 2f), Vector3(x / 2f, y / 2f, z / 2f))
              }

              val initialAnimationTrackStr = parser.getAttributeValue(null, "initialAnimationTrack")
              currentMeshInitialAnimationTrack = initialAnimationTrackStr?.toInt()
            }

            "panel" -> {
              currentPanelTitle = parser.getAttributeValue(null, "title")
              assert(currentPanelTitle != null)

              val layoutStr = parser.getAttributeValue(null, "layout")
              currentPanelLayout = PanelContentType.valueOf(layoutStr ?: "UNKNOWN")
              assert(currentPanelLayout != PanelContentType.UNKNOWN)

              val currentPanelAnimationTrackStr = parser.getAttributeValue(null, "animationTrack")
              currentPanelAnimationTrack = currentPanelAnimationTrackStr?.toInt()

              when (currentPanelLayout) {
                PanelContentType.TILES -> {
                  currentPanelTiles = mutableListOf()
                }

                PanelContentType.IMAGE_COPY -> {
                  currentPanelImageResId = null
                  currentPanelCopyResId = null
                }

                else -> {
                  throw RuntimeException("Unsupported panel content type $layoutStr")
                }
              }
            }

            "tile" -> {
              assert(currentPanelLayout == PanelContentType.TILES && currentPanelTiles != null)

              val title = parser.getAttributeValue(null, "title") ?: ""
              val subtitle = parser.getAttributeValue(null, "subtitle") ?: ""

              val imageResIdValue = parser.getAttributeResourceValue(null, "image", 0)
              val imageResId = if (imageResIdValue != 0) imageResIdValue else null

              currentPanelTiles!!.add(TileContent(title, subtitle, imageResId))
            }

            "image" -> {
              assert(currentPanelLayout == PanelContentType.IMAGE_COPY)
              assert(parser.next() == XmlPullParser.TEXT)

              val imageRefText = parser.text
              assert(imageRefText.startsWith("@drawable/"))

              val resName = imageRefText.substringAfter("@drawable/")
              currentPanelImageResId =
                  context.resources.getIdentifier(
                      resName,
                      "drawable",
                      "com.meta.pixelandtexel.scanner",
                  )
              assert(currentPanelImageResId != 0)
            }

            "body" -> {
              assert(currentPanelLayout == PanelContentType.IMAGE_COPY)
              assert(parser.next() == XmlPullParser.TEXT)

              val copyRefText = parser.text
              assert(copyRefText.startsWith("@string/"))

              val resName = copyRefText.substringAfter("@string/")
              currentPanelCopyResId =
                  context.resources.getIdentifier(
                      resName,
                      "string",
                      "com.meta.pixelandtexel.scanner",
                  )
              assert(currentPanelCopyResId != 0)
            }
          }
        }

        XmlPullParser.END_TAG -> {
          when (tagName) {
            "panel" -> {
              if (currentPanelTitle != null && currentUiPanels != null) {
                when (currentPanelLayout) {
                  PanelContentType.TILES -> {
                    if (currentPanelTiles != null) {
                      currentUiPanels.add(
                          TilesPanelContent(
                              currentPanelTitle,
                              currentPanelAnimationTrack,
                              currentPanelTiles,
                          ))
                    }
                  }

                  PanelContentType.IMAGE_COPY -> {
                    val body =
                        if (currentPanelCopyResId != null) context.getString(currentPanelCopyResId)
                        else null

                    assert(currentPanelImageResId != null || body != null)

                    currentUiPanels.add(
                        ImageCopyPanelContent(
                            currentPanelTitle,
                            currentPanelAnimationTrack,
                            currentPanelImageResId,
                            body,
                        ))
                  }

                  else ->
                      throw RuntimeException("Unsupported panel type with title $currentPanelTitle")
                }
              }

              // reset
              currentPanelTitle = null
              currentPanelLayout = PanelContentType.UNKNOWN
              currentPanelAnimationTrack = null
              currentPanelTiles = null
              currentPanelImageResId = null
              currentPanelCopyResId = null
            }

            "object" -> {
              if (currentObjectName != null &&
                  currentMatchingLabels != null &&
                  currentUiPanels != null) {
                assert(currentMatchingLabels.isNotEmpty() && currentUiPanels.isNotEmpty())

                val curatedObject =
                    CuratedObject(
                            currentObjectName,
                            currentMatchingLabels.toList(),
                            currentUiPanels.toList(),
                            curatedObjects.size,
                        )
                        .apply {
                          currentMeshPositionOffset?.let { meshPositionOffset = it }
                          currentMeshRotationOffset?.let { meshRotationOffset = it }
                          initialAnimationTrack = currentMeshInitialAnimationTrack
                          meshBounds = currentMeshBounds
                        }
                curatedObjects[currentObjectName] = curatedObject

                if (currentMeshEntityName != null) {
                  pendingObjectEntities[currentObjectName] = currentMeshEntityName
                }
              }

              // reset
              currentObjectName = null
              currentMatchingLabels = null
              currentMeshEntityName = null
              currentUiPanels = null
            }
          }
        }
      }

      eventType = parser.next()
    }
  }
}
