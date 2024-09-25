// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.utils

import com.meta.pixelandtexel.geovoyage.utils.AudioUtils.calculateRMS
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import kotlin.math.sqrt

/**
 * Extension function to convert a byte buffer (of PCM format) to an audio signal – an array of
 * floating point numbers representing the audio signal's amplitude
 */
fun ByteBuffer.toAmplitudeArray(): ShortArray {
  // calculate the number of 16-bit samples from the available bytes in the ByteBuffer – each
  // audio sample in a 16-bit PCM format consists of 2 bytes (a 16-bit short value)
  val shortArray = ShortArray(this.remaining() / 2)

  for (i in shortArray.indices) {
    val low = this.get().toInt() and 0xFF
    val high = this.get().toInt() shl 8
    val amp = (low or high).toShort()
    shortArray[i] = amp
  }

  return shortArray
}

/** Extension function to copy the remaining contents of this byte buffer to another */
fun ByteBuffer.copyTo(dest: ByteBuffer) {
  val startPosition = this.position()

  try {
    dest.put(this)
  } catch (e: BufferOverflowException) {
    e.printStackTrace()
  } catch (e: Exception) {
    e.printStackTrace()
  }

  // restore the original position of the source buffer
  this.position(startPosition)
}

class NoiseLevelAdjuster(bufferSize: Int, private val adjustmentFactor: Double = 1.5) {
  private val internalBuffer: ByteBuffer = ByteBuffer.allocateDirect(bufferSize)
  private var baselineNoiseLevel: Double = 0.0

  /**
   * Adds new audio data to the internal buffer
   *
   * @return whether or not the internal buffer is full
   */
  fun addAudioData(audioData: ByteBuffer): Boolean {
    if (internalBuffer.remaining() == 0) {
      return true
    }

    audioData.rewind()

    // we can write the whole audio data buffer to our internal one
    if (internalBuffer.remaining() >= audioData.capacity()) {
      audioData.copyTo(internalBuffer)
      return false
    }

    // we can partially write the audio data buffer to our internal one
    while (internalBuffer.hasRemaining()) {
      internalBuffer.put(audioData.get())
    }

    return true
  }

  fun updateBaselineNoiseLevel(): Double {
    // Prepare the internal buffer for reading
    internalBuffer.rewind()

    // Calculate RMS from the internal buffer
    val amplitudeArray = internalBuffer.toAmplitudeArray()
    val noiseLevel = calculateRMS(amplitudeArray)

    // Update the baseline noise level with a running average
    baselineNoiseLevel = (baselineNoiseLevel + noiseLevel) / 2

    // get our buffer ready to read again
    internalBuffer.clear()

    return noiseLevel
  }

  fun getSilenceThreshold(): Double {
    return baselineNoiseLevel * adjustmentFactor
  }
}

object AudioUtils {
  /**
   * Calculate the root mean square (rms), or average absolute amplitude to determine the volume
   * level of the audio signal
   */
  fun calculateRMS(amplitudeArray: ShortArray): Double {
    var sum = 0.0
    for (amp in amplitudeArray) {
      sum += (amp * amp).toDouble()
    }
    return sqrt(sum / amplitudeArray.size)
  }

  /** Detect silence by checking if the volume level is below a threshold */
  fun isSilence(rms: Double, threshold: Double): Boolean {
    return rms < threshold
  }
}
