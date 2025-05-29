// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.detector.models

import android.graphics.PointF
import androidx.core.graphics.toRect
import com.meta.pixelandtexel.scanner.objectdetection.detector.OpenCVObjectDetector

/**
 * Represents the result of a object detection, encapsulating a list of detected objects, the time
 * taken for inference, and the dimensions of the input image on which detection was performed.
 *
 * @property objects A list of [DetectedObject] instances found in the image.
 * @property inferenceTime The time taken for the object detection model to process the image, in
 *   milliseconds.
 * @property inputImageWidth The width of the image that was processed for object detection.
 * @property inputImageHeight The height of the image that was processed for object detection.
 */
data class DetectedObjectsResult(
    val objects: List<DetectedObject>,
    val inferenceTime: Long,
    val inputImageWidth: Int,
    val inputImageHeight: Int
) {
  companion object {
    /**
     * Creates a [DetectedObjectsResult] instance from MediaPipe's object detection results.
     *
     * This function converts a list of [com.google.mediapipe.tasks.components.containers.Detection]
     * objects into a standardized [DetectedObjectsResult]. It extracts bounding boxes, labels, and
     * confidence scores. Objects without categories are filtered out.
     *
     * @param mpDetectedObjects A list of detection results from the MediaPipe object detector.
     * @param inferenceTime The time taken for inference, in milliseconds.
     * @param inputImageWidth The width of the input image.
     * @param inputImageHeight The height of the input image.
     * @return A [DetectedObjectsResult] containing the processed detection information.
     */
    fun fromMPResults(
        mpDetectedObjects: List<com.google.mediapipe.tasks.components.containers.Detection>,
        inferenceTime: Long,
        inputImageWidth: Int,
        inputImageHeight: Int
    ): DetectedObjectsResult {
      val detectedObjects =
          mpDetectedObjects.mapNotNull {
            if (it.categories().isEmpty()) {
              return@mapNotNull null
            }

            val rect = it.boundingBox().toRect()
            val point = PointF(it.boundingBox().centerX(), it.boundingBox().centerY())
            val label = it.categories()[0].categoryName()
            val confidence = it.categories()[0].score()

            DetectedObject(point, rect, label, confidence)
          }

      return DetectedObjectsResult(
          detectedObjects, inferenceTime, inputImageWidth, inputImageHeight)
    }

    /**
     * Creates a [DetectedObjectsResult] instance from ML Kit's object detection results,
     * transforming a list of [com.google.mlkit.vision.objects.DetectedObject] objects from ML Kit
     * into a standardized [DetectedObjectsResult]. It extracts bounding boxes, labels, confidence
     * scores, and tracking IDs. Objects without labels are filtered out.
     *
     * @param mlkitDetectedObjects A list of detected object results from ML Kit object detection.
     * @param inferenceTime The time taken for inference, in milliseconds.
     * @param inputImageWidth The width of the input image.
     * @param inputImageHeight The height of the input image.
     * @return A [DetectedObjectsResult] containing the processed detection information.
     */
    fun fromMLKitResults(
        mlkitDetectedObjects: List<com.google.mlkit.vision.objects.DetectedObject>,
        inferenceTime: Long,
        inputImageWidth: Int,
        inputImageHeight: Int
    ): DetectedObjectsResult {
      val detectedObjects =
          mlkitDetectedObjects.mapNotNull {
            if (it.labels.isEmpty()) {
              return@mapNotNull null
            }

            val point = PointF(it.boundingBox.exactCenterX(), it.boundingBox.exactCenterY())
            val label = it.labels[0].text
            val confidence = it.labels[0].confidence

            DetectedObject(point, it.boundingBox, label, confidence, it.trackingId)
          }

      return DetectedObjectsResult(
          detectedObjects, inferenceTime, inputImageWidth, inputImageHeight)
    }

    /**
     * Creates a [DetectedObjectsResult] instance from custom OpenCV object detection results,
     * converting a list of [OpenCVObjectDetector.CvDetectedObject] into a standardized
     * [DetectedObjectsResult], extracting bounding boxes, labels, and confidence scores.
     *
     * @param cvDetectedObjects A list of custom detected object results from our OpenCV-based
     *   detector.
     * @param inferenceTime The time taken for inference, in milliseconds.
     * @param inputImageWidth The width of the input image.
     * @param inputImageHeight The height of the input image.
     * @return A [DetectedObjectsResult] containing the processed detection information.
     */
    fun fromOpenCVResults(
        cvDetectedObjects: List<OpenCVObjectDetector.CvDetectedObject>,
        inferenceTime: Long,
        inputImageWidth: Int,
        inputImageHeight: Int
    ): DetectedObjectsResult {
      val detectedObjects =
          cvDetectedObjects.map {
            val point = PointF(it.bounds.exactCenterX(), it.bounds.exactCenterY())

            DetectedObject(point, it.bounds, it.label, it.confidence.toFloat())
          }

      return DetectedObjectsResult(
          detectedObjects, inferenceTime, inputImageWidth, inputImageHeight)
    }
  }
}
