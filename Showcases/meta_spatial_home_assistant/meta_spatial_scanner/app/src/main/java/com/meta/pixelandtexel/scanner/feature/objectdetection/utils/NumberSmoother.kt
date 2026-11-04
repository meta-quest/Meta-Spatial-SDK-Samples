// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.feature.objectdetection.utils

/**
 * A simple class that smoothes a number over time, useful for tracking rapidly changing numbers
 * like frame rates. Applies exponential smoothing over time.
 *
 * @property alpha How much smoothing to apply to the number.
 */
class NumberSmoother(private val alpha: Float = 0.1f) {
    private var smoothed: Float = 0.0f
    private var firstUpdate: Boolean = true

    /**
     * Updates the smoothed value with a new integer input, first converting to a float.
     *
     * @param current The new integer value to incorporate into the smoothed average.
     */
    fun update(current: Int) {
        update(current.toFloat())
    }

    /**
     * Updates the smoothed value with a new long input, first converting to a float.
     *
     * @param current The new long value to incorporate into the smoothed average.
     */
    fun update(current: Long) {
        update(current.toFloat())
    }

    /**
     * Updates the smoothed value with a new float input using exponential smoothing. If this is the
     * first call to an `update` method for this instance, the `smoothed` value is initialized
     * directly with the `current` value.
     *
     * @param current The new float value to incorporate into the smoothed average.
     */
    fun update(current: Float) {
        if (firstUpdate) {
            smoothed = current
            firstUpdate = false
        } else {
            smoothed = alpha * current + (1 - alpha) * smoothed
        }
    }

    /**
     * Retrieves the current smoothed number.
     *
     * @return The latest calculated smoothed float value.
     */
    fun getSmoothedNumber(): Float = smoothed
}
