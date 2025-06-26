// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.activities

import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.objectdetection.camera.CameraController
import com.meta.pixelandtexel.scanner.objectdetection.camera.models.CameraProperties
import com.meta.pixelandtexel.scanner.objectdetection.views.android.ISurfaceProvider
import com.meta.pixelandtexel.scanner.services.http.VideoServer
import com.meta.pixelandtexel.scanner.utils.HttpUtils
import com.meta.pixelandtexel.scanner.viewmodels.LiveStreamingViewModel
import com.meta.pixelandtexel.scanner.views.camera.LiveStreamingScreen
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.VRFeature
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * An activity which demonstrates as a proof-of-concept of how you could implement live-streaming
 * functionality into your app, to broadcast over local network the video feed from the camera, for
 * consumption by VLC, OBS, or other software that can read a mjpeg video feed over IP.
 */
class LiveStreamingActivity :
    ActivityCompat.OnRequestPermissionsResultCallback,
    AppSystemActivity(),
    CameraController.ImageAvailableListener {
  companion object {
    private const val TAG = "LiveStreamingActivity"

    private const val PERMISSIONS_REQUEST_CODE = 1000
    private val PERMISSIONS_REQUIRED = arrayOf("horizonos.permission.HEADSET_CAMERA")
  }

  private lateinit var permissionsResultCallback: (granted: Boolean) -> Unit

  private lateinit var cameraController: CameraController
  private lateinit var videoServer: VideoServer

  private var liveStreamPanelEntity: Entity? = null
  private lateinit var liveStreamingViewModel: LiveStreamingViewModel
  private lateinit var cameraViewSurfaceProvider: ISurfaceProvider

  override fun registerFeatures(): List<SpatialFeature> {
    val features =
        mutableListOf(VRFeature(this), ComposeFeature(), IsdkFeature(this, spatial, systemManager))
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    NetworkedAssetLoader.init(File(applicationContext.cacheDir.canonicalPath), OkHttpAssetFetcher())

    cameraController = CameraController(this)
    cameraController.onCameraPropertiesChanged += ::onCameraPropertiesChanged
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // set the reference space to enable re-centering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.2f)
    scene.updateIBLEnvironment("museum_lobby.env")

    scene.setViewOrigin(0.0f, 0.0f, 0.0f, 180.0f)

    scene.enablePassthrough(true)

    // create the live streaming panel

    liveStreamPanelEntity =
        Entity.createPanelEntity(
            R.integer.video_streaming_panel_id,
            Transform(Pose(Vector3(0f, 1.4f, -1.5f), Quaternion(0f, 180f, 0f))),
            Grabbable(type = GrabbableType.PIVOT_Y))
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.integer.video_streaming_panel_id) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            fractionOfScreen = 0.4f
            width = 0.8f
            height = 0.8f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
          composePanel {
            liveStreamingViewModel =
                LiveStreamingViewModel(
                    requestPermission = {
                      // first ask permission if we haven't already
                      if (!hasPermissions()) {
                        this@LiveStreamingActivity.requestPermissions { granted ->
                          liveStreamingViewModel.setPermissionGranted(granted)
                        }

                        return@LiveStreamingViewModel
                      }

                      liveStreamingViewModel.setPermissionGranted(true)
                    },
                    onSurfaceAvailable = {
                      cameraViewSurfaceProvider =
                          object : ISurfaceProvider {
                            override val surface: Surface
                              get() = it
                          }

                      if (!::videoServer.isInitialized) {
                        videoServer = VideoServer()

                        val ip = HttpUtils.getIPAddress()
                        if (ip != null) {
                          liveStreamingViewModel.updateStreamingInfo(ip, videoServer.port)
                        }
                      }

                      startCamera()
                    },
                    onSurfaceDestroyed = {
                      // handle surface destroyed; stop current session

                      stopCamera()
                    })

            setContent { LiveStreamingScreen(liveStreamingViewModel) }
          }
        })
  }

  /** Starts the camera session, or initializes if the camera controller isn't initialized yet. */
  private fun startCamera() {
    if (cameraController.isInitialized) {
      cameraController.start(
          surfaceProviders =
              listOfNotNull(
                  if (::cameraViewSurfaceProvider.isInitialized) cameraViewSurfaceProvider
                  else null),
          imageAvailableListener = this)
      return
    }

    cameraController.initialize()
  }

  /**
   * Callback for when the device camera is initialized, and its properties are processed. Typically
   * only executed once when the user first starts scanning and accepts the camera access
   * permissions.
   *
   * @param properties The device camera properties, encapsulated in a [CameraProperties].
   */
  private fun onCameraPropertiesChanged(properties: CameraProperties) {
    liveStreamingViewModel.updateAspectRatio(
        properties.resolution.width.toFloat() / properties.resolution.height)

    startCamera()
  }

  /** Stops the camera capture session. */
  private fun stopCamera() {
    if (!cameraController.isInitialized || !cameraController.isRunning) {
      return
    }

    cameraController.stop()
  }

  /**
   * Callback from the CameraController for when a new image frame is available from the device
   * camera feed. Encodes the video frame as a jpeg, and writes the frame to the video server
   * stream.
   *
   * **IMPORTANT** new image frames will not be read from the device camera feed until the finally
   * callback is invoked by the receiver.
   *
   * @param image The camera feed [Image] image frame, in the format specified by
   *   CameraController.CAMERA_IMAGE_FORMAT
   * @param width The width of the image in pixels.
   * @param height The height of the image in pixels.
   * @param finally The callback to be executed by the object detector when it is finished writing
   *   the video frame to the mjpeg video stream.
   */
  override fun onNewImage(image: Image, width: Int, height: Int, finally: () -> Unit) {
    try {
      /**
       * NOTE: This mjpeg stream implementation is only meant as a proof-of-concept. For production
       * code, you should consider looking into using a h.264 MediaCodec surface, and potentially
       * the real-time streaming protocol service depending on your use case.
       */
      if (image.format != ImageFormat.JPEG) {
        // very inefficient; takes 40-60 ms for SHD
        val nv21 = image.getNv21()
        val jpegBytes = nv21ToJpeg(nv21, width, height)
        videoServer.writeFrameBytes(jpegBytes)
      } else {
        videoServer.writeFrame(image)
      }
    } finally {
      finally.invoke()
    }
  }

  /** Returns a NV21 Image format byte array for creating a YUV image with jpeg compression */
  private fun Image.getNv21(): ByteArray {
    val w = width
    val h = height
    val nv21 = ByteArray(w * h * 3 / 2)

    // copy the Y plane
    planes[0].buffer.run {
      val rowStride = planes[0].rowStride
      var dst = 0
      for (row in 0 until h) {
        position(row * rowStride)
        get(nv21, dst, w) // Y is always packed
        dst += w
      }
    }

    // copy & interleave U + V  (-> VU order for NV21)
    val uPlane = planes[1]
    val vPlane = planes[2]

    val chHeight = h / 2
    val chWidth = w / 2

    // start of chroma section
    var dst = w * h

    for (row in 0 until chHeight) {
      for (col in 0 until chWidth) {
        // every sample’s location depends on BOTH stride values
        val uIndex = row * uPlane.rowStride + col * uPlane.pixelStride
        val vIndex = row * vPlane.rowStride + col * vPlane.pixelStride

        nv21[dst++] = vPlane.buffer.get(vIndex) // V first...
        nv21[dst++] = uPlane.buffer.get(uIndex) // ...then U (NV21)
      }
    }

    return nv21
  }

  /**
   * Converts a NV21 Image format byte array into a compressed JPEG representation byte array.
   *
   * @param nv21 The byte array containing the image data in NV21 format.
   * @param width The width of the image in pixels.
   * @param height The height of the image in pixels.
   * @param quality The desired quality of the JPEG compression, ranging from 0 (lowest) to 100
   *   (highest). Defaults to 90.
   * @return A byte array containing the JPEG encoded image data.
   */
  private fun nv21ToJpeg(nv21: ByteArray, width: Int, height: Int, quality: Int = 90): ByteArray {
    val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val stream = ByteArrayOutputStream()
    yuv.compressToJpeg(Rect(0, 0, width, height), quality, stream)
    return stream.toByteArray()
  }

  override fun onPause() {
    stopCamera()
    super.onPause()
  }

  override fun onSpatialShutdown() {
    cameraController.dispose()
    videoServer.dispose()
    super.onSpatialShutdown()
  }

  // permissions requesting

  private fun hasPermissions() =
      PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
      }

  private fun requestPermissions(callback: (granted: Boolean) -> Unit) {
    permissionsResultCallback = callback

    ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    when (requestCode) {
      PERMISSIONS_REQUEST_CODE -> {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          Log.d(TAG, "Camera permission granted")
          permissionsResultCallback(true)
        } else {
          Log.w(TAG, "Camera permission denied")
          permissionsResultCallback(false)
        }
      }
    }
  }
}
