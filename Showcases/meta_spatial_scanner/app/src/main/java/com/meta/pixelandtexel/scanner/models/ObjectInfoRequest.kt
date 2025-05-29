// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.models

import android.graphics.Bitmap

/**
 * A model representing a request for more information about a detected object in the device camera
 * feed. Passed the the Llama 3.2 Vision invocation for information.
 *
 * @property name The label or classification of the detected object assigned by the object
 *   detector.
 * @property image The [Bitmap] of the object, cropped from the image frame of the device camera
 *   feed from which it was detected.
 */
data class ObjectInfoRequest(val name: String, val image: Bitmap)
