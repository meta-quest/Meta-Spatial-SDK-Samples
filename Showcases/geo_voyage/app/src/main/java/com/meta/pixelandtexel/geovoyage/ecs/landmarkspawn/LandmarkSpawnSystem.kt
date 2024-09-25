// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ecs.landmarkspawn

import android.content.Context
import android.net.Uri
import android.util.Log
import com.meta.pixelandtexel.geovoyage.activities.MainActivity
import com.meta.pixelandtexel.geovoyage.models.GeoCoordinates
import com.meta.pixelandtexel.geovoyage.models.Landmark
import com.meta.pixelandtexel.geovoyage.utils.MathUtils
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.PIf
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.clamp01
import com.meta.pixelandtexel.geovoyage.utils.MathUtils.yawAngle
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
import com.meta.spatial.toolkit.AvatarAttachment
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.getAbsoluteTransform
import kotlin.math.abs
import kotlin.math.min
import org.xmlpull.v1.XmlPullParser

private class LandmarkData(
    val info: Landmark,
    val entity: Entity,
    var sceneObject: SceneObject? = null
)

class LandmarkSpawnSystem(private val xmlResourceID: Int, private val resourceContext: Context) :
    SystemBase() {
  companion object {
    private const val TAG: String = "LandmarkSpawnSystem"

    private const val MAX_LANDMARKS_VISIBLE = 4
  }

  private var landmarksEnabled: Boolean = false

  private val ScaleUpMultiplier = 1.4f

  private var landmarksMap: MutableMap<String, LandmarkData> = mutableMapOf()
  private var interactionButton: Int =
      ButtonBits.ButtonTriggerL or
          ButtonBits.ButtonTriggerR or
          ButtonBits.ButtonA or
          ButtonBits.ButtonX

  private fun parseLandmarksXml(xmlResourceID: Int, context: Context): List<Landmark> {
    val landmarks = mutableListOf<Landmark>()
    val parser = context.resources.getXml(xmlResourceID)

    var eventType = parser.eventType

    // hold the variables of each new landmark
    var name = ""
    var description = ""
    var latitude = 0f
    var longitude = 0f
    var meshFile = ""
    var scale = 1f
    var yaw = 0f
    var zOffset = 0.37f

    while (eventType != XmlPullParser.END_DOCUMENT) {
      val tagName = parser.name
      when (eventType) {
        XmlPullParser.START_TAG -> {
          when (tagName) {
            "landmark" -> {
              // reset values for the new landmark
              name = ""
              description = ""
              latitude = 0f
              longitude = 0f
              meshFile = ""
              scale = 1f
              yaw = 0f
              zOffset = 0.37f
            }

            "name" -> name = parser.nextText()
            "description" -> description = parser.nextText()
            "latitude" -> latitude = parser.nextText().toFloatOrNull() ?: 0f
            "longitude" -> longitude = parser.nextText().toFloatOrNull() ?: 0f
            "model" -> meshFile = parser.nextText()
            "scale" -> scale = parser.nextText().toFloatOrNull() ?: 1f
            "yaw" -> yaw = parser.nextText().toFloatOrNull() ?: 0f
            "zOffset" -> zOffset = parser.nextText().toFloatOrNull() ?: 0.37f
          }
        }

        XmlPullParser.END_TAG -> {
          if (tagName == "landmark") {
            // create a new Landmark object with the parsed values
            val landmark =
                Landmark(
                    meshName = meshFile,
                    scale = scale,
                    yaw = yaw,
                    zOffset = zOffset,
                    latitude = latitude,
                    longitude = longitude,
                    landmarkName = name,
                    description = description)
            landmarks.add(landmark)
          }
        }
      }
      eventType = parser.next()
    }

    return landmarks
  }

  private fun spawnObject(landmark: Landmark, earth: Entity): Entity? {
    if (landmark.meshName.isEmpty()) {
      Log.w(TAG, "Mesh name not found on landmark info")
      return null
    }

    val coords = GeoCoordinates(landmark.latitude, landmark.longitude)
    val position = coords.toCartesianCoords(landmark.zOffset)
    val rotation =
        Quaternion.lookRotation(position)
            .times(Quaternion(90f, 0f, 0f))
            .times(Quaternion(0f, landmark.yaw, 0f))

    val entity =
        Entity.create(
            listOf(
                Mesh(Uri.parse("landmarks/${landmark.meshName}")),
                Scale(Vector3(landmark.scale)),
                Transform(Pose(position, rotation)),
                TransformParent(earth),
                Visible(false)))

    return entity
  }

  fun onCompositionLoaded(composition: GLXFInfo) {
    // parse the xml to get all landmark info
    val landmarks = parseLandmarksXml(xmlResourceID, resourceContext)

    // spawn all landmarks
    val earthEntity = composition.getNodeByName("earth").entity
    landmarks.forEach { landmark ->
      val entity = spawnObject(landmark, earthEntity)

      if (entity != null) {
        landmarksMap[landmark.landmarkName] = LandmarkData(landmark, entity)
      }
    }
  }

  fun toggleLandmarks(enabled: Boolean) {
    if (enabled) {
      val selectedLandmarks =
          landmarksMap.values.shuffled().take(min(landmarksMap.size, MAX_LANDMARKS_VISIBLE))
      selectedLandmarks.forEach { it.entity.setComponent(Visible(true)) }
    } else {
      landmarksMap.forEach { it.value.entity.setComponent(Visible(false)) }
    }

    landmarksEnabled = enabled
  }

  override fun execute() {
    findNewObjects()
    updateLandmarkVisibility()
  }

  private fun findNewObjects() {
    val landmarkEntities =
        Query.where { has(Mesh.id, Transform.id) and changed(Mesh.id) }
            .eval()
            .filter { entity ->
              landmarksMap.entries.any { (_, data) ->
                data.entity.id == entity.id && data.sceneObject == null
              }
            }

    landmarkEntities.forEach { entity ->
      val mapEntry =
          landmarksMap.entries.find { (_, data) -> data.entity.id == entity.id } ?: return@forEach

      val completable =
          systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity) ?: return@forEach

      completable.thenAccept { sceneObject ->
        landmarksMap[mapEntry.key]!!.sceneObject = sceneObject

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
                if (!landmarksEnabled) {
                  return true
                }

                if (hitInfo.entity != entity) {
                  return true
                }

                val buttonsPressed = changed and clicked

                if ((buttonsPressed and interactionButton) != 0) {
                  MainActivity.instance
                      .get()
                      ?.userSelectedLandmark(
                          mapEntry.value.info,
                          GeoCoordinates(
                              mapEntry.value.info.latitude, mapEntry.value.info.longitude))
                }

                return true
              }
            })
      }
    }
  }

  private fun updateLandmarkVisibility() {
    if (!landmarksEnabled) {
      return
    }

    if (!landmarksMap.any()) {
      return
    }

    val headTransform = getHeadTransformComp()
    val headPose = headTransform.transform

    landmarksMap.entries.forEach { (_, data) ->
      // get all of our objects

      val landmarkEntity = data.entity
      val landmarkTransform = landmarkEntity.getComponent<Transform>()
      var landmarkPose = landmarkTransform.transform

      val parentComp = data.entity.getComponent<TransformParent>()
      val parentPose = getAbsoluteTransform(parentComp.entity)

      // translate our landmark pose into world space
      landmarkPose = parentPose.times(landmarkPose)

      // calculate the vectors and angle between them

      val v1 = headPose.t - parentPose.t
      val v2 = landmarkPose.t - parentPose.t
      val dot = v1.dot(v2)

      if (dot < 0f) {
        landmarkTransform.recycle()
        parentComp.recycle()

        return@forEach
      }

      val angle = abs(v1.yawAngle(v2)) * 180f / PIf

      // calculate our new scale

      val lowerLimit = 45f
      val upperLimit = 30f
      val frac = ((angle - lowerLimit) / (upperLimit - lowerLimit)).clamp01()
      val newScale = MathUtils.lerp(data.info.scale, data.info.scale * ScaleUpMultiplier, frac)

      // update our scale component

      val scaleComp = landmarkEntity.getComponent<Scale>()

      scaleComp.scale = Vector3(newScale)
      landmarkEntity.setComponent(scaleComp)

      // recycle all of our components

      landmarkTransform.recycle()
      parentComp.recycle()
      scaleComp.recycle()
    }

    headTransform.recycle()
  }

  private fun getHeadTransformComp(): Transform {
    val head =
        Query.where { has(AvatarAttachment.id) }
            .eval()
            .filter { it.isLocal() && it.getComponent<AvatarAttachment>().type == "head" }
            .first()
    return head.getComponent<Transform>()
  }
}
