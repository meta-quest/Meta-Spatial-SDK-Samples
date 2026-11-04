// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.feature.objectdetection.android.views.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.SystemClock
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.models.DetectedObject
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.NumberSmoother
import kotlin.math.floor

/**
 * A custom [View] that is meant to be rendered over a [android.view.Surface] displaying the device
 * camera feed, and draws object detection results from the processed the camera frames. Takes a
 * list of [DetectedObject] instances and draws them as rectangular bounding boxes with labels. Also
 * displays a provided statistics string and calculates/shows the rate of result updates per second.
 */
class GraphicOverlay : View {
    private var results: List<DetectedObject>? = null
    private var stats: String? = null
    private var xScale: Float = 1f
    private var yScale: Float = 1f

    private var lastResultsUpdateTimeMs = 0L
    private val smoothedUpdatesPerSec = NumberSmoother()

    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = TextPaint()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        // setup our paints

        boxPaint.color = Color.BLUE
        boxPaint.strokeWidth = 2f
        boxPaint.style = Paint.Style.STROKE

        textBackgroundPaint.color = Color.BLUE
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 16f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 16f
    }

    /**
     * Clears all bounding boxes and labels of detected objects, plus the stats String, and
     * invalidates the View so that draw is called with no results.
     */
    fun clear() {
        results = null
        stats = null

        invalidate()
    }

    /**
     * Updates the detection results to be drawn and triggers a view redraw.
     *
     * @param newResults A list of [DetectedObject] instances representing the latest detection
     *   results from the model.
     * @param imageWidth The width of the source image from which the `newResults` were derived, used
     *   to calculate the horizontal scaling factor.
     * @param imageHeight The height of the source image from which the `newResults` were derived,
     *   used to calculate the vertical scaling factor.
     */
    fun drawResults(newResults: List<DetectedObject>, imageWidth: Int, imageHeight: Int) {
        results = newResults
        xScale = width.toFloat() / imageWidth
        yScale = height.toFloat() / imageHeight

        invalidate()
    }

    /**
     * Updates the statistics string to be displayed and triggers a view redraw.
     *
     * @param newStats A [String] containing the new statistics or informational text to be displayed
     *   on the view.
     */
    fun drawStats(newStats: String) {
        stats = newStats

        updateResultsTiming()

        invalidate()
    }

    /** Updates our local timing stat for how often updates are sent. */
    private fun updateResultsTiming() {
        // keep track of how often we're receiving updates

        val nowMs = SystemClock.uptimeMillis()
        val elapsedMs = nowMs - lastResultsUpdateTimeMs
        if (elapsedMs > 0) {
            smoothedUpdatesPerSec.update(floor(1000f / elapsedMs))
        }
        lastResultsUpdateTimeMs = nowMs
    }

    /**
     * Overrides [draw] to render object detection results and statistics onto the canvas. Draws
     * rectangular bounding boxes around the detected objects, with labels underneath. Also draws a
     * statistics String, if supplied, plus a local timing stat.
     *
     * @param canvas The [Canvas] instance on which all drawing operations will be performed.
     */
    @SuppressLint("DefaultLocale")
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // for debugging, draw borders around this entire view
        canvas.drawRect(RectF(4f, 4f, width.toFloat() - 4, height.toFloat() - 4), boxPaint)

        // draw boxes around all of our results, with a category and category score

        results
            ?.map { Rect(it.bounds.left, it.bounds.top, it.bounds.right, it.bounds.bottom) }
            ?.forEachIndexed { i, rect ->
                val top = rect.top * yScale
                val bottom = rect.bottom * yScale
                val left = rect.left * xScale
                val right = rect.right * xScale
                val centerX = left + (right - left) / 2

                // draw the box

                val drawableRect = RectF(left, top, right, bottom)
                canvas.drawRect(drawableRect, boxPaint)

                // create text to display alongside detected objects

                var drawableText =
                    results!![i].label + " :: " + String.format("%.2f", results!![i].confidence)
                if (results!![i].id != null) {
                    drawableText = "${results!![i].id} :: $drawableText"
                }

                // draw rect behind display text

                val bounds = Rect()
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)

                val textWidth = bounds.width()
                val textHeight = bounds.height()
                val padding = 4
                canvas.drawRect(
                    centerX - textWidth / 2 - padding,
                    bottom,
                    (centerX - textWidth / 2) + textWidth + padding,
                    bottom + textHeight + padding,
                    textBackgroundPaint,
                )

                // draw text for detected object

                canvas.drawText(
                    drawableText,
                    centerX - textWidth / 2,
                    bottom + bounds.height(),
                    textPaint,
                )
            }

        // draw our inference stats near the top left

        if (stats != null) {
            val resultsUpdatesPerSec = smoothedUpdatesPerSec.getSmoothedNumber().toInt()
            val drawableStats = "$resultsUpdatesPerSec results/sec\n${stats!!}"

            drawString(canvas, drawableStats, 150f, 100f, textPaint)
        }
    }

    /**
     * Convenience function for drawing strings with new lines.
     *
     * @param canvas The [Canvas] on which to draw the text.
     * @param text The [String] of text to be drawn. If it contains newline characters, it will be
     *   rendered as multiple lines.
     * @param x The x-coordinate for the starting position of the text (left edge).
     * @param y The y-coordinate for the baseline of the first line of text.
     * @param paint The [TextPaint] object that defines the text's appearance. The text size from this
     *   paint is used to calculate line spacing for multi-line text.
     */
    private fun drawString(canvas: Canvas, text: String, x: Float, y: Float, paint: TextPaint) {
        var dy = y
        if (text.contains("\n")) {
            val texts = text.split("\n")
            for (txt in texts) {
                canvas.drawText(txt, x, dy, paint)
                dy += paint.textSize.toInt()
            }
        } else {
            canvas.drawText(text, x, y, paint)
        }
    }
}
