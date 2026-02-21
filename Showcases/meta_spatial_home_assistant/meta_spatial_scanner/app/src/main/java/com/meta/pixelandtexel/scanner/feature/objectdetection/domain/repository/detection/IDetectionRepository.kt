package com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection

import android.media.Image
import com.meta.pixelandtexel.scanner.feature.objectdetection.model.RaycastRequestModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines the contract for a repository that manages the object detection workflow.
 *
 * This interface abstracts the logic for processing image frames and exposing
 * the detection results, allowing for different implementations to be used
 * interchangeably.
 */

interface IObjectDetectionRepository {

    /**
     * A [StateFlow] that emits the latest [DetectionState] result.
     * Consumers can collect this flow to receive updates when a new object
     * has been detected. A `null` value indicates no current detection result.
     */
    val detectionState: StateFlow<DetectionState?>

    /**
     * Tries to process a new image frame for object detection.
     *
     * Implementations of this method should handle concurrency, such as dropping
     * frames if the detection process is already busy.
     *
     * @param image The image to process.
     * @param width The width of the image.
     * @param height The height of the image.
     * @param finally A callback that must be executed to release the image resource,
     *   regardless of whether detection was successful or the frame was dropped.
     */
    fun processImage(image: Image, width: Int, height: Int, `finally`: () -> Unit)

    fun clear()

    fun requestInfoForObject(id: Int, pose: RaycastRequestModel)
}