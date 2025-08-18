// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.detector.models

import android.graphics.PointF
import android.graphics.Rect

/**
 * Represents an object detected by a model, including its location, bounding box, descriptive
 * label, detection confidence, and an optional identifier.
 *
 * @property point A [PointF] representing the location of the detected object, in the processed
 *   image.
 * @property bounds A [Rect] defining the bounding box that encloses the detected object within the
 *   image.
 * @property label A [String] providing a human-readable name or category for the detected object
 *   (e.g., "cat", "car", "person").
 * @property confidence A [Float] value indicating the detector's certainty about the correctness of
 *   the detection, in the range of [0.0, 1.0].
 * @property id An optional [Int] used to uniquely identify or track an instance of the detected
 *   object across multiple frames or detections. Defaults to null if no specific ID is assigned.
 */
data class DetectedObject(
    val point: PointF,
    val bounds: Rect,
    val label: String,
    val confidence: Float,
    val id: Int? = null,
)
