// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.utils

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import androidx.core.graphics.createBitmap
import java.nio.ByteBuffer

object ImageUtils {
  /** Returns a RGB888 ByteBuffer representation of a YUV_420_888 Image */
  fun Image.getByteBuffer(): ByteBuffer {
    if (this.format != ImageFormat.YUV_420_888) {
      throw IllegalArgumentException(
          "Unsupported format ${this.format}; expected ${ImageFormat.YUV_420_888}"
      )
    }

    val width = this.width
    val height = this.height

    // RGB values (3 bytes per pixel)
    val rgbBuffer = ByteBuffer.allocateDirect(width * height * 3)

    // retrieve the image planes

    val planes = this.planes
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    // get row and pixel strides for each plane

    val yRowStride = planes[0].rowStride
    val yPixelStride = planes[0].pixelStride
    val uRowStride = planes[1].rowStride
    val uPixelStride = planes[1].pixelStride
    val vRowStride = planes[2].rowStride
    val vPixelStride = planes[2].pixelStride

    // loop over every pixel in the Y plane

    for (row in 0 until height) {
      for (col in 0 until width) {
        // calculate the index for Y value

        val yIndex = row * yRowStride + col * yPixelStride
        val y = yBuffer.get(yIndex).toInt() and 0xFF

        // U and V are subsampled by 2 (YUV 4:2:0)

        val uvRow = row / 2
        val uvCol = col / 2
        val uIndex = uvRow * uRowStride + uvCol * uPixelStride
        val vIndex = uvRow * vRowStride + uvCol * vPixelStride
        val u = uBuffer.get(uIndex).toInt() and 0xFF
        val v = vBuffer.get(vIndex).toInt() and 0xFF

        // convert YUV to RGB using standard formulas

        val c = y - 16
        val d = u - 128
        val e = v - 128

        var r = (298 * c + 409 * e + 128) shr 8
        var g = (298 * c - 100 * d - 208 * e + 128) shr 8
        var b = (298 * c + 516 * d + 128) shr 8

        // clamp the values to the [0, 255] range

        r = r.coerceIn(0, 255)
        g = g.coerceIn(0, 255)
        b = b.coerceIn(0, 255)

        // write the RGB values into the ByteBuffer sequentially

        rgbBuffer.put(r.toByte())
        rgbBuffer.put(g.toByte())
        rgbBuffer.put(b.toByte())
      }
    }

    // reset the ByteBuffer's position to the beginning
    rgbBuffer.rewind()

    return rgbBuffer
  }

  /** Returns a ARGB_8888 Bitmap representation of a YUV_420_888 Image */
  fun Image.getBitmap(): Bitmap {
    if (this.format != ImageFormat.YUV_420_888) {
      throw IllegalArgumentException(
          "Unsupported format ${this.format}; expected ${ImageFormat.YUV_420_888}"
      )
    }

    val width = this.width
    val height = this.height

    // init output structures

    val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)

    // retrieve the image planes

    val planes = this.planes
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    // get row and pixel strides for each plane

    val yRowStride = planes[0].rowStride
    val yPixelStride = planes[0].pixelStride
    val uRowStride = planes[1].rowStride
    val uPixelStride = planes[1].pixelStride
    val vRowStride = planes[2].rowStride
    val vPixelStride = planes[2].pixelStride

    // index for the 'pixels' array
    var pixelIndex = 0

    // loop over every pixel in the Y plane

    for (row in 0 until height) {
      for (col in 0 until width) {
        // calculate the index for Y value

        val yIndex = row * yRowStride + col * yPixelStride
        val y = yBuffer.get(yIndex).toInt() and 0xFF

        // U and V are subsampled by 2 (YUV 4:2:0)

        val uvRow = row / 2
        val uvCol = col / 2
        val uIndex = uvRow * uRowStride + uvCol * uPixelStride
        val vIndex = uvRow * vRowStride + uvCol * vPixelStride
        val u = uBuffer.get(uIndex).toInt() and 0xFF
        val v = vBuffer.get(vIndex).toInt() and 0xFF

        // convert YUV to RGB using standard formulas

        val c = y - 16
        val d = u - 128
        val e = v - 128

        var r = (298 * c + 409 * e + 128) shr 8
        var g = (298 * c - 100 * d - 208 * e + 128) shr 8
        var b = (298 * c + 516 * d + 128) shr 8

        // clamp the values to the [0, 255] range

        r = r.coerceIn(0, 255)
        g = g.coerceIn(0, 255)
        b = b.coerceIn(0, 255)

        // combine into ARGB_8888 format (fully opaque alpha) and store in IntArray

        pixels[pixelIndex++] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
      }
    }

    // set our bitmap pixels

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

    return bitmap
  }

  /**
   * Returns a ARGB_8888 Bitmap representation of a YUV_420_888 Image, cropped to the Rect.
   *
   * @param crop The [Rect] representing the area from the source image to crop from.
   * @return
   */
  fun Image.getBitmap(crop: Rect): Bitmap {
    if (this.format != ImageFormat.YUV_420_888) {
      throw IllegalArgumentException(
          "Unsupported format ${this.format}; expected ${ImageFormat.YUV_420_888}"
      )
    }

    // validate our bounds

    require(!crop.isEmpty) { "Bounds empty" }
    require(
        crop.left >= 0 && crop.top >= 0 && crop.right <= this.width && crop.bottom <= this.height
    ) {
      "Bounds not within image dimensions"
    }
    require(crop.width() > 0 && crop.height() > 0) { "Bounds must be non zero" }

    val width = crop.width()
    val height = crop.height()

    // init output structures

    val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)

    // retrieve the image planes

    val planes = this.planes
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    // get row and pixel strides for each plane

    val yRowStride = planes[0].rowStride
    val yPixelStride = planes[0].pixelStride
    val uRowStride = planes[1].rowStride
    val uPixelStride = planes[1].pixelStride
    val vRowStride = planes[2].rowStride
    val vPixelStride = planes[2].pixelStride

    // index for the 'pixels' array
    var pixelIndex = 0

    // loop over every pixel in the Y plane

    for (row in crop.top until crop.bottom) {
      for (col in crop.left until crop.right) {
        // calculate the index for Y value

        val yIndex = row * yRowStride + col * yPixelStride
        val y = yBuffer.get(yIndex).toInt() and 0xFF

        // U and V are subsampled by 2 (YUV 4:2:0)

        val uvRow = row / 2
        val uvCol = col / 2
        val uIndex = uvRow * uRowStride + uvCol * uPixelStride
        val vIndex = uvRow * vRowStride + uvCol * vPixelStride
        val u = uBuffer.get(uIndex).toInt() and 0xFF
        val v = vBuffer.get(vIndex).toInt() and 0xFF

        // convert YUV to RGB using standard formulas

        val c = y - 16
        val d = u - 128
        val e = v - 128

        var r = (298 * c + 409 * e + 128) shr 8
        var g = (298 * c - 100 * d - 208 * e + 128) shr 8
        var b = (298 * c + 516 * d + 128) shr 8

        // clamp the values to the [0, 255] range

        r = r.coerceIn(0, 255)
        g = g.coerceIn(0, 255)
        b = b.coerceIn(0, 255)

        // combine into ARGB_8888 format (fully opaque alpha) and store in IntArray

        pixels[pixelIndex++] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
      }
    }

    // set our bitmap pixels

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

    return bitmap
  }
}
