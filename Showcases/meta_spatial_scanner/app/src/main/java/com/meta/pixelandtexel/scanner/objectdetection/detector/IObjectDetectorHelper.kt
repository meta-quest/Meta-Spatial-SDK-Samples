// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.detector

import android.media.Image

/**
 * Defines the contract for an object detection helper. Implementations of this interface are
 * responsible for processing an image and identifying objects within it.
 */
interface IObjectDetectorHelper {
  /**
   * Sets the listener that will receive callbacks when objects are detected.
   *
   * @param listener The [IObjectsDetectedListener] to be notified of detection results.
   */
  fun setObjectDetectedListener(listener: IObjectsDetectedListener)

  /**
   * Initiates the object detection process on the provided image.
   *
   * This method should perform detection asynchronously. The `finally` lambda must be called once
   * the detection process for this specific image is complete, regardless of success or failure.
   * This is crucial for managing processing state, such as releasing resources or allowing the next
   * frame to be processed.
   *
   * @param image The [Image] to be processed for object detection.
   * @param width The width of the image.
   * @param height The height of the image.
   * @param finally A callback function that must be invoked when the detection for this image has
   *   finished (successfully or unsuccessfully).
   */
  fun detect(image: Image, width: Int, height: Int, finally: () -> Unit)
}
