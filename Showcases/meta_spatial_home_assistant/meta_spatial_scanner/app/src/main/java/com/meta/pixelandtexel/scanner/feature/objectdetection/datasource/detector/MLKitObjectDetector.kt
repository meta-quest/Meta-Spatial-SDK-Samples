package com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector

import android.media.Image
import android.os.SystemClock
import android.util.Log
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.models.DetectedObjectsResult

/**
 * A stateless worker that utilizes Google's ML Kit to perform object detection.
 *
 * This class has a single responsibility: to process an `Image` and return the
 * detection result through a callback. It does not manage any state related to
 * concurrency or stream processing.
 *
 */
class MLKitObjectDetector() : IObjectDetectorHelper {
    companion object {
        private const val TAG = "MLKitObjectDetector"
        private const val MODEL_EFFICIENTNET = "mlkit/mobile_object_labeler_v1.tflite"
        // Other models...
    }

    private var objectDetector: ObjectDetector? = null

    init {
        val localModel = LocalModel.Builder().setAssetFilePath("models/$MODEL_EFFICIENTNET").build()
        try {
            val customOptions =
                CustomObjectDetectorOptions.Builder(localModel)
                    .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                    .setMaxPerObjectLabelCount(1)
                    .setClassificationConfidenceThreshold(0.75f)
                    .enableMultipleObjects()
                    .enableClassification()
                    .build()
            objectDetector = ObjectDetection.getClient(customOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize ML Kit Detector", e)
        }
    }

    /**
     * Performs object detection on a single image.
     *
     * @param image The Image to be processed.
     * @param width The width of the image.
     * @param height The height of the image.
     * @param finally A callback function that will be invoked with the [DetectedObjectsResult]
     *   or null if detection failed.
     */
    override fun detect(
        image: Image,
        width: Int,
        height: Int,
        finally: (DetectedObjectsResult?) -> Unit
    ) {
        if (objectDetector == null) {
            Log.w(TAG, "objectDetector not initialized, cannot detect.")
            finally(null)
            return
        }

        val startTime = SystemClock.uptimeMillis()
        val iImage = InputImage.fromMediaImage(image, 0)

        objectDetector!!
            .process(iImage)
            .addOnSuccessListener { detectedObjects ->
                val finishTimeMs = SystemClock.uptimeMillis()
                val inferenceTime = finishTimeMs - startTime
                val result = DetectedObjectsResult.Companion.fromMLKitResults(
                    detectedObjects,
                    inferenceTime,
                    width,
                    height
                )
                finally(result)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Detection failed", e)
                finally(null)
            }
    }
}