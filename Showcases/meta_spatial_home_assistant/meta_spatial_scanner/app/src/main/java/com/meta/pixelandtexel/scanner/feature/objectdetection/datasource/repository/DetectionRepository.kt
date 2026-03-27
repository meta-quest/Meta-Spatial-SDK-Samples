package com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.repository

import android.graphics.Rect
import android.media.Image
import android.util.Log
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.models.DetectedObject
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.ImageUtils.getBitmap
import kotlin.collections.mutableListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.IObjectDetectorHelper
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.DetectionState
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.IObjectDetectionRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.IDisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.model.RaycastRequestModel
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.math.MathUtils.area
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.math.MathUtils.intersection
import com.meta.pixelandtexel.scanner.models.smarthomedata.SmartHomeInfoRequest
import com.meta.pixelandtexel.scanner.models.smarthomedata.getEnumFromString
import java.util.concurrent.atomic.AtomicReference

/**
 * Manages the entire object detection workflow, including state reconciliation.
 * This repository coordinates with a detector worker to process images,
 * reconciles the results against a cache, and emits the final set of
 * found, updated, and lost objects.
 *
 * @param detector The worker implementation that performs the actual detection.
 */
class ObjectDetectionRepository(private val detector: IObjectDetectorHelper,
        private val displayRepository: IDisplayedEntityRepository
    ) : IObjectDetectionRepository {
    companion object {
        private const val TAG = "ObjectDetectionRepo"
    }

    private val isDetecting = AtomicBoolean(false)
    private val objectInfoRequest = AtomicReference<Pair<Int, RaycastRequestModel>?>(null)

    private val cachedObjectIds = mutableSetOf<Int>()
    private val cachedObjects = mutableListOf<DetectedObject>()

    private val _detectionState = MutableStateFlow<DetectionState?>(null)
    override val detectionState: StateFlow<DetectionState?> = _detectionState


    override fun processImage(image: Image, width: Int, height: Int, `finally`: () -> Unit) {
        if (!isDetecting.compareAndSet(false, true)) {
            Log.v(TAG, "Frame dropped, detector busy.")
            finally()
            return
        }

        detector.detect(image, width, height) { result ->
            try {
                if (result != null) {
                    val (found, updated, lost) = reconcileDetectedObjects(result.objects)
                    cachedObjects.clear()
                    cachedObjects.addAll(result.objects.filter { cachedObjectIds.contains(it.id) })

                    checkRequestImageForObject(image)

                    _detectionState.value = DetectionState(image, found, updated, lost, finally)
                } else {
                    finally()
                }
            } finally {
                isDetecting.set(false)
            }
        }
    }

    private fun checkRequestImageForObject(image: Image) {
        objectInfoRequest.getAndSet(null)?.let { (id, raycastModelPose) ->
            val obj = cachedObjects.firstOrNull { it.id == id }
            val bmp = obj?.let { image.getBitmap(it.bounds) }

            if (obj != null && bmp != null) {
                val typeSmartHome = getEnumFromString(obj.label)
                displayRepository.createGenericInfoPanel(
                    R.integer.info_panel_id,
                    SmartHomeInfoRequest(
                        typeSmartHome,
                        raycastModelPose,
                    ),
                )
            }
        }
    }


    private fun reconcileDetectedObjects(
        incomingObjects: List<DetectedObject>
    ): Triple<List<DetectedObject>, List<DetectedObject>, List<Int>> {
        val incomingIds = incomingObjects.mapNotNull { it.id }.toSet()
        val existingIds = cachedObjectIds intersect incomingIds
        val foundIds = incomingIds subtract existingIds
        val lostIds = cachedObjectIds subtract existingIds

        val newIds = existingIds union foundIds
        val newDetectedObjects = incomingObjects.filter { newIds.contains(it.id) }

        val trimmedIds = getOverlappingObjects(newDetectedObjects)
        val finalNewIds = newIds subtract trimmedIds

        val finalFoundIds = foundIds subtract trimmedIds
        val finalExistingIds = existingIds subtract trimmedIds
        val finalLostIds = lostIds union trimmedIds

        val found = incomingObjects.filter { finalFoundIds.contains(it.id) }
        val updated = incomingObjects.filter { finalExistingIds.contains(it.id) }

        cachedObjectIds.clear()
        cachedObjectIds.addAll(finalNewIds)

        return Triple(found, updated, finalLostIds.toList())
    }

    private fun getOverlappingObjects(incomingObjects: List<DetectedObject>): Set<Int> {
        if (incomingObjects.isEmpty()) { return setOf() }
        val trimmed = mutableSetOf<Int>()
        for (i in incomingObjects.indices) {
            if (trimmed.contains(i)) continue
            for (q in incomingObjects.indices) {
                if (trimmed.contains(q) || i == q) continue
                val objA = incomingObjects[i]
                val objB = incomingObjects[q]
                if (objA.id == null || objB.id == null) continue
                val intersection = Rect()
                if (!objA.bounds.intersection(objB.bounds, intersection)) continue

                if (intersection == objA.bounds) { trimmed.add(objA.id); continue }
                if (intersection == objB.bounds) { trimmed.add(objB.id); continue }

                val areaA = objA.bounds.area()
                val areaB = objB.bounds.area()
                val intersectionArea = intersection.area()

                if (areaA <= areaB && intersectionArea >= areaA / 2f) {
                    trimmed.add(objA.id)
                } else if (areaB <= areaA && intersectionArea >= areaB / 2f) {
                    trimmed.add(objB.id)
                }
            }
        }
        return trimmed
    }

    override fun clear() {
        cachedObjectIds.clear()
        cachedObjects.clear()
    }

    override fun requestInfoForObject(id: Int, pose: RaycastRequestModel) {
        objectInfoRequest.set(id to pose)
    }
}