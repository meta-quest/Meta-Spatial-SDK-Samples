// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive.panel

object PanelRegistrationIds {
  const val GALLERY = 1
  const val GALLERY_MENU = 2
  const val MEDIA_FILTER = 3
  const val UPLOAD_MEDIA = 4
  const val PRIVACY_POLICY = 5

  private const val MEDIA_PREFIX = "00"
  private const val MEDIA_POPUP_PREFIX = "11"
  private const val MEDIA_IMMERSIVE_PREFIX = "22"

  fun media(mediaId: Long): Int {
    var mediaIdStr = mediaId.toString()

    if (mediaIdStr.length > 7) {
      mediaIdStr = mediaIdStr.substring(0, 7)
    } else {
      mediaIdStr = mediaIdStr.padStart(7, '0')
    }
    return ("$MEDIA_PREFIX$mediaIdStr").toInt()
  }

  fun mediaPopUpButton(mediaId: Long): Int {
    var mediaIdStr = mediaId.toString()

    if (mediaIdStr.length > 7) {
      mediaIdStr = mediaIdStr.substring(0, 7)
    } else {
      mediaIdStr = mediaIdStr.padStart(7, '0')
    }

    return ("$MEDIA_POPUP_PREFIX$mediaIdStr").toInt()
  }

  fun mediaImmersive(mediaId: Long): Int {
    var mediaIdStr = mediaId.toString()

    if (mediaIdStr.length > 7) {
      mediaIdStr = mediaIdStr.substring(0, 7)
    } else {
      mediaIdStr = mediaIdStr.padStart(7, '0')
    }
    return ("$MEDIA_IMMERSIVE_PREFIX$mediaIdStr").toInt()
  }
}
