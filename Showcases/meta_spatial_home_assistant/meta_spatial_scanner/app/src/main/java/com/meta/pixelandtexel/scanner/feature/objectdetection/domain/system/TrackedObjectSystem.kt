package com.meta.pixelandtexel.scanner.feature.objectdetection.domain.system

import android.graphics.Rect
import android.net.Uri
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.TrackedObject
import com.meta.pixelandtexel.scanner.feature.objectdetection.android.viewmodels.ObjectLabelViewModel
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.camera.models.CameraProperties
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.models.DetectedObject
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.IPoolable
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.ObjectPool
import com.meta.pixelandtexel.scanner.feature.objectdetection.android.views.ObjectLabelScreen
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.IObjectDetectionRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.model.RaycastRequestModel
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.math.MathUtils
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.math.MathUtils.copy
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.math.MathUtils.toVector2
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.math.Plane
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.math.Ray
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.core.Vector4
import com.meta.spatial.runtime.BlendMode
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.runtime.DepthTest
import com.meta.spatial.runtime.HitInfo
import com.meta.spatial.runtime.InputListener
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.PanelShapeLayerBlendType
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.SceneMaterialAttribute
import com.meta.spatial.runtime.SceneMaterialDataType
import com.meta.spatial.runtime.SceneMesh
import com.meta.spatial.runtime.SceneObject
import com.meta.spatial.runtime.SceneTexture
import com.meta.spatial.runtime.SortOrder
import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Hittable
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Quad
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.toolkit.reparentChildInWorldCoordinates
import com.meta.spatial.uiset.theme.SpatialColor

/**
 * Manages the lifecycle and rendering of tracked objects detected in the scene – handles creating,
 * updating, and removing visual representations (quads with outlines and labels) for objects
 * detected in the device camera feed. Also handles user interaction with these tracked objects.
 *
 * @property activity The main activity context, used for accessing resources and registering
 *   panels.
 * @property fov The field of view of the camera, used in projection calculations.
 * @property headToCameraOffset The transformation from the head's pose to the camera's pose.
 * @property screenPointToRayInCamera A function that converts a 2D screen point to a 3D ray in
 *   camera space.
 * @property screenPointToPointOnViewPlane A function that converts a 2D screen point to a 3D point
 *   on the view plane at a specified distance.
 */
class TrackedObjectSystem(
    activity: AppSystemActivity,
    private val detectionRepository: IObjectDetectionRepository,
    private var fov: Float = 72f,
    private var headToCameraOffset: Pose = Pose(),
    private var screenPointToRayInCamera: ((Vector2) -> Vector3) = { _ -> Vector3.Forward },
    private var screenPointToPointOnViewPlane: ((Vector2, Float) -> Vector3) = { _, _ ->
        Vector3.Forward
    },
) : SystemBase() {
    companion object {
        private const val TAG: String = "TrackedObjectSystem"

        private const val Z_DIST: Float = 2f
        private const val LABEL_PANEL_Y_OFFSET = 0.07f

        // material params for the 9-slice shader and texture

        // the size of the input texture
        private val SLICE_TEX_SIZE = Vector2(96f, 96f)

        // the slice size of the image – left, top, right, bottom
        private val SLICE_SIZE = Vector4(32f, 32f, 32f, 32f)

        // the pixels per unit multiplier – increasing this scales the outline width
        private const val PPU_MULTIPLIER = 1f
    }

    private val trackedObjectPool = ObjectPool(::createNewTrackedObj)

    // key is the detected object id, not the entity id
    private var trackedObjects = HashMap<Int, TrackedObjectInfo>()

    private val outlineDrawable = activity.getDrawable(R.drawable.rounded_box_outline)!!

    private var lastTime = System.currentTimeMillis()

    private var currentlyClickedObjectId: Int? = null

    init {
        activity.registerPanel(
            PanelRegistration(R.integer.object_label_panel_id) { entity ->
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    fractionOfScreen = 0.25f
                    width = 0.5f
                    height = 0.1f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                composePanel {
                    val info =
                        trackedObjects.values.firstOrNull { it.labelPanelEntity.id == entity.id }
                            ?: throw RuntimeException("Failed to find tracked object for panel")

                    setContent { ObjectLabelScreen(info.uiVM) { onTrackedObjectClicked(info.entity) } }
                }
            }
        )
    }

    /**
     * Updates the positions and appearances of tracked objects, and processing new object detections.
     */
    override fun execute() {
        // calculate our delta time
        val currentTime = System.currentTimeMillis()
        val dt = ((currentTime - lastTime) / 1000f).coerceAtMost(0.1f)
        lastTime = currentTime

        findNewObjects()

        val headPose = getScene().getViewerPose()

        // update the positions of all tracked objects

        trackedObjects.forEach { (_, info) ->
            // our first frame with this pooled item; reveal it and re-enable collision

            if (info.shouldTeleport) {
                info.entity.setComponent(Visible(true))
                info.entity.setComponent(Hittable(MeshCollision.LineTest))
                info.labelPanelEntity.setComponent(Visible(true))
            }

            val targetPose = getObjectPoseForRay(headPose, info.cameraRayToObject)
            val targetScale = getObjectScaleForBounds(headPose, info.cameraFrameBounds)

            // snap to target pose/scale on first frame, or smoothly lerp there otherwise

            val newPose =
                if (info.shouldTeleport) {
                    targetPose
                } else {
                    val currentPose = info.entity.getComponent<Transform>().transform
                    currentPose.lerp(targetPose, dt * 4f)
                }
            val newScale =
                if (info.shouldTeleport) {
                    targetScale
                } else {
                    val currentScale = info.entity.getComponent<Scale>().scale
                    currentScale.lerp(targetScale, dt * 4f)
                }

            info.entity.setComponent(Transform(newPose))
            info.entity.setComponent(Scale(newScale))

            // update the shader to maintain a consistent outline width

            val sliceParams = Vector4(newScale.x, newScale.y, SLICE_TEX_SIZE.x, SLICE_TEX_SIZE.y)
            info.outlineMaterial.setAttribute("sliceParams", sliceParams)

            // update the label panel location

            val panelY = -newScale.y / 2f - LABEL_PANEL_Y_OFFSET
            info.labelPanelEntity.setComponent(Transform(Pose(Vector3(0f, panelY, 0f))))

            info.shouldTeleport = false
        }
    }

    /**
     * Queries for newly created entities with [TrackedObject] components and sets up their mesh,
     * material, and input listeners.
     */
    private fun findNewObjects() {
        val query = Query.where { changed(TrackedObject.id) }
        for (entity in query.eval()) {
            val completable = systemManager.findSystem<SceneObjectSystem>().getSceneObject(entity)

            completable?.thenAccept {
                // setup our mesh and material
                val trackedObjectComp = entity.getComponent<TrackedObject>()

                val id = trackedObjectComp.objectId

                val quadMesh =
                    SceneMesh.quad(
                        Vector3(-0.5f, -0.5f, 0f),
                        Vector3(0.5f, 0.5f, 0f),
                        trackedObjects[id]!!.outlineMaterial,
                    )
                it.setSceneMesh(quadMesh, "trackedObjectQuad")

                // add our on click listener
                it.addInputListener(
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
                                        ButtonBits.ButtonTriggerL

                            val isButtonPressed = (selectButtons and buttonState and changed) != 0
                            val isButtonHeld = (selectButtons and buttonState) != 0

                            if (isButtonPressed && currentlyClickedObjectId == null) {
                                val trackedObjectComp =
                                    receiver.entity!!.getComponent<TrackedObject>()
                                currentlyClickedObjectId = trackedObjectComp.objectId
                                onTrackedObjectClicked(receiver.entity!!)
                            }

                            if (!isButtonHeld) {
                                currentlyClickedObjectId = null
                            }

                            return true
                        }
                    }
                )
            }
        }
    }

    /**
     * Handles the click event on a tracked object by calculating the interaction pose
     * based on the object's position and the user's head position.
     *
     * @param entity The entity representing the clicked tracked object.
     */
    private fun onTrackedObjectClicked(entity: Entity) {
        val comp = entity.getComponent<TrackedObject>()
        val headPose = getScene().getViewerPose()
        val headPosition = headPose.t

        val transformComp = entity.getComponent<Transform>()
        val pose = transformComp.transform

        val direction = (pose.t - headPosition).normalize()


        val scaleComp = entity.getComponent<Scale>()
        val scale = scaleComp.scale
        val position = pose.times(Vector3.Right * (scale.x / 2))

        // zero out any pitch to calculate our direction vector
        position.y = headPosition.y

        val rotation = Quaternion.lookRotationAroundY(position - headPosition)

        detectionRepository.requestInfoForObject(
            comp.objectId,
            RaycastRequestModel(headPosition, direction, rotation)
        )
    }

    /**
     * Creates a new [TrackedObjectInfo] instance for the [trackedObjectPool] to generate new objects
     * when the pool is empty. Sets up the entity, its label panel, and the outline material.
     *
     * @return A new [TrackedObjectInfo] instance.
     */
    private fun createNewTrackedObj(): TrackedObjectInfo {
        val material =
            SceneMaterial.custom(
                "9slice",
                arrayOf(
                    SceneMaterialAttribute("sliceTex", SceneMaterialDataType.Texture2D),
                    SceneMaterialAttribute("sliceParams", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("sliceSize", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("tintColor", SceneMaterialDataType.Vector4),
                    SceneMaterialAttribute("stereoParams", SceneMaterialDataType.Vector4),
                ),
            )
                .apply {
                    setBlendMode(BlendMode.TRANSLUCENT)
                    setSortOrder(SortOrder.TRANSLUCENT)
                    setDepthTest(DepthTest.ALWAYS)
                    setStereoMode(StereoMode.None)

                    setAttribute("sliceTex", SceneTexture(outlineDrawable))

                    // x = quad width, y = quad height, z = texture width, w = texture height
                    setAttribute("sliceParams", Vector4(1f, 1f, SLICE_TEX_SIZE.x, SLICE_TEX_SIZE.y))
                    // slice size in order: left, right, top, bottom
                    setAttribute("sliceSize", SLICE_SIZE)

                    // rgb, a = pixels per unit multiplier
                    setAttribute(
                        "tintColor",
                        Vector4(
                            SpatialColor.b70.red,
                            SpatialColor.b70.green,
                            SpatialColor.b70.blue,
                            PPU_MULTIPLIER,
                        ),
                    )
                }

        val entity =
            Entity.create(
                Transform(),
                Scale(1f),
                TrackedObject(),
                Mesh(Uri.parse("mesh://quad")),
                Quad(),
                Material(),
                Visible(false),
                Hittable(MeshCollision.NoCollision),
            )
        val labelPanelEntity =
            Entity.createPanelEntity(
                R.integer.object_label_panel_id,
                Transform(),
                Visible(false),
                Hittable(MeshCollision.NoCollision),
            )

        // wait until panel is created to do this?
        reparentChildInWorldCoordinates(entity, labelPanelEntity)

        return TrackedObjectInfo(entity, labelPanelEntity, material)
    }

    /**
     * Called when new objects are detected in the camera's video feed. For each detected object, it
     * takes or creates a [TrackedObjectInfo] instance, updates its properties, and adds it to the
     * [trackedObjects] map.
     *
     * @param objects A list of [DetectedObject] instances representing the newly found objects.
     */
    fun onObjectsFound(objects: List<DetectedObject>) {
        for (obj in objects) {
            if (obj.id == null) {
                continue
            }

            val ray = screenPointToRayInCamera(obj.point.toVector2())

            // create (or recycle) a new entity to represent the tracked object
            val trackedObj = trackedObjectPool.take()
            trackedObj.update(obj.id, ray, obj.bounds, obj.label)

            trackedObjects[obj.id] = trackedObj
        }

        // Log.d(TAG, "Added ${objects.size} tracked objects; new size: ${trackedObjects.size}")
    }

    /**
     * Called when existing tracked objects are updated by the object detector. Updates the camera ray
     * and bounding box for each corresponding [TrackedObjectInfo].
     *
     * @param objects A list of [DetectedObject] instances with updated information.
     */
    fun onObjectsUpdated(objects: List<DetectedObject>) {
        for (obj in objects) {
            if (obj.id == null || !trackedObjects.keys.contains(obj.id)) {
                continue
            }

            val ray = screenPointToRayInCamera(obj.point.toVector2())

            // update the camera ray to detected object

            trackedObjects[obj.id]!!.cameraRayToObject = ray
            trackedObjects[obj.id]!!.cameraFrameBounds = obj.bounds
        }

        // Log.d(TAG, "Updated ${objects.size} tracked objects; new size: ${trackedObjects.size}")
    }

    /**
     * Called when objects are lost or no longer detected by the object detector. It removes the
     * corresponding [TrackedObjectInfo] instances from [trackedObjects] and returns them to the
     * [trackedObjectPool].
     *
     * @param objectIds A list of IDs for the objects that were lost.
     */
    fun onObjectsLost(objectIds: List<Int>) {
        for (id in objectIds) {
            // remove the tracked object, and recycle the associated entity

            val trackedObject = trackedObjects[id] ?: continue

            trackedObjectPool.put(trackedObject)
            trackedObjects.remove(id)
        }

        // Log.d(TAG, "Removed ${objectIds.size} tracked objects; new size: ${trackedObjects.size}")
    }

    /**
     * Calculates the world pose (position and rotation) for a tracked object based on a ray from the
     * camera. The object is positioned on a view plane at a fixed Z distance and oriented to face the
     * camera.
     *
     * @param headPose The current pose of the user's head.
     * @param ray The direction ray from the camera to the object in camera space.
     * @return The calculated [Pose] for the object in world space.
     */
    private fun getObjectPoseForRay(headPose: Pose, ray: Vector3): Pose {
        val cameraPose = headPose.times(headToCameraOffset)
        val worldRayDir = cameraPose.q.times(ray).normalize()

        // find position by intersecting direction to view plane
        val viewPlane = Plane(cameraPose.forward() * Z_DIST, -cameraPose.forward())
        val position = MathUtils.rayPlaneIntersection(Ray(cameraPose.t, worldRayDir), viewPlane)!!

        // orient this to face the user
        val rotation = cameraPose.q

        return Pose(position, rotation)
    }

    /**
     * Calculates the world scale for a tracked object based on its screen-space bounding box.
     * Projects the corners of the bounding box onto the view plane and uses the distances between
     * these projected points to determine the width and height.
     *
     * @param headPose The current [Pose] pose of the user's head.
     * @param bounds The screen-space bounding box ([Rect]) of the detected object.
     * @return The calculated [Vector3] scale for the object.
     */
    private fun getObjectScaleForBounds(headPose: Pose, bounds: Rect): Vector3 {
        val cameraPose = headPose.times(headToCameraOffset)
        val viewPlane = Plane(cameraPose.forward() * Z_DIST, -cameraPose.forward())

        // find 3 points of the bounds projected into world space relative to the camera pose

        val topLeft =
            screenPointToPointOnViewPlane(
                Vector2(bounds.left.toFloat(), bounds.top.toFloat()),
                Z_DIST
            )
        val tlWorldRayDir = cameraPose.q.times(topLeft).normalize()
        val tlPosition =
            MathUtils.rayPlaneIntersection(Ray(cameraPose.t, tlWorldRayDir), viewPlane)!!

        val topRight =
            screenPointToPointOnViewPlane(
                Vector2(bounds.right.toFloat(), bounds.top.toFloat()),
                Z_DIST
            )
        val trWorldRayDir = cameraPose.q.times(topRight).normalize()
        val trPosition =
            MathUtils.rayPlaneIntersection(Ray(cameraPose.t, trWorldRayDir), viewPlane)!!

        val bottomLeft =
            screenPointToPointOnViewPlane(
                Vector2(bounds.left.toFloat(), bounds.bottom.toFloat()),
                Z_DIST,
            )
        val blWorldRayDir = cameraPose.q.times(bottomLeft).normalize()
        val blPosition =
            MathUtils.rayPlaneIntersection(Ray(cameraPose.t, blWorldRayDir), viewPlane)!!

        // use the distance between those corners to calculate the new panel scale

        val height = (tlPosition - blPosition).length()
        val width = (trPosition - tlPosition).length()

        return Vector3(width, height, 1f)
    }

    /**
     * Called when the camera configuration changes, and updates camera-related properties used by the
     * system.
     *
     * @param properties The new [CameraProperties] containing FOV, head-to-camera offset, and
     *   screen-to-ray conversion functions.
     */
    fun onCameraPropertiesChanged(properties: CameraProperties) {
        fov = properties.fov
        headToCameraOffset = properties.getHeadToCameraPose()
        screenPointToRayInCamera = properties::screenPointToRayInCamera
        screenPointToPointOnViewPlane = properties::screenPointToPointOnViewPlane
    }

    fun clear(){
        trackedObjects.forEach {
            (_, trackedObjectInfo) ->
            trackedObjectPool.put(trackedObjectInfo)
        }

        trackedObjects.clear()
    }

    /**
     * Data class holding information about a single tracked object – including its entity, label
     * panel entity, material, UI view model, and state related to its position and appearance.
     * Implements [IPoolable] to be used with [ObjectPool].
     *
     * @property entity The main entity representing the tracked object (the quad).
     * @property labelPanelEntity The entity for the label panel associated with this object.
     * @property outlineMaterial The [SceneMaterial] used for the object's outline.
     * @property uiVM The [ObjectLabelViewModel] for the object's label UI.
     * @property cameraRayToObject The ray from the camera to the object in camera space.
     * @property cameraFrameBounds The bounding box of the object in the camera frame.
     * @property targetPose The target pose for the object in the next frame.
     * @property targetScale The target scale for the object in the next frame.
     */
    private data class TrackedObjectInfo(
        val entity: Entity,
        val labelPanelEntity: Entity,
        val outlineMaterial: SceneMaterial,
        val uiVM: ObjectLabelViewModel = ObjectLabelViewModel(),
        var cameraRayToObject: Vector3 = Vector3.Forward,
        var cameraFrameBounds: Rect = Rect(),
        var targetPose: Pose = Pose(),
        var targetScale: Vector3 = Vector3(0f),
    ) : IPoolable {
        var shouldTeleport = false

        /**
         * Updates the [TrackedObjectInfo] with new data from a [DetectedObject]. Sets the object ID,
         * camera ray, bounding rectangle, and display name. Marks [shouldTeleport] as true to indicate
         * it's a new or re-activated object.
         *
         * @param id The ID of the detected object.
         * @param ray The ray from the camera to the object.
         * @param rect The bounding box of the object in the camera frame.
         * @param name The display name for the object's label.
         */
        fun update(id: Int, ray: Vector3, rect: Rect, name: String) {
            cameraRayToObject.copy(ray)
            cameraFrameBounds = rect

            entity.setComponent(TrackedObject(id))
            uiVM.updateName(name)

            shouldTeleport = true
        }

        /**
         * Resets the [TrackedObjectInfo] to a default state when it's returned to the pool. Hides the
         * entity and its label panel, and disables collision.
         */
        override fun reset() {
            entity.setComponent(Visible(false))
            entity.setComponent(Hittable(MeshCollision.NoCollision))
            labelPanelEntity.setComponent(Visible(false))
        }
    }
}
