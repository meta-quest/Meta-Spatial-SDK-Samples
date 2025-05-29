// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services.http

import android.media.Image
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.IOException
import java.io.OutputStream
import kotlinx.coroutines.awaitCancellation

/**
 * A server that streams video as a Motion JPEG (MJPEG) by serving individual JPEG frames, using
 * Ktor to set up an embedded HTTP server.
 *
 * @property port The port number on which the server will listen. Defaults to 5000.
 */
class VideoServer(val port: Int = 5000) {
  companion object {
    private const val TAG = "VideoServer"
  }

  // cached header byte arrays
  private val headerBufferFrame = "--frame\r\n".toByteArray()
  private var headerBufferContentLength = "Content-Length: 0\r\n".toByteArray()
  private val headerBufferContentType = "Content-Type: image/jpeg\r\n\r\n".toByteArray()
  private var frameBuffer = ByteArray(0) // will grow if needed
  private var headerBufferEnd = "\r\n\r\n".toByteArray()

  // our server and single client objects
  private val server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>
  private var client: OutputStream? = null

  init {
    // start an embedded server to serve the jpeg frames as an mjpeg stream
    // from the device LAN IP

    server =
        embeddedServer(Netty, host = "0.0.0.0", port = port) {
          routing {
            get("/") {
              call.respondOutputStream(
                  ContentType.parse("multipart/x-mixed-replace;boundary=frame"),
                  HttpStatusCode.OK) {
                    // first close the current client if we have one
                    client?.close()
                    client = this

                    try {
                      awaitCancellation()
                    } finally {
                      if (client == this) {
                        client = null
                      }
                    }
                  }
            }
          }
        }

    server.start(wait = false)
  }

  /**
   * Writes a single video frame, obtained from an [Image] object, to the connected client. The
   * frame is sent as a JPEG image part of the MJPEG stream. If no client is connected, this method
   * does nothing.
   *
   * @param image The [Image] object containing the video frame data. The first plane of the image
   *   is assumed to contain the JPEG data.
   */
  fun writeFrame(image: Image) {
    val out = client ?: return

    // write one MJPEG frame to the connected client

    val src = image.planes[0].buffer
    val size = src.remaining()

    // reallocate if the image bytes size is greater than the buffer
    if (size > frameBuffer.size) {
      frameBuffer = ByteArray(size)
      headerBufferContentLength = "Content-Length: $size\r\n".toByteArray()
    }

    src.get(frameBuffer, 0, size)

    try {
      out.write(headerBufferFrame)
      out.write(headerBufferContentLength)
      out.write(headerBufferContentType)
      out.write(frameBuffer, 0, size) // reused buffer
      out.write(headerBufferEnd)
      out.flush()
    } catch (e: IOException) {
      // close the client if there was a connection issue mid-transfer
      client = null
      kotlin.runCatching { out.close() }
    }
  }

  /**
   * Writes a single video frame, provided as a [ByteArray], to the connected client. The frame is
   * sent as a JPEG image part of the MJPEG stream. If no client is connected, this method does
   * nothing.
   *
   * @param bytes The [ByteArray] containing the JPEG data for the video frame.
   */
  fun writeFrameBytes(bytes: ByteArray) {
    val out = client ?: return

    // write one MJPEG frame to the connected client

    val size = bytes.size

    try {
      out.write(headerBufferFrame)
      out.write("Content-Length: $size\r\n".toByteArray())
      out.write(headerBufferContentType)
      out.write(bytes, 0, size)
      out.write(headerBufferEnd)
      out.flush()
    } catch (e: IOException) {
      // close the client if there was a connection issue mid-transfer
      client = null
      kotlin.runCatching { out.close() }
    }
  }

  /**
   * Closes any active client connection and stops the embedded HTTP server. This should be called
   * to release resources when the server is no longer needed.
   */
  fun dispose() {
    client?.close()
    server.stop()
  }
}
