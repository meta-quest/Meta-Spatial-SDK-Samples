// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import com.meta.pixelandtexel.scanner.objectdetection.camera.enums.CameraEye
import com.meta.pixelandtexel.scanner.objectdetection.camera.models.CameraProperties
import com.meta.pixelandtexel.scanner.objectdetection.utils.Event1
import com.meta.pixelandtexel.scanner.objectdetection.views.android.ISurfaceProvider
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Manages the device camera lifecycle and provides access to camera frames and properties.
 *
 * This class handles camera initialization, querying available cameras (specifically targeting
 * left/right eye cameras based on metadata), and retrieving intrinsic and extrinsic properties. It
 * manages background threads for camera operations and image acquisition.
 *
 * The controller can start a camera capture session, directing the output to provided surfaces (via
 * [ISurfaceProvider]) and/or an internal [ImageReader]. Frames from the [ImageReader] are delivered
 * via the [ImageAvailableListener] interface, suitable for tasks like computer vision processing.
 * It supports averaging the poses of both eye cameras for a central viewpoint.
 *
 * Adapted from:
 * https://github.com/android/camera-samples/blob/main/Camera2Basic/app/src/main/java/com/example/android/camera2/basic/fragments/CameraFragment.kt
 *
 * @param context The application context, used to access the [CameraManager].
 * @param cameraEye The primary camera eye ([CameraEye.LEFT] or [CameraEye.RIGHT]) to use.
 */
class CameraController(
    private val context: Context,
    private val cameraEye: CameraEye = CameraEye.LEFT,
) {
  companion object {
    private const val TAG = "Camera"

    private const val CAMERA_IMAGE_FORMAT = ImageFormat.YUV_420_888
    private const val CAMERA_SOURCE_KEY = "com.meta.extra_metadata.camera_source"
    private const val CAMERA_POSITION_KEY = "com.meta.extra_metadata.position"
  }

  private var _isRunning = AtomicBoolean(false)

  val isRunning: Boolean
    get() = _isRunning.get()

  val isInitialized: Boolean
    get() = ::cameraProperties.isInitialized

  private lateinit var cameraManager: CameraManager
  private val cameraEyeIds = HashMap<CameraEye, String>()
  private val cameraEyeCharacteristics = HashMap<CameraEye, CameraCharacteristics>()

  private lateinit var cameraProperties: CameraProperties
  val onCameraPropertiesChanged = Event1<CameraProperties>()

  // threads for camera handling and image frame acquisition
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var imageReaderThread: HandlerThread
  private lateinit var imageReaderHandler: Handler

  // objects initialized per session
  private var camera: CameraDevice? = null
  private var session: CameraCaptureSession? = null
  private var imageReader: ImageReader? = null

  // our frame ready for object detection
  private val isProcessingFrame = AtomicBoolean(false)

  // the output size from our camera characteristics
  val cameraOutputSize: Size
    get() = cameraProperties.resolution

  /**
   * Initializes the resources and services needed for the camera frame image reading, and assembles
   * the properties of the camera, which are used throughout the app. Call this after the user has
   * accepted permissions to use the device camera.
   *
   * @throws [RuntimeException] If the camera manager failed to get any device cameras, or it failed
   *   to fetch the camera for the requested eye.
   */
  fun initialize() {
    // start our background threads

    cameraExecutor = Executors.newSingleThreadExecutor()
    imageReaderThread =
        HandlerThread("ImageReaderThread").apply {
          start()
          imageReaderHandler = Handler(this.looper)
        }

    // get our camera manager

    cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    if (cameraManager.cameraIdList.isEmpty()) {
      throw RuntimeException("Failed to get system camera")
    }

    // get our camera characteristics

    Log.d(TAG, "Found camera ids: ${cameraManager.cameraIdList.joinToString()}")

    for (id in cameraManager.cameraIdList) {
      val characteristics = cameraManager.getCameraCharacteristics(id)

      val position =
          characteristics.get(CameraCharacteristics.Key(CAMERA_POSITION_KEY, Int::class.java))
      val eye =
          when (position) {
            0 -> CameraEye.LEFT
            1 -> CameraEye.RIGHT
            else -> CameraEye.UNKNOWN
          }

      // store the characteristics of each camera
      cameraEyeIds[eye] = id
      cameraEyeCharacteristics[eye] = characteristics
    }

    if (cameraEyeIds[cameraEye] == null) {
      throw RuntimeException("Failed to get camera for ${cameraEye.name} eye")
    }

    cameraProperties = getCameraProperties(cameraEye)

    onCameraPropertiesChanged.invoke(cameraProperties)
  }

  /**
   * Function to start the camera session, and begin reading image frames. Call this after calling
   * initialize.
   *
   * @param surfaceProviders List of [ISurfaceProvider] objects to display the camera feed.
   * @param imageAvailableListener Optional object which implements the [ImageAvailableListener]
   *   pattern to receive image frames for processing.
   * @throws [RuntimeException] if the camera hasn't been initialized yet, or not all surfaces were
   *   ready.
   */
  fun start(
      surfaceProviders: List<ISurfaceProvider> = listOf(),
      imageAvailableListener: ImageAvailableListener? = null,
  ) {
    if (!isInitialized) {
      throw RuntimeException("Camera not initialized")
    }

    if (surfaceProviders.isEmpty() && imageAvailableListener == null) {
      Log.w(TAG, "No reason to start camera")
      return
    }

    if (_isRunning.get()) {
      Log.w(TAG, "Camera controller already running")
      return
    }

    CoroutineScope(Dispatchers.Main).launch {
      var elapsed = 0L
      while (surfaceProviders.any { !it.surfaceAvailable }) {
        Log.w(TAG, "Waiting for the camera preview surface to become available...")

        // wait for the surface to become available
        delay(10L)
        elapsed += 10L

        if (elapsed >= 10000L) {
          throw RuntimeException("Timeout while waiting for surface(s)")
        }
      }

      startInternal(surfaceProviders, imageAvailableListener)
    }
  }

  /**
   * The internal start function, called after all surface providers are ready, which performs the
   * actual camera session initialization and image reader setup.
   *
   * @param surfaceProviders The list of [ISurfaceProvider] surface providers onto which to display
   *   the device camera feed.
   * @param imageAvailableListener An (optional) object which implements the
   *   [ImageAvailableListener] pattern to receive camera frame images for processing.
   */
  private suspend fun startInternal(
      surfaceProviders: List<ISurfaceProvider>,
      imageAvailableListener: ImageAvailableListener? = null,
  ) {
    try {
      _isRunning.set(true)

      // open our device camera

      val id = cameraEyeIds[cameraEye]
      camera = openCamera(cameraManager, id!!, cameraExecutor)

      // assemble our targets
      val targets = surfaceProviders.map { it.surface!! }.toMutableList()

      // initialize our image ready for frame object detection

      if (imageAvailableListener != null) {
        imageReader =
            ImageReader.newInstance(
                cameraOutputSize.width,
                cameraOutputSize.height,
                CAMERA_IMAGE_FORMAT,
                2,
            )
        targets.add(imageReader!!.surface)
      }

      // create and start our session with the open camera and list of target surfaces

      session = createCameraPreviewSession(camera!!, targets, cameraExecutor)

      val captureRequestBuilder =
          camera!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            targets.forEach { addTarget(it) }
          }

      session!!.setSingleRepeatingRequest(
          captureRequestBuilder.build(),
          cameraExecutor,
          object : CaptureCallback() {},
      )

      // setup our image reader to receive frames

      imageReader?.setOnImageAvailableListener(
          { reader ->
            if (isProcessingFrame.get()) {
              // still processing our last image
              return@setOnImageAvailableListener
            }

            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

            // Log.d(TAG, "Image available: ${image.format}, ${image.width}x${image.height}")
            isProcessingFrame.set(true)

            imageAvailableListener?.onNewImage(image, image.width, image.height) {
              image.close()
              isProcessingFrame.set(false)
            }
          },
          imageReaderHandler,
      )
    } catch (e: Exception) {
      e.printStackTrace()
      this.stop()
    }
  }

  /**
   * Asynchronously opens a camera device with the specified [cameraId].
   *
   * @param manager The [CameraManager] system service instance.
   * @param cameraId The unique identifier of the camera to open.
   * @param executor The [Executor] on which camera state callbacks will be invoked.
   * @return The opened [CameraDevice] upon successful connection.
   * @throws RuntimeException If the camera encounters an error during the opening process.
   */
  @SuppressLint("MissingPermission")
  private suspend fun openCamera(
      manager: CameraManager,
      cameraId: String,
      executor: Executor,
  ): CameraDevice = suspendCoroutine { cont ->
    Log.d(TAG, "openCamera")

    manager.openCamera(
        cameraId,
        executor,
        object : CameraDevice.StateCallback() {
          override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "camera onOpened")

            cont.resume(camera)
          }

          override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "camera onDisconnected")

            this@CameraController.stop()
          }

          override fun onError(camera: CameraDevice, error: Int) {
            Log.d(TAG, "camera onError")

            val msg =
                when (error) {
                  ERROR_CAMERA_DEVICE -> "Fatal (device)"
                  ERROR_CAMERA_DISABLED -> "Device policy"
                  ERROR_CAMERA_IN_USE -> "Camera in use"
                  ERROR_CAMERA_SERVICE -> "Fatal (service)"
                  ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                  else -> "Unknown"
                }
            val ex = RuntimeException("Camera $cameraId error: ($error) $msg")
            Log.e(TAG, ex.message, ex)

            cont.resumeWithException(ex)
          }
        },
    )
  }

  /**
   * Creates a camera capture session for preview.
   *
   * @param device The [CameraDevice] for which the session is to be created.
   * @param targets A list of [Surface] objects to be used as output targets for the camera preview.
   * @param executor The [Executor] on which the [CameraCaptureSession.StateCallback] will be
   *   invoked.
   * @return The configured [CameraCaptureSession].
   * @throws RuntimeException if the camera session configuration fails.
   */
  private suspend fun createCameraPreviewSession(
      device: CameraDevice,
      targets: List<Surface>,
      executor: Executor,
  ): CameraCaptureSession = suspendCoroutine { cont ->
    Log.d(TAG, "createCameraPreviewSession")

    device.createCaptureSession(
        SessionConfiguration(
            SessionConfiguration.SESSION_REGULAR,
            targets.map { OutputConfiguration(it) },
            executor,
            object : CameraCaptureSession.StateCallback() {
              override fun onConfigured(session: CameraCaptureSession) {
                Log.d(TAG, "CameraCaptureSession::onConfigured")

                cont.resume(session)
              }

              override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "CameraCaptureSession::onConfigureFailed")
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)

                cont.resumeWithException(exc)
              }
            },
        ))
  }

  /**
   * Retrieves and processes the properties for the camera corresponding to the specified
   * [CameraEye].
   *
   * @param eye The specific [CameraEye] (e.g., left or right) for which to retrieve properties.
   * @return A [CameraProperties] object containing detailed information about the specified camera.
   * @throws NullPointerException if the [CameraCharacteristics] for the given [eye] are not found
   *   in the `cameraEyeCharacteristics` map.
   * @throws RuntimeException if essential camera intrinsic properties (pose translation, rotation,
   *   calibration, or sensor size) cannot be queried from the [CameraCharacteristics].
   */
  private fun getCameraProperties(eye: CameraEye): CameraProperties {
    val characteristics = cameraEyeCharacteristics[eye]!!

    // camera source (always 0) and position (0: left camera; 1: right camera from user's
    // perspective)

    val source = characteristics.get(CameraCharacteristics.Key(CAMERA_SOURCE_KEY, Int::class.java))
    val position =
        characteristics.get(CameraCharacteristics.Key(CAMERA_POSITION_KEY, Int::class.java))
    Log.d(TAG, "Using camera source $source with position $position")

    // output formats: 256 (JPEG), 34 (PRIVATE), 35 (YUV_420_888)

    val formats =
        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.outputFormats
    Log.d(TAG, "Found camera output formats: ${formats.joinToString()}")

    // output sizes: 320x240, 640x480, 800x600, 1280x960

    val sizes =
        characteristics
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            .getOutputSizes(CAMERA_IMAGE_FORMAT)
    Log.d(TAG, "Found camera output sizes: ${sizes.joinToString()}")

    // physical properties for screen-to-world calculations
    // https://github.com/oculus-samples/Unity-PassthroughCameraApiSamples/blob/main/Assets/PassthroughCameraApiSamples/PassthroughCamera/Scripts/PassthroughCameraUtils.cs

    val translation = characteristics.get(CameraCharacteristics.LENS_POSE_TRANSLATION)
    val rotation = characteristics.get(CameraCharacteristics.LENS_POSE_ROTATION)
    val intrinsicsArr = characteristics.get(CameraCharacteristics.LENS_INTRINSIC_CALIBRATION)
    val sensorSizePx =
        characteristics.get(CameraCharacteristics.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE)

    if (translation == null || rotation == null || intrinsicsArr == null || sensorSizePx == null) {
      throw RuntimeException("Failed to query camera intrinsics")
    }
    Log.d(TAG, "$sensorSizePx ${intrinsicsArr.joinToString()}} $translation $rotation")

    var quat = Quaternion(rotation[3], -rotation[0], -rotation[1], rotation[2]).inverse()
    quat = quat.times(Quaternion(180f, 0f, 0f))

    val props =
        CameraProperties(
            eye,
            Vector3(translation[0], translation[1], -translation[2]),
            quat,
            Vector2(intrinsicsArr[0], intrinsicsArr[1]),
            Vector2(intrinsicsArr[2], intrinsicsArr[3]),
            Size(sensorSizePx.right, sensorSizePx.bottom),
        )

    return props
  }

  /**
   * Stops the camera session, closing the opened camera and stopping the image reader. Call this
   * when you want to pause or stop the camera, but possibly resume later.
   */
  fun stop() {
    Log.d(TAG, "stop")

    _isRunning.set(false)

    // wait for any object detection service to finish performing an inference with the image
    CoroutineScope(Dispatchers.Main).launch {
      delay(100L)
      imageReader?.close()
      imageReader = null
    }

    session?.close()
    session = null

    camera?.close()
    camera = null
  }

  /**
   * Stops the camera session by calling [stop], and then disposes of the background thread
   * resources used for the camera session and image reader. Call this when you want to stop the
   * camera, and don't plan on restarting it again.
   */
  fun dispose() {
    Log.d(TAG, "dispose")

    stop()

    if (::cameraExecutor.isInitialized) {
      cameraExecutor.shutdown()
    }
    if (::imageReaderThread.isInitialized) {
      imageReaderThread.quitSafely()
    }
  }

  /**
   * Simple pattern for objects to implement which want to receive a camera frame image for
   * processing.
   */
  interface ImageAvailableListener {
    /**
     * Function called when a new image frame is available from the device camera feed.
     *
     * **IMPORTANT** new image frames will not be read from the device camera feed until the finally
     * callback is invoked by the receiver.
     *
     * @param image The camera feed [Image] image frame, in the format specified by
     *   CameraController.CAMERA_IMAGE_FORMAT
     * @param width The width of the image in pixels.
     * @param height The height of the image in pixels.
     * @param finally The callback to be executed by the object when any image processing has
     *   complated.
     */
    fun onNewImage(image: Image, width: Int, height: Int, finally: () -> Unit)
  }
}
