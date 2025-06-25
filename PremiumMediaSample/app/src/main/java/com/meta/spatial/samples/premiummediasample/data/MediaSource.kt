/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.data

import com.meta.spatial.runtime.StereoMode
import java.io.Serializable

sealed class VideoSource : Serializable {
  data class Raw(val videoRaw: Int) : VideoSource()

  data class Url(val videoUrl: String, val drmLicenseUrl: String? = null) : VideoSource()
}

data class MediaSource(
    val videoSource: VideoSource,
    val stereoMode: StereoMode = StereoMode.None,
    val videoShape: VideoShape = VideoShape.Rectilinear,
    var videoDimensionsPx: Size = Size(1920, 1080),
    var position: Long = 0L,
    var mips: Int = 4,
    var audioCodecType: AudioCodecType = AudioCodecType.None,
) : Serializable {

  enum class VideoShape {
    Rectilinear,
    Equirect180,
  }

  enum class AudioCodecType {
    None
  }

  val aspectRatio: Float
    get() =
        (videoDimensionsPx.x * if (stereoMode == StereoMode.LeftRight) 0.5f else 1f) /
            (videoDimensionsPx.y * if (stereoMode == StereoMode.UpDown) 0.5f else 1f)

  fun isRemote(): Boolean {
    return (videoSource is VideoSource.Url && videoSource.videoUrl.startsWith("http"))
  }

  fun isStream(): Boolean {
    return (videoSource is VideoSource.Url && videoSource.videoUrl.endsWith("mpd"))
  }
}
