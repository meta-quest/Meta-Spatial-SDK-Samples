// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.detector

import android.content.Context
import android.graphics.Rect
import android.media.Image
import android.os.SystemClock
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObjectsResult
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.JavaCamera2Frame
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgproc.Imgproc

/**
 * Implementation adapted from:
 * https://github.com/opencv/opencv/blob/master/samples/android/mobilenet-objdetect/src/org/opencv/samples/opencv_mobilenet/MainActivity.java
 */
class OpenCVObjectDetector(context: Context) : IObjectDetectorHelper {
  companion object {
    private const val TAG = "ObjectDetection"
  }

  private var modelBuffer: MatOfByte? = null
  private var configBuffer: MatOfByte? = null

  private lateinit var net: Net

  private var resultsListener: IObjectsDetectedListener? = null

  private val classNames: List<String> =
      listOf(
          "background",
          "aeroplane",
          "bicycle",
          "bird",
          "boat",
          "bottle",
          "bus",
          "car",
          "cat",
          "chair",
          "cow",
          "diningtable",
          "dog",
          "horse",
          "motorbike",
          "person",
          "pottedplant",
          "sheep",
          "sofa",
          "train",
          "tvmonitor")

  init {
    try {
      val success = OpenCVLoader.initLocal()
      if (!success) {
        throw RuntimeException("Failed to initialize OpenCV")
      }

      // initialize open cv model net

      modelBuffer = getAssetMatOfBytes("models/opencv/mobilenet_iter_73000.caffemodel", context)
      configBuffer = getAssetMatOfBytes("models/opencv/deploy.prototxt", context)

      if (modelBuffer == null || configBuffer == null) {
        throw RuntimeException("Failed to read model or config")
      }

      net = Dnn.readNet("caffe", modelBuffer, configBuffer)
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
   * Initiates the object detection process on the provided image. Converts to an OpenCV Mat with
   * RGB format, calls [getDetections], and processes the resulting Mat to extract the detected
   * results.
   *
   * @param image The [Image] to be processed for object detection.
   * @param width The width of the image.
   * @param height The height of the image.
   * @param finally A callback function that must be invoked when the detection for this image has
   *   finished (successfully or unsuccessfully).
   */
  override fun detect(image: Image, width: Int, height: Int, finally: () -> Unit) {
    val startTime = SystemClock.uptimeMillis()

    // convert image to a CvCameraViewFrame and get the Mat

    val cvFrame = JavaCamera2Frame(image)
    val frameSrc = cvFrame.rgba()
    val frame = Mat()

    // convert to 3 channel
    Imgproc.cvtColor(frameSrc, frame, Imgproc.COLOR_RGBA2RGB)

    CoroutineScope(Dispatchers.IO).launch {
      var detections = getDetections(frame)

      val finishTimeMs = SystemClock.uptimeMillis()
      val inferenceTime = finishTimeMs - startTime

      val rows = frame.rows()
      val cols = frame.cols()

      detections = detections.reshape(1, detections.total().toInt() / 7)

      val cvDetectedObjects = mutableListOf<CvDetectedObject>()

      for (i in 0 until detections.rows()) {
        val confidence = detections.get(i, 2)[0]
        if (confidence < 0.5) {
          continue
        }

        val classId = detections.get(i, 1)[0].toInt()
        val label = classNames[classId]

        val left = (detections.get(i, 3)[0] * cols).toInt()
        val top = (detections.get(i, 4)[0] * rows).toInt()
        val right = (detections.get(i, 5)[0] * cols).toInt()
        val bottom = (detections.get(i, 6)[0] * rows).toInt()
        val bounds = Rect(left, top, right, bottom)

        cvDetectedObjects.add(CvDetectedObject(bounds, label, confidence))
      }

      val result =
          DetectedObjectsResult.fromOpenCVResults(cvDetectedObjects, inferenceTime, width, height)
      resultsListener?.onObjectsDetected(result, image)

      frame.release()
      cvFrame.release()

      finally() // calls image.close()
    }
  }

  /**
   * Creates the input blob from the provided image Mat, and forwards the input to the neural
   * network for processing.
   *
   * @param frame The [Mat] representing the image frame for processing.
   * @return The [Mat] containing the results from the neural network processing.
   */
  private suspend fun getDetections(frame: Mat): Mat {
    return withContext(Dispatchers.IO) {
      // set our new input

      val size = Size(640.0, 640.0)
      val mean = Scalar(127.5)
      val inScaleFactor = 1.0 / 127.5
      val blob = Dnn.blobFromImage(frame, inScaleFactor, size, mean, false, false)

      net.setInput(blob)

      // compute the detections

      net.forward()
    }
  }

  /**
   * Some model loading for OpenCV must be done by passing an absolute path to the file. This
   * function reads the file data from the assets bundle, writes it to a temporary file, and returns
   * that absolute path.
   *
   * @param assetFilePath The path to the asset file within the assets directory (e.g.,
   *   "models/my_model.caffemodel").
   * @param context The Android [Context] used to access the application's assets and cache
   *   directory.
   * @return The absolute path to the created temporary file.
   */
  private fun getAssetTempPath(assetFilePath: String, context: Context): String {
    // extract our file name and extension from the path
    val fileNameWithExtension = assetFilePath.substringAfterLast('/')
    val fileName = fileNameWithExtension.substringBeforeLast('.', fileNameWithExtension)
    val extension =
        if (fileNameWithExtension.contains('.')) {
          fileNameWithExtension.substringAfterLast('.')
        } else {
          ""
        }

    val inputStream = context.assets.open(assetFilePath)
    val suffix = if (extension.isNotEmpty()) ".$extension" else null
    val tempFile = File.createTempFile(fileName, suffix, context.cacheDir)

    FileOutputStream(tempFile).use { outputStream -> inputStream.copyTo(outputStream) }

    return tempFile.absolutePath
  }

  /**
   * Reads the bytes of the model from the assets bundle, and returns a [MatOfByte] of that model.
   *
   * @param assetFilePath The path to the asset file within the assets directory (e.g.,
   *   "models/my_model.caffemodel").
   * @param context The Android [Context] used to access the application's assets and cache
   *   directory.
   * @return An OpenCV [MatOfByte] object representing the bytes of the model read from the assets
   *   bundle.
   */
  private fun getAssetMatOfBytes(assetFilePath: String, context: Context): MatOfByte? {
    val buffer: ByteArray
    try {
      val inputStream = context.assets.open(assetFilePath)
      val size = inputStream.available()
      buffer = ByteArray(size)
      inputStream.read(buffer)
      inputStream.close()
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }

    return MatOfByte(*buffer)
  }

  /**
   * Represents an object detected by an OpenCV Net, encapsulating info about a single detected
   * object, including its location, classification, and the confidence level of the detection.
   *
   * @property bounds The rectangular bounding box delineating the detected object within an image
   *   or frame.
   * @property label The classification label assigned to the detected object (e.g., "cat", "car",
   *   "text").
   * @property confidence A score, typically between 0.0 and 1.0, indicating the detector's
   *   certainty about the classification and localization of the object.
   */
  data class CvDetectedObject(val bounds: Rect, val label: String, val confidence: Double)
}
