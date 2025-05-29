// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection

import android.graphics.Bitmap
import android.graphics.Rect
import android.media.Image
import androidx.lifecycle.AtomicReference
import com.meta.pixelandtexel.scanner.objectdetection.detector.IObjectsDetectedListener
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObject
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObjectsResult
import com.meta.pixelandtexel.scanner.objectdetection.math.MathUtils.area
import com.meta.pixelandtexel.scanner.objectdetection.math.MathUtils.intersection
import com.meta.pixelandtexel.scanner.objectdetection.utils.Event1
import com.meta.pixelandtexel.scanner.objectdetection.utils.ImageUtils.getBitmap
import kotlin.collections.mutableListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manages a cache of detected objects.
 *
 * This class implements [IObjectsDetectedListener] to receive updates on detected objects. It
 * reconciles incoming object data with its internal cache, determining which objects are newly
 * found, which are updates to existing objects, and which objects are no longer detected (lost).
 *
 * Key functionalities include:
 * - Tracking currently detected objects.
 * - Emitting events ([onObjectsFound], [onObjectsUpdated], [onObjectsLost]) to notify listeners
 *   about changes in the detected object set.
 * - Filtering out overlapping objects based on defined criteria (e.g., one object being completely
 *   within another, or significant overlap).
 * - Providing a mechanism to request a cropped [android.graphics.Bitmap] of a specific detected
 *   object from the most recent frame via [tryRequestImageForObject].
 * - Allowing retrieval of a specific [DetectedObject] by its ID using [getObject].
 * - Offering a [clear] method to reset the cache.
 */
class DetectedObjectCache : IObjectsDetectedListener {
  companion object {
    private const val TAG: String = "ObjectDetection"
  }

  private val detectedObjectIds = mutableSetOf<Int>()
  private val detectedObjects = mutableListOf<DetectedObject>()

  private var bitmapRequest = AtomicReference<BitmapCropRequest?>()

  val onObjectsFound = Event1<List<DetectedObject>>()
  val onObjectsUpdated = Event1<List<DetectedObject>>()
  val onObjectsLost = Event1<List<Int>>()

  /**
   * Callback method invoked when new object detection results are available. It reconciles the new
   * list of detected objects with the existing cache, updates the internal state, and fulfills any
   * pending bitmap crop requests.
   *
   * @param result The [DetectedObjectsResult] containing the list of detected objects.
   * @param image The [Image] frame from which the objects were detected, used for bitmap cropping.
   */
  override fun onObjectsDetected(result: DetectedObjectsResult, image: Image) {
    reconcileDetectedObjects(result.objects)

    detectedObjects.clear()
    detectedObjects.addAll(result.objects.filter { detectedObjectIds.contains(it.id) })

    // if there's a request for a cropped image of an object from the frame, fulfill it

    val request = bitmapRequest.getAndSet(null) ?: return

    val obj = detectedObjects.firstOrNull { it.id == request.objectId }
    if (obj == null) {
      request.receiver.invoke(null)
      return
    }

    // convert to a bitmap, cropping the object from the frame

    val bmp = image.getBitmap(obj.bounds)

    request.receiver.invoke(bmp)
  }

  /**
   * Reconciles the list of newly detected objects with the currently cached objects. This function
   * determines which objects are new, which are updates to existing ones, and which ones are no
   * longer present. It also handles filtering of overlapping objects and triggers the appropriate
   * [onObjectsFound], [onObjectsUpdated], and [onObjectsLost] events.
   *
   * @param incomingObjects A list of [DetectedObject] instances from the latest detection frame.
   */
  private fun reconcileDetectedObjects(incomingObjects: List<DetectedObject>) {
    // separate into our 3 groups using set operations

    val incomingIds = incomingObjects.mapNotNull { it.id }.toSet()

    val tempExistingIds = detectedObjectIds intersect incomingIds
    val tempFoundIds = incomingIds subtract tempExistingIds // just detected
    val tempLostIds = detectedObjectIds subtract tempExistingIds // no longer detected

    val tempNewIds = tempExistingIds union tempFoundIds
    val tempNewDetectedObjects = incomingObjects.filter { tempNewIds.contains(it.id) }

    // find any overlapping detected objects, and remove from our final sets

    val trimmedIds =
        getOverlappingObjects(tempNewDetectedObjects) // those that shouldn't be displayed
    val finalNewIds = tempNewIds subtract trimmedIds

    // reconstruct our sets with the trimmed objects filtered out

    val finalExistingIds = tempExistingIds subtract trimmedIds
    val finalFoundIds = tempFoundIds subtract trimmedIds
    val finalLostIds = tempLostIds union trimmedIds // report the trimmed objects as lost

    // notify our listeners of detected object changes

    if (finalFoundIds.isNotEmpty()) {
      val found = incomingObjects.filter { finalFoundIds.contains(it.id) }
      CoroutineScope(Dispatchers.Main).launch { onObjectsFound(found) }
    }

    if (finalExistingIds.isNotEmpty()) {
      val existing = incomingObjects.filter { finalExistingIds.contains(it.id) }
      CoroutineScope(Dispatchers.Main).launch { onObjectsUpdated(existing) }
    }

    if (finalLostIds.isNotEmpty()) {
      CoroutineScope(Dispatchers.Main).launch { onObjectsLost(finalLostIds.toList()) }
    }

    detectedObjectIds.clear()
    detectedObjectIds.addAll(finalNewIds)
  }

  /**
   * Identifies and returns a set of IDs for objects that overlap significantly with other objects.
   * The criteria for removal include one object being completely contained within another, or a
   * substantial portion of a smaller object overlapping with a larger one.
   *
   * @param incomingObjects A list of [DetectedObject] instances to check for overlaps.
   * @return A set of [Int] IDs representing the objects that should be trimmed due to overlap.
   */
  private fun getOverlappingObjects(incomingObjects: List<DetectedObject>): Set<Int> {
    if (incomingObjects.isEmpty()) {
      return setOf()
    }

    val trimmed = mutableSetOf<Int>()

    // iterate through our objects, and accumulate the ones which shouldn't be shown

    for (i in incomingObjects.indices) {
      if (trimmed.contains(i)) {
        continue
      }

      for (q in incomingObjects.indices) {
        if (trimmed.contains(q)) {
          continue
        }

        if (i == q) {
          continue
        }

        val objA = incomingObjects[i]
        val objB = incomingObjects[q]

        if (objA.id == null || objB.id == null) {
          continue
        }

        val intersection = Rect()
        if (!objA.bounds.intersection(objB.bounds, intersection)) {
          continue
        }

        // if 2 objects intersect, where 1 is completely within the other, remove the interior one

        if (intersection == objA.bounds) {
          trimmed.add(objA.id)
          continue
        }
        if (intersection == objB.bounds) {
          trimmed.add(objB.id)
          continue
        }

        // if 2 objects are overlapping, and the intersection area makes up a majority of
        // the smaller of the two, remove it

        val areaA = objA.bounds.area()
        val areaB = objB.bounds.area()
        val intersectionArea = intersection.area()

        if (areaA <= areaB && intersectionArea >= areaA.toFloat() / 2f) {
          trimmed.add(objA.id)
        } else if (areaB <= areaA && intersectionArea >= areaB.toFloat() / 2f) {
          trimmed.add(objB.id)
        }
      }
    }

    return trimmed
  }

  /**
   * Retrieves a [DetectedObject] from the cache by its unique ID.
   *
   * @param id The unique identifier of the object to retrieve.
   * @return The [DetectedObject] if found, or `null` if no object with the given ID exists in the
   *   cache.
   */
  fun getObject(id: Int): DetectedObject? {
    return detectedObjects.firstOrNull { id == it.id }
  }

  /**
   * Attempts to request a cropped [Bitmap] image of a specific detected object. The request is
   * processed asynchronously, and the resulting bitmap (or `null` if the object is not found or
   * another request is pending) is delivered via the [receiver] callback.
   *
   * @param id The ID of the object for which to request an image.
   * @param receiver A callback function that will receive the [Bitmap] or `null`.
   * @return `true` if the request was successfully queued, `false` otherwise (e.g., if the object
   *   is not currently detected or another request is already pending).
   */
  fun tryRequestImageForObject(id: Int, receiver: (Bitmap?) -> Unit): Boolean {
    // object not available this frame, so it won't be next frame either
    getObject(id) ?: return false

    if (bitmapRequest.get() != null) {
      receiver.invoke(null)
      return false
    }

    bitmapRequest.set(BitmapCropRequest(receiver, id))

    return true
  }

  /**
   * Clears all detected objects from the cache. This also triggers the [onObjectsLost] event with
   * the IDs of all objects that were in the cache before clearing.
   */
  fun clear() {
    onObjectsLost(detectedObjectIds.toList())
    detectedObjectIds.clear()
  }

  /**
   * Data class representing a request to crop a bitmap image of a detected object.
   *
   * @property receiver The callback function to invoke with the resulting [Bitmap] or null.
   * @property objectId The ID of the object to crop.
   */
  private data class BitmapCropRequest(val receiver: (Bitmap?) -> Unit, val objectId: Int)
}
