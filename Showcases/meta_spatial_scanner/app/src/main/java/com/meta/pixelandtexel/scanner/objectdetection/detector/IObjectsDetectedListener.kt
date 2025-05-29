// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.detector

import android.media.Image
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObjectsResult

/** Simple interface pattern for a listener that receives notifications about detected objects. */
interface IObjectsDetectedListener {
  /**
   * Callback method invoked when objects are detected in an image.
   *
   * @param result The [DetectedObjectsResult] containing information about the detected objects.
   * @param image The [Image] in which the objects were detected.
   */
  fun onObjectsDetected(result: DetectedObjectsResult, image: Image)
}
