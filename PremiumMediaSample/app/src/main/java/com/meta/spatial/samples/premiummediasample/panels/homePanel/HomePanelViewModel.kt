// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.panels.homePanel

import com.meta.spatial.runtime.StereoMode
import com.meta.spatial.samples.premiummediasample.R
import com.meta.spatial.samples.premiummediasample.data.Description
import com.meta.spatial.samples.premiummediasample.data.HomeItem
import com.meta.spatial.samples.premiummediasample.data.MediaSource
import com.meta.spatial.samples.premiummediasample.data.Size
import com.meta.spatial.samples.premiummediasample.data.VideoSource

class HomePanelViewModel {

  lateinit var onItemSelectedHandler: (homeItem: HomeItem) -> Unit

  var items: List<HomeItem> =
      listOf(
          HomeItem(
              id = "sk8chicken",
              thumbId = R.drawable.poster_sk8_chickens,
              description =
                  Description(
                      title = "Sk8 Chickens", description = "Rectilinear Stereoscopic Video"),
              showInMenu = true,
              media =
                  MediaSource(
                      stereoMode = StereoMode.LeftRight,
                      videoDimensionsPx = Size(1920 * 2, 1080),
                      mips = 9,
                      videoSource =
                          VideoSource.Url(
                              "https://dw9dv4drqa1gx.cloudfront.net/DASH_Chickens/SK8_Chickens_3840x1080_Stereo.mpd")),
          ),
          HomeItem(
              id = "apoIsland180",
              thumbId = R.drawable.poster_apo_island,
              description =
                  Description(
                      title = "Apo Island", description = "Equirectangular 180 Stereoscopic Video"),
              showInMenu = true,
              media =
                  MediaSource(
                      stereoMode = StereoMode.LeftRight,
                      videoShape = MediaSource.VideoShape.Equirect180,
                      videoDimensionsPx = Size(2880 * 2, 2880),
                      mips = 9,
                      videoSource =
                          VideoSource.Url(
                              "https://dw9dv4drqa1gx.cloudfront.net/DASH_apo_island/echeng_apo_island_35mbps_vr180.mpd")),
          ),
          HomeItem(
              id = "sintelDRM",
              thumbId = R.drawable.poster_sintel,
              description = Description(title = "Sintel", description = "4K DRM Video"),
              showInMenu = true,
              media =
                  MediaSource(
                      stereoMode = StereoMode.None,
                      videoDimensionsPx = Size(3840, 1636),
                      mips = 1,
                      videoSource =
                          VideoSource.Url(
                              "https://storage.googleapis.com/shaka-demo-assets/sintel-widevine/dash.mpd",
                              "https://cwip-shaka-proxy.appspot.com/no_auth")),
          ),
      )

  companion object {
    val TAG = "HomePanelViewModel"
  }
}
