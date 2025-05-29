// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.detector

import android.content.Context
import android.media.Image
import android.os.SystemClock
import android.util.Log
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObjectsResult
import java.util.concurrent.atomic.AtomicReference

/**
 * An implementation of [IObjectDetectorHelper] that utilizes Google's ML Kit for performing object
 * detection on [android.media.Image] inputs.
 *
 * This class configures and uses an [ObjectDetector] from ML Kit with a custom TFLite model
 * (EfficientNet by default) to detect objects in a stream of images. It handles the asynchronous
 * processing of images and delivers the detection results through an [IObjectsDetectedListener].
 *
 * The detector is configured for stream mode, allowing multiple objects and classification. It also
 * manages a simple mechanism to prevent processing a new image while a previous one is still being
 * analyzed, effectively dropping frames if the processing takes too long.
 *
 * @param context The application [Context] required for initializing ML Kit components,
 *   particularly for accessing model assets.
 */
class MLKitObjectDetector(context: Context) : IObjectDetectorHelper {
  companion object {
    private const val TAG = "ObjectDetection"
    private const val MODEL_EFFICIENTNET = "mlkit/efficientnet-tflite-lite4-uint8-v1.tflite"
    private const val MODEL_LABELER = "mlkit/mobile_object_labeler_v1.tflite"
    private const val MODEL_MOBILENET =
        "mlkit/mobilenet-v1-tflite-1-0-224-quantized-metadata-v1.tflite"
    private const val MODEL_NASNET = "mlkit/nasnet-tflite-mobile-metadata-v1.tflite"
  }

  private var objectDetector: ObjectDetector? = null
  private var resultsListener: IObjectsDetectedListener? = null

  private var finishedDetectingCallback = AtomicReference<(() -> Unit)?>()
  private val isDetecting: Boolean
    get() = finishedDetectingCallback.get() != null

  init {
    // https://developers.google.com/ml-kit/vision/object-detection/custom-models/android
    val localModel = LocalModel.Builder().setAssetFilePath("models/$MODEL_EFFICIENTNET").build()

    try {
      val options =
          ObjectDetectorOptions.Builder()
              .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
              .enableMultipleObjects()
              .enableClassification()
              .build()
      val customOptions =
          CustomObjectDetectorOptions.Builder(localModel)
              .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
              .setMaxPerObjectLabelCount(1) // we're only using the first one anyway
              .setClassificationConfidenceThreshold(0.65f)
              .enableMultipleObjects()
              .enableClassification()
              .build()

      // objectDetector = ObjectDetection.getClient(options)
      objectDetector = ObjectDetection.getClient(customOptions)
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
   * Initiates the object detection process on the provided image. Uses the callbacks on the
   * [com.google.android.gms.tasks.Task] returned from the process call to ensure that finally is
   * always called.
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

    val startTime = SystemClock.uptimeMillis()

    val iImage = InputImage.fromMediaImage(image, 0)

    objectDetector!!
        .process(iImage)
        .addOnSuccessListener { detectedObjects ->
          val finishTimeMs = SystemClock.uptimeMillis()
          val inferenceTime = finishTimeMs - startTime

          val result =
              DetectedObjectsResult.fromMLKitResults(detectedObjects, inferenceTime, width, height)
          resultsListener?.onObjectsDetected(result, image)

          finishedDetectingCallback.getAndSet(null)!!()
        }
        .addOnFailureListener { e ->
          Log.e(TAG, e.message, e)
          finishedDetectingCallback.getAndSet(null)!!()
        }
  }
}
