// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.detector

import android.content.Context
import android.media.Image
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.ByteBufferImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObjectsResult
import com.meta.pixelandtexel.scanner.objectdetection.utils.ImageUtils.getByteBuffer
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicReference

/**
 * Adapted from:
 * https://github.com/google-ai-edge/mediapipe-samples/blob/main/examples/object_detection/android/app/src/main/java/com/google/mediapipe/examples/objectdetection/MediaPipeObjectDetector.kt
 */
class MediaPipeObjectDetector(context: Context) : IObjectDetectorHelper {
  companion object {
    private const val TAG = "ObjectDetection"
  }

  private var objectDetector: ObjectDetector? = null
  private var resultsListener: IObjectsDetectedListener? = null

  private lateinit var imageProcessingOptions: ImageProcessingOptions

  private var finishedDetectingCallback = AtomicReference<(() -> Unit)?>(null)
  private val isDetecting: Boolean
    get() = finishedDetectingCallback.get() != null

  private var processingImage = AtomicReference<Image?>(null)

  init {
    // build our object detection options

    val baseOptionsBuilder = BaseOptions.builder()

    baseOptionsBuilder.setDelegate(Delegate.GPU)

    baseOptionsBuilder.setModelAssetPath("models/mediapipe/efficientdet_lite0.tflite")

    try {
      val optionsBuilder =
          ObjectDetector.ObjectDetectorOptions.builder()
              .setBaseOptions(baseOptionsBuilder.build())
              .setScoreThreshold(0.5f)
              .setRunningMode(RunningMode.LIVE_STREAM)
              .setMaxResults(3)

      imageProcessingOptions = ImageProcessingOptions.builder().setRotationDegrees(0).build()

      optionsBuilder.setResultListener(this::onResult)
      optionsBuilder.setErrorListener(this::onError)

      val options = optionsBuilder.build()
      objectDetector = ObjectDetector.createFromOptions(context, options)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * Sets the listener that will receive callbacks when objects are detected.
   *
   * @param listener The [IObjectsDetectedListener] to be notified of detection results.
   */
  override fun setObjectDetectedListener(listener: IObjectsDetectedListener) {
    resultsListener = listener
  }

  /**
   * Initiates the object detection process on the provided image. First converts the image to a
   * [ByteBuffer] in RGB888 format, then passes to the [detect] function below. Saves the finally
   * callback to our local atomic reference for calling after the detection has completed.
   *
   * @param image The [Image] to be processed for object detection.
   * @param width The width of the image.
   * @param height The height of the image.
   * @param finally A callback function that must be invoked when the detection for this image has
   *   finished (successfully or unsuccessfully).
   */
  override fun detect(image: Image, width: Int, height: Int, finally: () -> Unit) {
    if (objectDetector == null) {
      Log.w(TAG, "objectDetector not initialized")
      return
    }

    if (isDetecting) {
      // Image dropped; still processing last frame
      return
    }

    finishedDetectingCallback.set(finally)
    processingImage.set(image)

    val rgbBytes = image.getByteBuffer()
    detect(rgbBytes, width, height)
  }

  /**
   * Initiates the actual detection after converting the bytes to a [MPImage].
   *
   * @param rgbBytes The [ByteArray] representing the image in RGB888 format.
   * @param width The width of the image.
   * @param height The height of the image.
   */
  private fun detect(rgbBytes: ByteBuffer, width: Int, height: Int) {
    val startTime = SystemClock.uptimeMillis()

    val mpImage = ByteBufferImageBuilder(rgbBytes, width, height, MPImage.IMAGE_FORMAT_RGB).build()

    objectDetector?.detectAsync(mpImage, imageProcessingOptions, startTime)
  }

  /**
   * TODO
   *
   * @param result
   * @param input
   */
  private fun onResult(result: ObjectDetectorResult, input: MPImage) {
    val finishTimeMs = SystemClock.uptimeMillis()
    val inferenceTime = finishTimeMs - result.timestampMs()

    val image = processingImage.getAndSet(null)

    resultsListener?.onObjectsDetected(
        DetectedObjectsResult.fromMPResults(
            result.detections(), inferenceTime, input.width, input.height),
        image!!)

    finishedDetectingCallback.getAndSet(null)!!()
  }

  private fun onError(e: RuntimeException) {
    Log.e(TAG, e.message, e)
    finishedDetectingCallback.getAndSet(null)!!()
  }
}
